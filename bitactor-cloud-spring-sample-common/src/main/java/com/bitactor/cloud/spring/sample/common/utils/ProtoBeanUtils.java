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

package com.bitactor.cloud.spring.sample.common.utils;

import com.google.gson.Gson;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;

/**
 * @author WXH
 */
public class ProtoBeanUtils {

    /**
     * 将ProtoBean对象转化为Json字符串
     *
     * @param sourceMessage
     * @return
     */
    public static String toJsonStr(Message sourceMessage) {
        try {
            if (sourceMessage == null) {
                throw new IllegalArgumentException("No source message specified");
            }
            return JsonFormat.printer().omittingInsignificantWhitespace().print(sourceMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将ProtoBean对象转化为POJO对象
     *
     * @param destPojoClass 目标POJO对象的类类型
     * @param sourceMessage 含有数据的ProtoBean对象实例
     * @param <T>           目标POJO对象的类类型范型
     * @return
     * @throws IOException
     */
    public static <T> T toPojoBean(Class<T> destPojoClass, Message sourceMessage) {
        try {
            if (destPojoClass == null) {
                throw new IllegalArgumentException
                        ("No destination pojo class specified");
            }
            if (sourceMessage == null) {
                throw new IllegalArgumentException("No source message specified");
            }
            String json = JsonFormat.printer().print(sourceMessage);
            return new Gson().fromJson(json, destPojoClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将POJO对象转化为ProtoBean对象
     *
     * @param destBuilder    目标Message对象的Builder类
     * @param sourcePojoBean 含有数据的POJO对象
     * @return
     * @throws IOException
     */
    public static <T extends Message.Builder> T toProtoBean(Message.Builder destBuilder, Object sourcePojoBean) {
        try {
            if (destBuilder == null) {
                throw new IllegalArgumentException
                        ("No destination message builder specified");
            }
            if (sourcePojoBean == null) {
                throw new IllegalArgumentException("No source pojo specified");
            }
            String json = new Gson().toJson(sourcePojoBean);
            JsonFormat.parser().ignoringUnknownFields().merge(json, destBuilder);
            return (T) destBuilder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
