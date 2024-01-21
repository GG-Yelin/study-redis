package com.wangyu.study.studyredis.infra.repository.storage.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseRedisStorage {

    @Autowired
    protected StringRedisTemplate redisTemplate;

}
