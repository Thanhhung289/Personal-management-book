package com.group6.moneymanagementbooking.util;

import java.util.Random;

public class NumberUtils {
    private static final Random RANDOM = new Random();


    public static int getRandomInt(int bound){
        return RANDOM.nextInt(bound);
    }

    
    public static int getRandomInt(int min, int max){
        return  RANDOM.nextInt(max - min + 1) + min;
    }


}
