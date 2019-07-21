package com.seckill.controller.access;

import com.alibaba.fastjson.JSON;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.api.user.UserServiceApi;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.common.rediskeyconf.AccessKey;
import com.seckill.common.result.CodeMsg;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 限刷防流
        if (handler instanceof HandlerMethod) {
            HandlerMethod hd = (HandlerMethod) handler;
            MiaoshaUser miaoshaUser = getMiaoshaUser(request, response);
            UserContext.setUser(miaoshaUser);
            AccessLimit methodAnnotation = hd.getMethodAnnotation(AccessLimit.class);
            if (methodAnnotation == null) {
                return true;
            }
            int seconds = methodAnnotation.seconds();
            int maxCount = methodAnnotation.maxCount();
            boolean needLogin = methodAnnotation.needLogin();
            if (needLogin) {
                if (miaoshaUser == null) {
                    render(response, CodeMsg.USER_NOT_LOGIN);
                    return false;
                }
            }
            String key = request.getRequestURI();
            key += miaoshaUser.getId();
            AccessKey ak = AccessKey.accessKey(seconds);
            Integer count = redisService.get(AccessKey.accessKey(seconds), key, int.class);
            if (count == null) {
                redisService.set(ak, key, 1);
            } else if (count <= maxCount) {
                redisService.increament(ak, key);
            } else {
                render(response, CodeMsg.REQUEST_TOO_MUCH);
                return false;
            }
        }
        return true;
    }

    private MiaoshaUser getMiaoshaUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String paramToken = httpServletRequest.getParameter(UserServiceApi.COOKIE_TOKEN_KEY);
        String cookieToken = getCookieValue(httpServletRequest, UserServiceApi.COOKIE_TOKEN_KEY);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        MiaoshaUser miaoshaUser = userService.getByToken(token);
        return miaoshaUser;
    }

    private String getCookieValue(HttpServletRequest httpServletRequest, String cookieTokenKey) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null || cookies.length < 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieTokenKey)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(codeMsg);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

}
