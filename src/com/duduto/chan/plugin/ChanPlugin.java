/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.plugin;

import com.duduto.chan.model.GamePlayer;
import com.duduto.chan.model.Player;
import com.duduto.util.MessagingHelper;
import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.UserEnterContext;
import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.ErrorCode;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.Message;
import com.duduto.chan.enums.PlayerState;
import com.duduto.chan.process.AutoPlayGame;
import com.duduto.chan.process.StartGame;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyUserVariable;
import com.electrotank.electroserver5.extensions.api.value.UserPublicMessageContext;
import com.netgame.lobby.controllers.PublicMessageController;
import com.netgame.lobby.model.LobbyModel;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Blacker
 */
public class ChanPlugin extends BasePlugin {

    private GamePlayer gamePlayer;
    private PublicMessageController publicMessageController;
    private LobbyModel model;
    private PluginApi api;
    private int timeWaitting = 10000;

    @Override
    public void init(EsObjectRO parameters) {
        this.api = getApi();
        this.model = new LobbyModel(getApi());
        this.publicMessageController = new PublicMessageController(model);
        if (!api.getRoomVariables(api.getZoneId(), api.getRoomId()).isEmpty()) {
            //set time waiting
            EsObjectRO roomInfo = api.getRoomVariable(api.getZoneId(), api.getRoomId(), Field.Info.getName()).getValue();
            timeWaitting = roomInfo.getInteger(Field.TimeWaiting.getName()) * 1000;
        }
        gamePlayer = new GamePlayer(parameters, getApi(), timeWaitting);
    }

    @Override
    public ChainAction userSendPublicMessage(UserPublicMessageContext message) {
        if (message.getEsObject().variableExists(Field.Command.getName())) {
            String commandName = message.getEsObject().getString(Field.Command.getName());
            return publicMessageController.processCommand(commandName, message.getUserName(), message.getEsObject());
        } else {
            return ChainAction.OkAndContinue;
        }
    }

