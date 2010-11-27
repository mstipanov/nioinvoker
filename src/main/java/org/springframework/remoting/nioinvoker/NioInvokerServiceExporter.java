/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.remoting.nioinvoker;

import hr.sting.niocommunicator.channel.PacketChannel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.nioinvoker.context.RmiTargetContext;
import org.springframework.remoting.nioinvoker.io.RemoteInvocationContainer;
import org.springframework.remoting.nioinvoker.io.RemoteInvocationResultWrapper;
import org.springframework.remoting.nioinvoker.io.RmiPacket;
import org.springframework.remoting.nioinvoker.io.RmiPacketTarget;
import org.springframework.remoting.nioinvoker.io.monitor.RmiPacketCommunicatorMonitor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.remoting.support.RemoteInvocationResult;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;

import static org.springframework.util.Assert.notNull;

/**
 * HTTP request handler that exports the specified service bean as HTTP invoker
 * service endpoint, accessible via an HTTP invoker proxy.
 * <p/>
 * <p>Deserializes remote invocation objects and serializes remote invocation
 * result objects. Uses Java serialization just like RMI, but provides the
 * same ease of setup as Caucho's HTTP-based Hessian and Burlap protocols.
 * <p/>
 * <p><b>HTTP invoker is the recommended protocol for Java-to-Java remoting.</b>
 * It is more powerful and more extensible than Hessian and Burlap, at the
 * expense of being tied to Java. Nevertheless, it is as easy to set up as
 * Hessian and Burlap, which is its main advantage compared to RMI.
 *
 * @author Juergen Hoeller
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 * @see org.springframework.remoting.caucho.BurlapServiceExporter
 * @since 1.1
 */
public class NioInvokerServiceExporter extends RemoteInvocationBasedExporter implements InitializingBean, RmiPacketTarget {
    private RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor;
    private Object proxy;
    private RmiTargetContext rmiTargetContext = new RmiTargetContext();

    public void afterPropertiesSet() {
        prepare();
    }

    /**
     * Initialize this service exporter.
     */
    public void prepare() {
        rmiTargetContext.check();

        this.proxy = getProxyForService();
        rmiPacketCommunicatorMonitor.monitorServer(this);
    }

    /**
     * Reads a remote invocation from the request, executes it,
     * and writes the remote invocation result to the response.
     *
     * @see #invokeAndCreateResult(org.springframework.remoting.support.RemoteInvocation, Object)
     */
    @Override
    public void handlePacket(PacketChannel<RmiPacket> packetChannel, RmiPacket rmiPacket) throws IOException {

        notNull(this.proxy, "HttpInvokerServiceExporter has not been initialized");

        try {
            Object obj = rmiPacket.getObject();
            long id = 0;
            if (obj instanceof RemoteInvocationContainer) {
                RemoteInvocationContainer rmic = (RemoteInvocationContainer) obj;
                obj = rmic.getRemoteInvocation();
                id = rmic.getId();
            }

            if (!(obj instanceof RemoteInvocation)) {
                throw new RemoteException("Deserialized object needs to be assignable to type [" +
                        RemoteInvocation.class.getName() + "]: " + obj);
            }
            RemoteInvocation invocation = (RemoteInvocation) obj;
            RemoteInvocationResult result = invokeAndCreateResult(invocation, this.proxy);
            writeRemoteInvocationResult(packetChannel, id, result);
        } catch (Exception ex) {
            throw new RemoteException("Class not found during deserialization", ex);
        }
    }

    /**
     * Serialize the given RemoteInvocation to the given OutputStream.
     * <p>The default implementation gives a chance
     * to decorate the stream first (for example, for custom encryption or compression).
     * Calls {@link hr.sting.niocommunicator.serialization.ByteArraySerializer#serialize(java.lang.Object)} to actually write the object.
     * <p>Can be overridden for custom serialization of the invocation.
     *
     * @param result the RemoteInvocationResult object
     * @throws IOException in case of I/O failure
     * @see hr.sting.niocommunicator.serialization.ByteArraySerializer#serialize(java.lang.Object)
     */
    protected void writeRemoteInvocationResult(PacketChannel<RmiPacket> packetChannel, long id, RemoteInvocationResult result)
            throws IOException {
        byte[] bytes = rmiPacketCommunicatorMonitor.getByteArraySerializer().serialize(new RemoteInvocationResultWrapper(id, result.getValue(), result.getException(), getRmiTargetContext().getPath()));
        packetChannel.sendPacket(new RmiPacket(bytes, result));
    }

    @Override
    public RmiTargetContext getRmiTargetContext() {
        return rmiTargetContext;
    }

    @Override
    public void setPacketChannel(PacketChannel<RmiPacket> packetChannel) {
        //todo: implement me
    }

    @Override
    public boolean accepts(PacketChannel<RmiPacket> packetChannel, RmiPacket packet) {
        return rmiTargetContext.getInetSocketAddress().equals(packetChannel.getLocalSocketAddress()) && rmiTargetContext.getPath().equals(packet.getContextPath());  //todo: implement me
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        rmiTargetContext.setInetSocketAddress(inetSocketAddress);
    }

    public void setContextPath(String contextPath) {
        rmiTargetContext.setPath(contextPath);
    }

    public void setRmiPacketCommunicatorMonitor(RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor) {
        this.rmiPacketCommunicatorMonitor = rmiPacketCommunicatorMonitor;
    }
}