var taskSizeChart = echarts.init(document.getElementById('taskSize'));
var startTime = new Date();

var todaySyncedNum = 0;
var todayNoSyncNum = 0;
var delayNum = 0;
var co2_public = 0;
var searchboxtext = "";

$(function () {
    $('input').bind('input propertychange', function () {
        $('.commonTable tbody tr').hide()
            .filter(":contains('" + ($(this).val()) + "')").show();
    });

    $("#refreshBtn").click(function () {
        initTable();
    });
    $("#searchBusBtn").click(function () {
        searchboxtext = $("#searchBus").val();
        searchPost();
    });
    initTable();
    setInterval(initTable, 1000 * 10);
    setInterval(tick, 1000);
    if ($("#searchBus").val() !== "")
    {
        setInterval(searchPost, 100);
    }

});

function searchPost(){
    var searchName={
        "search": searchboxtext
    }
    $.ajax({
        type:'POST',
        dataType:"json",
        contentType: "application/json",
        url:'http://bus.hwhhome.net:8080/bus',
        data:JSON.stringify(searchName),
        success:function(data){
            console.log("searchPost:"+data);
            var obj = console.log(JSON.parse(JSON.stringify(data)));

        }
    });
}

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

function syncTj() {
    var today = new Date();

    var option = taskSizeChart._option;
    var data0 = option.series[0].data;//本次

    data0.shift();//删除第一个
    data0.push(200);//追加一个新数据

    option.xAxis[0].data.shift();
    option.xAxis[0].data.push( today.getMinutes() + ":" + today.getSeconds());//更新x轴

    taskSizeChart.setOption(option);
}

function taskSizeTj(){
    var names = [];
    var values = [];
    var option = {
        color: ['#00b3ac'],
        tooltip : {
            trigger: 'axis',
            axisPointer : {
                type : 'shadow'
            }
        },
        xAxis : [
            {
                data : names,
                type: 'category',
                splitLine:{
                    show: false
                },
                axisLabel: {
                    interval: 0,
                    show: true,
                    textStyle: {
                        color: '#c3dbff',  //更改坐标轴文字颜色
                        fontSize : 14      //更改坐标轴文字大小
                    }
                }
            }
        ],
        yAxis : [
            {
                type : 'value',
                axisLabel: {
                    show: true,
                    textStyle: {
                        color: '#c3dbff'
                    }
                }
            }
        ],
        series : [
            {
                name:'データ数',
                type:'line',
                smooth: true,
                barWidth: '60%',
                data:values
            }
        ]
    };
    var _time = new Date().getTime();
    for(var i = 12; i > 0; i--){
        var _tempDate = new Date(_time - 1000 * 10 * i);
        names.push(_tempDate.getMinutes() + ":" + _tempDate.getSeconds());
        values.push(co2_public);
    }
    option.xAxis[0].data.value = names;
    option.series[0].data.value = values;
    taskSizeChart.setOption(option);
}


function mySort(data){
    var arr = [];
    for(i in data){
        if("flag" != i && "exceptions" != i && "delay" != i){
            var temp = data[i];
            temp["name"] = i;
            arr.push(temp);
        }
        console.log("data_index:"+i);
    }
    arr.sort(function(a,b){return b['timeline'] > a['timeline'] ? 1 : -1 })
    return arr;
}

function initTable() {
    taskSizeTj();
    syncTj();
    if ($("#searchBus").val() !== "")
    {
        searchPost();
    }
    var HTML = "<thead>\n" +
        "        <td title=\"車ナンバー\">車ナンバー</td>\n" +
        "        <td title=\"時間\">時間</td>\n" +
        "        <td title=\"番号\">番号</td>\n" +
        "        <td title=\"電波\">電波</td>\n" +
        "        <td title=\"電圧\">電圧</td>\n" +
        "        <td title=\"温度\">温度</td>\n" +
        "        <td title=\"湿度\">湿度</td>\n" +
        "        <td title=\"CO2\">CO2</td>\n" +
        "        <td title=\"乗客数\">乗客数</td>\n" +
        "        <td title=\"運行情報\">運行情報</td>\n" +
        "        </thead>\n" +
        "        <tbody>\n";
    var data = getData();
    var myData = mySort(data);
    var busNumber = "";
    var co2Number = 0;
    var passNumber = "";
    var count = 0;
    $(myData).each(function (index, ele) {
        HTML += "<tr>\n" +
            "            <td>" + ele['busnumber'] + "</td>\n" +
            "            <td>" + ele['timeline'] + "</td>\n" +
            "            <td>" + ele['sensorid'] + "</td>\n" +
            "            <td>" + ele['sensignal'] + "</td>\n" +
            "            <td>" + ele['senbattery'] + "</td>\n" +
            "            <td>" + ele['sentemp'] + "</td>\n" +
            "            <td>" + ele['senhumi'] + "</td>\n" +
            "            <td>" + ele['senco2'] + "</td>\n" +
            "            <td>" + ele['passenger'] + "</td>\n" +
            "            <td>" + ele['businfo'] + "</td>\n";
        count ++;
        busNumber = ele['busnumber'];
        co2Number = (co2Number+ parseInt(ele['senco2']))/(count+1);
        passNumber = ele['passenger'];
        co2_public = co2Number;
        console.log(busNumber.toString()+co2Number.toString()+passNumber.toString());
    });
    HTML += "</tbody>";
    $('.commonTable').html(HTML);
    $("#busNumberArea").text(busNumber);
    $("#averageCo2Area").text(co2Number);
    $("#passengerArea").text(passNumber);
    delayNum = 1;
    $("#statusArea").html("<font color='#00cc00'>やや混雑</font>");
    $('.commonTable tbody tr').hide()
        .filter(":contains('" + ($("#searchText").val()) + "')").show();

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
    if  ( ww==0 )  colorhead="<font color=\"#ffffff\">";
    if  ( ww > 0 && ww < 6 )  colorhead="<font color=\"#ffffff\">";
    if  ( ww==6 )  colorhead="<font color=\"#ffffff\">";
    if  (ww==0)  ww="日曜日";
    if  (ww==1)  ww="月曜日";
    if  (ww==2)  ww="火曜日";
    if  (ww==3)  ww="水曜日";
    if  (ww==4)  ww="木曜日";
    if  (ww==5)  ww="金曜日";
    if  (ww==6)  ww="土曜日";
    colorfoot="</font>"
    str = colorhead + yy + "-" + MM + "-" + dd + " " + hh + ":" + mm + ":" + ss + "  " + ww + colorfoot;
    return(str);
}

function hideBugBtn() {
    $("#bugBtn").hide();
}

function getData() {
    return {
        "flag": true,
        "4708e673-bc77-4922-b0cf-76107a236e11": {
            "busnumber": "SHOUNAN330A7040",
            "passenger": "5",
            "senbattery": "2.75",
            "senco2": "876",
            "senhumi": "27.5",
            "sensignal": "159",
            "sensorid": "2165032249",
            "sentemp": "21.4",
            "timeline": "2021-03-04 11:09:09",
            "businfo": "湘南台-慶応大学",
        },
        "06e6d9bb-a18a-4f60-86ae-1bbf133a14fd": {
            "busnumber": "SHOUNAN330A7040",
            "passenger": "6",
            "senbattery": "2.76",
            "senco2": "873",
            "senhumi": "27.5",
            "sensignal": "159",
            "sensorid": "2165032249",
            "sentemp": "21.5",
            "timeline": "2021-03-04 11:09:14",
            "businfo": "湘南台-慶応大学",
        },
        "delay": 0
    };
}
