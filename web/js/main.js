var startTime = new Date();
var co2_received_json = null;
var passenger_received_json = null;
var bus_list_json = null;
var co2_public;
var pass_num_public;
var co2Interval;
var passengerInterval;
var searchingBus = null;
var loginuser = null;
var map = L.map('map').setView({lon: 139.4499528, lat: 35.3948313}, 13);

$(function () {
    console.log("Kanachu Bus Sensing Project by Wenhao Huang, Keio University");
    getCookie();
    fetchBus();
    setInterval(tick, 500);
    if (searchingBus != null) {
        co2Interval = setInterval(renderTable, 5000);
        passengerInterval = setInterval(passengerData, 10000);
    }
});

function fetchBus(){
    var message={
        "ACCESS": "1"
    }
    var HTML = "<thead>\n" +
        "        <td>車ナンバー</td>\n" +
        "        </thead>\n" +
        "        <tbody>\n";
    $.ajax({
        timeout:1000,
        type:'POST',
        dataType:"json",
        contentType: "application/json",
        url:"http://bus.hwhhome.net/fetch_bus_list",
        data:JSON.stringify(message),
        async:false,
        success:function(data){
            var obj = JSON.parse(JSON.stringify(data));
            bus_list_json = obj;
            console.log("fetchBus: received:");
            console.log(bus_list_json);
        },
        complete : function(XMLHttpRequest,status){
            if(status=='timeout'){
                ajaxTimeoutTest.abort();
                alert("fetchBus: TIME OUT");
            }
        }
    });
    if (bus_list_json != null) {
        console.log("fetchBus: listing...");
        for (var i = 0; i < bus_list_json.data.length; i++) {
            HTML += "<tr>\n" +
                "<td><button id =bus_sel_"+bus_list_json.data[i].BUS_NUMBER.toString() + ">" + bus_list_json.data[i].BUS_NUMBER.toString() + "</button></td></tr>\n";
        }
    }
    HTML += "<tr><td><button id=\"reset\">Reset</button></td></tr></tbody>";
    $('.busList').html(HTML);
}

$(function (){
    $("button[id^='bus_sel_']" ).each( function(){
        $(this).bind("click" , function(){
            var bus_id = $(this).attr("id").substring(8);
            console.log("bus: " + bus_id + " selected!");
            searchingBus = bus_id;
            $("#sensing_bus").text(bus_id);
            startCO2();
            startPassenger();
        });
    });
    $("#reset").click(function () {
        stopCO2();
        stopPassenger();
        searchingBus = null;
        co2_received_json = null;
        passenger_received_json = null;
        bus_list_json = null;
        co2_public = null;
        pass_num_public = null;
        $("#pass_num_show").text("0");
        $("#sensing_bus").text("なし");
        $("#gps_data").text("NULL");
        $("#map").innerHTML = null;
    });
});

function sensingData(){
    var searchNumber={
        "SEARCH_BUS_NUMBER": searchingBus
    }
    $.ajax({
        type:'POST',
        dataType:"json",
        contentType: "application/json",
        url:"http://bus.hwhhome.net/request_sensing",
        data:JSON.stringify(searchNumber),
        success:function(data){
            var obj = JSON.parse(JSON.stringify(data));
            co2_received_json = obj;
            console.log("sensingData: received:");
            console.log(co2_received_json);
        }
    });
}

function stopCO2()
{
    console.log("main: CO2 Sensing STOP")
    clearInterval(co2Interval);
}
function startCO2()
{
    console.log("main: CO2 Sensing START")
    co2Interval = setInterval(renderTable,5000);
}
function stopPassenger()
{
    console.log("main: Passsenger STOP")
    clearInterval(passengerInterval);
}
function startPassenger()
{
    console.log("main: Passsenger START")
    passengerInterval = setInterval(passengerData,10000);
}

function passengerData(){
    var searchNumber={
        "SEARCH_BUS_NUMBER": searchingBus
    }
    $.ajax({
        type:'POST',
        dataType:"json",
        contentType: "application/json",
        url:"http://bus.hwhhome.net/request_passenger",
        data:JSON.stringify(searchNumber),
        success:function(data){
            var obj = JSON.parse(JSON.stringify(data));
            passenger_received_json = obj;
            console.log("passengerData: received:");
            console.log(passenger_received_json);
            pass_num_public = passenger_received_json.data[0].PASSENGER_NUM;
            $("#pass_num_show").text(pass_num_public);
            var gps = [];
            gps = passenger_received_json.data[0].GPS_LOCATION.split(',');
            console.log("gpsdata: "+gps[0]+", "+gps[1]);
            $("#gps_data").text(gps);
            L.marker({lon: gps[1], lat: gps[0]}).bindPopup('The center of the world').addTo(map);
        }
    });
}

