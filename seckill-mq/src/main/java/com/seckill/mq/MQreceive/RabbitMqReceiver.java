package com.seckill.mq.MQreceive;

import com.seckill.common.api.goods.GoodsServiceApi;
import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.order.OrderServiceApi;
import com.seckill.common.api.order.domain.MiaoshaOrder;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.api.seckill.SeckillApi;
import com.seckill.common.api.seckill.vo.MiaoshaVo;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.common.rediskeyconf.OrderKey;
import com.seckill.mq.rabbitMQconf.RabbitMqConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqReceiver {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;
    @Reference(interfaceClass = GoodsServiceApi.class,version = "goods")
    GoodsServiceApi goodsService;
    @Reference(interfaceClass = SeckillApi.class)
    SeckillApi miaoshaService;
    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;


    @RabbitListener(queues = RabbitMqConfig.MIAOSHA_QUEUE)
    public void receiveMiaosha(String message) {
        logger.info("receive miaosha message" + message);
        MiaoshaVo miaoshaVo = redisService.stringToBean(message, MiaoshaVo.class);
        MiaoshaUser miaoshaUser = miaoshaVo.getMiaoshaUser();
        long goodsId = miaoshaVo.getGoodsId();
        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        int num = goods.getStockCount();
        if (num <= 0) {
            return;
        }
        // 判断是否已经购买(过滤重复秒杀)
        MiaoshaOrder orderInfo = orderService.getOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if (orderInfo != null) {
            redisService.set(OrderKey.getOrderKey, "" + orderInfo.getUserId() + orderInfo.getGoodsId(), orderInfo);
            return;
        }
        // 执行秒杀(事物) 1.减库存 2.写入用户订单表 3.写入秒杀订单表
        miaoshaService.executionMiaosha(miaoshaUser, goods);
    }
}
