package com.seckill.common.result;


import java.io.Serializable;

public class CodeMsg implements Serializable {
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg INNERERROR = new CodeMsg(500100, "系统内部异常");
    public static CodeMsg REQUEST_ILLAGEAL = new CodeMsg(500102, "请求非法");
    public static CodeMsg VALIDATOR_ERROR = new CodeMsg(500101, "参数检验异常:%s");
    public static CodeMsg REDISKEYVALUE = new CodeMsg(500200, "redis键值对设置有问题");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500201, "用户密码为空");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500202, "用户密码错误");
    public static CodeMsg MOBILE_FORMAT_ERROR = new CodeMsg(500203, "用户电话格式错误");
    public static CodeMsg USER_NOTEXISTS = new CodeMsg(500203, "用户未注册");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500300, "用户电话为空");
    public static CodeMsg USER_NOT_LOGIN = new CodeMsg(500301, "用户登陆超时");
    public static CodeMsg VERIFY_CODE_ERROR = new CodeMsg(500302, "验证码错误");
    public static CodeMsg VERIFY_CODE_TIMEOUE = new CodeMsg(500303, "验证码超时，请刷新");
    public static CodeMsg REQUEST_TOO_MUCH = new CodeMsg(500303, "请求太频繁,限制访问");
    //500500 秒杀
    public static CodeMsg REPEAT_MIAOSHA = new CodeMsg(500501, "重复秒杀");
    public static CodeMsg MIAOSHA_OVER = new CodeMsg(500502, "库存不足");
    private int code;
    private String msg;


    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private CodeMsg() {

    }

    public CodeMsg format(Object... args) {
        int code = this.code;
        String msg = String.format(this.msg, args);
        return new CodeMsg(code, msg);
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
