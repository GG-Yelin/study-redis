package com.wangyu.study.studyredis.controller;

import com.wangyu.study.studyredis.domain.model.User;
import com.wangyu.study.studyredis.infra.repository.storage.UserRedisStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/user")
public class RedisController {

    @Autowired
    private UserRedisStorage userRedisStorage;

    @PutMapping("/save-string")
    public boolean saveForString(@RequestBody User user) {
        return userRedisStorage.saveUserForString(user);
    }

    @PutMapping("/save-hash")
    public boolean saveForHash(@RequestBody User user) {
        return userRedisStorage.saveUserForHash(user);
    }

    @GetMapping("/get-string")
    public User getForString(@RequestParam(value = "id") long id) {
        Optional<User> userOpt = userRedisStorage.getUserByIdForString(id);
        return userOpt.orElse(null);
    }

    @GetMapping("/get-hash")
    public User getForHash(@RequestParam(value = "id") long id) {
        Optional<User> userOpt = userRedisStorage.getUserByIdForHash(id);
        return userOpt.orElse(null);
    }

    @GetMapping("/get/attr")
    public String getAttr(
            @RequestParam(value = "id") long id,
            @RequestParam(value = "propKey") String propKey
    ) {
        Optional<String> attrOpt = userRedisStorage.getUserProp(id, propKey);
        return attrOpt.orElse(null);
    }

}
