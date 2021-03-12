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

package com.bitactor.cloud.spring.sample.common.aspect;

import com.bitactor.cloud.spring.sample.common.exception.GameCodeException;
import com.bitactor.cloud.spring.sample.common.player.bean.NetPlayer;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author WXH
 */
@Slf4j
public class BasePointcutHandler {
    public Object printRequest(ProceedingJoinPoint pjp) throws Throwable {
        StringBuilder sb = new StringBuilder();
        Object ret = null;
        sb.append("\n");
        sb.append("-----------------------------------------------------------------------------------------\n");
        sb.append("执行CONTROLLER:").append(pjp.getTarget().getClass()).append(" METHOD: ").append(pjp.getSignature().getName()).append("\n");
        long start = System.currentTimeMillis();
        Throwable throwable = null;
        try {
            for (Object o : pjp.getArgs()) {
                if (o instanceof com.google.protobuf.GeneratedMessageV3) {
                    sb.append("请求:\n").append(o).append("\n");
                    continue;
                }
                if (o instanceof NetPlayer) {
                    sb.append("玩家uid:").append(((NetPlayer) o).getUid()).append(" session id: ").append(((NetPlayer) o).getSessionId()).append("\n");
                }
            }
            //执行目标对象方法
            ret = pjp.proceed();
            return ret;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            long end = System.currentTimeMillis();
            if (throwable != null) {
                sb.append("异常:");
                if (throwable instanceof GameCodeException) {
                    sb.append("code: ")
                            .append(((GameCodeException) throwable).getCode())
                            .append(" msg:")
                            .append(((GameCodeException) throwable).getMsg());
                } else {
                    sb.append("code: ")
                            .append(CodeEnum.SYSTEM_ERROR);
                }
                sb.append("\n");
            } else {
                sb.append("结果:\n").append(ret).append("\n");
            }
            sb.append("耗时:").append(end - start).append("ms\n");
            sb.append("-----------------------------------------------------------------------------------------\n");
            log.info(sb.toString());
        }
    }
}
