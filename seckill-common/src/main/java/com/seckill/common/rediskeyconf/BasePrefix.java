package com.seckill.common.rediskeyconf;

import java.io.Serializable;

public abstract class BasePrefix implements KeyPrefix , Serializable {
    public int expireTime;

    public String prefix;

    public BasePrefix(int expireTime, String prefix) {
        this.expireTime = expireTime;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {
        this(0, prefix);
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }

    @Override
    public int getExpireTime() {
        return expireTime;
    }
}
