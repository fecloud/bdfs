/**
 * Created by Feng OuYang on 2014-07-08.
 */

Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //day
        "h+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
        (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
            RegExp.$1.length == 1 ? o[k] :
                ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}

//可在Javascript中使用如同C#中的string.format (對jQuery String的擴充方法)
//使用方式 : var fullName = 'Hello. My name is {0} {1}.'.format('FirstName', 'LastName');
String.prototype.format = function () {
    var txt = this.toString();
    for (var i = 0; i < arguments.length; i++) {
        var exp = getStringFormatPlaceHolderRegEx(i);
        txt = txt.replace(exp, (arguments[i] == null ? "" : arguments[i]));
    }
    return cleanStringFormatResult(txt);
}
//讓輸入的字串可以包含{}
function getStringFormatPlaceHolderRegEx(placeHolderIndex) {
    return new RegExp('({)?\\{' + placeHolderIndex + '\\}(?!})', 'gm')
}
//當format格式有多餘的position時，就不會將多餘的position輸出
//ex:
// var fullName = 'Hello. My name is {0} {1} {2}.'.format('firstName', 'lastName');
// 輸出的 fullName 為 'firstName lastName', 而不會是 'firstName lastName {2}'
function cleanStringFormatResult(txt) {
    if (txt == null) return "";
    return txt.replace(getStringFormatPlaceHolderRegEx("\\d+"), "");
}

function getArgs(strParame) {
    var query = location.search.substring(1); // Get query string
    var pairs = query.split("&"); // Break at ampersand
    for (var i = 0; i < pairs.length; i++) {
        var pos = pairs[i].indexOf('='); // Look for "name=value"
        if (pos == -1)
            continue; // If not found, skip

        var argname = pairs[i].substring(0, pos); // Extract the name
        var value = pairs[i].substring(pos + 1); // Extract the value
        value = decodeURIComponent(value); // Decode it, if needed
        if (argname == strParame) {

            return  value; // Store as a property

        }

    }

    return ""; // Return the object
}

function back_bind(site) {

    $('.back_btn').bind('click', function (e) {

        e.preventDefault();
        e.stopPropagation();
        if (site) {
            window.location.replace(site);
        } else {
            window.history.back();
        }

    });
}

function getParentDir(path) {
    var last = path.lastIndexOf('/');
    return path.substring(0, last);
}

function getFileTypeCss(name) {

    var last = name.lastIndexOf('.');
    var fix = name.substring(last + 1);
    fix = fix.toLowerCase();
    if (fix == 'apk') {
        return 'apk';
    } else if (fix == 'doc') {
        return 'doc';
    } else if (fix == 'exe') {
        return 'exe';
    } else if (fix == 'mov' || fix == 'rmvb'
        || fix == 'mp4' || fix == 'rm'
        || fix == 'ts' || fix == 'mkv'
        || fix == 'flv' || fix == 'wmv') {
        return 'video';
    } else if (fix == 'mp3' || fix == 'wma'
        || fix == 'arm') {
        return 'music';
    } else if (fix == 'pdf') {
        return 'pdf';
    } else if (fix == 'rar' || fix == 'zip'
        || fix == 'gz' || fix == 'bz2'
        || fix == 'img' || fix == 'dmg'
        || fix == 'iso') {
        return 'rar';
    } else if (fix == 'txt') {
        return 'txt';
    } else {
        return 'file';
    }

}

/**
 * 格式化时间到天时分秒
 */
function mToH(maxtime) {

    days = Math.floor(maxtime / 86400);
    hours = Math.floor((maxtime % 86400) / 3600);
    minutes = Math.floor(((maxtime % 86400) % 3600) / 60);
    seconds = Math.floor(((maxtime % 86400) % 3600) % 60);

    var str = "";
    if (days > 0) {
        str += days + "天";
    }
    if (hours > 0) {
        str += hours + "小时";
    }

    if (minutes > 0) {
        str += minutes + "分";
    }
    if (seconds > 0) {
        str += seconds + "秒";
    }
    return str;
}

/**
 * 文件大小格式
 * @param value
 * @param p
 * @param record
 * @returns {*}
 */
function renderSize(value) {
    if (null == value || value == '') {
        return "0b";
    }
    var unitArr = new Array("B", "K", "M", "G", "T", "P", "E", "Z", "Y");
    var index = 0;


    var srcsize = parseFloat(value);
    var size = roundFun(srcsize / Math.pow(1024, (index = Math.floor(Math.log(srcsize) / Math.log(1024)))), 2);
    return size + unitArr[index];
}

/**
 四舍五入保留小数位数
 numberRound 被处理的数
 roundDigit  保留几位小数位
 */
function roundFun(numberRound, roundDigit) {
    if (numberRound >= 0) {
        var tempNumber = parseInt((numberRound * Math.pow(10, roundDigit) + 0.5)) / Math.pow(10, roundDigit);
        return   tempNumber;
    } else {
        numberRound1 = -numberRound
        var tempNumber = parseInt((numberRound1 * Math.pow(10, roundDigit) + 0.5)) / Math.pow(10, roundDigit);
        return   -tempNumber;
    }
}

/**
 * 取得客户端的agent
 * @returns {string}
 */
function agent() {

    var user_agent = window.navigator.userAgent;

    if (user_agent.indexOf('iPhone') > 1) {
        return "iPhone";
    } else if (user_agent.indexOf('iPad') > 1) {
        return 'iPad';
    } else if (user_agent.indexOf('Android') > 1) {
        return 'Android';
    } else {
        return "pc"
    }

}

/**
 * 是否是qq或者微信的浏览器
 * @returns {boolean}
 */
function agentWechatOrQQ() {

    var user_agent = window.navigator.userAgent;
    if (user_agent.indexOf('MicroMessenger') > 1 || user_agent.indexOf('QQ') > 1) {
        return true;
    }
    return false;
}

/**
 * 取token
 */
function getToken() {

    var tk = $.cookie('token');
    if (!tk) {
        tk = getArgs("token");
    }
    return tk;

}

/**
 * 生成随机数字
 * @returns {Number}
 */
function randomInt (){

    return parseInt(Math.random() * 10000000);

}