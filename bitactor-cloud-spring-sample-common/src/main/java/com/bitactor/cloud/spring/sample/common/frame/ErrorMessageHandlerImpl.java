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

package com.bitactor.cloud.spring.sample.common.frame;


import com.bitactor.cloud.spring.sample.common.exception.GameCodeException;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.bitactor.framework.cloud.spring.controller.bean.MessageBytes;
import com.bitactor.framework.cloud.spring.controller.extension.ErrorMessageHandler;
import com.bitactor.framework.cloud.spring.controller.session.ClientNetSession;
import com.bitactor.framework.cloud.spring.model.constants.MsgErrorType;
import com.bitactor.framework.cloud.spring.model.constants.ProtocolType;
import org.springframework.stereotype.Component;

/**
 * @author WXH
 */
@Component
public class ErrorMessageHandlerImpl implements ErrorMessageHandler {

    @Override
    public MessageBytes getErrorMessageByte(Throwable exception, int msgErrorType, ClientNetSession session, ProtocolType type, int msgId, int commandId) {
        if (!type.equals(ProtocolType.PROTO)) {
            return null;
        }
        CommonResp.Builder errorResponse = CommonResp.newBuilder();
        if (exception instanceof GameCodeException) {
            errorResponse.setCode(((GameCodeException) exception).getCode()).setMsg(((GameCodeException) exception).getMsg()).build();
        } else {
            if (exception != null) {
                exception.printStackTrace();
            }
            if (msgErrorType == MsgErrorType.SYS_ERROR) {
                errorResponse.setCode(CodeEnum.SYSTEM_ERROR).build();
            } else if (msgErrorType == MsgErrorType.NEED_AUTH) {
                errorResponse.setCode(CodeEnum.NEED_AUTH).build();
            } else {
                errorResponse.setCode(CodeEnum.UNKNOWN_ERROR).build();
            }
        }
        int respCommandId = CommonResp.class.getSimpleName().hashCode();
        return new MessageBytes(msgId, respCommandId, errorResponse.build().toByteArray());
    }
}
