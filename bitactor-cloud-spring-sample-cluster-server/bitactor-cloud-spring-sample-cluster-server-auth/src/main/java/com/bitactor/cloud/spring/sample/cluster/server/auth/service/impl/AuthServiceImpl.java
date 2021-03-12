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

package com.bitactor.cloud.spring.sample.cluster.server.auth.service.impl;

import com.bitactor.cloud.spring.sample.cluster.server.auth.component.PlatformComponent;
import com.bitactor.cloud.spring.sample.cluster.server.auth.service.AuthService;
import com.bitactor.cloud.spring.sample.common.bean.Role;
import com.bitactor.cloud.spring.sample.common.consts.GameConstants;
import com.bitactor.cloud.spring.sample.common.dao.RoleDao;
import com.bitactor.cloud.spring.sample.common.enums.GroupType;
import com.bitactor.cloud.spring.sample.common.enums.LogoutType;
import com.bitactor.cloud.spring.sample.common.exception.Assert;
import com.bitactor.cloud.spring.sample.common.platform.AuthInfo;
import com.bitactor.cloud.spring.sample.common.service.gateway.provider.GatewayService;
import com.bitactor.cloud.spring.sample.msg.proto.auth.LoginAuthReq;
import com.bitactor.cloud.spring.sample.msg.proto.auth.LoginAuthResp;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import com.bitactor.framework.cloud.spring.controller.bean.online.OnlineInfo;
import com.bitactor.framework.cloud.spring.controller.bean.online.OnlineManager;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.rpc.annotation.RefRPC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WXH
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private PlatformComponent platformComponent;

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private OnlineManager onlineManager;

    @RefRPC(GroupType.GATEWAY)
    private GatewayService gatewayService;

    @Override
    public LoginAuthResp auth(LoginAuthReq request, ClientNetSession session) {
        AuthInfo authResult = platformComponent.auth(request.getPlatform(), request.getAuthType(), request.getParamsMap());
        Assert.isNull(authResult, CodeEnum.FAILED);
        // 设置 授权信息
        session.setUserId(authResult.getUserId());
        session.addParam(GameConstants.SESSION_AUTH_KEY, authResult);
        forceDisconnectByRepeated(session, authResult);
        Role role = roleDao.findByUserId(authResult.getUserId());
        if (role != null) {
            // 绑定session uid
            session.setUid(role.getUid());
        }
        return LoginAuthResp.newBuilder().setNeedCreateRole(role == null).build();

    }

    /**
     * 强制断开重复登录的
     *
     * @param session
     * @param authResult
     */
    private void forceDisconnectByRepeated(ClientNetSession session, AuthInfo authResult) {
        OnlineInfo onlineInfo = session.build();
        OnlineInfo old = onlineManager.add(onlineInfo);
        if (old != null && !old.getSessionId().equals(onlineInfo.getSessionId())) {
            // 强制离线前面登录的
            gatewayService.forceDisconnect(old.getSessionId(), LogoutType.REPEATED_LOGIN);
        }
    }
}
