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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Collection;

/**
 * 获取登录玩家
 *
 * @author WXH
 */
@Slf4j
@ShellComponent
public class ShowAccountShell {
    @Autowired
    private PlayerClientManager playerClientManager;

    @ShellMethod("获取登录玩家，格式: account")
    public String account() {
        Collection<PlayerClient> all = playerClientManager.all();
        StringBuilder sb = new StringBuilder();
        for (PlayerClient playerClient : all) {
            sb.append("account: ").append(playerClient.getUid()).append("\n");
        }
        sb.append("account size: ").append(all.size());
        return sb.toString();
    }
}
