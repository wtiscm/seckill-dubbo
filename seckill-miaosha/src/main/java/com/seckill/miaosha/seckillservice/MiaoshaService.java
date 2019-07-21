package com.seckill.miaosha.seckillservice;

import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.order.OrderServiceApi;
import com.seckill.common.api.order.domain.MiaoshaOrder;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.api.seckill.SeckillApi;
import com.seckill.common.api.seckill.domain.OrderInfo;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.common.exception.GlobalException;
import com.seckill.common.rediskeyconf.GoodsKey;
import com.seckill.common.rediskeyconf.MiaoshaKey;
import com.seckill.common.result.CodeMsg;
import com.seckill.common.util.MD5util;
import com.seckill.common.util.UUIDutil;
import com.seckill.miaosha.goodsservice.GoodsService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Component
@Service(interfaceClass = SeckillApi.class)
public class MiaoshaService implements SeckillApi {
    private static final String MIAOSHA_SALT = "123456";
    private static final char[] OPS = new char[]{'+', '-', '*'};
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    GoodsService goodsService;
    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    @Transactional
    public OrderInfo executionMiaosha(MiaoshaUser miaoshaUser, GoodsVo goods) {
        //减少库存
        Boolean res = goodsService.redurceStock(goods);
        if (res) {
            //写入商品订单，与秒杀订单
            return orderService.createOrder(miaoshaUser, goods);
        }
        setGoodsOver(goods.getId());
        return null;
    }

    private void setGoodsOver(Long id) {
        redisService.set(GoodsKey.getGoodResult, "" + id, true);
    }

    private Boolean getGoodsOver(long goodsId) {
        return redisService.exists(GoodsKey.getGoodResult, "" + goodsId);
    }

    public long getResult(long userId, long goodsId) {
        MiaoshaOrder orderInfo = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (orderInfo != null) {
            return orderInfo.getOrderId();
        } else {
            Boolean res = getGoodsOver(goodsId);
            if (res) {
                return -1;
            }
            return 0;
        }
    }

    public String setMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId) {
        String path = MD5util.MD5(UUIDutil.getUUID() + MIAOSHA_SALT);
        redisService.set(MiaoshaKey.miaoshaPath, "" + miaoshaUser.getId() + goodsId, path);
        return path;
    }

    public String getMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId) {
        return redisService.get(MiaoshaKey.miaoshaPath, "" + miaoshaUser.getId() + goodsId, String.class);
    }

    public BufferedImage getVerifyCode(Long userId, long goodsId) {
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = createVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.verify_code, userId + "," + goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine javaScript = manager.getEngineByName("JavaScript");
        int res = 0;
        try {
            res = (Integer) javaScript.eval(verifyCode);
        } catch (ScriptException e) {
            throw new GlobalException(CodeMsg.INNERERROR);
        }
        return res;
    }

    private String createVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char ops1 = OPS[random.nextInt(3)];
        char ops2 = OPS[random.nextInt(3)];
        return "" + num1 + ops1 + num2 + ops2 + num3;
    }

    public boolean verifyCodeValidate(Long userId, long goodsId, Integer verifyCode) {
        if (userId == null || goodsId <= 0 || verifyCode == null || !(verifyCode instanceof Integer)) {
            return false;
        }
        Integer res = redisService.get(MiaoshaKey.verify_code, userId + "," + goodsId, Integer.class);
        if (res == null) {
            throw new GlobalException(CodeMsg.VERIFY_CODE_TIMEOUE);
        }
        if (res - ((Integer) verifyCode).intValue()!= 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.verify_code, userId + "," + goodsId);
        return true;
    }
}
