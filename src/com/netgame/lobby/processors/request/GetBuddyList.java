package com.netgame.lobby.processors.request;


import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.duduto.chan.enums.Command;
import com.netgame.lobby.model.LobbyModel;

public class GetBuddyList implements IRequestProcessor {

	@Override
	public void process(LobbyModel model, String playerName,
			EsObjectRO requestParameters) {
		// TODO write get buddy list function...

	}

	@Override
	public String getCommand() {
		return Command.GetBuddyList.getCommand();
	}

}
