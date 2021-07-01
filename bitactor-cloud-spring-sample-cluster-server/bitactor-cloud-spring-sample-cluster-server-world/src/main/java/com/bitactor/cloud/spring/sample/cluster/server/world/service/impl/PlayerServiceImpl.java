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

package com.bitactor.cloud.spring.sample.cluster.server.world.service.impl;


import com.bitactor.cloud.spring.sample.cluster.server.world.service.PlayerService;
import com.bitactor.cloud.spring.sample.common.bean.Role;
import com.bitactor.cloud.spring.sample.common.consts.GameConstants;
import com.bitactor.cloud.spring.sample.common.dao.RoleDao;
import com.bitactor.cloud.spring.sample.common.enums.GroupType;
import com.bitactor.cloud.spring.sample.common.exception.Assert;
import com.bitactor.cloud.spring.sample.common.exception.GameCodeException;
import com.bitactor.cloud.spring.sample.common.platform.AuthInfo;
import com.bitactor.cloud.spring.sample.common.player.PlayerManager;
import com.bitactor.cloud.spring.sample.common.player.bean.NetPlayer;
import com.bitactor.cloud.spring.sample.common.utils.ProtoBeanUtils;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.RoleProto;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerCreateRoleReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerEnterWorldReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerEnterWorldResp;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.core.BitactorApplicationProperties;
import com.bitactor.framework.cloud.spring.core.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author WXH
 */
@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private RoleDao roleDao;

    @Resource
    private PlayerManager playerManager;

    @Override
    //@Transactional
    public boolean createRole(PlayerCreateRoleReq req, ClientNetSession session) {
        AuthInfo authInfo = session.getParamInstance(GameConstants.SESSION_AUTH_KEY);
        if (roleDao.existByUserId(authInfo.getUserId())) {
            throw new GameCodeException(CodeEnum.SUCCESS);
        }
        if (roleDao.existByNickname(req.getNickname())) {
            throw new GameCodeException(CodeEnum.REPEAT_NICKNAME);
        }
        Role role = roleDao.save(new Role().setUserId(authInfo.getUserId()).setNickname(req.getNickname()));
        // 榜单session uid
        session.setUid(role.getUid());
        return true;
    }

    @Override
    public PlayerEnterWorldResp enterWorld(PlayerEnterWorldReq req, ClientNetSession session) {
        BitactorApplicationProperties properties = SpringUtils.getBean(BitactorApplicationProperties.class);
        session.addParam(GroupType.WORLD, properties.getSID());
        // 获取玩家属性信息
        Role role = roleDao.findByUid(session.typeUid());
        Assert.isNull(role);
        NetPlayer player = new NetPlayer(role, session);
        playerManager.add(player);
        RoleProto.Builder roleProto = ProtoBeanUtils.toProtoBean(RoleProto.newBuilder(), player.getRole());
        Assert.isNull(roleProto);
        return PlayerEnterWorldResp.newBuilder()
                .setRole(roleProto)
                .build();
    }

}
