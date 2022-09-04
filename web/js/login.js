function setCookie(name, value, daysToLive) {
    var cookie = name + "=" + value;
    if(typeof daysToLive === "number")
        cookie += "; max-age=" + (daysToLive*60*60);
    document.cookie = cookie ;
}

$(function(){

    setCookie("user", "", 0);

    $("#login_btn").click(function () {
        window.location.replace("http://bus.hwhhome.net/main.html");
        setCookie("user", $("#login_name").val(), 1);
    });
});

// $(function(){
//     $("#login_btn").click(function () {
//         $("form").ajaxSubmit(function (data) {
//             console.log("Login")
//             if (JSON.parse(data).code == 200){
//                 window.location.href = "http://bus.hwhhome.net/main.html"    /*页面跳转*/
//                 setCookie("user",$("#login_name").val(),1);
//             }
//             if (JSON.parse(data).code == 500){
//                 alert(JSON.parse(data).message)
//             }
//         });
//     })
// });