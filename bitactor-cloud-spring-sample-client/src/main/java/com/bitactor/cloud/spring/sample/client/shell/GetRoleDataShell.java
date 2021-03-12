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

package com.bitactor.cloud.spring.sample.client.shell;

import com.bitactor.cloud.spring.sample.client.manager.PlayerClient;
import com.bitactor.cloud.spring.sample.client.manager.PlayerClientManager;
import com.bitactor.cloud.spring.sample.client.service.PlayerReqService;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerGetRoleReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerGetRoleResp;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求获取玩家数据
 *
 * @author WXH
 */
@Slf4j
@ShellComponent
public class GetRoleDataShell {
    @Autowired
    private PlayerClientManager playerClientManager;
    @Autowired
    private PlayerReqService playerReqService;

    @ShellMethod("获取 role 数据，格式: role reqType account times")
    public String role(String reqType, String account, int times) {
        String result = null;
        try {
            PlayerClient playerClient = playerClientManager.get(account);
            if ("sync".equals(reqType)) {
                result = sync(times, playerClient);
            } else if ("async".equals(reqType)) {
                result = async(times, playerClient);
            } else {
                result = "unknown type:" + reqType;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return result;
    }

    private String sync(int times, PlayerClient playerClient) {
        AtomicInteger count = new AtomicInteger();
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            try {
                CommonResp commonResp = playerReqService.getRole(playerClient);
                printResult(commonResp);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            } finally {
                count.incrementAndGet();
            }
        }
        return "use: " + (System.currentTimeMillis() - start) + " ms count: " + count.get();
    }

    private String async(int times, PlayerClient playerClient) throws ExecutionException, InterruptedException {
        AtomicInteger count = new AtomicInteger();
        long start = System.currentTimeMillis();
        List<CompletableFuture<CommonResp>> futures = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            CompletableFuture<CommonResp> future = playerClient.sendProtoAsync(PlayerGetRoleReq.newBuilder().build());
            futures.add(future);
            future.whenComplete((commonResp, casue) -> {
                try {
                    printResult(commonResp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                } finally {
                    count.incrementAndGet();
                }
            });
        }
        CompletableFuture<String> result = new CompletableFuture<>();
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.stream().toArray(CompletableFuture<?>[]::new));
        allOf.whenComplete((resp, casue) -> {
            result.complete("use: " + (System.currentTimeMillis() - start) + " ms count: " + count.get());
        });
        return result.get();
    }

    private void printResult(CommonResp commonResp) throws InvalidProtocolBufferException {
        if (Objects.equals(commonResp.getCode(), CodeEnum.SUCCESS)) {
            log.debug("获取玩家角色成功：{}", commonResp.getData().unpack(PlayerGetRoleResp.class));
        } else {
            log.debug("获取玩家角色失败, code:{}", commonResp.getCode());
        }
    }
}
