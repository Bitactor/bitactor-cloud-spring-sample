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

package com.bitactor.cloud.spring.sample.cluster.server.auth.component;


import com.bitactor.cloud.spring.sample.cluster.server.auth.component.platform.BitactorPlatform;
import com.bitactor.cloud.spring.sample.cluster.server.auth.component.platform.TestPlatform;
import com.bitactor.cloud.spring.sample.common.exception.Assert;
import com.bitactor.cloud.spring.sample.common.platform.AuthInfo;
import com.bitactor.cloud.spring.sample.common.platform.Platform;
import com.bitactor.cloud.spring.sample.msg.proto.auth.AuthType;
import com.bitactor.cloud.spring.sample.msg.proto.auth.ParamKeyType;
import com.bitactor.cloud.spring.sample.msg.proto.auth.PlatformEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WXH
 */
@Component
public class PlatformComponent {
    /**
     * 平台授权处理集合
     */
    private final static ConcurrentHashMap<PlatformEnum, Platform<AuthInfo>> platformMap = new ConcurrentHashMap<>();


    static {
        platformMap.put(PlatformEnum.TEST, new TestPlatform());
        platformMap.put(PlatformEnum.BITACTOR, new BitactorPlatform());
    }

    /**
     * 授权码验证
     *
     * @return
     */
    public AuthInfo auth(PlatformEnum platformEnum, AuthType authType, Map<Integer, String> params) {
        Platform<AuthInfo> platform = platformMap.get(platformEnum);

        Assert.isNull(platform, CodeEnum.NONSUPPORT_PLATFORM);

        Assert.isInclude(authType, Arrays.asList(AuthType.values()), CodeEnum.ILLEGALITY);

        if (AuthType.AUTH_TOKEN.equals(authType)) {
            return platform.authToken(params.get(ParamKeyType.USERID_VALUE), params.get(ParamKeyType.TOKEN_VALUE));
        } else if (AuthType.AUTH_PASSWORD.equals(authType)) {
            return platform.authAccount(params.get(ParamKeyType.ACCOUNT_VALUE), params.get(ParamKeyType.PASSWORD_VALUE));
        } else {
            return null;
        }

    }
}
