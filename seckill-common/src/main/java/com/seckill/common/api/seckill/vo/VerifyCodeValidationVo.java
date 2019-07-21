package com.seckill.common.api.seckill.vo;

import com.seckill.common.validator.IsVerifyCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class VerifyCodeValidationVo implements Serializable {
    private long goodsId;

    @IsVerifyCode
    @NotNull
    private String verifyCode;

    @Override
    public String toString() {
        return "VerifyCodeValidationVo{" +
                "goodsId=" + goodsId +
                ", verifyCode='" + verifyCode + '\'' +
                '}';
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
