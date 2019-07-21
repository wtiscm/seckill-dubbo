package com.seckill.common.api.order;

import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.order.domain.MiaoshaOrder;
import com.seckill.common.api.seckill.domain.OrderInfo;
import com.seckill.common.api.user.vo.MiaoshaUser;

public interface OrderServiceApi {
    public MiaoshaOrder getOrderByUserIdGoodsId(long userId, long goodsId);
    public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goods);
    public MiaoshaOrder getMiaoshaOrderByOrderId(long orderId);
    public OrderInfo getOrderByorderId(long orderId);
}
