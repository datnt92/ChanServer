/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.model;

import com.duduto.Global;
import com.duduto.chan.enums.ErrorCode;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.GameState;
import com.duduto.chan.enums.PlayerState;
import com.duduto.util.RandomUtil;
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
    private int bettingMoney;
    private int card[];
    private int currentTurn;
    private List<Integer> noc;
    private int lastCard;
    private int nextTurn;
    private int timeWaiting;

    public GamePlayer(EsObjectRO message, PluginApi api,int timeWaiting) {
        this._api = api;
        _dbController = (DatabaseController) api.acquireManagedObject("DatabaseControllerFactory", null);
        this.maxPlayer = Global.MAX_USER_IN_ROOM;
        lstPlayerInRoom = new ArrayList<Player>();
        noc = new ArrayList<Integer>();
        this.arrPlayers = new Player[maxPlayer];
        this.gameState = GameState.WaitingNewGame;
        this.timeWaiting = timeWaiting;
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

    public Player getOtherPlayer() {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                if (!arrPlayers[i].isMasterRoom()) {
                    return player;
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
     * check player in room can sit or not
     */
//    public ErrorCode checkSit(Player playerSit, int position) {
//        if (maxPlayer == getNumPlayerSit()) {
//            return ErrorCode.FullSlot;
//        } else if (playerSit.getPlayerData().getGold() < bettingMoney) {
//            return ErrorCode.NotEnoughtMoney;
//        } else if (!playerSit.getState().equals(PlayerState.View)) {
//            return ErrorCode.PlayerSit;
//        } else if (arrPlayers[position] != null) {
//            return ErrorCode.SlotNotEmpty;
//        } else if (gameState == GameState.Started) {
//            return ErrorCode.GameStarted;
//        }
//        return ErrorCode.IsSuccess;
//    }
    public ErrorCode checkStartGame(Player master) {
        if (GameState.Started == gameState) {
            return ErrorCode.GameStarted;
        } else if (arrPlayers.length < 2) {
            return ErrorCode.NumberSit;
        } else if (!master.isMasterRoom()) {
            return ErrorCode.NotMaster;
        }
        return ErrorCode.IsSuccess;
    }

    public EsObject[] getSlotSit() {
        EsObject[] arr = new EsObject[arrPlayers.length];
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i] != null) {
                arr[i] = arrPlayers[i].toEsObject(_api);
                arr[i].setBoolean(Field.Empty.getName(), false);
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
                playersInfo[count] = arrPlayers[i].toEsObject(_api);
                count++;
            }
        }
        return playersInfo;
    }

    public void leaveRoom(Player playerExit) {
        lstPlayerInRoom.remove(playerExit);
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i] != null && arrPlayers[i].equals(playerExit)) {
                arrPlayers[i] = null;
            }
        }
    }

    public Player getPlayer(String username) {
        for (int i = 0; i < lstPlayerInRoom.size(); i++) {
            Player player = lstPlayerInRoom.get(i);
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public Player getPlayerSit(String username) {
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

    public int setMasterRoom() {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                player.setMasterRoom(true);
                return i;
            }
        }
        return -1;
    }

    public void playerUp(Player playerUp) {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null && playerUp.equals(player)) {
                arrPlayers[i] = null;
                break;
            }
        }
    }

    public void resetGame() {
        noc = new ArrayList<Integer>();
        this.gameState = GameState.WaitingNewGame;
        this.card = RandomUtil.getArrCard();
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                player.setState(PlayerState.Watting);
            }
        }
    }

    public EsObject getEsPlayerData(Player p) {
        EsObject es = new EsObject();
        es.setString(Field.UserName.getName(), p.getUsername());
        es.setInteger(Field.Money.getName(), p.getPlayerData().getGold());
        return es;
    }

    public int getNextTurn(Player player) {
        for (int i = currentTurn; i < 4; i++) {
            if (i == 3 && arrPlayers[i] == null) {
                i = 0;
            }
            if (arrPlayers[i] != null && arrPlayers[i] != player && arrPlayers[i].getState().equals(PlayerState.Playing)) {
                return i;
            }
        }
        return -1;
    }

    public int getPrevTurn(Player player) {
        for (int i = currentTurn; i > -1; i--) {
            if (i == 0 && arrPlayers[i] == null) {
                i = 3;
            }
            if (arrPlayers[i] != null && arrPlayers[i] != player && arrPlayers[i].getState().equals(PlayerState.Playing)) {
                return i;
            }
        }
        return -1;
    }

    public int draw(Player p) {
        if (!noc.isEmpty()) {
            int c = noc.get(0);
            p.getMyCard().add(c);
            noc.remove(0);
            return c;
        }
        return -1;
    }

    public boolean isCurrentTurn(Player player) {
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player1 = arrPlayers[i];
            if (player1 != null && player.getPosition() == currentTurn) {
                return true;
            }
        }
        return false;
    }

    public Player getRight(Player player) {
        return arrPlayers[getNextTurn(player)];
    }

    public Player getLeft(Player player) {
        return arrPlayers[getPrevTurn(player)];
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

    public int getBettingMoney() {
        return bettingMoney;
    }

    public void setBettingMoney(int bettingMoney) {
        this.bettingMoney = bettingMoney;
    }

    public int[] getCard() {
        return card;
    }

    public void setCard(int[] card) {
        this.card = card;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int turn) {
        this.currentTurn = turn;
    }

    public List<Integer> getNoc() {
        return noc;
    }

    public void setNoc(List<Integer> noc) {
        this.noc = noc;
    }

    public int getLastCard() {
        return lastCard;
    }

    public void setLastCard(int lastCard) {
        this.lastCard = lastCard;
    }

    public int getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(int nextTurn) {
        this.nextTurn = nextTurn;
    }

    public int getTimeWaiting() {
        return timeWaiting;
    }

    public void setTimeWaiting(int timeWating) {
        this.timeWaiting = timeWating;
    }
}
