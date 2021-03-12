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

package com.bitactor.cloud.spring.sample.client.manager;

import com.bitactor.cloud.spring.sample.client.bean.AccountInfo;
import com.bitactor.cloud.spring.sample.client.config.AccountConfig;
import com.bitactor.cloud.spring.sample.client.service.PlayerReqService;
import com.bitactor.cloud.spring.sample.msg.proto.auth.PlatformEnum;
import com.bitactor.framework.cloud.spring.boot.client.BitactorClientProperties;
import com.bitactor.framework.cloud.spring.boot.client.config.SpringClientConfig;
import com.bitactor.framework.cloud.spring.boot.client.extension.ClientManager;
import com.bitactor.framework.cloud.spring.boot.client.net.ClientChannelManager;
import com.bitactor.framework.core.config.UrlProperties;
import com.bitactor.framework.core.net.netty.client.NettyModeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WXH
 */
@Slf4j
@Component
public class PlayerClientManager implements ClientManager<PlayerClient, String> {

    private final ConcurrentHashMap<String, PlayerClient> uidPlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PlayerClient> accountPlayers = new ConcurrentHashMap<>();
    private AtomicInteger index = new AtomicInteger(0);
    @Autowired
    private PlayerReqService playerReqService;
    @Autowired
    private AccountConfig accountConfig;
    @Autowired
    private BitactorClientProperties bitactorClientProperties;

    @Override
    public PlayerClient get(String uid) {
        return uidPlayers.get(uid);
    }

    public PlayerClient getByAccount(String account) {
        return accountPlayers.get(account);
    }

    @Override
    public void add(PlayerClient playerClient) {
        uidPlayers.put(playerClient.getUid(), playerClient);
        uidPlayers.put(playerClient.getAccount(), playerClient);
    }

    @Override
    public PlayerClient remove(String uid) {
        PlayerClient client = uidPlayers.remove(uid);
        if (Objects.isNull(client)) {
            return client;
        }
        return accountPlayers.remove(client.getAccount());
    }


    @Override
    public Collection<PlayerClient> all() {
        return uidPlayers.values();
    }

    @Override
    public PlayerClient buildNext() {
        AccountInfo accountInfo = accountConfig.getAccounts().get(index.getAndIncrement());
        log.info("buildNext account : {}", accountInfo);
        return new PlayerClient(accountInfo.getAccount(), accountInfo.getPassword(), PlatformEnum.TEST, playerReqService);
    }

    public PlayerClient login(String account, String pwd) throws Throwable {
        if (accountPlayers.containsKey(account)) {
            return null;
        }
        SpringClientConfig clientConfig = bitactorClientProperties.getClient();
        UrlProperties urlProperties = clientConfig.toUrl();
        PlayerClient player = new PlayerClient(account, pwd, PlatformEnum.TEST, playerReqService);
        NettyModeClient client = new NettyModeClient(new ClientChannelManager(player, this), urlProperties);
        player.setClient(client);
        client.threadStart().sync();
        player.init();
        this.add(player);
        return player;
    }

    @Override
    public int startCount() {
        return accountConfig.getAccounts().size();
    }
}
