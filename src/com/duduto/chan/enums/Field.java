package com.duduto.chan.enums;

public enum Field {
    JoinRoom("joinRoom"),
    PlayerEmpty("playerEmpty"),
    KickPlayer("kickPlayer"),
    PlayerKick("playerKick"),
    AlertMessage("alertMessage"),
    Message("message"),
    MasterRoom("masterRoom"),
    FakeMoney("fakeMoney"),
    Money("money"),
    UserName("userName"),
    UserData("userData"),
    Email("email"),
    DisplayName("displayName"),
    Command("command"),
    ErrorCode("errorCode"),
    Action("action"),
    RoomName("roomName"),
    Capacity("capacity"),
    NumPlayer("numPlayer"),
    Description("description"),
    RoomId("roomId"),
    Betting("betting"),
    BettingList("bettingList"),
    Choice("choice"),
    Info("info"),
    Status("status"),
    HasPass("hasPass"),
    RoomList("roomList"),
    CommonInfo("commonInfo"),
    Avatar("avatar"),
    PlayerList("playerList");
    private final String name;

    private Field(String code) {
        this.name = code;
    }

    public String getName() {
        return name;
    }
}
