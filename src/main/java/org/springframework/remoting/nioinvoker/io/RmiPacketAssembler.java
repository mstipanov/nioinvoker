package org.springframework.remoting.nioinvoker.io;

import hr.sting.niocommunicator.channel.PacketAssembler;
import hr.sting.niocommunicator.channel.PacketChannel;
import hr.sting.niocommunicator.serialization.ByteArraySerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 21:46:02
 */
public class RmiPacketAssembler implements PacketAssembler<RmiPacket> {
    private ByteArraySerializer byteArraySerializer;
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public RmiPacketAssembler(ByteArraySerializer byteArraySerializer) {
        this.byteArraySerializer = byteArraySerializer;
    }

    @Override
    public synchronized List<RmiPacket> appendReceivedData(PacketChannel<RmiPacket> packetChannel, byte[] data) {
        LinkedList<RmiPacket> list = new LinkedList<RmiPacket>();

        for (byte b : data) {
            stream.write(b);

            //todo: obrisati
            if (stream.size() < data.length)
                continue;
            //todo: /obrisati

            try {
                RmiPacket rmiPacket = tryParse();
                if (null == rmiPacket)
                    continue;
                Object o = rmiPacket.getObject();
                if (null == o)
                    continue;
                if (o instanceof ContextPathContainer) {
                    ContextPathContainer contextPathContainer = (ContextPathContainer) o;
                    rmiPacket.setContextPath(contextPathContainer.getContextPath());
                }
                if (o instanceof RemoteInvocationContainer) {
                    RemoteInvocationContainer remoteInvocationContainer = (RemoteInvocationContainer) o;
                    rmiPacket.setId(remoteInvocationContainer.getId());
                }
                list.add(rmiPacket);
                stream.reset();
            } catch (Exception e) {
                throw new RuntimeException(e);  //todo: handle exception
            }
        }

        return list;
    }

    private RmiPacket tryParse() throws ClassNotFoundException, IOException {
        try {
            byte[] bytes = stream.toByteArray();
            Object o = tryDeserialize(bytes, RemoteInvocationWrapper.class, false);
            if (null == o) {
                o = tryDeserialize(bytes, RemoteInvocationResultWrapper.class, true);
                if (null == o)
                    return null;
            }

            return new RmiPacket(bytes, o);
        } catch (Exception e) {
            //ignore
        }

        return null;
    }

    private Object tryDeserialize(byte[] bytes, Class aClass, boolean reportError) throws IOException, ClassNotFoundException {
        try {
            return byteArraySerializer.deserialize(bytes, aClass);
        } catch (Exception e) {
            if (reportError)
                e.printStackTrace();
            //ignore
            return null;
        }
    }
}
