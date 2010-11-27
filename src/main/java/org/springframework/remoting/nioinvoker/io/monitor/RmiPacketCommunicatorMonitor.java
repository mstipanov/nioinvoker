package org.springframework.remoting.nioinvoker.io.monitor;

import hr.sting.niocommunicator.SelectorThread;
import hr.sting.niocommunicator.channel.PacketAssemblerFactory;
import hr.sting.niocommunicator.channel.PacketChannel;
import hr.sting.niocommunicator.channel.PacketChannelEventProcessor;
import hr.sting.niocommunicator.channel.PacketChannelListener;
import hr.sting.niocommunicator.communicator.DefaultPacketClient;
import hr.sting.niocommunicator.communicator.DefaultPacketServer;
import hr.sting.niocommunicator.communicator.PacketClient;
import hr.sting.niocommunicator.communicator.PacketServer;
import hr.sting.niocommunicator.serialization.ByteArraySerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.remoting.nioinvoker.context.RmiTargetContext;
import org.springframework.remoting.nioinvoker.io.RmiPacket;
import org.springframework.remoting.nioinvoker.io.RmiPacketAssemblerFactory;
import org.springframework.remoting.nioinvoker.io.RmiPacketTarget;
import org.springframework.remoting.nioinvoker.io.serialization.DefaultByteArraySerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 23:30:01
 */
public class RmiPacketCommunicatorMonitor implements PacketChannelListener<RmiPacket> {
    private static final Log LOGGER = LogFactory.getLog(RmiPacketCommunicatorMonitor.class);

    private final PacketAssemblerFactory<RmiPacket> factory;
    private final ConcurrentHashMap<RmiTargetContext, RmiPacketTarget> targetMap = new ConcurrentHashMap<RmiTargetContext, RmiPacketTarget>();
    private final ConcurrentHashMap<String, PacketServer> serverMap = new ConcurrentHashMap<String, PacketServer>();
    private final SelectorThread selector;
    private final PacketChannelEventProcessor<RmiPacket> packetChannelEventProcessor;

    public RmiPacketCommunicatorMonitor() throws IOException {
        this(new PacketChannelEventProcessor<RmiPacket>(), new DefaultByteArraySerializer());
    }

    public RmiPacketCommunicatorMonitor(PacketChannelEventProcessor<RmiPacket> packetPacketChannelEventProcessor, ByteArraySerializer byteArraySerializer) throws IOException {
        factory = new RmiPacketAssemblerFactory(byteArraySerializer);
        selector = new SelectorThread();
        packetChannelEventProcessor = packetPacketChannelEventProcessor;
    }

    public int getActiveCount() {
        return packetChannelEventProcessor.getActiveCount();
    }

    public int getQueueSize() {
        return packetChannelEventProcessor.getQueueSize();
    }

    public void setPoolSize(int poolSize) {
        packetChannelEventProcessor.setPoolSize(poolSize);
    }

    public void start() {
        packetChannelEventProcessor.start();
    }

    public void stop() {
        packetChannelEventProcessor.stop();
    }

    public synchronized void monitorServer(RmiPacketTarget target) {
        monitor(target);

        RmiTargetContext rmiTargetContext = target.getRmiTargetContext();
        InetSocketAddress inetSocketAddress = rmiTargetContext.getInetSocketAddress();
        if (serverMap.containsKey(inetSocketAddress.toString()))
            return;

        try {
            PacketServer packetServer = new DefaultPacketServer<RmiPacket>(selector, inetSocketAddress, factory, this, packetChannelEventProcessor);
            packetServer.start();
            serverMap.put(inetSocketAddress.toString(), packetServer);
        } catch (Exception e) {
            //todo: handle exception
            throw new RuntimeException(e);
        }
    }

    public void monitorClient(RmiPacketTarget target) {
        monitor(target);
        try {
            PacketClient packetClient = new DefaultPacketClient<RmiPacket>(selector, factory, this, packetChannelEventProcessor);
            packetClient.start();
            packetClient.connect(target.getRmiTargetContext().getInetSocketAddress(), target.getRmiTargetContext());
        } catch (Exception e) {
            //todo: handle exception
            throw new RuntimeException(e);
        }
    }

    private void monitor(RmiPacketTarget target) {
        RmiTargetContext rmiTargetContext = target.getRmiTargetContext();
        if (targetMap.contains(rmiTargetContext)) {
            throw new IllegalArgumentException("Target already exists!");
        }

        targetMap.put(rmiTargetContext, target);
    }

    @Override
    public void socketConnected(PacketChannel<RmiPacket> packetChannel, Object context) {
        if (null == context)
            return;

        RmiTargetContext rmiTargetContext = (RmiTargetContext) context;
        targetMap.get(rmiTargetContext).setPacketChannel(packetChannel);
/*
        try {
            packetChannel.sendPacket(new RmiPacket(new Success()));
        } catch (IOException e) {
            throw new RuntimeException(e);  //todo: handle exception
        }
*/
    }

    @Override
    public void packetArrived(PacketChannel<RmiPacket> packetChannel, RmiPacket packet) {
        try {
            RmiPacketTarget target = getTarget(packetChannel, packet);
            target.handlePacket(packetChannel, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);  //todo: handle exception
        }
    }

    private RmiPacketTarget getTarget(PacketChannel<RmiPacket> packetChannel, RmiPacket packet) {
        for (RmiPacketTarget rmiPacketTarget : targetMap.values()) {
            if (rmiPacketTarget.accepts(packetChannel, packet))
                return rmiPacketTarget;
        }
        return null;
    }

    @Override
    public void packetSent(PacketChannel<RmiPacket> packetChannel, RmiPacket packet) {
        //todo: implement me
    }

    @Override
    public void socketException(PacketChannel<RmiPacket> packetChannel, Exception ex) {
        //todo: implement me
    }

    @Override
    public void socketDisconnected(PacketChannel<RmiPacket> packetChannel) {
        //todo: implement me
    }

    @Override
    public void onSocketReadyForWrite() {
        //todo: implement me
    }

    @Override
    public void onSocketNotReadyForWrite() {
        //todo: implement me
    }

    public PacketAssemblerFactory<RmiPacket> getFactory() {
        return factory;
    }

    public void setByteArraySerializer(ByteArraySerializer byteArraySerializer) {
        factory.setByteArraySerializer(byteArraySerializer);
    }

    public ByteArraySerializer getByteArraySerializer() {
        return factory.getByteArraySerializer();
    }
}
