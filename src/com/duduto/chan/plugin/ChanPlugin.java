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
        debug("zoneid = " + getApi().getZoneId() + " Room = " + getApi().getRoomId());
        Player p = gamePlayer.getPlayerData(userName);
        gamePlayer.getLstPlayerInRoom().add(p);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.JoinRoom.getCommand());
        es.setString(Field.UserName.getName(), userName);
        es.setInteger(Field.GameState.getName(), gamePlayer.getGameState().getState());
        MessagingHelper.sendMessageToRoom(es, getApi());
        getApi().getLogger().warn(userName + " join to room");
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
            this.rqDisCard(userName);
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
                int masterRoom = gamePlayer.setMasterRoom(playerExit);
                es.setInteger(Field.MasterRoom.getName(), masterRoom);
            }
            MessagingHelper.sendMessageToRoom(es, getApi());
        } else {
            getApi().destroyRoom(getApi().getZoneId(), getApi().getRoomId());
            getApi().getLogger().warn("room " + getApi().getRoomId() + " destroyed");
        }
    }

    private void rqListPlayer(String userName) {
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.ListPlayer.getCommand());
        es.setEsObjectArray(Field.slotSit.getName(), gamePlayer.getSlotSit());
        MessagingHelper.sendMessageToPlayer(userName, es, getApi());
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
                int master = gamePlayer.setMasterRoom(playerUp);
                es.setInteger(Field.MasterRoom.getName(), master);
            }
        }
        es.setEsObjectArray(Field.slotSit.getName(), gamePlayer.getSlotSit());
        MessagingHelper.sendMessageToRoom(es, getApi());
    }

    public void rqStartGame(String username) {
        Player master = gamePlayer.getPlayerSit(username);
        EsObject es = new EsObject();
        if (gamePlayer.getGameState() == GameState.WaitingNewGame) {
            if (gamePlayer.getNumPlayerSit() >= 2) {
                if (master.isMasterRoom()) {
                    //start game
                    gamePlayer.setGameState(GameState.Started);
                    gamePlayer.startGame(getApi());
                    es.setInteger(Field.CurrentTurn.getName(), gamePlayer.getFirstPlayer());
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
            es = gamePlayer.getEsPlayerData(player);
            es.setInteger(Field.Position.getName(), position);
            es.setString(Field.Command.getName(), Field.Sit.getName());
            es.setString(Field.UserName.getName(), username);
            debug(username + " sit ");
            MessagingHelper.sendMessageToRoom(es, getApi());
        } else {
            es = new EsObject();
            es.setString(Field.Command.getName(), Field.Sit.getName());
            es.setInteger(Field.ErrorCode.getName(), errCode.getCode());
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }
    }
    
    public void rqDisCard(String username){
//        Player  pl
    }

    private void debug(String msg) {
        getApi().getLogger().debug("debug = " + msg);
    }
}
