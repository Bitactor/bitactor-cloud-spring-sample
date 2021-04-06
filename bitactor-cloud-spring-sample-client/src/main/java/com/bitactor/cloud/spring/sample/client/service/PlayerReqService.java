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

package com.bitactor.cloud.spring.sample.client.service;


import com.bitactor.cloud.spring.sample.client.manager.PlayerClient;
import com.bitactor.cloud.spring.sample.msg.proto.auth.AuthType;
import com.bitactor.cloud.spring.sample.msg.proto.auth.LoginAuthReq;
import com.bitactor.cloud.spring.sample.msg.proto.auth.ParamKeyType;
import com.bitactor.cloud.spring.sample.msg.proto.auth.PlatformEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerCreateRoleReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerEnterWorldReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerGetRoleReq;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WXH
 */
@Service
public class PlayerReqService {

    public CommonResp login(PlayerClient player) throws InvalidProtocolBufferException {
        Map<Integer, String> authParams = new HashMap<>();
        authParams.put(ParamKeyType.ACCOUNT_VALUE, player.getAccount());
        authParams.put(ParamKeyType.PASSWORD_VALUE, player.getPassword());
        return player.sendProtoSync(LoginAuthReq.newBuilder()
                .setPlatform(PlatformEnum.TEST)
                .setAuthType(AuthType.AUTH_PASSWORD).putAllParams(authParams).build());
    }

    public CommonResp createRole(PlayerClient player) {
        return player.sendProtoSync(PlayerCreateRoleReq.newBuilder()
                .setNickname(player.getPlatform().name() + "_" + player.getAccount())
                .build());
    }

    public CommonResp enterWorld(PlayerClient player) {
        return player.sendProtoSync(PlayerEnterWorldReq.newBuilder().build());
    }

    public CommonResp getRole(PlayerClient player) {
        return player.sendProtoSync(1122,PlayerGetRoleReq.newBuilder().build());
    }
}
