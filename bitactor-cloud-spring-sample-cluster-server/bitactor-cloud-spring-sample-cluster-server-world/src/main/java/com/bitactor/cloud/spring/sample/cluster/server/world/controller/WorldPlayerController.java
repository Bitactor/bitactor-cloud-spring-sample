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

package com.bitactor.cloud.spring.sample.cluster.server.world.controller;


import com.bitactor.cloud.spring.sample.cluster.server.world.service.PlayerService;
import com.bitactor.cloud.spring.sample.common.base.BaseController;
import com.bitactor.cloud.spring.sample.common.enums.GroupType;
import com.bitactor.cloud.spring.sample.common.player.bean.NetPlayer;
import com.bitactor.cloud.spring.sample.common.utils.ProtoBeanUtils;
import com.bitactor.cloud.spring.sample.msg.json.common.CommonJsonResp;
import com.bitactor.cloud.spring.sample.msg.json.common.JsonRole;
import com.bitactor.cloud.spring.sample.msg.json.player.PlayerGetRoleJsonReq;
import com.bitactor.cloud.spring.sample.msg.json.player.PlayerGetRoleJsonResp;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.cloud.spring.sample.msg.proto.common.RoleProto;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerCreateRoleReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerEnterWorldReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerGetRoleReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerGetRoleResp;
import com.bitactor.framework.cloud.spring.controller.annotation.BitactorController;
import com.bitactor.framework.cloud.spring.controller.annotation.BitactorRequestMapping;
import com.bitactor.framework.cloud.spring.controller.annotation.ProtocolBody;
import com.bitactor.framework.cloud.spring.controller.annotation.constants.AsyncEnum;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.model.constants.ProtocolType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author WXH
 */
@Slf4j
@BitactorController(connector = GroupType.GATEWAY, async = AsyncEnum.ASYNC)
public class WorldPlayerController extends BaseController {

    @Autowired
    private PlayerService playerService;

    @BitactorRequestMapping
    public CommonResp createRole(@ProtocolBody PlayerCreateRoleReq req, ClientNetSession session) {
        return toR(playerService.createRole(req, session));
    }

    @BitactorRequestMapping
    public CommonResp enterWorld(@ProtocolBody PlayerEnterWorldReq req, ClientNetSession session) {
        return success(playerService.enterWorld(req, session));
    }

    @BitactorRequestMapping()
    public CommonResp getRoleProto(@ProtocolBody PlayerGetRoleReq req, NetPlayer player) {
        RoleProto.Builder roleProto = ProtoBeanUtils.toProtoBean(RoleProto.newBuilder(), player.getRole());
        log.info("do get role method, uid: " + player.getUid());
        return success(PlayerGetRoleResp.newBuilder().setRole(roleProto).build());
    }

    @BitactorRequestMapping(protocol = ProtocolType.JSON)
    public CommonJsonResp getRoleJson(@ProtocolBody PlayerGetRoleJsonReq req, NetPlayer player) {
        JsonRole role = new JsonRole();
        BeanUtils.copyProperties(player.getRole(), role);
        PlayerGetRoleJsonResp resp = new PlayerGetRoleJsonResp();
        resp.setRole(role);
        return success(resp);
    }
}
