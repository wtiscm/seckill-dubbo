package com.seckill.controller.controller;

import com.seckill.common.api.user.UserServiceApi;
import com.seckill.common.api.user.vo.LoginVo;
import com.seckill.common.exception.GlobalException;
import com.seckill.common.rediskeyconf.SeckillUserKey;
import com.seckill.common.result.CodeMsg;
import com.seckill.common.result.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/seckill")
public class LoginController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;

    // 登陆
    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    // 执行登陆
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        String res = userService.doLogin(loginVo);
        // 验证通过，添加cookie
        if (res != null) {
            addCookie(response, res);
            return Result.success(res);
        } else {
            throw new GlobalException(CodeMsg.INNERERROR);
        }
    }

    private void addCookie(HttpServletResponse response, String tokenNum) {
        Cookie cookie = new Cookie(UserServiceApi.COOKIE_TOKEN_KEY, tokenNum);
        cookie.setMaxAge(SeckillUserKey.getByToken.expireTime);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
