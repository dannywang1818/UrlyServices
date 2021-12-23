package com.urly.urlyservices.controller;

import com.urly.urlyservices.exception.UrlValidationException;
import com.urly.urlyservices.service.LongToShortService;
import com.urly.urlyservices.vo.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/url")
public class LongToShortController {

    @Autowired
    private LongToShortService longToShortService;

    @PostMapping("/shorten")
    public Url shorten(@RequestBody Url longUrl) {
        Url shortUrl = longToShortService.longToShort(longUrl.getUrl());
        if(shortUrl == null) {
            log.error("Threw UrlValidation Exception: Invalid Long Url");
            throw new UrlValidationException(longUrl.getUrl());
        }
        return shortUrl;
    }
}
