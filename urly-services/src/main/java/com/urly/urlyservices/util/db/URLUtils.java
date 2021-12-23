package com.urly.urlyservices.util.db;

import java.net.URL;

public class URLUtils {
    public static boolean isValidLongUrl(String longUrl) {
        if(longUrl.startsWith("http://localhost")) {
            return false;
        }

        try{
            new URL(longUrl).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
