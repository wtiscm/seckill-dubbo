package com.seckill.controller.access;

import com.seckill.common.api.user.vo.MiaoshaUser;

public class UserContext {
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }

    public static void setUser(MiaoshaUser miaoshaUser) {
        userHolder.set(miaoshaUser);
    }
}
