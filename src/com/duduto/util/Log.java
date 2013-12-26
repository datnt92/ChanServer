/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * log util only use in j2me
 *
 * @author PhongTn
 * @since Nov 20, 2012 3:26:56 PM
 * @version 1.0
 */
public class Log {

    private static boolean showDebug = true;

    public static void debug(final String message) {
        if (showDebug) {
            System.out.println(buildMessage(message));
        }
    }

    public static void debug(Throwable ex) {
        if (showDebug) {
            System.out.println(buildMessage(ex.getMessage()));
        }
    }

    public static void setShowDebug(final boolean show) {
        showDebug = show;
    }

    private static String buildMessage(String str) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        String current_time = format.format(new Date());
        StringBuilder buffer = new StringBuilder("[");
        buffer.append(current_time).append("]");
        buffer.append(" ").append("DEBUG").append(" ");
        buffer.append(str);
        return buffer.toString();
    }
}
