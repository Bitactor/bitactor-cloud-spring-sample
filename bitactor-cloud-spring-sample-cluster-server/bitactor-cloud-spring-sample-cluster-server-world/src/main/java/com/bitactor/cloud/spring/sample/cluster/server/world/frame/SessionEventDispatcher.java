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

import com.bitactor.framework.cloud.spring.controller.concurrent.SessionMsgDispatcher;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.core.constant.NetConstants;
import com.bitactor.framework.core.eventloop.BitactorEventLoopGroup;
import com.bitactor.framework.core.threadpool.AtomicOrderedExecutorQueue;
import com.bitactor.framework.core.threadpool.NamedThreadFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author WXH
 */
@Component
public class SessionEventDispatcher implements SessionMsgDispatcher {

    private final AtomicOrderedExecutorQueue<Runnable> noUserDispatcher;

    private final BitactorEventLoopGroup eventLoopGroup;

    public SessionEventDispatcher() {
        ExecutorService service = Executors.newFixedThreadPool(1, new NamedThreadFactory("session-dispatcher"));
        noUserDispatcher = new AtomicOrderedExecutorQueue<Runnable>(service);
        eventLoopGroup = new BitactorEventLoopGroup(NetConstants.DEFAULT_IO_THREADS, new NamedThreadFactory(this.getClass().getSimpleName()));
    }

    @Override
    public void doIt(ClientNetSession session, Runnable runnable) {
        if (Objects.isNull(session.getUid())) {
            noUserDispatcher.add(runnable);
        } else {
            eventLoopGroup.next((loops) -> {
                long uid = session.typeUid();
                return loops[(int) (uid % loops.length)];
            }).execute(runnable);
        }
    }
}
