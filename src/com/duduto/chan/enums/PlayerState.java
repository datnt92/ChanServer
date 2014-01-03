package com.duduto.chan.enums;

public enum PlayerState {

    View("view"),
    Watting("waitting"),
    Playing("playing");
    
    private final String state;

    private PlayerState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
