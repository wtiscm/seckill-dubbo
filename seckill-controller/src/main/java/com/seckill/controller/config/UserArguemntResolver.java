package com.seckill.controller.config;

import com.seckill.common.api.user.UserServiceApi;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.controller.access.UserContext;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Service
public class UserArguemntResolver implements HandlerMethodArgumentResolver {
    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();
    }


}
