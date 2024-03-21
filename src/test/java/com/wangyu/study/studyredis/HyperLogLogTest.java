package com.wangyu.study.studyredis;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@Ignore
@SpringBootTest
public class HyperLogLogTest {

    @Autowired
    private Jedis jedis;

    private static final int REAL_TOTAL = 100000;

    @Test
    public void testHyperLogLog() {
        String key = "user-count";
        for (int i = 0; i < REAL_TOTAL; i++) {
            jedis.pfadd(key, "user_" + i);
        }
        long total = jedis.pfcount(key);
        System.out.println("总数：" + total);
        System.out.println("误差：" + ((double) Math.abs(REAL_TOTAL - total)) / REAL_TOTAL * 100 + "%");
    }

}
