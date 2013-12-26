package com.netgame.lobby.processors.request;


import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.lobby.model.LobbyModel;

public interface IRequestProcessor {
	void process(LobbyModel model, String playerName, EsObjectRO requestParameters);
	String getCommand();
}
