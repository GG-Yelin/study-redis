package com.wangyu.study.studyredis.infra.repository.storage.redis.delayqueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@Slf4j
public class RedisDelayingQueue<T> {

    private String queueKey;
    private Jedis jedis;

    public void delay(T task) {
        TaskItem<T> taskItem = new TaskItem<>();
        taskItem.setId(UUID.randomUUID().toString());
        taskItem.setTask(task);
        // 序列化
        String itemJson = JSON.toJSONString(taskItem);
        // 5s后执行
        jedis.zadd(queueKey, System.currentTimeMillis() + 5000, itemJson);
    }

    /**
     * 循环执行
     */
    public void loop() {
        while (!Thread.interrupted()) {
            // 取最早可执行的一条
            Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            // 如果没有可执行的，歇一会再继续
            if (CollectionUtils.isEmpty(values)) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            String itemJson = values.iterator().next();
            // 移除成功表示：该线程可执行，否则可能被别的线程取到了
            if (jedis.zrem(queueKey, itemJson) > 0) {
                TaskItem<T> taskItem = JSON.parseObject(itemJson, new TypeReference<TaskItem<T>>() {});
                log.info("handle task : {}", taskItem);
                this.handleTask(taskItem.getTask());
            }
        }
    }

    /**
     * 处理任务
     * @param task
     */
    private void handleTask(T task) {
        System.out.println(task);
    }

    /**
     * 任务项
     * @param <T>
     */
    @Data
    static class TaskItem<T> {
        private String id;
        private T task;
    }

}
