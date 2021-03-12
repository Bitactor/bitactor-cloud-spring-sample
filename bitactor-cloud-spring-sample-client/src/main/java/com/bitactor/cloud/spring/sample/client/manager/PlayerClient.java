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

package com.bitactor.cloud.spring.sample.client.manager;


import com.bitactor.cloud.spring.sample.client.service.PlayerReqService;
import com.bitactor.cloud.spring.sample.msg.json.common.CommonJsonResp;
import com.bitactor.cloud.spring.sample.msg.proto.auth.LoginAuthResp;
import com.bitactor.cloud.spring.sample.msg.proto.auth.PlatformEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.cloud.spring.sample.msg.proto.common.RoleProto;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerEnterWorldResp;
import com.bitactor.cloud.spring.sample.msg.proto.system.DisconnectNotify;
import com.bitactor.framework.cloud.spring.boot.client.extension.AbstractClientEntity;
import com.bitactor.framework.cloud.spring.boot.client.extension.RequestStage;
import com.bitactor.framework.cloud.spring.model.constants.ProtocolType;
import com.bitactor.framework.core.net.api.Channel;
import com.bitactor.framework.core.net.api.Client;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author WXH
 */
@Slf4j
public class PlayerClient extends AbstractClientEntity<String> {

    private Client client;
    private final String account;
    private final String password;
    private final PlatformEnum platform;
    private RoleProto role;
    private PlayerReqService playerReqService;
    private boolean activity;

    public PlayerClient(String account, String password, PlatformEnum platform, PlayerReqService playerReqService) {
        super();
        this.account = account;
        this.password = password;
        this.platform = platform;
        this.playerReqService = playerReqService;
    }

    @Override
    public String getUid() {
        return account;
    }

    @Override
    public void init() {
        initLogin();
        initEnterWorld();
    }

    @Override
    public Channel getChannel() {
        return client.getChannel();
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void onActivity() {
        this.activity = true;
    }


    @Override
    public void onDestroy() {
        this.activity = false;
    }

    ///////////////////////////////////////////PlayerClient MSG ///////////////////////////////////////////////////

    /**
     * 异步请求
     *
     * @param req
     * @return
     */
    public CompletableFuture<CommonResp> sendProtoAsync(GeneratedMessageV3 req) {
        return sendAsync(req, CommonResp.class, getRequestStage(req.getClass().getSimpleName()));
    }

    /**
     * 同步请求
     *
     * @param req
     * @return
     */
    public CommonResp sendProtoSync(GeneratedMessageV3 req) {
        return sendSync(req, CommonResp.class, getRequestStage(req.getClass().getSimpleName()));
    }

    /**
     * 异步请求
     *
     * @param req
     * @return
     */
    public CompletableFuture<CommonJsonResp> sendJsonAsync(Object req) {
        return sendAsync(req, CommonJsonResp.class, getRequestStage(req.getClass().getSimpleName()));
    }

    /**
     * 同步请求
     *
     * @param req
     * @return
     */
    public CommonJsonResp sendJsonSync(Object req) {
        return sendSync(req, CommonJsonResp.class, getRequestStage(req.getClass().getSimpleName()));
    }


    private RequestStage getRequestStage(String reqName) {
        long start = System.currentTimeMillis();
        final boolean[] exception = {false};
        StringBuilder sb = new StringBuilder();
        return new RequestStage() {
            @Override
            public void before() {
                sb.append("\n------------------------------------------------------消息请求--------------------------------------------------").append("\n");
                sb.append("====发送请求:").append(reqName).append("\n");
                sb.append("====玩家账号:").append(account).append("\n");
            }

            @Override
            public void after() {
                sb.append("====方法耗时:").append(System.currentTimeMillis() - start).append("ms\n");
                sb.append("====是否异常:").append(exception[0]).append("\n");
                sb.append("------------------------------------------------------消息请求--------------------------------------------------").append("\n");
                log.debug(sb.toString());
            }

            @Override
            public void exception(Throwable throwable) {
                exception[0] = true;
            }
        };
    }

    /**
     * 接受到消息
     *
     * @param message
     */
    @Override
    public void onReceivedNotice(ProtocolType protocolType, Object message) {
        if (message instanceof DisconnectNotify) {
            log.info("接受到连接断开消息:{},信息:{}", ((DisconnectNotify) message).getType(), ((DisconnectNotify) message).getMsg());
        } else {
            onUnknown(message);
        }
    }

    @Override
    public void doClose() {
        log.info("Do close channel:{}", getRole());
        if (getChannel() != null) {
            getChannel().close();
        }
    }
    ///////////////////////////////////////////PlayerClient attribute///////////////////////////////////////////////////

    public RoleProto getRole() {
        return role;
    }

    public void setRole(RoleProto role) {
        this.role = role;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public boolean isActivity() {
        return activity;
    }
    ///////////////////////////////////////////PlayerClient init    ///////////////////////////////////////////////////


    private void initLogin() {
        try {
            CommonResp authResponse = playerReqService.login(this);
            if (!authResponse.getCode().equals(CodeEnum.SUCCESS)) {
                doClose();
                return;
            }
            LoginAuthResp loginAuthResp = authResponse.getData().unpack(LoginAuthResp.class);
            if (loginAuthResp.getNeedCreateRole()) {
                initCreateRole();
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


    private void initCreateRole() {
        CommonResp createRoleResp = playerReqService.createRole(this);
        if (!CodeEnum.SUCCESS.equals(createRoleResp.getCode())) {
            this.doClose();
        }
    }

    private void initEnterWorld() {
        CommonResp enterWorldResp = playerReqService.enterWorld(this);
        if (!CodeEnum.SUCCESS.equals(enterWorldResp.getCode())) {
            this.doClose();
        }
        try {
            this.setRole(enterWorldResp.getData().unpack(PlayerEnterWorldResp.class).getRole());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

}
