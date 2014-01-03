/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.util;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Blacker
 */
public class ArrayUtil {

    public static int[] arrayIntegerToInt(Integer[] a) {
        int b[] = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            Integer integer = a[i];
            b[i] = a[i];
        }
        return b;
    }

    public static List<Integer> arrayintToInteger(int[] a) {
        List<Integer> lst = new LinkedList<Integer>();
        for (int i = 0; i < a.length; i++) {
            int j = a[i];
            lst.add(j);
        }
        return lst;
    }
    
}
