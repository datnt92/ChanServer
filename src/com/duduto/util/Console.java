/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.util;

import com.electrotank.electroserver5.extensions.api.EventApi;

/**
 * use for trace log to screen
 *
 * @author PhongTn
 * @since Nov 20, 2012 3:26:56 PM
 * @version 1.0
 */
public class Console {

    private static boolean showDebug = true;

    public static void debug(EventApi api, final String message) {
        if (showDebug) {
            api.getLogger().debug(message);
        }
    }

    public static void debug(EventApi api, String strClassName, Throwable ex) {
        if (showDebug) {
            api.getLogger().debug(strClassName, ex);
        }
    }

    public static void setShowDebug(final boolean show) {
        showDebug = show;
    }
}
