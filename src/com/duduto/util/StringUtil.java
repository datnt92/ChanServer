/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.util;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author PhongTn
 * @since Apr 26, 2013 1:56:46 AM
 * @version 1.0
 * @description
 */
public class StringUtil {

    public static String nvl(Object old) {
        if (old == null) {
            old = "";
        }
        return (String) old;
    }
     public static String convertToUtf8(String str) throws UnsupportedEncodingException{
            byte[] bytes =str.getBytes();
            return new String(bytes,"UTF-8");
    }
}
