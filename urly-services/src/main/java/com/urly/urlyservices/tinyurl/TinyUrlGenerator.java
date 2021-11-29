package com.urly.urlyservices.tinyurl;

import org.springframework.stereotype.Component;

@Component
public class TinyUrlGenerator {

    static private final int DEFAULT_LENGTH = 6;

    public String generate() {
        return RandomTinyUrl.generate(DEFAULT_LENGTH);
    }

    public String generate(long id) {
        return Base62TinyUrl.generate(id, DEFAULT_LENGTH);
    }

    public long shortUrlToId(String shortUrl) {
        return Base62TinyUrl.shortUrlToId(shortUrl);
    }
}
