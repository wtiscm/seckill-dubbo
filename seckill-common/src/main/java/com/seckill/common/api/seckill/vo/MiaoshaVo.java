package com.seckill.common.api.seckill.vo;


import com.seckill.common.api.user.vo.MiaoshaUser;

import java.io.Serializable;

public class MiaoshaVo implements Serializable {
    private MiaoshaUser miaoshaUser;
    private Long goodsId;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "MiaoshaVo{" +
                "miaoshaUser=" + miaoshaUser +
                ", goodsId=" + goodsId +
                '}';
    }
}
