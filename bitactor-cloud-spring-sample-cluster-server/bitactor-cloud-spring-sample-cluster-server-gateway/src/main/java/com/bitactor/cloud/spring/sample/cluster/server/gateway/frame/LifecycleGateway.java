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
import com.bitactor.cloud.spring.sample.common.enums.LogoutType;
import com.bitactor.framework.cloud.spring.controller.extension.ConnectorChannelHandler;
import com.bitactor.framework.cloud.spring.core.annotation.lifecycle.*;
import com.bitactor.framework.core.net.api.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author WXH
 */
@Slf4j
@Lifecycle(10)
public class LifecycleGateway {
    @Autowired
    private ConnectService connectService;
    @Autowired
    private ConnectorChannelHandler connectorChannelHandler;

    @StartBefore
    public void beforeStartUp() {
        log.warn("GatewayLifecycle do method [ beforeStartUp ]");
    }

    @StartAfter
    public void afterStartUp() {
        log.warn("GatewayLifecycle do method [ afterStartUp ]");
    }

    @ShutDownBefore
    public void beforeStop() {
        log.warn("GatewayLifecycle do method [ beforeStop ]");
        for (Channel channel : connectorChannelHandler.getConnectorChannels()) {
            if (channel == null) {
                return;
            }
            connectService.disConnectOpt(channel, LogoutType.CLOSE_SERVER);
        }
    }

    @ShutDownAfter
    public void afterStop() {
        log.warn("GatewayLifecycle do method [ afterStop ]");
    }
}
