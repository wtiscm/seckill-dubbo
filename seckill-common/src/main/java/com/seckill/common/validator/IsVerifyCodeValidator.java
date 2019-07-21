package com.seckill.common.validator;

import com.seckill.common.util.VerifyCodeValidationUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsVerifyCodeValidator implements ConstraintValidator<IsVerifyCode, String> {

    private Boolean required;

    @Override
    public void initialize(IsVerifyCode constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            return VerifyCodeValidationUtil.isVerifyCode(value);
        } else {
            if (StringUtils.isEmpty(value)) {
                return true;
            } else {
                return VerifyCodeValidationUtil.isVerifyCode(value);
            }
        }
    }
}
