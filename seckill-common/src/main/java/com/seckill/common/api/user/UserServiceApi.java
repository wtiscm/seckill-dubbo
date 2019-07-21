package com.seckill.common.api.user;

import com.seckill.common.api.user.vo.LoginVo;
import com.seckill.common.api.user.vo.MiaoshaUser;

import javax.servlet.http.HttpServletResponse;

public interface UserServiceApi {
    public static final String COOKIE_TOKEN_KEY = "token";
    public String doLogin(LoginVo loginVo);
    public MiaoshaUser getById(long id);
    public Boolean updatePassword(long id, String password, String token);
    public MiaoshaUser getByToken(String token);
    public MiaoshaUser getAndUpdateByToken(String token);
}
