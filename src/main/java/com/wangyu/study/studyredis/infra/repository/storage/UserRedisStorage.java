package com.wangyu.study.studyredis.infra.repository.storage;

import com.wangyu.study.studyredis.domain.model.User;

import java.util.Optional;

public interface UserRedisStorage {

    /** 如果key存在则保存，不存在返回false保存失败 */
    boolean saveUserForString(User user);

    boolean saveUserForHash(User user);

    Optional<User> getUserByIdForString(long id);

    Optional<User> getUserByIdForHash(long id);

    Optional<String> getUserProp(long id, String propKey);

}
