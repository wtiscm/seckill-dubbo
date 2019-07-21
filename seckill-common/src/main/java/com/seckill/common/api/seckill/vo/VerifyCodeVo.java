package com.seckill.common.api.seckill.vo;

import java.awt.image.BufferedImage;

public class VerifyCodeVo {
    private BufferedImage image;
    private Integer result;

    VerifyCodeVo() {
    }

    public VerifyCodeVo(BufferedImage image,Integer result){
        this.image = image;
        this.result = result;
    }

    @Override
    public String toString() {
        return "VerifyCodeVo{" +
                "image=" + image +
                ", result=" + result +
                '}';
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
