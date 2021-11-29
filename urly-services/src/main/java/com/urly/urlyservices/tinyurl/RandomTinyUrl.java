package com.urly.urlyservices.tinyurl;

import java.util.Random;

public class RandomTinyUrl {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generate(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        while (sb.length() < length) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }

        return sb.toString();
    }
}
