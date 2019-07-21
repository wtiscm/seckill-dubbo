package com.seckill.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyCodeValidationUtil {
    public static Logger logger = LoggerFactory.getLogger(VerifyCodeValidationUtil.class);
    private static final Pattern mobilePattern = Pattern.compile("0|-?[1-9]\\d*");

    public static Boolean isVerifyCode(String verifyCode) {
        if (StringUtils.isEmpty(verifyCode)) {
            return false;
        }
        Matcher matcher = mobilePattern.matcher(verifyCode);
        return matcher.matches();
    }
}

