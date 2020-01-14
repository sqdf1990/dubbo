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
package org.apache.dubbo.rpc;

import org.apache.dubbo.common.Node;

/**
 * Invoker. (API/SPI, Prototype, ThreadSafe)
 *
 * 在Dubbo的核心领域模型中：
 *  Protocol是服务域，它是Invoker暴露和引用的主功能入口，它负责Invoker的生命周期管理。
 *  Invoker是实体域，它是Dubbo的核心模型，其它模型都向它靠拢，或转换成它，它代表一个可执行体，可向它发起invoke调用，它有可能是一个本地的实现，
 *  也可能是一个远程的实现，也可能是一个集群的实现。
 *  Invocation是会话域，它持有调用过程中的变量，比如方法名，参数等。
 * @see org.apache.dubbo.rpc.Protocol#refer(Class, org.apache.dubbo.common.URL)
 * @see org.apache.dubbo.rpc.InvokerListener
 * @see org.apache.dubbo.rpc.protocol.AbstractInvoker
 */
public interface Invoker<T> extends Node {

    /**
     * get service interface.
     *
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     *
     * @param invocation
     * @return result
     * @throws RpcException
     */
    Result invoke(Invocation invocation) throws RpcException;

}