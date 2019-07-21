package com.seckill.mq.MQsend;

import com.seckill.common.api.rabbitMQ.RabbitMQApi;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.mq.rabbitMQconf.RabbitMqConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = RabbitMQApi.class)
public class RabbitMQSend implements RabbitMQApi {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    AmqpTemplate amqpTemplate;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    @Override
    public void sendMiaosha(Object message) {
        String str = redisService.beanToString(message);
        logger.info("send miaosha message" + message);
        amqpTemplate.convertAndSend(RabbitMqConfig.MIAOSHA_QUEUE, str);
    }

}
