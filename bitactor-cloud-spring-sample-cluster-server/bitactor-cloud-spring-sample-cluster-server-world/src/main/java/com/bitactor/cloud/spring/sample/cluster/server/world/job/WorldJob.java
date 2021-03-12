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

package com.bitactor.cloud.spring.sample.cluster.server.world.job;

import com.bitactor.cloud.spring.sample.common.player.bean.NetPlayer;
import com.bitactor.framework.cloud.spring.controller.bean.conn.ConnectManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author WXH
 */
@Slf4j
@Component
public class WorldJob {
    @Autowired
    private ConnectManager<NetPlayer> playerManager;

    /**
     * 上一次执行完毕时间点后10秒再次执行
     * 检查网关服的连接如果在规定时间内未获授权则关闭连接
     */
    @Scheduled(fixedDelay = 500)
    private void playerUpdate() {
        Collection<NetPlayer> players = playerManager.all();
        for (NetPlayer player : players) {
            player.onUpdate();
        }
    }

    /**
     * 上一次执行完毕时间点后10秒再次执行
     * 检查网关服的连接如果在规定时间内未获授权则关闭连接
     */
    @Scheduled(fixedDelay = 120000)
    private void onlineSize() {
        log.info("World online number {}", playerManager.onlineSize());
    }
}
