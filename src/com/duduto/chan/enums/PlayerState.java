package com.duduto.chan.enums;

public enum PlayerState {

    View(101),
    Sit(102),
    PlayGame(0);
    private final int state;

    private PlayerState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
