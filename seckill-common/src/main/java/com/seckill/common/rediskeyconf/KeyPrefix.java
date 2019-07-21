package com.seckill.common.rediskeyconf;

public interface KeyPrefix {

    String getPrefix();

    int getExpireTime();
}
