# Servlet-Json-Data-Platform

## Web data platform for Sensing Project

Developer: Wenhao Huang

Technology Stack: Tomcat(Apache) + Servlet + SqlServer + CSS + jQuery + HTML


Server Platform : Tomcat 7.0


## Features (In Progress)

1.Receive & Transfer the sensors data by JSON.　--Finished

2.Saving the data in the SqlServer.  --Finished

3.Show in the webpage as tables and graphics.  --Finished

4.Low Signal and Data Alarm.  --Finished

## TEST WEBSITE
http://bus.hwhhome.net/login.html

## Project Paper
Bus Crowdedness Sensing Based on Deep Learning, 
Wenhao Huang , Akira Tsuge , Yin Chen , Tadashi Okoshi , Jin Nakazawa 研究報告ユビキタスコンピューティングシステム（UBI）,2021-UBI-69(1),1-7 (2021-02-22) , 2188-8698
http://id.nii.ac.jp/1001/00209517/


##API説明（日本語）

①乗客人数アップロード（テストしないてください DO NOT TEST）

POST方法、JSON例：{"BUS_NUMBER":"SOKANKAN","PASSENGER_NUM":"10","DATETIME":"2021/06/29 23:06:00","GPS_LOCATION":"35.3952,139.4642"}

URL：http://bus.hwhhome.net/push_passenger

②CO2データアップロード（テストしないてください DO NOT TEST）

POST方法、JSON例：{"IDENTIFIER":"510040","DATE":"2021/06/29","TIME":"15:24:05","TEMPERATURE1":"29","HUMIDITY1":"60.7","CO2":"643","SENSOR_STATUS":"81"}

URL：http://bus.hwhhome.net/push_sensordata

③バズナンバーでCO2データ取得

POST方法、JSON例：{"SEARCH_BUS_NUMBER":"SOKANKAN"}

URL：http://bus.hwhhome.net/request_sensing

④バズナンバーで乗客人数取得

POST方法、JSON例：{"SEARCH_BUS_NUMBER":"SOKANKAN"}

URL：http://bus.hwhhome.net/request_passenger

⑤バス情報とセンサ情報を取得（センサー情報登録状況確認用）

POST方法、JSON例：{"FINDTOREG":"0"} （1：未登録センサーを探す、0：全部センサーを探す）

URL：http://bus.hwhhome.net/fetch_sensor_list

⑥バス情報とセンサ情報の登録アップデート（テストしないてください DO NOT TEST）

POST方法、JSON例：{"SENSOR_ID":"510040","BUS_NUMBER":"SOKANKAN","INFO":"Keio Shuttle Bus"}

URL：http://bus.hwhhome.net//update_sensor

⑦バスリスト取得

POST方法、JSON例：{"ACCESS":"1"}

URL：http://bus.hwhhome.net/fetch_bus_list


## About

The project was developed based on a research project.

Please pay attention to the license of using in other situations.

Wenhao Huang All rights reserved.

## License
Apache 2.0
