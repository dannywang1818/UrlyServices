package com.urly.urlyservices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class SequenceIdService {

    private static final String SEQUENCE_ID = "Sequence_ID";

    private static final String GLOBAL_SEQUENCE_ID = "Global_Sequence_ID";

    @Autowired
    @Qualifier("counterScript")
    private RedisScript<Long> redisCounterScript;

    private RedisAtomicLong entityIdCounter;

    private final RedisTemplate<String, Long> sequenceIdRedisTemplate;

    @Autowired
    public SequenceIdService(RedisTemplate<String, Long> sequenceIdRedisTemplate) {
        this.sequenceIdRedisTemplate = sequenceIdRedisTemplate;
    }

    @PostConstruct
    public void setUp() {
        entityIdCounter = new RedisAtomicLong(SEQUENCE_ID, sequenceIdRedisTemplate.getConnectionFactory());
    }

    public long getNextSequenceIdByAtomic() {
        long increment = entityIdCounter.getAndIncrement();
        sequenceIdRedisTemplate.getConnectionFactory().getConnection().bgSave();
        return increment;
    }

    public long getNextSequenceIdByLua() {
        long sequenceId = this.sequenceIdRedisTemplate.execute(redisCounterScript, List.of(GLOBAL_SEQUENCE_ID));
        sequenceIdRedisTemplate.getConnectionFactory().getConnection().bgSave();
        return sequenceId;
    }

}
