package skunkworks;

import hr.sting.niocommunicator.channel.PacketAssemblerFactory;
import hr.sting.niocommunicator.channel.PacketChannel;
import hr.sting.niocommunicator.channel.PacketChannelEventProcessor;
import hr.sting.niocommunicator.channel.PacketChannelListener;
import hr.sting.niocommunicator.communicator.DefaultPacketClient;
import hr.sting.niocommunicator.communicator.DefaultPacketServer;
import hr.sting.niocommunicator.communicator.PacketClient;
import hr.sting.niocommunicator.communicator.PacketServer;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.springframework.remoting.nioinvoker.io.RmiPacket;
import org.springframework.remoting.nioinvoker.io.RmiPacketAssemblerFactory;
import org.springframework.remoting.nioinvoker.io.serialization.DefaultByteArraySerializer;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 18:30:53
 */
public class NioExample implements PacketChannelListener<RmiPacket> {
    public static void main(String[] args) throws Exception {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8181);

        PacketChannelListener listener = new NioExample();
        PacketAssemblerFactory<RmiPacket> factory = new RmiPacketAssemblerFactory(new DefaultByteArraySerializer());

        PacketServer packetServer = new DefaultPacketServer<RmiPacket>(inetSocketAddress, factory, listener, new PacketChannelEventProcessor<RmiPacket>());
        packetServer.start();

        PacketClient packetClient = new DefaultPacketClient<RmiPacket>(factory, listener, new PacketChannelEventProcessor<RmiPacket>());
        packetClient.start();
        packetClient.connect(inetSocketAddress, null);
    }

    @Override
    public void socketConnected(PacketChannel<RmiPacket> rmiPacketPacketChannel, Object context) {
        try {
            rmiPacketPacketChannel.sendPacket(createRmiPacket("Hello World 1!"));
            rmiPacketPacketChannel.sendPacket(createRmiPacket("Hello World 2!"));
            rmiPacketPacketChannel.sendPacket(createRmiPacket("Hello World 3!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //todo: implement me
    }

    private RmiPacket createRmiPacket(Object o) {
        return new RmiPacket(toBytes(o), o);
    }

    private byte[] toBytes(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(baos);
            os.writeObject(o);
        } catch (IOException e) {
            throw new RuntimeException(e);  //todo: handle exception
        } finally {
            IOUtils.closeQuietly(os);
        }

        return baos.toByteArray();
    }

    private String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            hexString.append(Integer.toHexString(0xFF & bytes[i])).append(" ");
        }
        return hexString.toString();
    }

    @Override
    public void packetArrived(PacketChannel<RmiPacket> rmiPacketPacketChannel, RmiPacket packet) {
        System.out.println("Packet received from (" + rmiPacketPacketChannel.getRemoteSocketAddress() + "): " + packet.getObject());
        //todo: implement me
    }

    @Override
    public void packetSent(PacketChannel<RmiPacket> rmiPacketPacketChannel, RmiPacket packet) {
        System.out.println("Packet sent to: " + (rmiPacketPacketChannel.getRemoteSocketAddress()) + packet.getObject());
        //todo: implement me
    }

    private String getString(RmiPacket packet) {
        return new String(packet.getBytes());
    }

    private String getString2(RmiPacket packet) {
        ObjectInputStream ois = null;
        try {
            byte[] bytes = packet.getBytes();
//            System.out.println("From: " + toHexString(bytes));
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject().toString();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);  //todo: handle exception
        } catch (IOException e) {
            throw new RuntimeException(e);  //todo: handle exception
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }

    @Override
    public void socketException(PacketChannel<RmiPacket> rmiPacketPacketChannel, Exception ex) {
        //todo: implement me
    }

    @Override
    public void socketDisconnected(PacketChannel<RmiPacket> rmiPacketPacketChannel) {
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
}
