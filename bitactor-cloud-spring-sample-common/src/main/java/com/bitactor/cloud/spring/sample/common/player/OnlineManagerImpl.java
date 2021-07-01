/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bitactor.cloud.spring.sample.common.player;


import com.bitactor.cloud.spring.sample.common.consts.GameConstants;
import com.bitactor.cloud.spring.sample.common.consts.redis.PlayerRedisKey;
import com.bitactor.cloud.spring.sample.common.redis.RedisCache;
import com.bitactor.cloud.spring.sample.common.utils.RedisKeyUtil;
import com.bitactor.framework.cloud.spring.controller.bean.online.OnlineInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author WXH
 */
@Slf4j
@Service
public class OnlineManagerImpl implements OnlinePlayerManager {

    // 在线缓存时间 需要通过响应的 触发去更新这个时间，否则可能出现丢失在线缓存
    private static final int ONLINE_TIMEOUT = 2;
    private static final TimeUnit TIME_UNIT = TimeUnit.HOURS;


    @Autowired
    public RedisCache redisCache;
    @Autowired
    public RedissonClient redissonClient;

    private String getRedisKey(String id) {
        return RedisKeyUtil.buildPrefixRedisKey(PlayerRedisKey.PLAYER_ONLINE_INFO, id);
    }

    private String getRedisLockKey(String id) {
        return RedisKeyUtil.buildPrefixRedisKey(GameConstants.LOCK, PlayerRedisKey.PLAYER_ONLINE_INFO, id);
    }

    @Override
    public OnlineInfo<String> add(OnlineInfo<String> onlineInfo) {
        OnlineInfo<String> oldVal = null;
        String lockKey = getRedisLockKey(onlineInfo.getId());
        String key = getRedisKey(onlineInfo.getId());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(GameConstants.LOCK_TIME_OUT, GameConstants.LOCK_TIME_OUT, GameConstants.LOCK_TIME_OUT_UNIT);
            oldVal = redisCache.getCacheObject(key);
            redisCache.setCacheObject(key, onlineInfo, ONLINE_TIMEOUT, TIME_UNIT);
            return oldVal;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public OnlineInfo<String> update(OnlineInfo<String> onlineInfo) {
        OnlineInfo<String> oldVal = null;
        String lockKey = getRedisLockKey(onlineInfo.getId());
        String key = getRedisKey(onlineInfo.getId());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(GameConstants.LOCK_TIME_OUT, GameConstants.LOCK_TIME_OUT, GameConstants.LOCK_TIME_OUT_UNIT);
            oldVal = redisCache.getCacheObject(key);
            if (oldVal == null) {
                return null;
            }
            if (onlineInfo.getSessionId().equals(oldVal.getSessionId())) {
                redisCache.setCacheObject(key, onlineInfo, ONLINE_TIMEOUT, TIME_UNIT);
            }
            return oldVal;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public OnlineInfo<String> get(String id) {
        return redisCache.getCacheObject(getRedisKey(id));
    }

    @Override
    public OnlineInfo<String> remove(OnlineInfo<String> onlineInfo) {
        OnlineInfo<String> oldVal = null;
        String lockKey = getRedisLockKey(onlineInfo.getId());
        String key = getRedisKey(onlineInfo.getId());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(GameConstants.LOCK_TIME_OUT, GameConstants.LOCK_TIME_OUT, GameConstants.LOCK_TIME_OUT_UNIT);
            oldVal = redisCache.getCacheObject(key);
            if (oldVal == null) {
                return null;
            }
            if (onlineInfo.getSessionId().equals(oldVal.getSessionId())) {
                redisCache.deleteObject(key);
            }
            return oldVal;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }
}
