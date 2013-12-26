/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.plugin;

import com.duduto.chan.enums.ErrorCode;
import com.duduto.chan.model.GamePlayer;
import com.duduto.chan.model.Player;
import com.duduto.util.MessagingHelper;
import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.UserEnterContext;
import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.Message;
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
        Player pb = gamePlayer.getPlayerData(userName);
        EsObject es = new EsObject();
        gamePlayer.addPlayer(pb);
        if (gamePlayer.getNumPlayerInRoom() == 1) {
            pb.setMasterRoom(true);
            es.setString(Field.Command.getName(), Command.JoinRoom.getCommand());
            es.setString(Field.Message.getName(), userName + " đã tạo phòng");
            MessagingHelper.sendMessageToRoom(es, getApi());
            getApi().getLogger().warn(userName + " created room");
        } else {
            es.setString(Field.Command.getName(), Command.JoinRoom.getCommand());
            es.setString(Field.Message.getName(), userName + " đã vào phòng");
            MessagingHelper.sendMessageToRoom(es, getApi());
            getApi().getLogger().warn(userName + " join to room");
        }
    }

    @Override
    public ChainAction userEnter(UserEnterContext context) {
        String username = context.getUserName();
        ErrorCode error = gamePlayer.checkJoinRoom();
        if (error == ErrorCode.MaxPlayer) {
            MessagingHelper.sendMessageAlertToPlayer(username, Message.MaxPlayer.getMessage(), getApi());
            return ChainAction.Fail;
        }
        return ChainAction.OkAndContinue;
    }

    @Override
    public void request(String userName, EsObjectRO requestParameters) {
        if (requestParameters.getString(Field.Command.getName()).equals(Command.GetPlayerList.getCommand())) {
            this.rqListPlayer();
        } else if (requestParameters.getString(Field.Command.getName()).equals(Command.KickPlayer.getCommand())) {
            kickOutRoom(userName, requestParameters);
        }
    }

    @Override
    public void userExit(String userName) {
        gamePlayer.removePlayer(userName);
        if (gamePlayer.getNumPlayerInRoom() > 0) {
            gamePlayer.setMasterRoom();
            EsObject es = new EsObject();
            es.setString(Field.Command.getName(), Command.LeaveRoom.getCommand());
            es.setString(Field.Message.getName(), userName + " đã rời khỏi phòng");
            MessagingHelper.sendMessageToRoom(es, getApi());
            getApi().getLogger().debug(userName + " leave the room.Master room set for " + gamePlayer.getMasterRoom().getUsername());
        } else {
            destroy();
            getApi().getLogger().warn("room " + getApi().getRoomId() + " destroyed");
        }
    }

    private void rqListPlayer() {
        EsObject[] EsPlayersInfo = gamePlayer.getListPlayerEsObject();
        MessagingHelper.sendMessageListPlayer(EsPlayersInfo, getApi());
    }

    private void kickOutRoom(String username, EsObjectRO request) {
        Player masterRoom = gamePlayer.getPlayerData(username);
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

    private void debug(String msg) {
        getApi().getLogger().debug("debug = " + msg);
    }
}
