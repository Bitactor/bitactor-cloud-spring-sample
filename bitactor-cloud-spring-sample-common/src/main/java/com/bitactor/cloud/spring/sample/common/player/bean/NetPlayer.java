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

package com.bitactor.cloud.spring.sample.common.player.bean;


import com.bitactor.cloud.spring.sample.common.bean.Role;
import com.bitactor.cloud.spring.sample.common.consts.GameConstants;
import com.bitactor.cloud.spring.sample.common.consts.PlayerCacheConstants;
import com.bitactor.cloud.spring.sample.common.dao.RoleDao;
import com.bitactor.cloud.spring.sample.common.enums.LogoutType;
import com.bitactor.cloud.spring.sample.common.player.OnlinePlayerManager;
import com.bitactor.framework.cloud.spring.controller.bean.conn.ConnectManager;
import com.bitactor.framework.cloud.spring.controller.bean.online.OnlineManager;
import com.bitactor.framework.cloud.spring.controller.sender.MsgSender;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.controller.session.SessionId;
import com.bitactor.framework.cloud.spring.core.utils.SpringUtils;
import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据存储玩家类
 *
 * @author WXH
 */
public class NetPlayer extends DBPlayer {
    private static final Logger logger = LoggerFactory.getLogger(NetPlayer.class);
    private ReentrantLock lock = new ReentrantLock(true);
    private ClientNetSession session;
    /**
     * 需要推送的消息
     */
    private ArrayList<GeneratedMessageV3> msgs = new ArrayList<GeneratedMessageV3>();
    private HashMap<Integer, Object> caches = new HashMap<>();

    public NetPlayer(Role role, ClientNetSession session) {
        super(role);
        this.session = session;
    }

    public ClientNetSession getSession() {
        return session;
    }

    @Override
    public void setSession(ClientNetSession session) {
        this.session = session;
    }

    private <P> P getCache(int key, P def) {
        P v = (P) caches.get(key);
        return v == null ? def : v;
    }

    private void putCache(int key, Object v) {
        caches.put(key, v);
    }

    public SessionId getSessionId() {
        return getSession().getSessionId();
    }

    @Override
    public void tryLock() throws Throwable {
        // 加锁
        //lock.tryLock(GameConstants.LOCK_TIME_OUT, GameConstants.LOCK_TIME_OUT_UNIT);
        //System.out.println("线程: " + Thread.currentThread().getName() + " 加锁：" + getUid());
    }

    @Override
    public void doFinishEvent() {
        super.doFinishEvent();
        // 通知客户端
        doUpdateToClientEvent();
    }

    /**
     * 更新玩家变更数据属性到客户端
     */
    public void doUpdateToClientEvent() {
        for (GeneratedMessageV3 msg : msgs) {
            MsgSender.sendMsg(getSessionId(), msg);
            logger.debug("Push the uid {} player to update the data", getUid());
        }
        msgs.clear();
    }

    @Override
    public void unLock() {
        // 释放锁
        //lock.unlock();
        //System.out.println("线程: " + Thread.currentThread().getName() + " 解锁：" + getUid());
    }

    /**
     * 更新玩家的驱动
     */
    public void onUpdate() {
        updateOnline();
    }

    private void updateOnline() {
        long last = getCache(PlayerCacheConstants.LAST_UPDATE_ONLINE_TIME, 0L);
        long now = System.currentTimeMillis();
        if (now - last > GameConstants.UPDATE_ONLINE_INTERVAL_MS) {
            SpringUtils.getBean(OnlinePlayerManager.class).update(getSession().build());
            putCache(PlayerCacheConstants.LAST_UPDATE_ONLINE_TIME, now);
        }
    }

    /**
     * 离线
     *
     * @param logoutType
     */
    public void logout(LogoutType logoutType) {
        SpringUtils.getBean(RoleDao.class).saveOrUpdate(getRole());
        SpringUtils.getBean(ConnectManager.class).remove(getUid());
        SpringUtils.getBean(OnlineManager.class).remove(getSession().build());
    }
}
