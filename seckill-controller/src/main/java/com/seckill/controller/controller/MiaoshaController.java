package com.seckill.controller.controller;

import com.seckill.common.api.goods.GoodsServiceApi;
import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.rabbitMQ.RabbitMQApi;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.api.seckill.SeckillApi;
import com.seckill.common.api.seckill.domain.OrderInfo;
import com.seckill.common.api.seckill.vo.MiaoshaVo;
import com.seckill.common.api.seckill.vo.VerifyCodeValidationVo;
import com.seckill.common.api.seckill.vo.VerifyCodeVo;
import com.seckill.common.api.user.UserServiceApi;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.common.exception.GlobalException;
import com.seckill.common.rediskeyconf.GoodsKey;
import com.seckill.common.rediskeyconf.MiaoshaKey;
import com.seckill.common.rediskeyconf.OrderKey;
import com.seckill.common.result.CodeMsg;
import com.seckill.common.result.Result;
import com.seckill.common.util.VerfyCodeUtil;
import com.seckill.controller.access.AccessLimit;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping(value = "/miaosha")
public class MiaoshaController implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    Map<Long, Boolean> isOverMap = new ConcurrentHashMap<Long, Boolean>();
    @Reference(interfaceClass = SeckillApi.class)
    SeckillApi miaoshaService;
    @Reference(interfaceClass = RabbitMQApi.class)
    RabbitMQApi rabbitMqSender;
    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;
    @Reference(interfaceClass = GoodsServiceApi.class, version = "goods")
    GoodsServiceApi goodsService;

    /**
     * 提前把每个商品的库存，商品售罄标志加载到redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return;
        }
        for (GoodsVo goodsVo : goodsList) {
            redisService.set(GoodsKey.goodsStockNum, "" + goodsVo.getId(), goodsVo.getStockCount());
            isOverMap.put(goodsVo.getId(), false);
        }
    }

    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaosha(Model model, MiaoshaUser miaoshaUser,
                                     @RequestParam("goodsId") long goodsId,
                                     @PathVariable("path") String path) {
        // 判断用户是否登陆
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        // 判断秒杀地址是否被篡改
        String getPath = miaoshaService.getMiaoshaPath(miaoshaUser, goodsId);
        if (!path.equals(getPath)) {
            return Result.error(CodeMsg.REQUEST_ILLAGEAL);
        }
        // 获取秒杀商品售罄标记
        Boolean res = isOverMap.get(goodsId);
        if (res) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        // 判断是否有库存
        int stockNum = redisService.get(GoodsKey.goodsStockNum, "" + goodsId, int.class);
        // 库存不足,设置售罄标记
        if (stockNum <= 0) {
            isOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        // 有库存,判断是否重复秒杀，然后从redis预减库存
        OrderInfo orderInfo = redisService.get(OrderKey.getOrderKey, "" + miaoshaUser.getId() + goodsId, OrderInfo.class);
        if (orderInfo != null) {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        // 预减库存
        long num = redisService.decrease(GoodsKey.goodsStockNum, "" + goodsId);
        if (num < 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        // rabbitmq异步下单
        MiaoshaVo miaoshaVo = new MiaoshaVo();
        miaoshaVo.setMiaoshaUser(miaoshaUser);
        miaoshaVo.setGoodsId(goodsId);
        rabbitMqSender.sendMiaosha(miaoshaVo);
        return Result.success(0);
    }

    /**
     * @param model
     * @param miaoshaUser
     * @param goodsId
     * @return 0:排队中
     * >0:秒杀成功,订单id
     * -1:秒杀失败
     * 页面轮询查看秒杀结果
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser miaoshaUser,
                                      @RequestParam("goodsId") long goodsId) {
        if (miaoshaUser == null) {
            Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        long res = miaoshaService.getResult(miaoshaUser.getId(), goodsId);
        return Result.success(res);
    }

    /**
     * 获取秒杀地址
     * 接口防刷，每5秒最多访问5次
     *
     * @param request
     * @param model
     * @param miaoshaUser
     * @param verifyCodeValidation
     * @return
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, Model model, MiaoshaUser miaoshaUser,
                                         @Valid VerifyCodeValidationVo verifyCodeValidation) {
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        long goodsId = verifyCodeValidation.getGoodsId();
        Integer verifyCode = Integer.parseInt(verifyCodeValidation.getVerifyCode());
        // 判断验证码格式
        if (goodsId <= 0 || verifyCode == null || !(verifyCode instanceof Integer)) {
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }
        // 判断验证码是否正确
        boolean verifyCodeValidate = miaoshaService.verifyCodeValidate(miaoshaUser.getId(), goodsId, ((Integer) verifyCode).intValue());
        if (!verifyCodeValidate) {
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }
        // 动态生成秒杀地址
        String path = miaoshaService.setMiaoshaPath(miaoshaUser, goodsId);
        return Result.success(path);
    }

    /**
     * 获取验证码图片
     * @param response
     * @param model
     * @param miaoshaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, Model model, MiaoshaUser miaoshaUser,
                                               @RequestParam("goodsId") long goodsId) {
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        // 获取验证码
        VerifyCodeVo image = VerfyCodeUtil.getVerifyCode(miaoshaUser.getId(), goodsId);
        if (image == null) {
            throw new GlobalException(CodeMsg.INNERERROR);
        }
        // 验证码答案放入 redis,下次redis获取值判断。
        redisService.set(MiaoshaKey.verify_code, miaoshaUser.getId() + "," + goodsId, image.getResult());
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(image.getImage(), "JPEG", out);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new GlobalException(CodeMsg.INNERERROR);
        }
        return null;
    }
}
