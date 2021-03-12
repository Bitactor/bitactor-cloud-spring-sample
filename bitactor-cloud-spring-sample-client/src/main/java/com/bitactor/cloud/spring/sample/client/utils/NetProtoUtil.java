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

package com.bitactor.cloud.spring.sample.client.utils;


import com.bitactor.cloud.spring.sample.msg.proto.auth.LoginAuthReq;
import com.bitactor.cloud.spring.sample.msg.proto.auth.LoginAuthResp;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.cloud.spring.sample.msg.proto.player.*;
import com.bitactor.cloud.spring.sample.msg.proto.system.DisconnectNotify;
import com.google.protobuf.GeneratedMessageV3;

import java.util.HashMap;

/**
 * @author WXH
 */
public class NetProtoUtil {

    private static HashMap<Integer, Class<? extends GeneratedMessageV3>> msgMapping = new HashMap<>();

    static {
        init();
    }

    public static void addMapping(Class<? extends GeneratedMessageV3> clazz) {
        msgMapping.put(clazz.getSimpleName().hashCode(), clazz);
    }

    public static Class<? extends GeneratedMessageV3> getClassByCommandId(int commandId) {
        return msgMapping.get(commandId);
    }

    public static void init() {
        addMapping(CommonResp.class);
        addMapping(LoginAuthReq.class);
        addMapping(LoginAuthResp.class);
        addMapping(PlayerCreateRoleReq.class);
        addMapping(PlayerEnterWorldReq.class);
        addMapping(PlayerEnterWorldResp.class);
        addMapping(PlayerGetRoleReq.class);
        addMapping(PlayerGetRoleResp.class);
        // 添加通知消息
        addMapping(DisconnectNotify.class);
    }

}
