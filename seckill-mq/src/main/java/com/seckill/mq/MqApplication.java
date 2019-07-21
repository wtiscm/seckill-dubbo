package com.seckill.mq;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class MqApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
    }

}
