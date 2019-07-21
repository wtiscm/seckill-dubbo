package com.seckill.common.api.seckill;

import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.seckill.domain.OrderInfo;
import com.seckill.common.api.seckill.vo.VerifyCodeVo;
import com.seckill.common.api.user.vo.MiaoshaUser;

import java.awt.image.BufferedImage;

public interface SeckillApi {
    public OrderInfo executionMiaosha(MiaoshaUser miaoshaUser, GoodsVo goods);
    public long getResult(long userId, long goodsId);
    public String setMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId);
    public String getMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId);
    public BufferedImage getVerifyCode(Long userId, long goodsId);
    public boolean verifyCodeValidate(Long userId, long goodsId, Integer verifyCode);
}
