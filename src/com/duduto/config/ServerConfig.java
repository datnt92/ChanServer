/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Blacker
 */
public class ServerConfig {


    private static Map<String, String> _mapSysParameter;
    private static ServerConfig INSTANCE = null;
    
     public static final String max_player = "room.max_player";
    //public variable for access    
  
    private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    private ServerConfig(Map<String, String> listSysParam) {
        ServerConfig._mapSysParameter = listSysParam;
        printParam();
    }

    public static ServerConfig Ist(Map<String, String> listSysParam) {
        if (INSTANCE == null) {
            INSTANCE = new ServerConfig(listSysParam);
        }
        return INSTANCE;
    }



    public static void reloadConfig(Map<String, String> listSysParam) {
        ServerConfig._mapSysParameter = listSysParam;
        logger.warn("game config is reloaded...");
        //GameConfig.INSTANCE.showConfig();
    }

    public static String get(String key) {
        if (_mapSysParameter.containsKey(key)) {
            return _mapSysParameter.get(key).toString();
        } else {
            return "0";
        }
    }

    private void printParam() {
        if (_mapSysParameter != null) {
            Set set = _mapSysParameter.entrySet();
            Iterator it = set.iterator();
            logger.info("=============================================");
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                logger.info(me.getKey() + ": " + me.getValue());
            }
            logger.info("=============================================");
        } else {
            logger.info("sys param null");
        }
    }
}
