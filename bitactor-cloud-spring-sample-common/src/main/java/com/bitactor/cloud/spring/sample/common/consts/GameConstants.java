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

package com.bitactor.cloud.spring.sample.common.consts;

import java.util.concurrent.TimeUnit;

/**
 * @author WXH
 */
public class GameConstants {
    /**
     * 未授权的连接强制断开的时间
     */
    public static final int NOT_AUTH_FORCE_KICK_TIME_MS = 60 * 1000;

    public static final int LOCK_TIME_OUT = 5;

    public static final TimeUnit LOCK_TIME_OUT_UNIT = TimeUnit.SECONDS;
    /**
     * session 的授权标识
     */
    public static final String SESSION_AUTH_KEY = "session_auth";
    /**
     * true 的 str
     */
    public static final String TRUE_STR = "0";
    /**
     * false 的 str
     */
    public static final String FALSE_STR = "1";

    public static final String LOCK = "lock";
    /**
     * 30分钟，更新在线信息的间隔
     */
    public static final long UPDATE_ONLINE_INTERVAL_MS = 30 * 60 * 1000;
}
