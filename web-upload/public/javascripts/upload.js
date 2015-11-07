/**
 * Created by lzh on 14-5-30.
 */

//初始化加载
$(document).ready(function(){
    //设置SWF语言
    xiuxiu.setLaunchVars("language", "zh_cn");
    //设置参数
    xiuxiu.setLaunchVars("maxFinalWidth", 220);
    xiuxiu.setLaunchVars("maxFinalHeight", 220);
    //初始化SWF
    xiuxiu.embedSWF("swfContainer",5,"100%","100%");
    //设置上传地址
    xiuxiu.setUploadURL("http://localhost:9000/upload");
    //设置上传参数
    xiuxiu.setUploadArgs({userId:11128, filetype:"avatar"});
    //设置上传方式
    xiuxiu.setUploadType (2);
    //初始化图片
    xiuxiu.onInit = function (){
        xiuxiu.loadPhoto("http://open.web.meitu.com/sources/images/1.jpg");
    }
    //成功返回
    xiuxiu.onUploadResponse = function (data){
        var obj = $.parseJSON(data);
        if(parseInt(obj.status) == 0){
            alert(obj.message);
        }else{
            alert("upload faild");
        }
    }
});