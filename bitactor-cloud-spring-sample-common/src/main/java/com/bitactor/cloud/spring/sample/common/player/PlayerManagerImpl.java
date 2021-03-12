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

import com.bitactor.cloud.spring.sample.common.player.bean.NetPlayer;
import com.bitactor.framework.cloud.spring.controller.bean.conn.ConnectManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家管理器实现类
 *
 * @author WXH
 */
@Service
public class PlayerManagerImpl implements ConnectManager<NetPlayer> {

    private final static ConcurrentHashMap<Long, NetPlayer> onlinePlayer = new ConcurrentHashMap<Long, NetPlayer>();

    @Override
    public NetPlayer get(long uid) {
        return onlinePlayer.get(uid);
    }

    @Override
    public boolean add(NetPlayer player) {
        onlinePlayer.put(player.getUid(), (NetPlayer) player);
        return true;
    }

    @Override
    public NetPlayer remove(long uid) {
        return onlinePlayer.remove(uid);
    }

    @Override
    public int onlineSize() {
        return onlinePlayer.size();
    }

    @Override
    public Collection<NetPlayer> all() {
        return onlinePlayer.values();
    }
}
