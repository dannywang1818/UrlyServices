package com.urly.urlyservices.service;

import com.urly.urlyservices.db.entity.LongToSequenceId;
import com.urly.urlyservices.db.entity.LongToShort;
import com.urly.urlyservices.db.repository.LongToSequenceIdRepository;
import com.urly.urlyservices.db.repository.LongToShortRepository;
import com.urly.urlyservices.tinyurl.TinyUrlGenerator;
import com.urly.urlyservices.util.db.URLUtils;
import com.urly.urlyservices.vo.service.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class LongToShortService {

    private final String shortUrlPrefix = "http://urly/";
    private final SequenceIdService sequenceIdService;
    private final RedisService redisService;
    private final TinyUrlGenerator tinyUrlGenerator;
    private final LongToShortRepository longToShortRepository;
    private final LongToSequenceIdRepository longToSequenceIdRepository;

    @Autowired
    public LongToShortService(
            TinyUrlGenerator tinyUrlGenerator,
            LongToShortRepository longToShortRepository,
            LongToSequenceIdRepository longToSequenceIdRepository,
            SequenceIdService sequenceIdService,
            RedisService redisService
    ) {
        this.tinyUrlGenerator = tinyUrlGenerator;
        this.longToShortRepository = longToShortRepository;
        this.longToSequenceIdRepository = longToSequenceIdRepository;
        this.sequenceIdService = sequenceIdService;
        this.redisService = redisService;
    }

    public Url longToShort(String longUrl) {
        // validation
        if(!URLUtils.isValidLongUrl(longUrl)) {
            return null;
        }

        // check redis
        Long sequenceId = fetchValueByKey(longUrl);
        if(sequenceId != null) {
            return convertSequenceIdToShortKey(sequenceId);
        }

        // check db
        Optional<LongToSequenceId> longToSequenceIdOpt = longToSequenceIdRepository.findByLongUrl(longUrl);
        if(longToSequenceIdOpt.isPresent()) {
            return postProcessFromDB(longToSequenceIdOpt.get());
        }

        // generate new one
        long nextGlobalSequenceId = sequenceIdService.getNextSequenceIdByLua();
        Url shortUrl = convertSequenceIdToShortKey(nextGlobalSequenceId);

        // update redis & db
        redisService.set(longUrl, nextGlobalSequenceId);
        LongToShort longToShort = persistLongToShort(longUrl, shortUrl.getUrl(), nextGlobalSequenceId);

        // send kafka asyn
        //...

        return new Url(shortUrlPrefix + longToShort.getShortUrl());
    }

    public String getLongByShort(String shortUrl) {
        Optional<LongToShort> longToShortOpt = longToShortRepository.findByShortUrl(shortUrl);
        if(longToShortOpt.isPresent()) {
            return longToShortOpt.get().getLongUrl();
        }
        return null;
    }

    @Transactional
    public LongToShort persistLongToShort(String longUrl, String shortUrl, Long sequenceId) {
        LongToShort longToShort = new LongToShort();
        longToShort.setLongUrl(longUrl);
        longToShort.setShortUrl(shortUrl);
        longToShortRepository.save(longToShort);

        persistLongToSequenceId(longUrl, sequenceId);
        return longToShort;
    }

    @Transactional
    public LongToSequenceId persistLongToSequenceId(String longUrl, Long sequenceId) {
        LongToSequenceId longToSequenceId = new LongToSequenceId();
        longToSequenceId.setLongUrl(longUrl);
        longToSequenceId.setSequenceId(sequenceId);
        longToSequenceIdRepository.save(longToSequenceId);
        return longToSequenceId;
    }

    public Url postProcessFromDB(LongToSequenceId longToSequenceId) {
        String longUrl = longToSequenceId.getLongUrl();
        Long sequenceId = longToSequenceId.getSequenceId();
        redisService.set(longUrl, sequenceId);
        return convertSequenceIdToShortKey(sequenceId);
    }

    public Url convertSequenceIdToShortKey(Long sequenceId) {
        String shortUrl = tinyUrlGenerator.generate(sequenceId);
        return new Url(shortUrl);
    }

    public Long fetchValueByKey(String longUrl) {
        return (Long) redisService.get(longUrl);
    }
}
