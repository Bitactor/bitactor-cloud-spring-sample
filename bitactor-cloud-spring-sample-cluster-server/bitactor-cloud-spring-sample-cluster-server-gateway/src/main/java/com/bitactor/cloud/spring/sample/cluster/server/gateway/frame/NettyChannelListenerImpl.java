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

package com.bitactor.cloud.spring.sample.cluster.server.gateway.frame;


import com.bitactor.cloud.spring.sample.cluster.server.gateway.provider.ConnectService;
import com.bitactor.cloud.spring.sample.common.enums.GroupType;
import com.bitactor.cloud.spring.sample.common.enums.LogoutType;
import com.bitactor.cloud.spring.sample.common.service.world.provider.WorldService;
import com.bitactor.framework.cloud.spring.boot.connector.net.ConnNettyChannel;
import com.bitactor.framework.cloud.spring.controller.extension.NettyChannelListener;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.rpc.annotation.RefRPC;
import com.bitactor.framework.core.net.api.Channel;
import com.bitactor.framework.core.net.netty.channel.NettyChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author WXH
 */
@Slf4j
@Service
public class NettyChannelListenerImpl implements NettyChannelListener, ConnectService {

    @RefRPC(GroupType.WORLD)
    private WorldService worldService;

    @Override
    public void onDestroyEvent(NettyChannel channel) {
        if (channel instanceof ConnNettyChannel) {
            disConnectOpt((ConnNettyChannel) channel, LogoutType.DISCONNECT);
        }
    }

    @Override
    public void disConnectOpt(Channel channel, LogoutType logoutType) {
        if (channel instanceof ConnNettyChannel) {
            ClientNetSession session = ((ConnNettyChannel) channel).getSession();
            String worldSid = session.getParamInstance(GroupType.WORLD);
            if (worldSid != null) {
                worldService.onChannelClosedEvent(session, logoutType);
                session.removeParam(GroupType.WORLD);
                log.info("Connect closed and notice event to world  by :{} with session:{}", logoutType, session);
            }else{
                log.info("Connect closed by :{} with session:{}", logoutType, session);
            }
        }
    }
}
