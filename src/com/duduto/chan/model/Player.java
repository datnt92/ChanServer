/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.model;

import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.PlayerState;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Blacker
 */
public class Player {

    private boolean masterRoom = false;
    private PlayerBean playerData;
    private PlayerState state;
//    private int myCard[];
//    private int carDised[];
    private List<Integer> myCard;
    private List<Integer> cardDised;
    private int position;
    private boolean steal = false;
    private boolean draw = false;
    private boolean disCard = false;

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
        state = PlayerState.View;
    }

    public void disCard(int id) {
        if (myCard.contains(id)) {
            myCard.remove(id);
            cardDised.add(id);
        }
    }

    public boolean hasCard(int id) {
        if (myCard.contains(id)) {
            return true;
        }
        return false;
    }

    public int getLastDisCard() {
        return myCard.get(myCard.size() - 1);
    }


    
    public boolean canSteal(int card1,int card2) {
        if (card1 == card2) {
            return true;
        }
        if ((card1++)==card2) {
            return true;
        }
        if ((card1+2)==card2) {
            return true;
        }
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

    public EsObject toEsObject() {
        EsObject es = playerData.toEsObject();
        es.setBoolean(Field.MasterRoom.getName(), masterRoom);
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
}
