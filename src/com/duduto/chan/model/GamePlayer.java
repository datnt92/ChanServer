/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.model;

import com.duduto.Global;
import com.duduto.chan.enums.ErrorCode;
import com.duduto.chan.enums.GameState;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.database.DatabaseController;

/**
 *
 * @author Blacker
 */
public class GamePlayer {

    private int maxPlayer = 2;
    private Player arrPlayers[];
    private PluginApi _api;
    private DatabaseController _dbController;
    private int countDownStartGame;
    private int countDownEndGame;
    private int countDownTurnPlay;
    private GameState gameState;

    public GamePlayer(EsObjectRO message, PluginApi api) {
        this._api = api;
        _dbController = (DatabaseController) api.acquireManagedObject("DatabaseControllerFactory", null);
        this.maxPlayer = Global.MAX_USER_IN_ROOM;
        this.arrPlayers = new Player[maxPlayer];
        this.gameState = GameState.WaitingNewGame;
    }

    public void prepareStartGame() {
        if (this.gameState == GameState.WaitingNewGame) {
        }
    }

    public Player getPlayerData(String username) {
        PlayerBean playerBean = _dbController.getPlayerData(username);
        Player player = new Player(playerBean);
        return player;
    }

    public void addPlayer(Player pb) {
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i] == null) {
                arrPlayers[i] = pb;
                return;
            }
        }
    }

    public void removePlayer(String username) {
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i] != null) {
                if (arrPlayers[i].getUsername().equals(username)) {
                    arrPlayers[i] = null;
                    return;
                }
            }
        }
    }

    public Player getMasterRoom() {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                if (arrPlayers[i].isMasterRoom()) {
                    return player;
                }
            }
        }
        return null;
    }

    public Player setMasterRoom() {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                player.setMasterRoom(true);
                return player;
            }
        }
        return null;
    }

    public Player getOtherPlayer() {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player playerBean = arrPlayers[i];
            if (playerBean != null) {
                if (!arrPlayers[i].isMasterRoom()) {
                    return playerBean;
                }
            }
        }
        return null;
    }

    public int getNumPlayerInRoom() {
        int num = 0;
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i] != null) {
                num++;
            }
        }
        return num;
    }

    public boolean checkUsernameInRoom(String username) {
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i].getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /* 
     * check user can join to room
     */
    public ErrorCode checkJoinRoom() {
        if (maxPlayer == getNumPlayerInRoom()) {
            return ErrorCode.MaxPlayer;
        }
        return ErrorCode.IsSuccess;
    }

    public EsObject[] getListPlayerEsObject() {
        int num = 0;
        EsObject[] arr = new EsObject[getNumPlayerInRoom()];
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i] != null) {
                arr[num] = arrPlayers[i].toEsObject();
                num++;
            }
        }
        return arr;
    }

    public EsObject[] getPlayersInfo() {
        int count = 0;
        EsObject[] playersInfo = new EsObject[arrPlayers.length];
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[count] != null) {
                playersInfo[count] = arrPlayers[i].toEsObject();
                count++;
            }
        }
        return playersInfo;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int max_player) {
        this.maxPlayer = max_player;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
