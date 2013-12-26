/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.util;

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
