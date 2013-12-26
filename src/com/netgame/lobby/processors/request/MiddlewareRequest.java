package com.netgame.lobby.processors.request;

import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyUserVariable;
import com.electrotank.electroserver5.extensions.api.value.UserValue;
import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.Field;
import com.netgame.lobby.model.LobbyModel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MiddlewareRequest implements IRequestProcessor {

	@Override
	public void process(LobbyModel model, String playerName, EsObjectRO requestParameters) {

		List<EsObject> playerList = new LinkedList<EsObject>();

		Collection<UserValue> playersInRoom = model.getApi().getUsersInRoom(model.getApi().getZoneId(), model.getApi().getRoomId());
		for (UserValue userValue : playersInRoom) {
			if (userValue.getUserName().equals(playerName)) continue;
			EsObject player = userToEsObject(userValue, model.getApi());
			playerList.add(player);
		}

		EsObject message = new EsObject();
		message.setString(Field.Command.getName(), Command.GetPlayerList.getCommand());
		if (playerList.size() > 0)
			message.setEsObjectArray(Field.PlayerList.getName(), playerList.toArray(new EsObject[playerList.size()]));

		model.getApi().sendPluginMessageToUser(playerName, message);
	}

	private EsObject userToEsObject(UserValue userValue, PluginApi api) {
		EsObject result = new EsObject();
		result.setString(Field.UserName.getName(), userValue.getUserName());
		Collection<ReadOnlyUserVariable> userVars = api.getUserVariables(userValue.getUserName());
		for (ReadOnlyUserVariable readOnlyUserVariable : userVars) {
			api.getLogger().debug("variable: " + readOnlyUserVariable.getName() + ", value: " + readOnlyUserVariable.getValue());
			if (readOnlyUserVariable.getName().equals("CommonInfo")){
				EsObject commonInfo = readOnlyUserVariable.getValue();
				api.getLogger().debug("get user variable: " + commonInfo);
				result.setString(Field.DisplayName.getName(),
						commonInfo.getString(Field.DisplayName.getName()));
				result.setString(Field.Money.getName(),
						commonInfo.getString(Field.Money.getName()));
				result.setString(Field.Avatar.getName(),
						commonInfo.getString(Field.Avatar.getName()));
//				result.setString(Field.Sex.getName(), commonInfo.getString(Field.Sex.getName()));
				break;
			}
		}
		return result;
	}

	@Override
	public String getCommand() {
		return Command.MiddlewareRequest.getCommand();
	}

}
