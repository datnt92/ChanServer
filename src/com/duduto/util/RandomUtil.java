/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.util;

import com.duduto.util.random.Shuffle;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Dark
 */
public class RandomUtil {

    public static int getNumMaxRandom(int Min, int Max) {
        int rnd = Min + (int) (Math.random() * (double) ((Max - Min) + 1));
        return rnd;
    }

    public static int[] getArrCard() {
        List<Integer> list = new LinkedList<Integer>();
        int num = 0;
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 4; j++) {
                list.add(i);
            }
        }
        Collections.shuffle(list);
        int arrCard[] = new int[100];
        for (int i = 0; i < list.size(); i++) {
            arrCard[i] = list.get(i);
        }
        for (int it = 0; it < arrCard.length; it++) {
            int el = arrCard[it];
            int rn = (int) Math.floor(Math.random() * arrCard.length);
            arrCard[it] = arrCard[rn];
            arrCard[rn] = el;
        }
        return arrCard;
    }

    public static float[] genSpeedBlock(int blockSize, float avg) {
        float[] arrRand = new float[blockSize];
//        float avgSpeed = total / blockSize;
        float sum = 0;
        for (int i = 0; i < blockSize; i++) {
            float rdNum = getRandomNumber();
            arrRand[i] = rdNum;
            sum += rdNum;
        }
        for (int i = 0; i < blockSize; i++) {
            arrRand[i] /= sum;
            arrRand[i] *= avg * blockSize;
        }
        return arrRand;
    }

    public static float getRandomNumber() {
        return getNumMaxRandom(4.0f, 6.0f);
    }

    public static float getNumMaxRandom(float Min, float Max) {
        float rnd = Min + (float) (Math.random() * (float) ((Max - Min)));
        return rnd;
    }

    public static String RandomOrderId() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }
}
