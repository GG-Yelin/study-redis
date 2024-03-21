package com.wangyu.study.studyredis.ratelimiter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 漏斗限流
 */
@Data
@AllArgsConstructor
public class FunnelRateLimiter {

    /** 总容量 */
    private int capacity;

    /** 漏流速率（容量/s） */
    private int leakRate;

    /** 剩余容量 */
    private int leftCapacity;

    /** 上次漏流时间戳（ms） */
    private long lastLeakTM;

    public FunnelRateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.leftCapacity = capacity;
        this.lastLeakTM = System.currentTimeMillis();
    }

    /**
     * 用户行为到达：
     *            1. 先腾出空间
     *            2. 记录用户行为占用空间
     *            3. 看剩余空间是否够用
     * @param quota     用户行为占用空间
     * @return
     */
    public boolean isActionAllowed(int quota) {
        leaking();
        return watering(quota) ;
    }

    /**
     * 腾出空间
     */
    private void leaking() {
        long now = System.currentTimeMillis();
        long deltaTM = (now - lastLeakTM) / 1000;
        int deltaQuota = (int) (leakRate * deltaTM);
        this.lastLeakTM = now;
        this.leftCapacity += deltaQuota;
        /** 剩余空间不能超过最大容量 */
        if (this.leftCapacity > this.capacity) {
            this.leftCapacity = this.capacity;
        }
    }

    /**
     * 记录用户行为占用空间
     */
    private boolean watering(int quota) {
        if (this.leftCapacity >= quota) {
            this.leftCapacity -= quota;
            return true;
        }
        return false;
    }

}
