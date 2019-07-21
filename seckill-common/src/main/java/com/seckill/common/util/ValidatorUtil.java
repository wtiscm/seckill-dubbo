package com.seckill.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern mobilePattern = Pattern.compile("1\\d{10}");

    public static Boolean isMobil(String moblie) {
        if (StringUtils.isEmpty(moblie)) {
            return false;
        }
        Matcher matcher = mobilePattern.matcher(moblie);
        return matcher.matches();
    }
}
