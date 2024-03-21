package com.wangyu.study.studyredis.ratelimiter;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Ignore
@SpringBootTest
public class FunnelRateLimiterTest {

    /** 使用本地缓存代替redis，可使用redis的hash结构存储FunnelRateLimiter对象 */
    private Map<String, FunnelRateLimiter> funnels = new HashMap<>();

    /** 初始容量：最初最多有点击次数 */
    private static final int INIT_CAPACITY = 10;

    /** 每秒允许点击两次 */
    private static final int LEAK_RATE = 2;

    /**
     * 理论上：最初10次行为允许，后续不允许；休息2s后，允许4次行为
     */
    @Test
    public void testFunnelRateLimiter() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            System.out.println(isActionAllowed("zhangsan", "click", INIT_CAPACITY, LEAK_RATE));
        }
        Thread.sleep(2000);
        for (int i = 0; i < 20; i++) {
            System.out.println(isActionAllowed("zhangsan", "click", INIT_CAPACITY, LEAK_RATE));
        }
    }

    private boolean isActionAllowed(String userId, String action, int capacity, int leakRate) {
        String key = String.format("fist:%s:%s", userId, action);
        FunnelRateLimiter funnel = funnels.get(key);
        if (funnel == null) {
            funnel = new FunnelRateLimiter(capacity, leakRate);
            funnels.put(key, funnel);
        }
        return funnel.isActionAllowed(1);
    }
}