function renderTable() {
    sensingData();
    var HTML = "<thead>\n" +
        "        <td title=\"車ナンバー\">車ナンバー</td>\n" +
        "        <td title=\"時間\">時間</td>\n" +
        "        <td title=\"番号\">番号</td>\n" +
        "        <td title=\"電波\">電波</td>\n" +
        "        <td title=\"温度\">温度</td>\n" +
        "        <td title=\"湿度\">湿度</td>\n" +
        "        <td title=\"CO2\">CO2</td>\n" +
        "        </thead>\n" +
        "        <tbody>\n";
    var data = co2_received_json;
    // var myData = mySort(data);
    var busNumber = "";
    var co2Number = 0;
    var co2sum = 0;
    var count = 0;
    if (co2_received_json != null) {
        for (var i = 0; i < co2_received_json.data.length; i++) {
            var datadate = co2_received_json.data[i].DATETIME.toString();
            datadate = datadate.substring(0, 19);
            datadate = datadate.replace(/-/g, '/');
            var datatimestamp = new Date(datadate).getTime();
            var now = new Date();
            var TTL = now - datatimestamp;
            //console.log(TTL.toString());
            if (TTL <= 600000 && co2_received_json.data[i].CO2 > 0 && co2_received_json.data[i].SENSOR_STATUS > 50) {
                HTML += "<tr>\n" +
                    "            <td>" + co2_received_json.data[i].BUS_NUMBER + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].DATETIME + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].IDENTIFIER + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].SENSOR_STATUS + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].TEMPERATURE + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].HUMIDITY + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].CO2 + "</td>\n";
                count++;
                busNumber = co2_received_json.data[i].BUS_NUMBER;
                co2sum = co2sum + parseInt(co2_received_json.data[i].CO2)
                co2Number = co2sum / count;
                console.log("renderTable:" + busNumber.toString() + "," + co2Number.toString());
            } else if (TTL <= 600000 && co2_received_json.data[i].CO2 > 0) {
                HTML += "<tr>\n" +
                    "            <td>" + co2_received_json.data[i].BUS_NUMBER + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].DATETIME + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].IDENTIFIER + "</td>\n" +
                    "            <td bgcolor=\"#ff8c00\">" + co2_received_json.data[i].SENSOR_STATUS + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].TEMPERATURE + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].HUMIDITY + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].CO2 + "</td>\n";
                count++;
                busNumber = co2_received_json.data[i].BUS_NUMBER;
                co2sum = co2sum + parseInt(co2_received_json.data[i].CO2)
                co2Number = co2sum / count;
                console.log("renderTable: " + busNumber.toString() + "," + co2Number.toString());
            } else if (TTL <= 600000 && co2_received_json.data[i].SENSOR_STATUS > 50)
                HTML += "<tr>\n" +
                    "            <td>" + co2_received_json.data[i].BUS_NUMBER + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].DATETIME + "</td>\n" +
                    "            <td bgcolor=\"#FF0000\">" + co2_received_json.data[i].IDENTIFIER + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].SENSOR_STATUS + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].TEMPERATURE + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].HUMIDITY + "</td>\n" +
                    "            <td bgcolor=\"#FF0000\">" + co2_received_json.data[i].CO2 + "</td>\n";
            else if (TTL <= 600000)
                HTML += "<tr>\n" +
                    "            <td>" + co2_received_json.data[i].BUS_NUMBER + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].DATETIME + "</td>\n" +
                    "            <td bgcolor=\"#FF0000\">" + co2_received_json.data[i].IDENTIFIER + "</td>\n" +
                    "            <td bgcolor=\"#ff8c00\">" + co2_received_json.data[i].SENSOR_STATUS + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].TEMPERATURE + "</td>\n" +
                    "            <td>" + co2_received_json.data[i].HUMIDITY + "</td>\n" +
                    "            <td bgcolor=\"#FF0000\">" + co2_received_json.data[i].CO2 + "</td>\n";
            else {
                console.log("renderTable: ERROR: OUT OF TTL, DATA TOO OLD!");
            }
        }
        co2_public = co2Number;
    }
    HTML += "</tbody>";
    $('.commonTable').html(HTML);
}

$(function (){
    // add the OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://openstreetmap.org/copyright">OpenStreetMap contributors</a>'
    }).addTo(map);

    // show the scale bar on the lower left corner
    L.control.scale({imperial: true, metric: true}).addTo(map);
});


function tick(){
    var today = new Date();
    document.getElementById("localtime").innerHTML = showLocale(today);

    var t = today - startTime;
    var day = Math.floor(t/1000/60/60/24);
    var hour = Math.floor(t/1000/60/60%24);
    var min = Math.floor(t/1000/60%60);
    var sec = Math.floor(t/1000%60);
    $("#runTimeTj").html(day+" 日 "+hour+" 时 "+min+" 分 "+sec+" 秒");
}

function showLocale(objD){
    var str,colorhead,colorfoot;
    var yy = objD.getYear();
    if(yy<1900) yy = yy+1900;
    var MM = objD.getMonth()+1;
    if(MM<10) MM = '0' + MM;
    var dd = objD.getDate();
    if(dd<10) dd = '0' + dd;
    var hh = objD.getHours();
    if(hh<10) hh = '0' + hh;
    var mm = objD.getMinutes();
    if(mm<10) mm = '0' + mm;
    var ss = objD.getSeconds();
    if(ss<10) ss = '0' + ss;
    var ww = objD.getDay();
    if  ( ww==0 )  colorhead="<font color=\"#000000\">";
    if  ( ww > 0 && ww < 6 )  colorhead="<font color=\"#000000\">";
    if  ( ww==6 )  colorhead="<font color=\"#000000\">";
    if  (ww==0)  ww="日曜日";
    if  (ww==1)  ww="月曜日";
    if  (ww==2)  ww="火曜日";
    if  (ww==3)  ww="水曜日";
    if  (ww==4)  ww="木曜日";
    if  (ww==5)  ww="金曜日";
    if  (ww==6)  ww="土曜日";
    colorfoot="</font>"
    str = colorhead + yy + "-" + MM + "-" + dd + " " + hh + ":" + mm + ":" + ss + "  " + ww + colorfoot + " || UID：" + loginuser;
    return(str);
}

function getCookie() {
    if (document.cookie == '')
    {
        window.location.replace("http://bus.hwhhome.net/login.html");
        console.log("main: 403: No Login");
    }
    else {
        var cookies = document.cookie;
        var list = cookies.split("; "); // 解析出名/值对列表
        var arr = list[0].split("="); // 解析出名和值
        loginuser = arr[1];
        console.log("main: loginuser: "+loginuser);
    }
}