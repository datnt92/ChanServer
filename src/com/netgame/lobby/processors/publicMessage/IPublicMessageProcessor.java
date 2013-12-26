package com.netgame.lobby.processors.publicMessage;


import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.lobby.model.LobbyModel;

public interface IPublicMessageProcessor {
	ChainAction process(LobbyModel model, String playerName, EsObjectRO message);
	String getCommand();
}
