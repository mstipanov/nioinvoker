package org.springframework.remoting.nioinvoker.io;

import hr.sting.niocommunicator.channel.PacketAssemblerFactory;
import hr.sting.niocommunicator.serialization.ByteArraySerializer;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 21:45:29
 */
public class RmiPacketAssemblerFactory implements PacketAssemblerFactory<RmiPacket> {
    private ByteArraySerializer byteArraySerializer;

    public RmiPacketAssemblerFactory(ByteArraySerializer byteArraySerializer) {
        this.byteArraySerializer = byteArraySerializer;
    }

    @Override
    public RmiPacketAssembler create() {
        return new RmiPacketAssembler(byteArraySerializer);
    }

    @Override
    public ByteArraySerializer getByteArraySerializer() {
        return byteArraySerializer;
    }

    @Override
    public void setByteArraySerializer(ByteArraySerializer byteArraySerializer) {
        this.byteArraySerializer = byteArraySerializer;
    }
}
