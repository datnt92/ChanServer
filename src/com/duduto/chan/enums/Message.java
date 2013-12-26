/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.enums;

import com.duduto.Global;

/**
 *
 * @author Dark
 */
public enum Message {

    NotMasterRoom("Bạn không phải chủ phòng."),
    PlayerEmpty("Tài khoản này không tồn tại."),
    PlayerNotFound("Người chơi này không ở trong phòng."),
    KickPlayer("Bạn vừa bị đuổi khỏi phòng."),
    MaxPlayer("Phòng đã đầy.Vui lòng chọn phòng khác."),
    LoginFaild("Tên tài khoản hoặc mật khẩu không đúng.");
    private final String message;

    private Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
