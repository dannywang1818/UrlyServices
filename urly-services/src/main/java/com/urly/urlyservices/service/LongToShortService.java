package com.urly.urlyservices.service;

import com.urly.urlyservices.tinyurl.TinyUrlGenerator;
import com.urly.urlyservices.util.UrlUtils;
import com.urly.urlyservices.vo.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LongToShortService {

    private final String shortUrlPrefix = "http://urly/";
    private final TinyUrlGenerator tinyUrlGenerator;

    @Autowired
    public LongToShortService(TinyUrlGenerator tinyUrlGenerator) {
        this.tinyUrlGenerator = tinyUrlGenerator;
    }

    public Url longToShort(String longUrl) {
        if(!UrlUtils.isValidLongUrl(longUrl)) {
            log.error("Invalid Long Url");
            return null;
        }
        return new Url(shortUrlPrefix + tinyUrlGenerator.generate());
    }
}
