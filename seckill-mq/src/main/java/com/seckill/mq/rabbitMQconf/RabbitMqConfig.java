package com.seckill.mq.rabbitMQconf;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String MIAOSHA_QUEUE = "miaosha.queue";

    @Bean
    public Queue getMiaoshaQueue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }

}
