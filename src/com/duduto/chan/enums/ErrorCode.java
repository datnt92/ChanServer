package com.duduto.chan.enums;

public enum ErrorCode {

    IsSuccess(999),
    SystemError(-1),
    FullSlot(100),
    SlotNotEmpty(101),
    NotEnoughtMoney(102),
    PlayerSit(103),
    GameStarted(104),
    NotCurrentTurn(105),
    HasCard(106),
    HasSteal(107),
    NotMaster(108),
    NumberSit(109),
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
