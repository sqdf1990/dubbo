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
package org.apache.dubbo.common.extension;

import org.apache.dubbo.common.URL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provide helpful information for {@link ExtensionLoader} to inject dependency extension instance.
 *
 * 参考网址：https://www.cnblogs.com/zzq6032010/p/11219611.html
 *
 * Dubbo用SPI机制来进行Bean的管理与引用，类似于Spring中BeanFactory对bean的管理。SPI实现了类似于 "在容器中管理Bean"的功能，那么问题来了，
 * 如果我想在程序运行时调用SPI中管理的类的方法，再通过运行时的参数来确定调用哪个实现类，这么矛盾的场景应该怎么实现？这时就要靠Dubbo的自适应扩展机制了。
 *
 * 实现的思路其实不难，我们先一起来分析一下。首先程序运行时直接调用的SPI管理类中的方法不是通过SPI加载的类，因为这时候还未加载，所以此时只能先通过代理类代理，
 * 在代理类的方法中再进行判断，看需要调用哪个实现类，再去加载这个实现类并调用目标方法。即最先调用的那个方法只是最终要调用的实现类方法的一个代理而已。
 * 添加了一个代理层，就实现了一个看似矛盾的场景，从这也可以看出软件开发的一个重要的思想武器-分层。
 *
 * 但要真正实现这个思路，将它落地，还是比较复杂的。首先要确定，哪些方法需要生成代理类进行代理？Dubbo中是通过@Adaptive注解来标识类与方法实现的。
 * 其次，代理类如何生成？Dubbo中先拼接出一段java代码的字符串，然后默认使用javassist编译这段代码加载进JVM得到class对象，再利用反射生成代理类。
 * 最后，代理类生成后，通过什么来确认最终要加载调用的实现类？Dubbo中对此进行了规范，统一从URL对象中获取参数找到最终调用的实现类。
 * 注意此处的URL是Dubbo中自己定义的一个类，类路径为  org.apache.dubbo.common.URL。
 *
 * Adaptive注解是自适应扩展的触发点，可以加在类上和方法上。加在类上，表示该类是一个扩展类，不需要生成代理直接用即可；加在方法上则表示该方法需生成代理。
 * Dubbo中此注解加在类上的情况只有两个：AdaptiveCompiler和AdaptiveExtensionFactory.
 * @see ExtensionLoader
 * @see URL
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Adaptive {
    /**
     * Decide which target extension to be injected. The name of the target extension is decided by the parameter passed
     * in the URL, and the parameter names are given by this method.
     * <p>
     * If the specified parameters are not found from {@link URL}, then the default extension will be used for
     * dependency injection (specified in its interface's {@link SPI}).
     * <p>
     * For example, given <code>String[] {"key1", "key2"}</code>:
     * <ol>
     * <li>find parameter 'key1' in URL, use its value as the extension's name</li>
     * <li>try 'key2' for extension's name if 'key1' is not found (or its value is empty) in URL</li>
     * <li>use default extension if 'key2' doesn't exist either</li>
     * <li>otherwise, throw {@link IllegalStateException}</li>
     * </ol>
     * If the parameter names are empty, then a default parameter name is generated from interface's
     * class name with the rule: divide classname from capital char into several parts, and separate the parts with
     * dot '.', for example, for {@code org.apache.dubbo.xxx.YyyInvokerWrapper}, the generated name is
     * <code>String[] {"yyy.invoker.wrapper"}</code>.
     *
     * @return parameter names in URL
     */
    String[] value() default {};

}