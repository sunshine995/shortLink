package com.nwnu.shortlink.admin.common.enums;

import com.nwnu.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCode implements IErrorCode {

    USER_NULL_CODE("B000200", "用户记录不存在"),
    USER_EXIST_CODE("B000201", "用户记录已存在"),
    USER_NAME_EXIST_CODE("B000202","用户名已存在"),
    USER_SAVE_CODE("B000203","用户记录新增失败")
            ;

    private final String code;

    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