    @Override
    public void userDidEnter(String userName) {
        debug("zoneid = " + getApi().getZoneId() + " Room = " + getApi().getRoomId());
        Player p = gamePlayer.getPlayerData(userName);
        if (!getApi().getUserVariables(userName).isEmpty()) {
            Collection<ReadOnlyUserVariable> userVars = getApi().getUserVariables(userName);
            for (ReadOnlyUserVariable readOnlyUserVariable : userVars) {
                if (readOnlyUserVariable.getName().equals("userInfo")) {
                    EsObject es = readOnlyUserVariable.getValue();
                    if (es.getBoolean("isAuto")) {
                        p.getPlayerData().setIsAuto(true);
                    }
                }
            }
        }
        gamePlayer.getLstPlayerInRoom().add(p);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.JoinRoom.getCommand());
        es.setString(Field.UserName.getName(), userName);
        es.setInteger(Field.GameState.getName(), gamePlayer.getGameState().getState());
        MessagingHelper.sendMessageToRoom(es, getApi());
        setGameDetail();
        getApi().getLogger().warn(userName + " join to room");
    }

    @Override
    public ChainAction userEnter(UserEnterContext context) {
        return ChainAction.OkAndContinue;
    }

    @Override
    public void request(String userName, EsObjectRO requestParameters) {
        if (requestParameters.getString(Field.Command.getName()).equals(Command.GetPlayerList.getCommand())) {
            this.rqListPlayer(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.KickPlayer.getCommand())) {
            rqKickPlayer(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Sit.getCommand())) {
            this.rqSit(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Stand.getCommand())) {
            this.rqUp(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.StartGame.getCommand())) {
            this.rqStartGame(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.DisCard.getCommand())) {
            this.rqDisCard(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Steal.getCommand())) {
            this.rqSteal(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Draw.getCommand())) {
            this.rqDraw(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Skip.getCommand())) {
            this.rqSkip(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Chiu.getCommand())) {
            
        }
    }

    @Override
    public void userExit(String userName) {
        Player playerExit = gamePlayer.getPlayer(userName);
        gamePlayer.leaveRoom(playerExit);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.LeaveRoom.getCommand());
        es.setString(Field.UserName.getName(), playerExit.getUsername());
        gamePlayer.resetGame();
        if (gamePlayer.getLstPlayerInRoom().size() > 0) {
            if (playerExit.isMasterRoom()) {
                int masterRoom = gamePlayer.setMasterRoom();
                es.setInteger(Field.MasterRoom.getName(), masterRoom);
            }
            MessagingHelper.sendMessageToRoom(es, getApi());
        } else {
            getApi().destroyRoom(getApi().getZoneId(), getApi().getRoomId());
            getApi().getLogger().warn("room " + getApi().getRoomId() + " destroyed");
        }
        setGameDetail();
        debug("number player in room" + gamePlayer.getLstPlayerInRoom().size());
    }

    private void rqListPlayer(String userName) {
        debug("aaaaaaa zone = " + getApi().getZoneId() + " room= " + getApi().getRoomId());
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.ListPlayer.getCommand());
        es.setEsObjectArray(Field.slotSit.getName(), gamePlayer.getSlotSit());
        MessagingHelper.sendMessageToPlayer(userName, es, getApi());
    }

    private void setGameDetail() {
        EsObject gameDetail = getApi().getGameDetails();
        gameDetail.setEsObjectArray(Field.slotSit.getName(), gamePlayer.getSlotSit());
        gameDetail.setInteger(Field.NumberSit.getName(), gamePlayer.getNumPlayerSit());
        getApi().setGameDetails(gameDetail);
    }

    private void rqKickPlayer(String username, EsObjectRO request) {
        Player masterRoom = gamePlayer.getPlayerSit(username);
        if (!request.variableExists(Field.KickPlayer.getName()) && masterRoom != null) {
            String playerKick = request.getString(Field.PlayerKick.getName());
            if (masterRoom.isMasterRoom()) {
                MessagingHelper.sendMessageAlertToPlayer(playerKick, Message.KickPlayer.getMessage(), getApi());
                EsObject[] esInfoPlayers = gamePlayer.getPlayersInfo();
                MessagingHelper.sendMessageQuitGame(esInfoPlayers, getApi());
                getApi().kickUserFromRoom(playerKick, getApi().getZoneId(), getApi().getRoomId(), Field.PlayerKick.getName());
            } else {
                MessagingHelper.sendMessageAlertToPlayer(username, Message.NotMasterRoom.getMessage(), getApi());
            }
        } else {
            MessagingHelper.sendMessageAlertToPlayer(username, Message.PlayerNotFound.getMessage(), getApi());
        }
    }

    public void rqUp(String username) {
        Player playerUp = gamePlayer.getPlayerSit(username);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.Stand.getCommand());
        gamePlayer.playerUp(playerUp);
        es.setString(Field.UserName.getName(), username);
        if (playerUp.isMasterRoom()) {
            playerUp.setMasterRoom(false);
            if (gamePlayer.getNumPlayerSit() > 0) {
                int master = gamePlayer.setMasterRoom();
                es.setInteger(Field.MasterRoom.getName(), master);
            }
        }
        setGameDetail();
        MessagingHelper.sendMessageToRoom(es, getApi());
    }

    public void rqStartGame(String username) {
        debug("==============start game================");
        Player master = gamePlayer.getPlayerSit(username);
        EsObject es = new EsObject();
        ErrorCode code = gamePlayer.checkStartGame(master);
        if (code == ErrorCode.IsSuccess) {
            StartGame start = new StartGame(gamePlayer);
            start.startGame(getApi());
            Player thisTurn = gamePlayer.getArrPlayers()[gamePlayer.getCurrentTurn()];
            if (thisTurn.getPlayerData().isIsAuto()) {
                AutoPlayGame auto = new AutoPlayGame(gamePlayer, thisTurn, getApi());
                auto.disCard(thisTurn.getMyCard().get(0), Command.DisCard.getCommand());
            }
        } else {
            es.setString(Field.Command.getName(), Command.StartGame.getCommand());
            es.setInteger(Field.ErrorCode.getName(), code.getCode());
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }
    }

    private void rqSit(String username, EsObjectRO request) {
        EsObject es = new EsObject();
        int position = request.getInteger(Field.Position.getName());
        if (gamePlayer.getNumPlayerSit() != gamePlayer.getMaxPlayer()) {
            if (gamePlayer.getArrPlayers()[position] != null) {
                es.setString(Field.Command.getName(), Field.Sit.getName());
                es.setString(Field.Message.getName(), "Vị trí này đã có người ngồi");
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            } else {
                Player player = gamePlayer.getPlayer(username);
                getApi().getLogger().debug(player.getState().getState());
                if (player.getState().equals(PlayerState.View)) {
                    gamePlayer.addPlayer(player, position);
                    if (gamePlayer.getNumPlayerSit() == 1) {
                        player.setMasterRoom(true);
                    }
                    player.setState(PlayerState.Watting);
                    player.setPosition(position);
                    es = gamePlayer.getEsPlayerData(player);
                    es.setString(Field.Command.getName(), Field.Sit.getName());
                    es.setInteger(Field.Position.getName(), position);
                    es.setString(Field.UserName.getName(), username);
                    setGameDetail();
                    MessagingHelper.sendMessageToRoom(es, getApi());
                    if (gamePlayer.getNumPlayerSit() > 2) {
                        test t = new test(gamePlayer.getMasterRoom().getUsername());
                        t.start();
                    }
                }
            }
        } else {
            es.setString(Field.Command.getName(), Field.Sit.getName());
            es.setString(Field.Message.getName(), "Bàn đã đầy");
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }
    }

    public void rqDisCard(String username, EsObjectRO request) {
        if (!request.variableExists(Field.CardId.getName())) {
            debug("not parameter card id");
        } else {
            EsObject es = new EsObject();
            es.setString(Field.Command.getName(), Command.DisCard.getCommand());
            int cardId = request.getInteger(Field.CardId.getName());
            Player player = gamePlayer.getPlayer(username);
            if (gamePlayer.isCurrentTurn(player)) {
                if (player.hasCard(cardId)) {
                    player.disCard(cardId);
                    es.setInteger(Field.CardId.getName(), cardId);
                    Player playerNext = gamePlayer.getRight(player);
                    playerNext.prevDisCard();
                    gamePlayer.setLastCard(cardId);
                    //set position next turn
                    gamePlayer.setNextTurn(gamePlayer.getNextTurn(player));
                    es.setInteger(Field.CurrentTurn.getName(), gamePlayer.getCurrentTurn());
                    es.setInteger(Field.NextTurn.getName(), gamePlayer.getNextTurn());
                    MessagingHelper.sendMessageToRoom(es, getApi());
                    if (playerNext.getPlayerData().isIsAuto()) {
                        AutoPlayGame auto = new AutoPlayGame(gamePlayer, playerNext, getApi());
                        auto.disCard(cardId, Command.DisCard.getCommand());
                    }
                } else {
                    es.setInteger(Field.ErrorCode.getName(), ErrorCode.HasCard.getCode());
                    MessagingHelper.sendMessageToPlayer(username, es, getApi());
                }
            } else {
                es.setInteger(Field.ErrorCode.getName(), ErrorCode.NotCurrentTurn.getCode());
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            }
        }
    }

    /*  
     * ăn quân bài đối phương vừa đánh ra
     */
    public void rqSteal(String username, EsObjectRO request) {
        if (!request.variableExists(Field.CardId.getName())) {
            debug("not parameter card id");
        } else {
            Player current = gamePlayer.getPlayer(username);
            int cardId = request.getInteger(Field.CardId.getName());
            EsObject es = new EsObject();
            es.setString(Field.Command.getName(), Command.Steal.getCommand());
            if (gamePlayer.isCurrentTurn(current)) {
                if (current.canSteal(gamePlayer.getLastCard(), cardId)) {
                    es.setInteger(Field.CardId.getName(), cardId);
                    current.afterSteal();
                    MessagingHelper.sendMessageToRoom(es, getApi());
                } else {
                    es.setString(Field.ErrorCode.getName(), "Bạn không ăn được quân này");
                    MessagingHelper.sendMessageToPlayer(username, es, getApi());
                }
            } else {
                es.setString(Field.ErrorCode.getName(), "Chưa đến lượt bạn");
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            }
        }
    }

    /*
     rút bài từ nọc
     */
    public void rqDraw(String username) {
        Player p = gamePlayer.getPlayer(username);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.Draw.getCommand());
        if (gamePlayer.isCurrentTurn(p)) {
            if (p.isDraw()) {
                es.setString(Field.UserName.getName(), p.getUsername());
                es.setInteger(Field.Position.getName(), p.getPosition());
                int id = gamePlayer.draw(p);
                es.setInteger(Field.CardId.getName(), id);
                getApi().getLogger().debug("draw cardid " + id);
                MessagingHelper.sendMessageToRoom(es, getApi());
                p.afterDraw();
                gamePlayer.setLastCard(id);
            } else {
                es.setString(Field.Message.getName(), "Bạn không có quyền rút");
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            }
        } else {
            es.setString(Field.Message.getName(), "Chưa đến lượt của bạn");
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }
    }

    public void rqSkip(String username) {
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.Skip.getCommand());
        Player player = gamePlayer.getPlayer(username);
        if (gamePlayer.isCurrentTurn(player)) {
            if (player.isSkip()) {
                es.setString(Field.UserName.getName(), player.getUsername());
                gamePlayer.setNextTurn(gamePlayer.getNextTurn(player));
                es.setInteger(Field.CurrentTurn.getName(), gamePlayer.getCurrentTurn());
                es.setInteger(Field.NextTurn.getName(), gamePlayer.getNextTurn());
                player.afterSkip();
                MessagingHelper.sendMessageToRoom(es, getApi());
            } else {
                es.setString(Field.Message.getName(), "Bạn không được phép dưới.");
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            }
        } else {
            es.setString(Field.Message.getName(), "Chưa đến lượt của bạn");
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }
    }

    private void debug(String msg) {
        getApi().getLogger().debug("" + msg);
    }

    
    //<editor-fold defaultstate="collapsed" desc="test auto play game">
    
        public class test extends Thread {

        private String username;

        public test(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(timeWaitting);
                rqStartGame(username);
            } catch (InterruptedException ex) {
                Logger.getLogger(ChanPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    //</editor-fold>

    }
}
