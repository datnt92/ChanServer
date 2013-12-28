package com.duduto.util.random;



public class Shuffle { 

    // swaps array elements i and j
    public static void exch(int[] a, int i, int j) {
        Integer swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // take as input an array of strings and rearrange them in random order
    public static void shuffle(int[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
        	//MTRandom rnd = new MTRandom(a);
        	MersenneTwisterFast rnd  = new MersenneTwisterFast(a);
            int r = i + (int) (rnd.nextDouble() * (N-i));   // between i and N-1
            exch(a, i, r);
        }
    }

}