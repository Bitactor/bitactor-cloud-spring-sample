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

package com.bitactor.cloud.spring.sample.cluster.server.gateway.job;


import com.bitactor.cloud.spring.sample.common.consts.GameConstants;
import com.bitactor.cloud.spring.sample.common.platform.AuthInfo;
import com.bitactor.framework.cloud.spring.boot.connector.net.ConnNettyChannel;
import com.bitactor.framework.cloud.spring.controller.extension.ConnectorChannelHandler;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.core.constant.NetConstants;
import com.bitactor.framework.core.net.api.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author WXH
 */
@Component
@Slf4j
public class ConnectJob {

    @Autowired
    private ConnectorChannelHandler connectorChannelHandler;

    /**
     * 上一次执行完毕时间点后10秒再次执行
     * 检查网关服的连接如果在规定时间内未获授权则关闭连接
     */
    @Scheduled(fixedDelay = 10000)
    private void checkAndCloseNotAuth() {
        Collection<Channel> channels = connectorChannelHandler.getConnectorChannels();
        for (Channel channel : channels) {
            if (channel instanceof ConnNettyChannel) {
                long connectTime = channel.getAttrVal(NetConstants.CONNECT_TIME, 0L);
                ClientNetSession session = ((ConnNettyChannel) channel).getSession();
                AuthInfo auth = session.getParamInstance(GameConstants.SESSION_AUTH_KEY);
                if (auth == null
                        && (System.currentTimeMillis() - connectTime) > GameConstants.NOT_AUTH_FORCE_KICK_TIME_MS) {
                    channel.close();
                    log.info("Close unauthorized connections,session：{}", session);
                }
            }
        }
    }

    /**
     * 当前承载连接数
     */
    @Scheduled(fixedDelay = 120000)
    private void printConnectSize() {
        Collection<Channel> channels = connectorChannelHandler.getConnectorChannels();
        log.info("Number of client connections：{}", channels.size());
    }

}
