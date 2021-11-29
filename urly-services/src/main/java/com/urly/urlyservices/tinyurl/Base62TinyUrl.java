package com.urly.urlyservices.tinyurl;

public class Base62TinyUrl {

    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static int toBase62(char c){
        if(c >= '0' && c <= '9') {
            return c - '0';
        }
        if(c >= 'a' && c <= 'z') {
            return c - 'z' + 10;
        }
        return c - 'A' + 36;
    }

    public static long shortUrlToId(String shortUrl){
        long id = 0;
        for(int i = 0; i < shortUrl.length(); i++) {
            id = id * 62 + toBase62(shortUrl.charAt(i));
        }
        return id;
    }

    public static String generate(long id, int length){
        String shortUrl = "";
        while (id > 0) {
            shortUrl = CHARS.charAt((int) id % 62) + shortUrl;
            id /= 62;
        }

        while (shortUrl.length() < length) {
            shortUrl = "0" + shortUrl;
        }

        return shortUrl;
    }
}
