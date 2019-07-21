package com.seckill.common.rediskeyconf;

public class MiaoshaKey extends BasePrefix {
    public static MiaoshaKey miaoshaPath = new MiaoshaKey(60, "mp");
    public static MiaoshaKey verify_code = new MiaoshaKey(300, "vc");
    private MiaoshaKey(int expire, String prefix) {
        super(expire, prefix);
    }
}
