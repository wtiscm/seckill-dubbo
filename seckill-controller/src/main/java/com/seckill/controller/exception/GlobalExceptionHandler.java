package com.seckill.controller.exception;

import com.seckill.common.exception.GlobalException;
import com.seckill.common.result.CodeMsg;
import com.seckill.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    public Result<String> exception(HttpServletRequest httpServletRequest, Exception e) {
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCm());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errorLis = ex.getAllErrors();
            ObjectError error = errorLis.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.VALIDATOR_ERROR.format(msg));
        } else {
            e.printStackTrace();
            return Result.error(CodeMsg.INNERERROR);
        }
    }
}
