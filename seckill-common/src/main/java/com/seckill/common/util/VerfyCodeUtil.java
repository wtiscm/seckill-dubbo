package com.seckill.common.util;

import com.seckill.common.api.seckill.vo.VerifyCodeVo;
import com.seckill.common.exception.GlobalException;
import com.seckill.common.result.CodeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VerfyCodeUtil {
    private static final char[] OPS = new char[]{'+', '-', '*'};
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static VerifyCodeVo getVerifyCode(Long userId, long goodsId) {
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
        VerifyCodeVo verifyCodeVo = new VerifyCodeVo(image, rnd);
        //输出图片
        return verifyCodeVo;
    }

    private static int calc(String verifyCode) {
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

    private static String createVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char ops1 = OPS[random.nextInt(3)];
        char ops2 = OPS[random.nextInt(3)];
        return "" + num1 + ops1 + num2 + ops2 + num3;
    }

}
