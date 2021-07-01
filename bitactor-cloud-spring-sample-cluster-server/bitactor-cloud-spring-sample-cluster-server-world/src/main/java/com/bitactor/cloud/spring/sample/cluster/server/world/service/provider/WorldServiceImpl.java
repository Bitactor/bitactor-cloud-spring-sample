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

package com.bitactor.cloud.spring.sample.cluster.server.world.service.provider;


import com.bitactor.cloud.spring.sample.common.enums.LogoutType;
import com.bitactor.cloud.spring.sample.common.player.PlayerManager;
import com.bitactor.cloud.spring.sample.common.player.bean.NetPlayer;
import com.bitactor.cloud.spring.sample.common.service.world.provider.WorldService;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.rpc.annotation.ServiceRPC;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author WXH
 */
@Slf4j
@ServiceRPC
public class WorldServiceImpl implements WorldService {

    @Resource
    private PlayerManager playerManager;


    @Override
    public void onChannelClosedEvent(ClientNetSession session, LogoutType logoutType) {
        boolean isFindPlayer = false;
        try {
            NetPlayer player = playerManager.get(session.typeUid());
            player.logout(logoutType);
            isFindPlayer = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("Player uid:{},logout by :{},local online size:{} has player isFindPlayer: {}", session.getUid(), logoutType, playerManager.onlineSize(), isFindPlayer);
        }
    }
}
