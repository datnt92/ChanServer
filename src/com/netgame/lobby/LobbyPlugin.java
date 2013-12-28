package com.netgame.lobby;

import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.UserPublicMessageContext;
import com.netgame.database.DatabaseController;
import com.netgame.lobby.controllers.PublicMessageController;
import com.netgame.lobby.controllers.RequestController;
import com.duduto.chan.enums.Field;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.netgame.lobby.model.LobbyModel;
import com.netgame.lobby.processors.request.GetPlayerList;
import com.netgame.lobby.processors.request.GetRoomList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyPlugin extends BasePlugin {

    private LobbyModel model;
    private PublicMessageController publicMessageController;
    private RequestController requestController;
    public DatabaseController dbController;
    private static final Logger logger = LoggerFactory.getLogger(LobbyPlugin.class);

    //<editor-fold defaultstate="collapsed" desc="init">
    @Override
    public void init(EsObjectRO parameters) {
        this.model = new LobbyModel(getApi());
        this.publicMessageController = new PublicMessageController(model);
        this.requestController = new RequestController(model);
        this.dbController = (DatabaseController) getApi().acquireManagedObject("DatabaseControllerFactory", null);
        this.dbController.writeLogServerStart();
        this.initRequestProcessors();
        logger.info("LobbyPlugin initialized...");
    }

    /**
     * resister process on server
     */
    private void initRequestProcessors() {
        this.requestController.register(new GetPlayerList());
        this.requestController.register(new GetRoomList());
    }

    @Override
    public void userDidEnter(String userName) {
        getApi().getLogger().info("User " + userName + " is logged");
    }
//</editor-fold>

    @Override
    public void request(String username, EsObjectRO requestParameters) {
       ;
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

    public void rqSendAlertMessage(EsObjectRO requestParameters) {
        String strMessage = requestParameters.getString(Field.Message.getName());
        String strToUser = requestParameters.getString(Field.UserName.getName());
        EsObject es = new EsObject();
        es.setString(Field.Command.getName(), Field.AlertMessage.getName());
        es.setString(Field.Message.getName(), strMessage);
        getApi().sendPluginMessageToUser(strToUser, es);
    }
}
