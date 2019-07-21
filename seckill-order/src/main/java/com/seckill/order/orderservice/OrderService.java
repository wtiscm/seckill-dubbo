package com.seckill.order.orderservice;

import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.order.OrderServiceApi;
import com.seckill.common.api.order.domain.MiaoshaOrder;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.api.seckill.domain.OrderInfo;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.common.rediskeyconf.OrderKey;
import com.seckill.order.dao.OrderDao;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Service(interfaceClass = OrderServiceApi.class)
public class OrderService implements OrderServiceApi {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    OrderDao orderDao;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    public MiaoshaOrder getOrderByUserIdGoodsId(long userId, long goodsId) {
        return orderDao.getOrderByUserIdGoodsId(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goods) {
        //写入普通订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(miaoshaUser.getId());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(0);
        orderInfo.setStatus(0);
        orderInfo.setCreateDate(new Date());
        orderDao.insertOrder(orderInfo);
        //写入秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserId(miaoshaUser.getId());
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        logger.warn(">>>>>"+"miaoshamiaohamiaoha");
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.getOrderKey, "" + orderInfo.getUserId() + orderInfo.getGoodsId(), orderInfo);
        return orderInfo;
    }

    public MiaoshaOrder getMiaoshaOrderByOrderId(long orderId) {
        return orderDao.getMiaoshaOrderByOrderId(orderId);
    }

    public OrderInfo getOrderByorderId(long orderId) {
        return orderDao.getOrderByorderId(orderId);
    }
}
