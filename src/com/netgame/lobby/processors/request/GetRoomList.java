package com.netgame.lobby.processors.request;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.RoomValue;
import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.Field;
import com.netgame.lobby.model.LobbyModel;

public class GetRoomList implements IRequestProcessor {

	@Override
	public void process(LobbyModel model, String playerName, EsObjectRO requestParameters) {

		Collection<RoomValue> rooms = model.getApi().getRoomsInZone(model.getApi().getZoneId());
		
		List<EsObject> roomListAsEsObjects = new LinkedList<EsObject>();

		for (RoomValue roomValue : rooms) {
			if (roomValue.getRoomName().equals("lobby"))
				continue;

			EsObject esoRoom = new EsObject();
			esoRoom.setString(Field.RoomName.getName(), roomValue.getRoomName());
			esoRoom.setInteger(Field.Capacity.getName(), roomValue.getCapacity());
			esoRoom.setInteger( Field.NumPlayer.getName(), model.getApi().getUsersInRoom(roomValue.getZoneId(), roomValue.getRoomId()).size());
			esoRoom.setInteger(Field.RoomId.getName(), roomValue.getRoomId());
			EsObjectRO roomInfo = model.getApi().getRoomVariable(roomValue.getZoneId(), roomValue.getRoomId(), Field.Info.getName()).getValue();
			esoRoom.setString(Field.Description.getName(), roomInfo.getString(Field.Description.getName()));
			esoRoom.setString(Field.Betting.getName(), roomInfo.getString(Field.Betting.getName()));
			esoRoom.setBoolean(Field.HasPass.getName(), roomInfo.getBoolean(Field.HasPass.getName()));
			roomListAsEsObjects.add(esoRoom);
		}

		EsObject message = new EsObject();
		message.setString(Field.Command.getName(), Command.GetRoomList.getCommand());
		
		if (roomListAsEsObjects.size() > 0)
			message.setEsObjectArray(Field.RoomList.getName(),	roomListAsEsObjects.toArray(new EsObject[roomListAsEsObjects.size()]));

		model.getApi().sendPluginMessageToUser(playerName, message);
	}

	@Override
	public String getCommand() {
		return Command.GetRoomList.getCommand();
	}

}
