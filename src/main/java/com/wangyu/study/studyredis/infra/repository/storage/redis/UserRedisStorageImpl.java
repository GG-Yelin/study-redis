package com.wangyu.study.studyredis.infra.repository.storage.redis;

import com.alibaba.fastjson.JSON;
import com.wangyu.study.studyredis.domain.model.User;
import com.wangyu.study.studyredis.infra.repository.storage.UserRedisStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
@Slf4j
public class UserRedisStorageImpl extends BaseRedisStorage implements UserRedisStorage {

    @Override
    public boolean saveUserForString(User user) {
        String key = String.valueOf(user.getId());
        String value = JSON.toJSONString(user);
        Boolean res = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (res == null) {
            throw new RuntimeException("redis连接异常");
        }
        return res;
    }

    @Override
    public boolean saveUserForHash(User user) {
        String key = String.valueOf(user.getId());
        Map<String, String> value = toMap(user);
        boolean res = true;
        for (Map.Entry<String, String> entry : value.entrySet()) {
            boolean hashRes = redisTemplate.opsForHash().putIfAbsent(key, entry.getKey(), entry.getValue());
            res = res && hashRes;
        }
        return res;
    }

    @Override
    public Optional<User> getUserByIdForString(long id) {
        String key = String.valueOf(id);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        User user = JSON.parseObject(value, User.class);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> getUserByIdForHash(long id) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(String.valueOf(id));
        if (map.isEmpty()) {
            return Optional.empty();
        }
        User user = new User();
        user.setId(Long.parseLong((String) map.get("id")));
        user.setName((String) map.get("name"));
        user.setAge(Integer.parseInt((String) map.get("age")));
        user.setMark((String) map.get("mark"));
        return Optional.of(user);
    }

    @Override
    public Optional<String> getUserProp(long id, String propKey) {
        Object hashValue = redisTemplate.opsForHash().get(String.valueOf(id), propKey);
        return Optional.ofNullable(String.valueOf(hashValue));
    }

    private Map<String, String> toMap(User user) {
        Map<String, String> map = new HashMap<>();
        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String fieldName= field.getName();
                String filedValue = String.valueOf(field.get(user));
                map.put(fieldName, filedValue);
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return map;
    }

}
