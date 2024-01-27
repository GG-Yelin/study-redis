package com.wangyu.study.studyredis.infra.repository.storage.redis.reentrantlock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RedisWithReentrantLock {

    private final ThreadLocal<Map<String, Integer>> lockers = new ThreadLocal<>();

    @Autowired
    private Jedis jedis;

    public boolean lock(String key) {
        Map<String, Integer> refs = currentLocks();
        Integer refCnt = refs.get(key);
        if (refCnt != null) {
            refs.put(key, refCnt + 1);
            return true;
        }
        boolean ok = this._lock(key);
        if (!ok) {
            return false;
        }
        refs.put(key, 1);
        return true;
    }

    public boolean unlock(String key) {
        Map<String, Integer> refs = currentLocks();
        Integer refCnt = refs.get(key);
        if (refCnt == null) {
            return false;
        }
        refCnt -= 1;
        if (refCnt > 0) {
            refs.put(key, refCnt);
        } else {
            refs.remove(key);
            this._unlock(key);
        }
        return true;
    }

    private Map<String, Integer> currentLocks() {
        Map<String, Integer> refs = lockers.get();
        if (refs != null) {
            return refs;
        }
        lockers.set(new HashMap<>());
        return lockers.get();
    }

    /** set [key] "" nx ex 5 */
    private boolean _lock(String key) {
        SetParams setParams = new SetParams();
        setParams.nx().ex(5L);
        return jedis.set(key, "", setParams) != null;
    }

    private void _unlock(String key) {
        jedis.del(key);
    }

}


