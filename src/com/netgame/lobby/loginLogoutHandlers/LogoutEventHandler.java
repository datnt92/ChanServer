package com.netgame.lobby.loginLogoutHandlers;

import com.electrotank.electroserver5.extensions.BaseLogoutEventHandler;
import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.gamemanager.AutoManager;

public class LogoutEventHandler extends BaseLogoutEventHandler {

    @Override
    public void executeLogout(String username) {
        getApi().getLogger().info("Execute logout for user '" + username + "'");
        super.executeLogout(username);
    }

    @Override
    public void init(EsObjectRO parameters) {
        getApi().getLogger().info("Init logout event handler");
        super.init(parameters);
    }
}
