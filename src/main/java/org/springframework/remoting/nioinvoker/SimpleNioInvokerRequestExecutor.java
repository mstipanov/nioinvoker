package org.springframework.remoting.nioinvoker;

import hr.sting.niocommunicator.channel.PacketChannel;
import hr.sting.niocommunicator.serialization.ByteArraySerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.remoting.nioinvoker.context.RmiTargetContext;
import org.springframework.remoting.nioinvoker.io.RemoteInvocationResultWrapper;
import org.springframework.remoting.nioinvoker.io.RemoteInvocationWrapper;
import org.springframework.remoting.nioinvoker.io.RmiPacket;
import org.springframework.remoting.nioinvoker.io.RmiPacketTarget;
import org.springframework.remoting.nioinvoker.io.monitor.RmiPacketCommunicatorMonitor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Marko Stipanov
 * @since 09.10.2010. 00:08:26
 */
public class SimpleNioInvokerRequestExecutor implements NioInvokerRequestExecutor, RmiPacketTarget {
    private static final Log LOGGER = LogFactory.getLog(SimpleNioInvokerRequestExecutor.class);

    private final ConcurrentMap<Long, BlockingQueue<RemoteInvocationResult>> packets = new ConcurrentHashMap<Long, BlockingQueue<RemoteInvocationResult>>();
    private final BlockingQueue<PacketChannel<RmiPacket>> channelQueue = new LinkedBlockingQueue<PacketChannel<RmiPacket>>();
    private final AtomicLong nextId = new AtomicLong(1);

    private PacketChannel<RmiPacket> packetChannel;
    private RmiTargetContext rmiTargetContext;
    private ByteArraySerializer byteArraySerializer;

    public SimpleNioInvokerRequestExecutor(ByteArraySerializer byteArraySerializer, RmiTargetContext rmiTargetContext) {
        this.byteArraySerializer = byteArraySerializer;
        this.rmiTargetContext = rmiTargetContext;
    }

    @Override
    public RemoteInvocationResult executeRequest(RmiTargetContext rmiTargetContext, RemoteInvocation remoteInvocation) throws Exception {
        PacketChannel<RmiPacket> packetChannel = getPacketChannel();
        long id = nextId.getAndIncrement();
        RemoteInvocationWrapper wrapper = new RemoteInvocationWrapper(id, remoteInvocation, rmiTargetContext.getPath());
        RmiPacket rmiPacket = new RmiPacket(byteArraySerializer.serialize(wrapper), wrapper);
        packets.put(id, new LinkedBlockingQueue<RemoteInvocationResult>());
        packetChannel.sendPacket(rmiPacket);
        return wailForResult(id);
        //todo: call, wait and return
        //todo: napraviti RemoteInvocationContext tako da ne moram dizati server za svaki bean ... iscupati iz serviceUrl-a
    }

    private RemoteInvocationResult wailForResult(long id) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Waiting for result...");

        RemoteInvocationResult result;
        try {
            result = packets.get(id).take();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Result received.");
            packets.remove(id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);  //todo: handle exception
        }
        return result;
    }

    @Override
    public void handlePacket(PacketChannel<RmiPacket> packetChannel, RmiPacket rmiPacket) throws IOException {
        RemoteInvocationResultWrapper container = (RemoteInvocationResultWrapper) rmiPacket.getObject();
        RemoteInvocationResult remoteInvocationResult = container.getRemoteInvocationResult();
        BlockingQueue<RemoteInvocationResult> queque = packets.get(container.getId());
        queque.add(remoteInvocationResult);
    }

    public RmiTargetContext getRmiTargetContext() {
        return rmiTargetContext;
    }

    @Override
    public void setPacketChannel(PacketChannel<RmiPacket> packetChannel) {
        channelQueue.add(packetChannel);
    }

    @Override
    public boolean accepts(PacketChannel<RmiPacket> packetChannel, RmiPacket packet) {
        return rmiTargetContext.getPath().equals(packet.getContextPath());
    }

    public PacketChannel<RmiPacket> getPacketChannel() {
        if (null != packetChannel) {
            channelQueue.remove(packetChannel);
            return packetChannel;
        }

        try {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Waiting for channel...");
            packetChannel = channelQueue.take();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Channel received.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);  //todo: handle exception
        }
        return packetChannel;
    }

    public void setRmiPacketCommunicatorMonitor(RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor) {
        rmiPacketCommunicatorMonitor.monitorClient(this);
    }
}
