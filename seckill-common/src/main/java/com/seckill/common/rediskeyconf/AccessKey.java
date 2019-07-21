package com.seckill.common.rediskeyconf;

public class AccessKey extends BasePrefix {
    private AccessKey(int expire, String prefix) {
        super(expire, prefix);
    }

    public static AccessKey accessKey(int expireTime) {
        return new AccessKey(expireTime, "access");
    }
}
