package com.urly.urlyservices.controller;

import com.urly.urlyservices.exception.UrlValidationException;
import com.urly.urlyservices.service.LongToShortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("/redirect")
public class ShortToLongController {

    @Autowired
    private LongToShortService longToShortService;

    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse httpServletResponse) {
        String longUrl = longToShortService.getLongByShort(shortUrl);
        if (longUrl == null) {
            log.error("Not Found Long Url");
            throw new NoSuchElementException("Cannot find long URL mapping to " + shortUrl);
        } else {
            httpServletResponse.setHeader("Location", longUrl);
            httpServletResponse.setStatus(HttpStatus.FOUND.value());
        }
    }
}
