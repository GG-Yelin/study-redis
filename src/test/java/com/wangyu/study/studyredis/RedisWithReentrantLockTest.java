package com.wangyu.study.studyredis;

import com.wangyu.study.studyredis.infra.repository.storage.redis.reentrantlock.RedisWithReentrantLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisWithReentrantLockTest {

    @Autowired
    private RedisWithReentrantLock reentrantLock;

    @Test
    public void testLock() {
        System.out.println(reentrantLock.lock("zhangsan"));
        System.out.println(reentrantLock.lock("zhangsan"));
        System.out.println(reentrantLock.unlock("zhangsan"));
        System.out.println(reentrantLock.unlock("zhangsan"));
    }

}
