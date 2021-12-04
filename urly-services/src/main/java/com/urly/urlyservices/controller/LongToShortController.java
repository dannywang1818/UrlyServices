package com.urly.urlyservices.controller;

import com.urly.urlyservices.service.LongToShortService;
import com.urly.urlyservices.vo.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
public class LongToShortController {

    @Autowired
    private LongToShortService longToShortService;

    @PostMapping("/shorten")
    public Url shorten(@RequestBody Url longUrl) {
        return longToShortService.longToShort(longUrl.getUrl());
    }
}
