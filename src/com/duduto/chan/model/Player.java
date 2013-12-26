/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.model;

import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.duduto.chan.enums.Field;

/**
 *
 * @author Blacker
 */
public class Player {

    private boolean masterRoom;
    private PlayerBean playerData;

    public Player(PlayerBean player) {
        this.playerData = player;
    }

    public String getUsername() {
        if (playerData != null) {
            return playerData.getUsername();
        }
        return null;
    }

    public PlayerBean getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerBean playerData) {
        this.playerData = playerData;
    }

    public boolean isMasterRoom() {
        return masterRoom;
    }

    public void setMasterRoom(boolean masterRoom) {
        this.masterRoom = masterRoom;
    }

    public EsObject toEsObject() {
        EsObject es = playerData.toEsObject();
        es.setBoolean(Field.MasterRoom.getName(), masterRoom);
        return es;
    }
}
