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

package com.bitactor.cloud.spring.sample.cluster.server.world.service;


import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerCreateRoleReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerEnterWorldReq;
import com.bitactor.cloud.spring.sample.msg.proto.player.PlayerEnterWorldResp;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;

/**
 * @author WXH
 */
public interface PlayerService {
    /**
     * 创建角色
     * @param req
     * @param session
     * @return
     */
    boolean createRole(PlayerCreateRoleReq req, ClientNetSession session);
    /**
     * 创建角色
     * @param req
     * @param session
     * @return
     */
    PlayerEnterWorldResp enterWorld(PlayerEnterWorldReq req, ClientNetSession session);
}
