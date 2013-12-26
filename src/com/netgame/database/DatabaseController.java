package com.netgame.database;

import com.duduto.chan.model.PlayerBean;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main controller for all application logic
 *
 */
public class DatabaseController {

    //<editor-fold defaultstate="collapsed" desc="init">
    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);
    private final BasicDataSource dataSource;
    private final DBI dbi;

    //public static boolean usingWebService;
    public DatabaseController(Properties properties) throws Exception {
        this.dataSource = newDataSource(properties);
        this.dbi = new DBI(dataSource);
        logger.debug("DatabaseController init");
    }
    public static int percentMoney = 10;

    /**
     * OTHER POSSIBLY USEFUL METHODS
     */
    /**
     * Executes any SQL command. WARNING!!!!!!!! Only use canned SQL here,
     * because there is no binding, so this is wide open to SQL injection
     * attacks if a user provides any part of it other than integers!
     */
    public boolean executeSQL(final String sqlCommand) {
        try {
            getDbi().withHandle(new HandleCallback<Object>() {
                @Override
                public Object withHandle(Handle handle)
                        throws Exception {
                    handle.createStatement(sqlCommand).execute();
                    return null;
                }
            });
            return true;
        } catch (Exception exception) {
            //logger.error(exception.getStackTrace().toString());
            logger.error("Error attempting to execute SQL: {} ", sqlCommand);
            logger.error(exception.getMessage());
            return false;
        }
    }

    //</editor-fold>
    public PlayerBean loginDB(String username, String pass) {
        Handle handle = getDbi().open();
        try {
            logger.warn("username " + username);
            Map<String, Object> map = handle.createQuery("sql/GetPlayerLogin.sql")
                    .bind("password", pass)
                    .bind("username", username)
                    .first();
            if (map == null || map.isEmpty()) {
                logger.warn("username and password doesn't exist");
                return null;
            } else {
                
                PlayerBean player = new PlayerBean();
                player.setUsername(username);
                player.setPassword(pass);
                player.setStatus(Integer.valueOf(map.get("status").toString()));
                player.setTimeRegister(map.get("time_insert").toString());
                player.setEmail(map.get("email").toString());
                player.setpId(Integer.valueOf(map.get("player_id").toString()));
                return player;
            }
        } catch (Exception e) {
            logger.error("error login db " + e.getMessage());
            return null;
        } finally {
            closeHandle(handle);
        }
    }
    
    public void writeLogLogin(String username, int state) {
        Handle handle = getDbi().open();
        try {
            handle.createStatement("sql/WriteLogLogin.sql").bind("username", username).bind("login_state", state).execute();
        } catch (Exception e) {
            logger.error("error write log login " + e.getMessage());
        } finally {
            closeHandle(handle);
        }
    }
    
    public PlayerBean getPlayerData(String username) {
        Handle handle = getDbi().open();
        try {
            Map<String, Object> map = handle.createQuery("sql/GetPlayerData.sql")
                    .bind("username", username)
                    .first();
            
            if (map == null || map.isEmpty()) {
                logger.warn("username and password doesn't exist");
                return null;
            } else {
                
                PlayerBean player = new PlayerBean();
                player.setUsername(username);
                player.setPassword(map.get("password").toString());
                player.setStatus(Integer.valueOf(map.get("status").toString()));
                player.setTimeRegister(map.get("time_insert").toString());
                player.setEmail(map.get("email").toString());
                player.setpId(Integer.valueOf(map.get("player_id").toString()));
                return player;
            }
        } catch (Exception e) {
            logger.error("error login db " + e.getMessage());
            return null;
        } finally {
            closeHandle(handle);
        }
    }
    
    private void writeNewPlayer(String username, String password, String email, int status) {
        Handle handle = getDbi().open();
        try {
            handle.createStatement("sql/WriteNewPlayer.sql")
                    .bind("username", username)
                    .bind("password", password)
                    .bind("email", email)
                    .bind("status", status).execute();
        } catch (Exception e) {
            logger.error("error write new player " + e.getMessage());
        } finally {
            closeHandle(handle);
        }
        
    }

    //<editor-fold defaultstate="collapsed" desc="don't remove">
    public void writeLogServerStart() {
        try {
            getDbi().inTransaction(new TransactionCallback<Object>() {
                @Override
                public Object inTransaction(Handle handle, TransactionStatus status) throws Exception {
                    writeServerStartDb(handle);
                    return null;
                }
            });
        } catch (Throwable t) {
            logger.error("writeLogServerStart error: ");
            logger.error(t.getMessage());
        }
    }
    
    private void writeServerStartDb(Handle handle) {
        handle.createStatement("sql/WriteLogServerStart.sql").execute();
    }
    
    private BasicDataSource newDataSource(Properties properties) throws Exception {
        Properties databaseProperties = new Properties();
        databaseProperties.setProperty("charset", "utf8");
        databaseProperties.setProperty("characterEncoding", "utf8");
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("database.")) {
                databaseProperties.setProperty(key.substring(9), properties.getProperty(key));
            }
        }
        
        return (BasicDataSource) BasicDataSourceFactory.createDataSource(databaseProperties);
    }
    
    private void closeHandle(Handle handle) {
        if (handle != null) {
            handle.close();
        }
    }
//</editor-fold>

    /**
     * OTHER PUBLIC METHODS
     *
     */
    //<editor-fold defaultstate="collapsed" desc="dispose">
    public void dispose() throws Exception {
        logger.warn("Controller.dispose invoked");
        logger.warn("Now attempting to close the dataSource");
        dataSource.getConnection().close();
        dataSource.close();
    }
    
    public DBI getDbi() {
        return dbi;
    }
    
    public boolean intToBoolean(int flag) {
        if (flag == 0) {
            return true;
        }
        return false;
    }
    
    public int booleanToInt(boolean isDemo) {
        if (isDemo) {
            return 0;
        }
        return 1;
    }
    //</editor-fold>
}
