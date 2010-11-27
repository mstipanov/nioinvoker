/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      nio://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.remoting.nioinvoker;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.nioinvoker.context.RmiTargetContext;
import org.springframework.remoting.nioinvoker.io.monitor.RmiPacketCommunicatorMonitor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InvalidClassException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link FactoryBean} for NIO invoker proxies. Exposes the proxied service
 * for use as a bean reference, using the specified service interface.
 * <p/>
 * <p>The service URL must be an NIO URL exposing an NIO invoker service.
 * Optionally, a codebase URL can be specified for on-demand dynamic code download
 * from a remote location. For details, see NioInvokerClientInterceptor docs.
 * <p/>
 * <p>Serializes remote invocation objects and deserializes remote invocation
 * result objects. Uses Java serialization just like RMI, but provides the
 * same ease of setup as Caucho's NIO-based Hessian and Burlap protocols.
 * <p/>
 * <p><b>NIO invoker is the recommended protocol for Java-to-Java remoting.</b>
 * It is more powerful and more extensible than Hessian and Burlap, at the
 * expense of being tied to Java. Nevertheless, it is as easy to set up as
 * Hessian and Burlap, which is its main advantage compared to RMI.
 *
 * @author Juergen Hoeller
 * @see #setServiceInterface
 * @see #setServiceUrl
 * @see NioInvokerServiceExporter
 * @see org.springframework.remoting.rmi.RmiProxyFactoryBean
 * @see org.springframework.remoting.caucho.HessianProxyFactoryBean
 * @see org.springframework.remoting.caucho.BurlapProxyFactoryBean
 * @since 1.1
 */
public class NioInvokerProxyFactoryBean extends RemoteInvocationBasedAccessor implements MethodInterceptor, BeanClassLoaderAware, FactoryBean<Object> {

    private static final Pattern SERVICE_URL_PATTERN = Pattern.compile("nio://(.+?):(\\d+?)/(.*)");
    private NioInvokerRequestExecutor nioInvokerRequestExecutor;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private Object serviceProxy;
    private int timeout;
    private TimeUnit timeUnit;
    private RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        // Eagerly initialize the default NioInvokerRequestExecutor, if needed.
        getNioInvokerRequestExecutor();

        if (getServiceInterface() == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
    }


    public Object getObject() {
        return this.serviceProxy;
    }

    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * Set the NioInvokerRequestExecutor implementation to use for executing
     * remote invocations.
     * <p>Default is {@link SimpleNioInvokerRequestExecutor}.
     * sophisticated needs.
     *
     * @see SimpleNioInvokerRequestExecutor
     */
    public void setNioInvokerRequestExecutor(NioInvokerRequestExecutor nioInvokerRequestExecutor) {
        this.nioInvokerRequestExecutor = nioInvokerRequestExecutor;
    }

    /**
     * Return the NioInvokerRequestExecutor used by this remote accessor.
     * <p>Creates a default SimpleNioInvokerRequestExecutor if no executor
     * has been initialized already.
     */
    public NioInvokerRequestExecutor getNioInvokerRequestExecutor() {
        if (this.nioInvokerRequestExecutor == null) {
            SimpleNioInvokerRequestExecutor executor = new SimpleNioInvokerRequestExecutor(rmiPacketCommunicatorMonitor.getByteArraySerializer(), getRmiTargetContext());
            executor.setRmiPacketCommunicatorMonitor(rmiPacketCommunicatorMonitor);
            this.nioInvokerRequestExecutor = executor;
        }
        return this.nioInvokerRequestExecutor;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /**
     * Return the ClassLoader that this accessor operates in,
     * to be used for deserializing and for generating proxies.
     */
    protected final ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }


    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "NIO invoker proxy for service URL [" + getServiceUrl() + "]";
        }

        RemoteInvocation invocation = createRemoteInvocation(methodInvocation);
        RemoteInvocationResult result = null;
        try {
            result = executeRequest(invocation);
        } catch (Throwable ex) {
            throw convertNioInvokerAccessException(ex);
        }
        try {
            return recreateRemoteInvocationResult(result);
        } catch (Throwable ex) {
            if (result.hasInvocationTargetException()) {
                throw ex;
            } else {
                throw new RemoteInvocationFailureException("Invocation of method [" + methodInvocation.getMethod() +
                        "] failed in NIO invoker remote service at [" + getServiceUrl() + "]", ex);
            }
        }
    }

    /**
     * Execute the given remote invocation via the NioInvokerRequestExecutor.
     * <p>Can be overridden in subclasses to pass a different configuration object
     * to the executor. Alternatively, add further configuration properties in a
     * subclass of this accessor: By default, the accessor passed itself as
     * configuration object to the executor.
     *
     * @param invocation the RemoteInvocation to execute
     * @return the RemoteInvocationResult object
     * @throws IOException            if thrown by I/O operations
     * @throws ClassNotFoundException if thrown during deserialization
     * @throws Exception              in case of general errors
     * @see #getNioInvokerRequestExecutor
     */
    protected RemoteInvocationResult executeRequest(RemoteInvocation invocation) throws Exception {
        return getNioInvokerRequestExecutor().executeRequest(getRmiTargetContext(), invocation);
    }

    private RmiTargetContext getRmiTargetContext() {
        String serviceUrl = getServiceUrl();
        Matcher matcher = SERVICE_URL_PATTERN.matcher(serviceUrl);
        if (!matcher.find())
            throw new IllegalArgumentException("Invalid address!"); //TODO better explanation!

        return new RmiTargetContext(new InetSocketAddress(matcher.group(1), getInt(matcher.group(2))), matcher.group(3));
    }

    private int getInt(String s) {
        return Integer.parseInt(s);
    }

    /**
     * Convert the given NIO invoker access exception to an appropriate
     * Spring RemoteAccessException.
     *
     * @param ex the exception to convert
     * @return the RemoteAccessException to throw
     */
    protected RemoteAccessException convertNioInvokerAccessException(Throwable ex) {
        if (ex instanceof ConnectException) {
            throw new RemoteConnectFailureException(
                    "Could not connect to NIO invoker remote service at [" + getServiceUrl() + "]", ex);
        } else if (ex instanceof ClassNotFoundException || ex instanceof NoClassDefFoundError ||
                ex instanceof InvalidClassException) {
            throw new RemoteAccessException(
                    "Could not deserialize result from NIO invoker remote service [" + getServiceUrl() + "]", ex);
        } else {
            throw new RemoteAccessException(
                    "Could not access NIO invoker remote service at [" + getServiceUrl() + "]", ex);
        }
    }

    public void setTimeout(int timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public void setRmiPacketCommunicatorMonitor(RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor) {
        this.rmiPacketCommunicatorMonitor = rmiPacketCommunicatorMonitor;
    }
}