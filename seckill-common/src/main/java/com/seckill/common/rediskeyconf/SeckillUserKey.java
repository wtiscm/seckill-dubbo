package com.seckill.common.rediskeyconf;

public class SeckillUserKey extends BasePrefix {
    private static final int EXPIRE_TIME = 3600 * 24 * 2;
    public static SeckillUserKey getByToken = new SeckillUserKey(EXPIRE_TIME, "tk");
    public static SeckillUserKey getById = new SeckillUserKey(0, "id");

    private SeckillUserKey(int expire, String prefix) {
        super(expire, prefix);
    }
}
