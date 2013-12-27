/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.model;

import com.duduto.Global;
import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.ErrorCode;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.GameState;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.database.DatabaseController;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Blacker
 */
public class GamePlayer {

    private int maxPlayer = 2;
    private Player arrPlayers[];
    private PluginApi _api;
    private DatabaseController _dbController;
    private GameState gameState;
    private List<Player> lstPlayerInRoom;

    public GamePlayer(EsObjectRO message, PluginApi api) {
        this._api = api;
        _dbController = (DatabaseController) api.acquireManagedObject("DatabaseControllerFactory", null);
        this.maxPlayer = Global.MAX_USER_IN_ROOM;
        lstPlayerInRoom = new ArrayList<Player>();
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

    public void addPlayer(Player pb, int position) {
        if (arrPlayers[position] == null) {
            arrPlayers[position] = pb;
        }
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

    public int getNumPlayerSit() {
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
    public ErrorCode checkSit() {
        if (maxPlayer == getNumPlayerSit()) {
            return ErrorCode.MaxPlayer;
        }
        return ErrorCode.IsSuccess;
    }

    public EsObject[] getSlotSit() {
        EsObject[] arr = new EsObject[arrPlayers.length];
        for (int i = 0; i < arrPlayers.length; i++) {

            if (arrPlayers[i] != null) {
                arr[i] = arrPlayers[i].toEsObject();
            } else {
                arr[i] = new EsObject();
                arr[i].setBoolean(Field.Empty.getName(), true);
            }
        }
        return arr;
    }

//    public EsObject[] getListPlayerEsObject() {
//        int num = 0;
//        EsObject[] arr = new EsObject[getNumPlayerSit()];
//        for (int i = 0; i < arrPlayers.length; i++) {
//            if (arrPlayers[i] != null) {
//                arr[num] = arrPlayers[i].toEsObject();
//                num++;
//            }
//        }
//        return arr;
//    }
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

    public EsObject leaveRoom(String username, List<Player> lst) {
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.LeaveRoom.getCommand());
        es.setString(Field.UserName.getName(), username);
        for (int i = 0; i < lst.size(); i++) {
            Player player = lst.get(i);
            if (player.getUsername().equals(username)) {
                lst.remove(player);
                if (lst.size() > 0 && player.isMasterRoom()) {
                    lst.get(0).setMasterRoom(true);
                    es.setString(Field.MasterRoom.getName(), lst.get(0).getUsername());
                }
                break;
            }
        }
        return es;
    }

    public Player getPlayer(String username) {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                if (player.getUsername().equals(username)) {
                    return player;
                }
            }
        }
        return null;
    }

    public EsObject getEsPlayerData(Player p) {
        EsObject es = new EsObject();
        es.setString(Field.UserName.getName(), p.getUsername());
        es.setInteger(Field.Money.getName(), p.getPlayerData().getMoney());
        return es;
    }

    public void removePlayerView(Player p) {
        lstPlayerInRoom.remove(p);
    }

    public List<Player> getLstPlayerInRoom() {
        return lstPlayerInRoom;
    }

    public void setLstPlayerInRoom(List<Player> lstPlayerInRoom) {
        this.lstPlayerInRoom = lstPlayerInRoom;
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

    public Player[] getArrPlayers() {
        return arrPlayers;
    }

    public void setArrPlayers(Player[] arrPlayers) {
        this.arrPlayers = arrPlayers;
    }
}
