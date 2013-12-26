package com.duduto.chan.enums;

public enum ErrorCode {

    IsSuccess(999),
    SystemError(-1),
    MaxPlayer(100),
    UserNameEmpty(200),
    UserNameExist(201),
    LoginFaild(202),
    LogEmpty(1005);
    private final int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
