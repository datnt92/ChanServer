package com.netgame.lobby.model;

import com.electrotank.electroserver5.extensions.api.PluginApi;

public class LobbyModel {
	private PluginApi api;

	public LobbyModel(PluginApi api){
		this.api = api;
	}
	
	public PluginApi getApi() {
		return api;
	}
}
