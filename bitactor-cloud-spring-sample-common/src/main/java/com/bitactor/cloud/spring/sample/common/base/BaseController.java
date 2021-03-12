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

package com.bitactor.cloud.spring.sample.common.base;


import com.bitactor.cloud.spring.sample.msg.json.common.CommonJsonResp;
import com.bitactor.cloud.spring.sample.msg.proto.common.CodeEnum;
import com.bitactor.cloud.spring.sample.msg.proto.common.CommonResp;
import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

/**
 * @author WXH
 */
public class BaseController {

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected CommonResp toR(int rows) {
        return rows > 0 ? success() : error();
    }

    /**
     * 响应返回结果
     *
     * @param code 结果枚举
     * @return 操作结果
     */
    protected CommonResp toR(CodeEnum code) {
        return CommonResp.newBuilder().setCode(code).build();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected CommonResp toR(boolean result) {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public CommonResp success() {
        return CommonResp.newBuilder().setCode(CodeEnum.SUCCESS).build();
    }

    /**
     * 返回成功
     */
    public CommonResp success(Any data) {
        return CommonResp.newBuilder().setCode(CodeEnum.SUCCESS).setData(data).build();
    }

    /**
     * 返回成功
     */
    public CommonResp success(GeneratedMessageV3 data) {
        return CommonResp.newBuilder().setCode(CodeEnum.SUCCESS).setData(Any.pack(data)).build();
    }

    /**
     * 返回失败消息
     */
    public CommonResp error() {
        return CommonResp.newBuilder().setCode(CodeEnum.FAILED).build();
    }

    /**
     * 返回成功消息
     */
    public CommonResp success(String message) {
        return CommonResp.newBuilder().setCode(CodeEnum.SUCCESS).setMsg(message).build();
    }

    /**
     * 返回失败消息
     */
    public CommonResp error(String message) {
        return CommonResp.newBuilder().setCode(CodeEnum.FAILED).setMsg(message).build();
    }

    /**
     * 返回错误码消息
     */
    public CommonResp error(CodeEnum code, String message) {
        return CommonResp.newBuilder().setCode(code).setMsg(message).build();
    }


    /**
     * 返回成功
     */
    public CommonJsonResp success(Object data) {
        CommonJsonResp commonJsonResp = new CommonJsonResp();
        commonJsonResp.setCode(CodeEnum.SUCCESS.getNumber());
        commonJsonResp.setData(data);
        return commonJsonResp;
    }
}
