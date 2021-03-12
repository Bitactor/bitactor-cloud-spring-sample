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

package com.bitactor.cloud.spring.sample.cluster.server.world.frame;


import com.bitactor.cloud.spring.sample.common.consts.GameConstants;
import com.bitactor.framework.cloud.spring.controller.extension.CustomAuthHandler;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import org.springframework.stereotype.Component;

/**
 * @author WXH
 */
@Component
public class WorldAuthHandler implements CustomAuthHandler {

    /**
     * 需要授权返回 true,不需要授权返回false
     *
     * @param session
     * @return
     */
    @Override
    public boolean checkNeedAuth(ClientNetSession session) {
        return !session.getParam().containsKey(GameConstants.SESSION_AUTH_KEY);
    }

}
