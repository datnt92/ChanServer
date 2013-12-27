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
        Player p = gamePlayer.getPlayerData(userName);
        gamePlayer.getLstPlayerInRoom().add(p);
        if (gamePlayer.getLstPlayerInRoom().size() == 1) {
            p.setMasterRoom(true);
        }
        p.setState(PlayerState.View);
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Command.JoinRoom.getCommand());
        es.setString(Field.UserName.getName(), userName);
        MessagingHelper.sendMessageToRoom(es, getApi());
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
        }
    }
    
    @Override
    public void userExit(String userName) {
        EsObject es = gamePlayer.leaveRoom(userName, gamePlayer.getLstPlayerInRoom());
        if (gamePlayer.getLstPlayerInRoom().size() > 0) {
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
    
    private void rqSit(String username, EsObjectRO request) {
        Player player = gamePlayer.getPlayer(username);
        EsObject es;
        int position = request.getInteger(Field.Position.getName());
        ErrorCode errCode = gamePlayer.checkSit(player, position);
        if (errCode == ErrorCode.IsSuccess) {
            gamePlayer.addPlayer(player, position);
            player.setState(PlayerState.Sit);
            es = gamePlayer.getEsPlayerData(player);
            es.setInteger(Field.Position.getName(), position);
            es.setString(Field.Command.getName(), Field.Sit.getName());
            es.setString(Field.UserName.getName(), username);
            MessagingHelper.sendMessageToRoom(es, getApi());
        } else {
            es = new EsObject();
            es.setString(Field.Command.getName(), Field.Sit.getName());
            es.setInteger(Field.ErrorCode.getName(), errCode.getCode());
            MessagingHelper.sendMessageToPlayer(username, es, getApi());
        }
    }
    
    private void debug(String msg) {
        getApi().getLogger().debug("debug = " + msg);
    }
}
