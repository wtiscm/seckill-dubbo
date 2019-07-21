package com.seckill.common.exception;


import com.seckill.common.result.CodeMsg;

public class GlobalException extends RuntimeException {
    private CodeMsg cm;

    public GlobalException(CodeMsg codeMsg) {
        this.cm = codeMsg;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
