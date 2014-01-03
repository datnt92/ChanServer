package com.duduto.chan.enums;

public enum Command {

    PassCard("passCard"),
    QuitGame("quitGame"),
    GetRoomList("getRoomList"),
    GetPlayerList("getPlayerList"),
    MiddlewareRequest("middlewareRequest"),
    MiddlewareUserName("MIDDLEWARE"),
    KickPlayer("kickPlayer"),
    CreateRoom("createRoom"),
    JoinRoom("joinRoom"),
    Sit("sit"),
    Stand("stand"),
    Steal("steal"),
    Draw("draw"),
    Skip("skip"),
    Chiu("chiu"),
    Card("card"),
    StartGame("start"),
    DisCard("disCard"),
    LeaveRoom("leaveRoom"),
    ListPlayer("listPlayer"),
    GetBuddyList("getBuddyList");
    private final String command;

    private Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
