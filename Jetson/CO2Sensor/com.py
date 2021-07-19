# =========================================================================================
#   通信モジュール
#       Original       ：Y.Takai (MM-Solution Corp.)
#       Version         1.00.00  GREEN BLUE Corp. 2020.2.7    新規作成(Y.Takai)
#                       1.01.00  GREEN BLUE Corp. 2021.6.7    神奈中向けカスタマイズ(Y.Takai)
# =========================================================================================
import time
import datetime
import main
import os
from collections import OrderedDict

items = ["CO2", "TEMPERATURE", "HUMIDITY", "BATT", "RSSI"]

# //============== send data =================
def postData(sData, fDt, fId):

    fBody = ""
    for i in range(len(sData)):
        if not (sData[i] is None):
            fBody += '"' + items[i] + '":"' + str(sData[i]) + '",'

    if (0 < len(fBody)):
        # 日付を入れる...
        fBody += '"DATE":"' + fDt.strftime("%Y-%m-%d") + '",'
        fBody += '"TIME":"' + fDt.strftime("%H:%M:%S") + '",'
        # 識別子を入れる...
        fBody += '"IDENTIFIER":"' + fId + '",'
        # fBody += '"ENABLE":"1",'
        # 最後の一文字を消してデータを閉じる...
        fBody = fBody[:-1]
        fBody = '"{' + fBody
        fBody += '}"'
        print(fBody)
        _wifi(fBody)

# //============== INTERNET wi-fi =================
def _wifi(fJSON):
    import json
    import urllib.request, json

    # fURL = "http://160.16.94.140/sensorBoxKanachu/post/putData.php"
    fURL = "http://160.16.94.140/sensorBoxKanachuGB/post/putData.php"
    fMethod = "POST"
    fHeaders = {"Content-Type" : "application/json"}
    fJSON = fJSON.replace('\\', '')
    if (fJSON[0] == '"'):
        fJSON = fJSON[1:]
    if (fJSON[-1:] == '"'):
        fJSON = fJSON[:-1]
    json_data = fJSON.encode(encoding='utf-8')
    # httpリクエストを準備してPOST
    request = urllib.request.Request(fURL, data=json_data, method=fMethod, headers=fHeaders)
    with urllib.request.urlopen(request) as response:
        response_body = response.read().decode("utf-8")
        print(response_body)
