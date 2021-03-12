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

package com.bitactor.cloud.spring.sample.cluster.server.auth.controller;


import com.bitactor.cloud.spring.sample.cluster.server.auth.service.AuthService;
import com.bitactor.cloud.spring.sample.common.base.BaseController;
import com.bitactor.cloud.spring.sample.common.enums.GroupType;
import com.bitactor.cloud.spring.sample.msg.proto.auth.LoginAuthReq;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.framework.cloud.spring.controller.annotation.BitactorController;
import com.bitactor.framework.cloud.spring.controller.annotation.BitactorRequestMapping;
import com.bitactor.framework.cloud.spring.controller.annotation.ProtocolBody;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.model.constants.ProtocolType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author WXH
 */
@BitactorController(connector = GroupType.GATEWAY)
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    /**
     * 登录授权
     *
     * @param request
     * @param session
     * @return
     */
    @BitactorRequestMapping(protocol = ProtocolType.PROTO)
    public CommonResp auth(@ProtocolBody LoginAuthReq request, ClientNetSession session) {
        return success(authService.auth(request, session));
    }
}
