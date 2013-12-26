package com.netgame.lobby.controllers;

import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.lobby.model.LobbyModel;
import com.netgame.lobby.processors.publicMessage.IPublicMessageProcessor;
import java.util.HashMap;
import java.util.Map;

public class PublicMessageController {
	private LobbyModel model;
	private Map<String, IPublicMessageProcessor> commandMapping;
	
	public void register(IPublicMessageProcessor processor){
		String commandName = processor.getCommand();
		if (commandMapping.get(commandName) != null) 
			throw new Error("Existed processor for command '"+commandName+"'");
		commandMapping.put(commandName, processor);
	}
	
	public PublicMessageController(LobbyModel model) {
		this.model = model;
		this.commandMapping = new HashMap<String, IPublicMessageProcessor>();
	}
	
	public ChainAction processCommand(String commandName, String playerName, EsObjectRO message){
		IPublicMessageProcessor processor = this.commandMapping.get(commandName);
		if (processor==null)
			throw new Error("There is no processor for command '"+commandName+"'");
		return processor.process(this.model, playerName, message);
	}
}
