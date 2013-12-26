package com.netgame.lobby.loginLogoutHandlers;

import com.duduto.util.Console;
import com.duduto.util.MessagingHelper;
import com.duduto.chan.enums.ErrorCode;
import com.duduto.chan.model.PlayerBean;
import com.electrotank.electroserver5.extensions.BaseLoginEventHandler;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.LoginContext;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.netgame.database.DatabaseController;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.Message;
import com.duduto.util.EnUtil;

public class LoginEventHandler extends BaseLoginEventHandler {

    DatabaseController dbController;

    @Override
    public void init(EsObjectRO parameters) {
        super.init(parameters);
        dbController = (DatabaseController) getApi().acquireManagedObject("DatabaseControllerFactory", null);
    }

    @Override
    public ChainAction executeLogin(final LoginContext context) {
        // EsObjectRO esLogin = context.getRequestParameters();
        String username = context.getUserName().trim();
        getApi().getLogger().debug("u =" + username);
        String strPassword = context.getPassword().trim();
        String password = "";
        int login_state = -999;
        try {
            password = EnUtil.getMD5String(strPassword);
            PlayerBean playerBean = dbController.loginDB(username, password);

            //login faild
            if (playerBean == null) {
                EsObject es = MessagingHelper.buildErrorMessage(username, ErrorCode.LoginFaild, Message.LoginFaild.getMessage());
                context.setResponseParameters(es);
                getApi().getLogger().warn("Username or password not match.");
                login_state = -1;
                return ChainAction.Fail;
            }
            //login successfull
            this.evictUserLogged(context);

            EsObject es = playerBean.toEsObject();
            getApi().getLogger().debug("" + playerBean.toEsObject());
            context.setResponseParameters(es);
            login_state = 1;
            return ChainAction.OkAndContinue;
        } catch (Exception e) {
            getApi().getLogger().warn(e.getMessage());
            return ChainAction.Fail;
        } finally {
            dbController.writeLogLogin(username, login_state);
        }
    }

    private void evictUserLogged(LoginContext context) {
        if (getApi().isUserLoggedIn(context.getUserName())) {
            Console.debug(getApi(), "User duplicate");
            EsObject esObject = new EsObject();
            esObject.setString(Field.Action.getName(), "evict");
            esObject.setString("evictReason", "Evict user '" + context.getUserName()
                    + "' from previous session 'cause duplicate login");
            getApi().evictUserFromServer(context.getUserName(), esObject);
        }
    }
}
