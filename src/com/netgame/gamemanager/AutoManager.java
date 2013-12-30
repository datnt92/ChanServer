package com.netgame.gamemanager;

import com.duduto.chan.enums.Field;
import com.duduto.util.Log;
import com.electrotank.electroserver5.client.ElectroServer;
import com.electrotank.electroserver5.client.api.EsConnectionClosedEvent;
import com.electrotank.electroserver5.client.api.EsConnectionResponse;
import com.electrotank.electroserver5.client.api.EsCreateRoomRequest;
import com.electrotank.electroserver5.client.api.EsFindGamesRequest;
import com.electrotank.electroserver5.client.api.EsFindGamesResponse;
import com.electrotank.electroserver5.client.api.EsGetUserCountRequest;
import com.electrotank.electroserver5.client.api.EsJoinGameRequest;
import com.electrotank.electroserver5.client.api.EsJoinRoomEvent;
import com.electrotank.electroserver5.client.api.EsLoginRequest;
import com.electrotank.electroserver5.client.api.EsLoginResponse;
import com.electrotank.electroserver5.client.api.EsMessageType;
import com.electrotank.electroserver5.client.api.EsPluginMessageEvent;
import com.electrotank.electroserver5.client.api.EsPluginRequest;
import com.electrotank.electroserver5.client.api.EsQuickJoinGameRequest;
import com.electrotank.electroserver5.client.api.EsSearchCriteria;
import com.electrotank.electroserver5.client.api.EsServerGame;
import com.electrotank.electroserver5.client.connection.AvailableConnection;
import com.electrotank.electroserver5.client.connection.TransportType;
import com.electrotank.electroserver5.client.extensions.api.value.EsObject;
import com.electrotank.electroserver5.client.server.Server;
import com.netgame.database.DatabaseController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoManager {

    private ElectroServer es = null;
    public String roomName = "Lobby1";
    public String lobbyName = "Lobby1";
    public String zoneName = "ChanZone";
    public String pluginName = "ChanPlugin";
    public String userName;
    private int _zoneID;
    private int _roomID;
    //public int outTime;
//	Timer timer;
    public int betting = 100;
    public final String[] ROOMNAME_ARRAY = new String[]{"C\u00f3 gi\u1ecfi v\u00e0o \u0111\u00e2y", "Chi\u1ebfn th\u00f4i", "Cao th\u1ee7 \u0111\u00e2y", "L\u00e0m m\u1ed9t v\u00e1n n\u00e0o", "C\u00f2n ch\u1edd g\u00ec n\u1eefa"};
    private static final Logger logger = LoggerFactory.getLogger(AutoManager.class);

    public void initialize() {
        logger.error("logger init");
        System.out.println("logger init");
        es = new ElectroServer();
        //listen for certain events to allow the application to flow, and to support chatting and user list updates
        es.getEngine().addEventListener(EsMessageType.ConnectionResponse, this, "onConnectionResponse", EsConnectionResponse.class);
        es.getEngine().addEventListener(EsMessageType.LoginResponse, this, "onLoginResponse", EsLoginResponse.class);
        es.getEngine().addEventListener(EsMessageType.JoinRoomEvent, this, "onJoinRoomOK", EsJoinRoomEvent.class);
        es.getEngine().addEventListener(EsMessageType.PluginMessageEvent, this, "onPluginMessageEvent", EsPluginMessageEvent.class);
        es.getEngine().addEventListener(EsMessageType.ConnectionClosedEvent, this, "onConnectionCloseHandler", EsConnectionClosedEvent.class);
        es.getEngine().addEventListener(EsMessageType.FindGamesResponse, this, "onFindGamesResponse", EsFindGamesResponse.class);
        try {
            Server server = new Server("server1");
            String currentIP = "127.0.0.1";
            try {
                currentIP = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                System.out.println("Unknown Host");
                e.printStackTrace();
                currentIP = "127.0.0.1";
            }
            AvailableConnection conn1 = new AvailableConnection(currentIP, 9899, TransportType.BinaryTCP);
            AvailableConnection conn2 = new AvailableConnection(currentIP, 9899, TransportType.BinaryHTTP);
            server.addAvailableConnection(conn1);
            server.addAvailableConnection(conn2);
            es.getEngine().addServer(server);
            es.getEngine().connect(server, conn1);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void exit() {
        es.getEngine().removeEventListener(EsMessageType.ConnectionResponse, this, "onConnectionResponse");
        es.getEngine().removeEventListener(EsMessageType.LoginResponse, this, "onLoginResponse");
        es.getEngine().removeEventListener(EsMessageType.JoinRoomEvent, this, "onJoinRoomOK");
        es.getEngine().removeEventListener(EsMessageType.PluginMessageEvent, this, "onPluginMessageEvent");
        es.getEngine().removeEventListener(EsMessageType.ConnectionClosedEvent, this, "onConnectionCloseHandler");
        es.getEngine().removeEventListener(EsMessageType.FindGamesResponse, this, "onFindGamesResponse");
        es.getEngine().close();
    }

    public void onPluginMessageEvent(EsPluginMessageEvent res) {
        EsObject esMessage = res.getParameters();
        logger.error("command : " + esMessage.getString("command") + " name = " + userName);
        if (esMessage.getString(Field.Command.getName()) != null) {
            String command = esMessage.getString(Field.Command.getName());
            if ("listPlayer".equals(command)) {
                EsObject[] esArray = res.getParameters().getEsObjectArray("slot");
                for (int i = 0; i < esArray.length; i++) {
                    EsObject esObject = esArray[i];
                    if (esObject.getBoolean("empty")) {
                        testSit(i);
                        break;
                    }
                }
            }
        }
    }

    public void testSit(int position) {
        EsObject esTest = new EsObject();
        esTest.setString("command", "sit");
        esTest.setInteger("position", position);
        this.sendRequestMessage(esTest);
    }

    public void onConnectionCloseHandler(EsConnectionClosedEvent e) {
//		timer.cancel();
    }

    public void onFindGamesResponse(EsFindGamesResponse e) {
        EsServerGame[] esg = e.getGames();
        if (esg.length == 0) {

            createRoom();
        } else {
            for (int i = 0; i < esg.length; i++) {
                if (esg[i] != null) {
                    int numberSit = esg[i].getGameDetails().getInteger("numbersit");
                    if (numberSit >= 2) {
                    } else {
                        logger.error("dong 135");
                        EsJoinGameRequest esq = new EsJoinGameRequest();
                        esq.setGameId(esg[i].getId());
                        es.getEngine().send(esq);
                        return;

                    }
                }
            }
            createRoom();
        }
    }

    public void createRoom() {
        logger.error(userName + " create room");
        EsQuickJoinGameRequest qjr = new EsQuickJoinGameRequest();
        qjr.setCreateOnly(true);
        qjr.setGameType(pluginName);
        qjr.setZoneName(zoneName);
        EsObject gameDetail = new EsObject();
        gameDetail.setString("lobby", lobbyName);
        String tmpRoomName = ROOMNAME_ARRAY[(int) Math.floor(Math.random() * ROOMNAME_ARRAY.length)];
        gameDetail.setString("description", tmpRoomName);
        qjr.setGameDetails(gameDetail);
        es.getEngine().send(qjr);

    }

    public void onConnectionResponse(EsConnectionResponse e) {
        logger.debug("connect success");
        if (e.isSuccessful()) {
            EsLoginRequest lr = new EsLoginRequest();
            lr.setUserName(userName);
            es.getEngine().send(lr);
        }
    }

    public void findGame() {
        logger.error("find game");
        EsFindGamesRequest fgr = new EsFindGamesRequest();
        EsSearchCriteria criteria = new EsSearchCriteria();
        criteria.setGameType(pluginName); //DemLaPlugin
        EsObject gameDetail = new EsObject();
        gameDetail.setString("lobby", lobbyName);
        criteria.setGameDetails(gameDetail);
        fgr.setSearchCriteria(criteria);
        es.getEngine().send(fgr);
    }

    public void onLoginResponse(EsLoginResponse e) {
        if (e.isSuccessful()) {
            findGame();
            logger.error("login success");
        }
    }

    public void getPlayerList() {
        EsObject esTest = new EsObject();
        esTest.setString("command", "getPlayerList");
        this.sendRequestMessage(esTest);
    }

    public void sendRequestMessage(EsObject eMessage) {
        EsPluginRequest request = new EsPluginRequest();
        request.setParameters(eMessage);
        request.setMessageType(EsMessageType.PluginRequest);
        request.setPluginName("ChanPlugin");
        request.setZoneId(_zoneID);
        request.setRoomId(_roomID);
        es.getEngine().send(request);
        logger.error("username = " + userName + " command = "
                + eMessage.getString("command") + " zone = " + _zoneID + " room = " + _roomID);
    }

    public void onJoinRoomOK(EsJoinRoomEvent e) {
        logger.error(userName + " join room " + e.getRoomId());
        _zoneID = e.getZoneId();
        _roomID = e.getRoomId();
        getPlayerList();
    }

    private void log(String logMessage) {
        logger.error(logMessage);
    }
}
