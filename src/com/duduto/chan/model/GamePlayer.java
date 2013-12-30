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
import com.duduto.chan.enums.PlayerState;
import com.duduto.util.MessagingHelper;
import com.duduto.util.RandomUtil;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.database.DatabaseController;
import java.util.ArrayList;
import java.util.Arrays;
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

    public GamePlayer(EsObjectRO message, PluginApi api) {
        this._api = api;
        _dbController = (DatabaseController) api.acquireManagedObject("DatabaseControllerFactory", null);
        this.maxPlayer = Global.MAX_USER_IN_ROOM;
        lstPlayerInRoom = new ArrayList<Player>();
        noc = new ArrayList<Integer>();
        this.arrPlayers = new Player[maxPlayer];
        card = RandomUtil.getArrCard();
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
     * check player in room can sit
     */
    public ErrorCode checkSit(Player playerSit, int position, GameState gameState) {
        if (maxPlayer == getNumPlayerSit()) {
            return ErrorCode.FullSlot;
        } else if (playerSit.getPlayerData().getMoney() < bettingMoney) {
            return ErrorCode.NotEnoughtMoney;
        } else if (playerSit.getState().equals(PlayerState.Sit)) {
            return ErrorCode.PlayerSit;
        } else if (arrPlayers[position] != null) {
            return ErrorCode.SlotNotEmpty;
        } else if (gameState == GameState.Started) {
            return ErrorCode.GameStarted;
        }
        return ErrorCode.IsSuccess;
    }

    public EsObject[] getSlotSit() {
        EsObject[] arr = new EsObject[arrPlayers.length];
        for (int i = 0; i < arrPlayers.length; i++) {
            if (arrPlayers[i] != null) {
                arr[i] = arrPlayers[i].toEsObject();
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
                playersInfo[count] = arrPlayers[i].toEsObject();
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
            if (player != null) {
                if (player.getUsername().equals(username)) {
                    return player;
                }
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

    public void startGame(PluginApi api) {
        gameState = GameState.Started;
        EsObject es = new EsObject();
        int num = 0;

        //add 23 la vao nọc
        for (int i = 76; i < card.length; i++) {
            noc.add(card[i]);
        }

        //chia bài cho từng user
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                int arrCard[];
                if (num == 0) {
                    arrCard = new int[20];
                    System.arraycopy(card, num, arrCard, 0, 20);
                    num = num + 20;
                    currentTurn = i;
                    player.setDisCard(true);
                } else {
                    arrCard = new int[19];
                    System.arraycopy(card, num + 20, arrCard, 0, 19);
                    num = num + 19;
                }
                for (int j = 0; j < arrCard.length; j++) {
                    player.getMyCard().add(j);
                }
                es.setIntegerArray(Field.Card.getName(), arrCard);
                es.setString(Field.Command.getName(), Command.Card.getCommand());
                MessagingHelper.sendMessageToPlayer(player.getUsername(), es, api);
            }
        }
    }

    public void resetGame() {
        this.gameState = GameState.WaitingNewGame;
        this.card = RandomUtil.getArrCard();
    }

    public EsObject getEsPlayerData(Player p) {
        EsObject es = new EsObject();
        es.setString(Field.UserName.getName(), p.getUsername());
        es.setInteger(Field.Money.getName(), p.getPlayerData().getMoney());
        return es;
    }

    public int getNextTurn() {
        if (currentTurn == 4) {
            currentTurn = 0;
        }
        for (int i = currentTurn; i > arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                return i;
            }
        }
        return -1;
    }

    public Player getRight() {
        if (currentTurn == 4) {
            currentTurn = 0;
        }
        for (int i = currentTurn; i > arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                return player;
            }
        }
        return null;
    }

    public Player getLeft() {
        if (currentTurn == 0) {
            currentTurn = 4;
        }
        for (int i = currentTurn; i > -1; i--) {
            Player player = arrPlayers[i];
            if (player != null) {
                return player;
            }
        }
        return null;
    }

    public int getPrevTurn(int turn) {
        if (turn == 0) {
            turn = 4;
        }
        for (int i = turn; i > -1; i--) {
            Player player = arrPlayers[i];
            if (player != null) {
                return i;
            }
        }
        return -1;
    }

    public int draw(Player p) {
        if (!noc.isEmpty()) {
            int card = noc.get(0);
            p.getMyCard().add(card);
            noc.remove(card);
            return card;
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
}
