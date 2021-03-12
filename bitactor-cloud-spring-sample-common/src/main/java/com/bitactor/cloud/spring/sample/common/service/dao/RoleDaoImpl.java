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

package com.bitactor.cloud.spring.sample.common.service.dao;

import com.bitactor.cloud.spring.sample.common.bean.Role;
import com.bitactor.cloud.spring.sample.common.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


/**
 * @author WXH
 */
@Service
public class RoleDaoImpl implements RoleDao {
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Role saveOrUpdate(Role role) {
        Query query = Query.query(Criteria.where("uid").is(role.getUid()));
        return mongoTemplate.findAndReplace(query, role, FindAndReplaceOptions.options().upsert());
    }
    @Override
    public Role save(Role role) {
        return mongoTemplate.save(role);
    }

    @Override
    public Role findByUid(long uid) {
        Query query = Query.query(Criteria.where("uid").is(uid));
        return mongoTemplate.findOne(query, Role.class);
    }

    @Override
    public Role findByUserId(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.findOne(query, Role.class);
    }

    @Override
    public boolean existByUid(long uid) {
        Query query = Query.query(Criteria.where("uid").is(uid));
        return mongoTemplate.exists(query, Role.class);
    }

    @Override
    public boolean existByUserId(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.exists(query, Role.class);
    }

    @Override
    public boolean existByNickname(String nickname) {
        Query query = Query.query(Criteria.where("nickname").is(nickname));
        return mongoTemplate.exists(query, Role.class);
    }
}
