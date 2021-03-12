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

package com.bitactor.cloud.spring.sample.cluster.server.gateway.provider;


import com.bitactor.cloud.spring.sample.common.enums.LogoutType;
import com.bitactor.cloud.spring.sample.common.service.gateway.provider.GatewayService;
import com.bitactor.cloud.spring.sample.msg.proto.common.DisconnectEnum;
import com.bitactor.cloud.spring.sample.msg.proto.system.DisconnectNotify;
import com.bitactor.framework.cloud.spring.controller.extension.ConnectorChannelHandler;
import com.bitactor.framework.cloud.spring.controller.session.SessionId;
import com.bitactor.framework.cloud.spring.rpc.MsgSender;
import com.bitactor.framework.cloud.spring.rpc.annotation.ServiceRPC;
import com.bitactor.framework.core.net.api.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author WXH
 */
@Slf4j
@ServiceRPC
public class GatewayServiceImpl implements GatewayService {
    @Autowired
    private ConnectService connectService;

    @Autowired
    private ConnectorChannelHandler connectorChannelHandler;
    @Override
    public void forceDisconnect(SessionId sessionId, LogoutType logoutType) {
        Channel channel = connectorChannelHandler.getConnectorChannel(sessionId);
        if (channel == null) {
            return;
        }
        connectService.disConnectOpt(channel, logoutType);
        //  发送关闭连接原因
        MsgSender.sendMsgProtoBuf(sessionId, DisconnectNotify.newBuilder().setType(DisconnectEnum.OTHER_LOGIN).build());
        // 关闭连接
        channel.close();
    }
}
