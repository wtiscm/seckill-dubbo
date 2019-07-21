package com.seckill.common.api.goods.vo;


import com.seckill.common.api.user.vo.MiaoshaUser;

import java.io.Serializable;

public class GoodsDetailVo implements Serializable {
    private GoodsVo goodsVo;
    private MiaoshaUser miaoshaUser;
    private int miaoshaStatus;
    private int remainSeconds;

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    @Override
    public String toString() {
        return "GoodsDetailVo{" +
                "goodsVo=" + goodsVo +
                ", miaoshaUser=" + miaoshaUser +
                ", miaoshaStatus=" + miaoshaStatus +
                ", remainSeconds=" + remainSeconds +
                '}';
    }
}
