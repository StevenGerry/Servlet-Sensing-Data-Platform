#!/usr/bin/env python3

import serial
import time
import datetime
import argparse
import os
import sys
#import com

fCOM = "/dev/ttyUSB0"     # for Linux
#fCOM = "COM22"                  # for Windows
fDir = "./data/"

# データ定義
class dataInfo:
    def __init__(self):
        self.clearData()

    # データの初期化 / メンバ定義
    def clearData(self):
        self.fids = 0       # センサー識別子
        self.feq = 0.0      # 電波強度
        self.fbat = 0.0     # 電池電圧
        self.ftmp = 0.0     # 温度（℃）
        self.fhum = 0.0     # 湿度（％）
        self.fco2 = 0       # CO2（ppm）


def getData(fData):
    # デバイスをセットする...
    fdev = setDevice()
    # データを取得する...
    while True:
        if fdev == None:
            fdev = setDevice()
        else:
            # データ取得する
            try:
                line = fdev.readline().decode('utf-8')
                if (0 < len(line)):
                    test = line
                if (20 < len(line)):
                    fData.clearData()
                    dList = line.split(":")
                    fData.fids = int(dList[5].lstrip("ed="), 16)
                    fData.feq  = int(dList[3].lstrip("lq="))
                    fData.fbat = int(dList[7].lstrip("bat="))/1000
                    fData.ftmp = int(dList[10].lstrip("tm="))/10
                    fData.fhum = int(dList[11].lstrip("hu="))/10
                    fData.fco2 = int(dList[12].lstrip("co2=").rstrip())
                    saveData(fData)
                    # データを送信
                    sendCloud(fData)
            except:
                # ドングルが抜き差しされた場合を想定…
                fdev = setDevice()
                pass
        

def setDevice():
    try:
        fdev = serial.Serial(fCOM, 115200, timeout=3)
    except:
        fdev = None
    return fdev


def main():
    # データオブジェクトの生成
    fData = dataInfo()
    # データを取得（書き込み・クラウド送信）    
    getData(fData)


def saveData(fData):
    dt_now = datetime.datetime.now()
    ldir = fDir + dt_now.strftime('/%Y/%Y-%m-%d/')
    os.makedirs(ldir, exist_ok=True)
    fName = dt_now.strftime('TW_CO2-%Y-%m-%d-') + str(fData.fids) + ".csv"
    wdata = dt_now.strftime('%Y-%m-%d %H:%M:%S')  + ","
    wdata += str(fData.fids) + ","
    wdata += str(fData.feq) + ","
    wdata += str(fData.fbat) + ","
    wdata += str(fData.ftmp) + ","
    wdata += str(fData.fhum) + ","
    wdata += str(fData.fco2)
    wdata += "\n"
    with open(ldir + fName, mode='a') as f:
        f.write(wdata)


def sendCloud(fData):
    dt_now = datetime.datetime.now()
    sData = []
    sData.append(fData.fco2)
    sData.append(fData.ftmp)
    sData.append(fData.fhum)
    sData.append(fData.fbat)
    sData.append(fData.feq)
    com.postData(sData, dt_now, str(fData.fids))


if __name__ == '__main__':
    main()
