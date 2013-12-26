package com.duduto.chan.enums;

public enum Command {

    QuitGame("quitGame"),
    GetRoomList("getRoomList"),
    GetPlayerList("getPlayerList"),
    MiddlewareRequest("middlewareRequest"),
    MiddlewareUserName("MIDDLEWARE"),
    KickPlayer("kickPlayer"),
    GetBuddyList("getBuddyList");
    private final String command;

    private Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
