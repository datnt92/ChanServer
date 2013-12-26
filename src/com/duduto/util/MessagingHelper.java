package com.duduto.util;

import com.duduto.chan.enums.ErrorCode;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.Field;
import com.netgame.lobby.LobbyPlugin;

public class MessagingHelper {

    public static void sendMessageToPlayer(String username, EsObject message, PluginApi api) {
        api.sendPluginMessageToUser(username, message);
        //api.getLogger().info(message.toString());
    }
    
      public static void sendMessageQuitGame(EsObject[] esPlayerArray, PluginApi api) {
        EsObject message = new EsObject();
        message.setString(Field.Command.getName(), Command.QuitGame.getCommand());
        message.setEsObjectArray(Field.PlayerList.getName(), esPlayerArray);
        sendMessageToRoom(message, api);
    }

    public static void sendMessageAlertToPlayer(String toUser, String message, PluginApi api) {
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Field.AlertMessage.getName());
        es.setString(Field.UserName.getName(), toUser);
        es.setString(Field.Message.getName(), message);
        LobbyPlugin lobby = (LobbyPlugin) api.getServerPlugin("LobbyPlugin");
        lobby.rqSendAlertMessage(es);
    }

    public static EsObject buildErrorMessage(String username, ErrorCode errorCode, String mes) {
        EsObject message = new EsObject();
        message.setString(Field.Command.getName(), "error");
        message.setString("mess", mes);
        message.setInteger(Field.ErrorCode.getName(), errorCode.getCode());
        return message;
    }

    public static void sendMessageListPlayer(EsObject[] esPlayerArray, PluginApi api) {
        EsObject message = new EsObject();
        message.setString(Field.Command.getName(), Command.GetPlayerList.getCommand());
        message.setEsObjectArray(Field.PlayerList.getName(), esPlayerArray);
        //api.getLogger().warn("sendMessageListPlayer: " + Arrays.toString(esPlayerArray));
        sendMessageToRoom(message, api);
    }

    public static void sendMessageToRoom(EsObject message, PluginApi api) {
        api.sendPluginMessageToRoom(api.getZoneId(), api.getRoomId(), message);
    }

    public static void sendGlobalMessage(EsObject message, PluginApi api) {
        api.sendGlobalPluginMessage(message);
    }
}
