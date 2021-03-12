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

package com.bitactor.cloud.spring.sample.client.frame;

import com.bitactor.cloud.spring.sample.client.manager.PlayerClient;
import com.bitactor.cloud.spring.sample.client.manager.PlayerClientManager;
import com.bitactor.cloud.spring.sample.msg.json.common.CommonJsonResp;
import com.bitactor.cloud.spring.sample.msg.json.player.PlayerGetRoleJsonReq;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerGetRoleReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerGetRoleResp;
import com.bitactor.framework.cloud.spring.boot.client.extension.ClientAllReady;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author WXH
 */
@Slf4j
@Component
public class ClientAllReadyImpl implements ClientAllReady {
    @Autowired
    private PlayerClientManager playerClientManager;

    @Override
    public void onAllReadyEvent() {
        for (PlayerClient playerClient : playerClientManager.all()) {
            // proto 协议
            CompletableFuture<CommonResp> futureProto = playerClient.sendProtoAsync(PlayerGetRoleReq.newBuilder().build());
            futureProto.whenComplete((resp, cause) -> {
                try {
                    if (resp != null) {
                        if (!CodeEnum.SUCCESS.equals(resp.getCode())) {
                            log.error("异步PROTO请求获取角色数据失败，code：" + resp.getCode());
                            return;
                        }
                        log.info("异步PROTO请求获取角色数据成功：{}", resp.getData().unpack(PlayerGetRoleResp.class));
                    } else {
                        throw cause;
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });

            // json 协议
            CompletableFuture<CommonJsonResp> futureJson = playerClient.sendJsonAsync(new PlayerGetRoleJsonReq());
            futureJson.whenComplete((resp, cause) -> {
                try {
                    if (resp != null) {
                        if (CodeEnum.SUCCESS.getNumber() != resp.getCode()) {
                            log.error("异步JSON请求获取角色数据失败，code：" + resp.getCode());
                            return;
                        }
                        log.info("异步JSON请求获取角色数据成功：{}", resp.getData());
                    } else {
                        throw cause;
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }

    }
}
