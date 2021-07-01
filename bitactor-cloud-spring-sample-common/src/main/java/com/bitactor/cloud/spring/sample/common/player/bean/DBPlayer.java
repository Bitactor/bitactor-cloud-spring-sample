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

package com.bitactor.cloud.spring.sample.common.player.bean;


import com.alibaba.fastjson.JSON;
import com.bitactor.cloud.spring.sample.common.bean.Role;
import com.bitactor.cloud.spring.sample.common.dao.RoleDao;
import com.bitactor.framework.cloud.spring.controller.bean.conn.AbstractConnect;
import com.bitactor.framework.cloud.spring.core.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据存储玩家类
 *
 * @author WXH
 */

public abstract class DBPlayer extends AbstractConnect<Long> {
    private static final Logger logger = LoggerFactory.getLogger(DBPlayer.class);

    private final Role role;

    public DBPlayer(Role role) {
        this.role = role;
    }

    @Override
    public Long getUid() {
        return this.role.getUid();
    }

    /**
     * 玩家实例化数据
     *
     * @return
     */
    public Role getRole() {
        return role;
    }

    @Override
    public void doFinishEvent() {
        // 保存更新数据
        doUpdateDBEvent();
    }

    /**
     * 更新玩家属性到数据库
     */
    public void doUpdateDBEvent() {
        // 保存玩家数据
        SpringUtils.getBean(RoleDao.class).saveOrUpdate(getRole());
        logger.debug("Save player data with uid {},data: {}", getUid(), JSON.toJSON(getRole()));
    }
}
