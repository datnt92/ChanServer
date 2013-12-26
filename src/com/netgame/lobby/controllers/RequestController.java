package com.netgame.lobby.controllers;

import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.lobby.model.LobbyModel;
import com.netgame.lobby.processors.request.IRequestProcessor;
import java.util.HashMap;
import java.util.Map;

public class RequestController {

    private LobbyModel model;
    private Map<String, IRequestProcessor> commandMapping;

    public void register(IRequestProcessor processor) {
        String commandName = processor.getCommand();
        if (commandMapping.get(commandName) != null) {
            throw new Error("Existed processor for command '" + commandName + "'");
        }
        commandMapping.put(commandName, processor);
    }

    public RequestController(LobbyModel model) {
        this.model = model;
        this.commandMapping = new HashMap<String, IRequestProcessor>();
    }

    public void processCommand(String commandName, String playerName, EsObjectRO requestParameters) {
        IRequestProcessor processor = this.commandMapping.get(commandName);
        if (processor == null) {
            throw new Error("There is no processor for command '" + commandName + "'");
        }
        processor.process(this.model, playerName, requestParameters);
    }
}
