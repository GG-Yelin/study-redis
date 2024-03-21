package com.wangyu.study.studyredis;

import com.wangyu.study.studyredis.infra.repository.storage.redis.delayqueue.RedisDelayingQueue;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
@Ignore
public class RedisDelayQueueTest {

    @Autowired
    private Jedis jedis;

    @Test
    public void testDelayQueue() {
        RedisDelayingQueue<String> delayQueue = new RedisDelayingQueue<>("queue-demo", jedis);

        // producer
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                delayQueue.delay("task_" + i);
            }
        });

        // consumer
        Thread consumer = new Thread(delayQueue::loop);

        consumer.start();
        producer.start();

        /** 如果没有后面这段，由于主线程已经结束，所以不会有输出。 */

        try {
            // 等待线程终止。主线程阻塞，直到producer线程执行完毕，再继续执行。
            // 用于协调多个线程的执行顺序
            producer.join();
            Thread.sleep(1000);
            // 中断consumer线程，在consumer线程之后while循环判断是否产生中断
            consumer.interrupt();
            // 等待consumer线程执行完成再结束主程序
            consumer.join();
        } catch (InterruptedException ignored) {

        }
    }

}
