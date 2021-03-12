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

package com.bitactor.cloud.spring.sample.common.utils;


import com.bitactor.cloud.spring.sample.common.enums.GlobalType;
import com.bitactor.framework.core.utils.lang.StringUtils;

/**
 * redis key 的处理工具
 *
 * @author WXH
 */
public class RedisKeyUtil {
    public static String buildRedisKey(String... param) {
        return buildPrefixRedisKey(null, param);
    }

    public static String buildPrefixRedisKey(String prefix, String... param) {
        StringBuffer sb = new StringBuffer();
        // 全局root key
        sb.append(GlobalType.APP_NAME).append(":");
        if (!StringUtils.isEmpty(prefix)) {
            sb.append(prefix).append(":");
        }
        for (String s : param) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            sb.append(s);
            sb.append(":");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
