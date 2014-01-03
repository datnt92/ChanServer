/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.model;

import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.PlayerState;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Blacker
 */
public class Player {

    private boolean masterRoom = false;
    private PlayerBean playerData;
    private PlayerState state = PlayerState.View;
    private List<Integer> myCard;
    private List<Integer> cardDised;
    private int position;
    private boolean steal = false;
    private boolean draw = false;
    private boolean disCard = false;
    private boolean skip = false;

    public Player(PlayerBean player) {
        this.playerData = player;
        state = PlayerState.View;
        cardDised = new LinkedList<Integer>();
        myCard = new LinkedList<Integer>();
    }

    public void resetGame() {
        steal = false;
        draw = false;
        disCard = false;
        myCard.removeAll(myCard);
        cardDised.removeAll(myCard);
    }

    public void disCard(int id) {
        for (Integer integer : myCard) {
            if (integer == id) {
                myCard.remove(myCard.indexOf(id));
                cardDised.add(id);
                return;
            }
        }
    }

    public boolean hasCard(int id) {
        if (myCard.contains(id)) {
            return true;
        }
        return false;
    }

//    public int getLastDisCard() {
//        return myCard.get(myCard.size() - 1);
//    }
    public boolean canSteal(int card1, int card2) {
        if (card1 == card2) {
            return true;
        }
        if (myCard.contains(card1)) {
            return true;
        }

//        if ((card1++)==card2) {
//            return true;
//        }
//        if ((card1+2)==card2) {
//            return true;
//        }
        return false;
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

    public EsObject toEsObject(PluginApi api) {
        EsObject es = playerData.toEsObject();
        es.setBoolean(Field.MasterRoom.getName(), masterRoom);
        if (state.equals(PlayerState.Playing) && !cardDised.isEmpty()) {
            es.setIntegerArray(Field.CardDised.getName(), convertToArray(cardDised));
        }
        es.setString(Field.PlayerState.getName(), state.getState());
        return es;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public List<Integer> getMyCard() {
        return myCard;
    }

    public void setMyCard(List<Integer> myCard) {
        this.myCard = myCard;
    }

    public List<Integer> getCardDised() {
        return cardDised;
    }

    public void setCardDised(List<Integer> cardDised) {
        this.cardDised = cardDised;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSteal() {
        return steal;
    }

    public void setSteal(boolean steal) {
        this.steal = steal;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public boolean isDisCard() {
        return disCard;
    }

    public void setDisCard(boolean disCard) {
        this.disCard = disCard;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public int[] convertToArray(List<Integer> lst) {
        int arr[] = new int[lst.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = lst.get(i);

        }
        return arr;
    }

    public void afterSkip() {
        this.setDraw(false);
        this.setSkip(false);
        this.setSteal(false);
        this.setDisCard(false);
    }

    public void afterDraw() {
        this.setDraw(false);
        this.setSkip(true);
        this.setSteal(true);
        this.setDisCard(true);
    }

    public void afterSteal() {
        this.setSteal(false);
        this.setDraw(false);
        this.setSkip(false);
        this.setDisCard(true);
    }

    public void prevDisCard() {
        this.setSteal(true);
        this.setDraw(true);
        this.setDisCard(true);
    }
}
