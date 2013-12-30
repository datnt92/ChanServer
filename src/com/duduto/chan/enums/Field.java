package com.duduto.chan.enums;

public enum Field {
    Noc("noc"),
    NumberSit("numbersit"),
    Card("card"),
    CurrentTurn("currentTurn"),
    GameState("gameState"),
    StartGame("start"),
    Empty("empty"),
    slotSit("slot"),
    Position("position"),
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
    PlayerData("playerData"),
    Email("email"),
    DisplayName("displayName"),
    Sit("sit"),
    Command("command"),
    ListPlayer("listPlayer"),
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
    PlayerState("state"),
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
