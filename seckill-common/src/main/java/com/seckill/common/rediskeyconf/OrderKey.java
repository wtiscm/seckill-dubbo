package com.seckill.common.rediskeyconf;

public class OrderKey extends BasePrefix {
    public static OrderKey getOrderKey = new OrderKey("tk");

    protected OrderKey(String prefix) {
        super(prefix);
    }
}
