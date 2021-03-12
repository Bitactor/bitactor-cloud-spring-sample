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

package com.bitactor.cloud.spring.sample.common.platform;


import com.bitactor.cloud.spring.sample.msg.proto.auth.PlatformEnum;

/**
 * @author WXH
 */
public abstract class Platform<T> {

    private PlatformEnum platformType;

    public Platform(PlatformEnum platformType) {
        this.platformType = platformType;
    }

    public PlatformEnum getPlatformType() {
        return platformType;
    }

    /**
     * 授权码验证
     *
     * @param authToken
     * @return
     */
    public abstract T authToken(String userId,String authToken);

    /**
     * 账号密码验证
     *
     * @param account
     * @param password
     * @return
     */
    public abstract T authAccount(String account, String password);
}
