package com.seckill.common.api.order.vo;

import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.seckill.domain.OrderInfo;

import java.io.Serializable;

public class OrderDetailVo implements Serializable {
    private GoodsVo goodsVo;
    private OrderInfo order;

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderDetailVo{" +
                "goodsVo=" + goodsVo +
                ", order=" + order +
                '}';
    }
}
