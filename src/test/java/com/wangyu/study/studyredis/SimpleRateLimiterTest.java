package com.wangyu.study.studyredis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
public class SimpleRateLimiterTest {

    @Autowired
    private Jedis jedis;

    public boolean isActionAllowed(String userId, String actionKey, int periodSecond, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long now = System.currentTimeMillis();
        jedis.zadd(key, now, String.valueOf(now));
        jedis.zremrangeByScore(key, 0, now - periodSecond * 1000L);
        jedis.expire(key, periodSecond + 1);
        return jedis.zcard(key) <= maxCount;
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


}
