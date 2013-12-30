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
import com.duduto.chan.enums.GameState;
import com.duduto.chan.enums.Message;
import com.duduto.chan.enums.PlayerState;
import com.electrotank.electroserver5.extensions.api.value.UserPublicMessageContext;
import com.netgame.lobby.controllers.PublicMessageController;
import com.netgame.lobby.model.LobbyModel;

/**
 *
 * @author Blacker
 */
public class ChanPlugin extends BasePlugin {

    private GamePlayer gamePlayer;
    private PublicMessageController publicMessageController;
    private LobbyModel model;

    @Override
    public void init(EsObjectRO parameters) {
        this.model = new LobbyModel(getApi());
        this.publicMessageController = new PublicMessageController(model);
        gamePlayer = new GamePlayer(parameters, getApi());
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
        setGameDetail();
        debug("zoneid = " + getApi().getZoneId() + " Room = " + getApi().getRoomId());
        Player p = gamePlayer.getPlayerData(userName);
        gamePlayer.getLstPlayerInRoom().add(p);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.JoinRoom.getCommand());
        es.setString(Field.UserName.getName(), userName);
        es.setInteger(Field.GameState.getName(), gamePlayer.getGameState().getState());
        MessagingHelper.sendMessageToRoom(es, getApi());
//        getApi().getLogger().warn(userName + " join to room");
    }

    @Override
    public ChainAction userEnter(UserEnterContext context) {
        return ChainAction.OkAndContinue;
    }

    @Override
    public void request(String userName, EsObjectRO requestParameters) {
//               debug(""+ getApi().getRoomVariable(getApi().getZoneId(), getApi().getRoomId(), "test").getValue().getInteger("money"));
        if (requestParameters.getString(Field.Command.getName()).equals(Command.GetPlayerList.getCommand())) {
            this.rqListPlayer(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.KickPlayer.getCommand())) {
            rqKickPlayer(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Sit.getCommand())) {
            this.rqSit(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Up.getCommand())) {
            this.rqUp(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.StartGame.getCommand())) {
            this.rqStartGame(userName);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.DisCard.getCommand())) {
            this.rqDisCard(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Steal.getCommand())) {
            this.rqSteal(userName, requestParameters);
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.Draw.getCommand())) {
            this.rqDraw(userName);
        }
    }

    @Override
    public void userExit(String userName) {
        Player playerExit = gamePlayer.getPlayer(userName);
        gamePlayer.leaveRoom(playerExit);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.LeaveRoom.getCommand());
        es.setString(Field.UserName.getName(), playerExit.getUsername());
        if (gamePlayer.getLstPlayerInRoom().size() > 0) {
            if (playerExit.isMasterRoom()) {
                int masterRoom = gamePlayer.setMasterRoom();
                es.setInteger(Field.MasterRoom.getName(), masterRoom);
            }
            setGameDetail();
            MessagingHelper.sendMessageToRoom(es, getApi());
        } else {
            getApi().destroyRoom(getApi().getZoneId(), getApi().getRoomId());
            getApi().getLogger().warn("room " + getApi().getRoomId() + " destroyed");
        }
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
        es.setString(Field.Command.getName(), Command.Up.getCommand());
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
        Player master = gamePlayer.getPlayerSit(username);
        EsObject es = new EsObject();
        if (gamePlayer.getGameState() == GameState.WaitingNewGame) {
            if (gamePlayer.getNumPlayerSit() >= 2) {
                if (master.isMasterRoom()) {
                    //start game
                    gamePlayer.startGame(getApi());
                    es.setInteger(Field.Noc.getName(), gamePlayer.getNoc().size());
                    es.setInteger(Field.CurrentTurn.getName(), gamePlayer.getCurrentTurn());
                    es.setString(Field.Command.getName(), Command.PassCard.getCommand());
                    MessagingHelper.sendMessageToRoom(es, getApi());
                } else {
                    es.setString(Field.Command.getName(), Command.StartGame.getCommand());
                    es.setBoolean(Field.MasterRoom.getName(), false);
                    MessagingHelper.sendMessageToPlayer(username, es, getApi());
                }
            } else {
                es.setString(Field.Command.getName(), Command.StartGame.getCommand());
                es.setInteger(Field.NumberSit.getName(), gamePlayer.getNumPlayerSit());
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            }
        } else {
            es.setString(Field.Command.getName(), Command.StartGame.getCommand());
            es.setInteger(Field.GameState.getName(), GameState.Started.getState());
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }
    }

    private void rqSit(String username, EsObjectRO request) {
        EsObject es;
        Player player = gamePlayer.getPlayer(username);
        int position = request.getInteger(Field.Position.getName());
        ErrorCode errCode = gamePlayer.checkSit(player, position, gamePlayer.getGameState());
        if (errCode == ErrorCode.IsSuccess) {
            gamePlayer.addPlayer(player, position);
            if (gamePlayer.getNumPlayerSit() == 1) {
                player.setMasterRoom(true);
            }
            player.setState(PlayerState.Sit);
            player.setPosition(position);
            es = gamePlayer.getEsPlayerData(player);
            es.setInteger(Field.Position.getName(), position);
            es.setString(Field.Command.getName(), Field.Sit.getName());
            es.setString(Field.UserName.getName(), username);
            setGameDetail();
            MessagingHelper.sendMessageToRoom(es, getApi());
        } else {
            es = new EsObject();
            es.setString(Field.Command.getName(), Field.Sit.getName());
            es.setInteger(Field.ErrorCode.getName(), errCode.getCode());
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
                    gamePlayer.setCurrentTurn(gamePlayer.getNextTurn());
                    Player nextTurn = gamePlayer.getRight();
                    nextTurn.setSteal(true);
                    nextTurn.setDraw(true);
                    nextTurn.setDisCard(true);
                    es.setInteger(Field.CurrentTurn.getName(), gamePlayer.getCurrentTurn());
                    MessagingHelper.sendMessageToRoom(es, getApi());
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

    public void rqSteal(String username, EsObjectRO request) {
        if (!request.variableExists(Field.CardId.getName())) {
            debug("not parameter card id");
        } else {
            Player current = gamePlayer.getPlayer(username);
            int cardId = request.getInteger(Field.CardId.getName());
            EsObject es = new EsObject();
            es.setString(Field.Command.getName(), Command.Steal.getCommand());
            if (gamePlayer.isCurrentTurn(current)) {
                Player leftPlayer = gamePlayer.getLeft();
                if (current.canSteal(cardId, leftPlayer.getLastDisCard())) {
                    es.setInteger(Field.CardId.getName(), cardId);
                    current.setSteal(false);
                    current.setDraw(false);
                    MessagingHelper.sendMessageToRoom(es, getApi());
                } else {
                    es.setInteger(Field.ErrorCode.getName(), ErrorCode.HasSteal.getCode());
                }
            } else {
                es.setInteger(Field.ErrorCode.getName(), ErrorCode.NotCurrentTurn.getCode());
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            }
        }
    }

    public void rqDraw(String username) {
        Player player = gamePlayer.getPlayer(username);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.Draw.getCommand());
        if (gamePlayer.isCurrentTurn(player)) {
            if (player.isDraw()) {
                int id = gamePlayer.draw(player);
                MessagingHelper.sendMessageToRoom(es, getApi());
                es.setInteger(Field.CardId.getName(), id);
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
                player.setDraw(false);
            } else {
                es.setInteger(Field.ErrorCode.getName(), ErrorCode.NotCurrentTurn.getCode());
                MessagingHelper.sendMessageToPlayer(username, es, getApi());
            }
        } else {
            es.setInteger(Field.ErrorCode.getName(), ErrorCode.NotCurrentTurn.getCode());
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }

    }

    private void debug(String msg) {
        getApi().getLogger().debug("debug = " + msg);
    }
}
