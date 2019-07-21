package com.seckill.user.test;

import com.seckill.user.test.dao.MiaoshaUserDao111;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/haha")
public class test {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    MiaoshaUserDao111 miaoshaUserDao;

    @RequestMapping("/1")
    public String haha() {
        long id = 13512341234L;
        logger.warn(">>>>>>>>>>>>");
        //MiaoshaUser byId = miaoshaUserDao.getById(id);
        int i = miaoshaUserDao.updateTest(id);
        logger.warn("hahahahahahha"+i);
        return "";
    }
}
