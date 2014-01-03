/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.process;

import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.GameState;
import com.duduto.chan.enums.PlayerState;
import com.duduto.chan.model.GamePlayer;
import com.duduto.chan.model.Player;
import com.duduto.util.ArrayUtil;
import com.duduto.util.MessagingHelper;
import com.duduto.util.RandomUtil;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import java.util.List;

/**
 *
 * @author Blacker
 */
public class StartGame {

    private GamePlayer gamePlayer;

    public StartGame(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
        arrPlayers = gamePlayer.getArrPlayers();
        card = gamePlayer.getCard();
        noc = gamePlayer.getNoc();
        currentTurn = gamePlayer.getCurrentTurn();;
    }
    private Player[] arrPlayers;
    private int[] card;
    private List<Integer> noc;
    private int currentTurn;

    public void startGame(PluginApi api) {
        gamePlayer.setGameState(GameState.Started);
        card = RandomUtil.getArrCard();

        int num = 0;
        //add 23 la vao nọc
        for (int i = 77; i < card.length; i++) {
            noc.add(card[i]);
        }
        //chia bài cho từng user
        for (int i = 0; i < arrPlayers.length; i++) {
            Player player = arrPlayers[i];
            if (player != null) {
                EsObject es = new EsObject();
                int arrCard[];
                //nguoi choi dau tien,co 20 la bai
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
                player.setState(PlayerState.Playing);
                player.setMyCard(ArrayUtil.arrayintToInteger(arrCard));
                es.setInteger(Field.Noc.getName(), noc.size());
                es.setInteger(Field.CurrentTurn.getName(), currentTurn);
                es.setIntegerArray(Field.Card.getName(), arrCard);
                es.setEsObjectArray(Field.slotSit.getName(), gamePlayer.getSlotSit());
                es.setString(Field.Command.getName(), Command.PassCard.getCommand());
                MessagingHelper.sendMessageToPlayer(player.getUsername(), es, api);
            }
        }
        for (int i = 0; i < gamePlayer.getLstPlayerInRoom().size(); i++) {
            EsObject esr = new EsObject();
            Player p = gamePlayer.getLstPlayerInRoom().get(i);
            esr.setEsObjectArray(Field.slotSit.getName(), gamePlayer.getSlotSit());
            if (p.getState().equals(PlayerState.View)) {
                esr.setString(Field.Command.getName(), Command.StartGame.getCommand());
                MessagingHelper.sendMessageToPlayer(p.getUsername(), esr, api);
            }
        }
    }
}
