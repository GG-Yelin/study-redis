package com.wangyu.study.studyredis.ratelimiter;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.List;

@SpringBootTest
@Ignore
public class SimpleRateLimiterTest {

    @Autowired
    private Jedis jedis;

    public boolean isActionAllowed(String userId, String actionKey, int periodSecond, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long now = System.currentTimeMillis();
        jedis.zadd(key, now, String.valueOf(now));
        jedis.zremrangeByScore(key, 0, now - periodSecond * 1000L);
        jedis.expire(key, (long) periodSecond + 1);
        return jedis.zcard(key) <= maxCount;
    }

    public boolean isActionAllowedByPipeline(String userId, String actionKey, int periodSecond, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long now = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        pipeline.zadd(key, now, String.valueOf(now));
        pipeline.zremrangeByScore(key, 0, now - periodSecond * 1000L);
        pipeline.expire(key, (long) periodSecond + 1);
        pipeline.zcard(key);
        List<Object> res = pipeline.syncAndReturnAll();
        long count = (long) res.get(3);
        return count <= maxCount;
    }

    @Test
    public void testSimpleRateLimiter() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            System.out.println(isActionAllowed("zhangsan", "CLICK", 1, 5));
        }
        Thread.sleep(1000);
        for (int i = 0; i < 20; i++) {
            System.out.println(isActionAllowed("zhangsan", "CLICK", 1, 5));
        }
    }

    @Test
    public void testSimpleRateLimiter2() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            System.out.println(isActionAllowedByPipeline("zhangsan", "CLICK", 1, 5));
        }
        Thread.sleep(1000);
        for (int i = 0; i < 20; i++) {
            System.out.println(isActionAllowedByPipeline("zhangsan", "CLICK", 1, 5));
        }
    }

}
