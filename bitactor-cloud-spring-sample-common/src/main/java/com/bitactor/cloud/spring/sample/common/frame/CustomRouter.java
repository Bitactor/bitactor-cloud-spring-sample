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

package com.bitactor.cloud.spring.sample.common.frame;


import com.bitactor.cloud.spring.sample.common.enums.GroupType;
import com.bitactor.cloud.spring.sample.common.exception.RouterSelectException;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.controller.session.SessionId;
import com.bitactor.framework.core.net.api.Channel;
import com.bitactor.framework.core.net.api.transport.AbstractClient;
import com.bitactor.framework.core.rpc.api.RPCRequest;
import com.bitactor.framework.core.rpc.api.router.PollingRouterAdapter;
import com.bitactor.framework.core.utils.lang.StringUtils;

import java.util.List;

/**
 * 自定义路由规则
 *
 * @author WXH
 */
public class CustomRouter extends PollingRouterAdapter {

    @Override
    public Channel routerAdapter(List<AbstractClient> clients, RPCRequest request) {
        String group = request.getGroup();
        ClientNetSession session = null;
        SessionId sessionId = null;
        for (Object arg : request.getInvocation().getArguments()) {
            if (arg instanceof ClientNetSession) {
                session = (ClientNetSession) arg;
            }
            if (arg instanceof SessionId) {
                sessionId = (SessionId) arg;
            }
        }
        // 网关服路由规则
        if (group.equals(GroupType.GATEWAY)) {
            Channel selectChannel = null;
            if (sessionId != null) {
                String assignSid = sessionId.getSid();
                for (AbstractClient client : clients) {
                    if (client.getUrl().getGroupAndId().equals(assignSid)) {
                        selectChannel = client.getChannel();
                        break;
                    }
                }
            } else {
                selectChannel = super.routerAdapter(clients, request);
            }
            return selectChannel;
        }

        // 世界服路由规则
        if (group.equals(GroupType.WORLD)) {
            Channel selectChannel = null;
            if (session != null) {
                String assignSid = session.getParamInstance(group);
                if (StringUtils.isEmpty(assignSid)) {
                    selectChannel = super.routerAdapter(clients, request);
                }
                for (AbstractClient client : clients) {
                    if (client.getUrl().getGroupAndId().equals(assignSid)) {
                        selectChannel = client.getChannel();
                        break;
                    }
                }
            } else {
                throw new RouterSelectException("The groupType: " + group + " need session info but null");
            }
            return selectChannel;
        }
        // 默认路由规则（轮训）
        return super.routerAdapter(clients, request);
    }
}
