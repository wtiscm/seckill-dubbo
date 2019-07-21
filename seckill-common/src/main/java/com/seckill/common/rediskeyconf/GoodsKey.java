package com.seckill.common.rediskeyconf;

public class GoodsKey extends BasePrefix {
    public static GoodsKey goodsList = new GoodsKey(60, "gl");
    public static GoodsKey goodsDetail = new GoodsKey(60, "gd");
    public static GoodsKey goodsStockNum = new GoodsKey(0, "gs");
    public static GoodsKey getGoodResult = new GoodsKey(0, "gr");
    private GoodsKey(int expire, String prefix) {
        super(expire, prefix);
    }
}
