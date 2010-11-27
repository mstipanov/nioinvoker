package org.springframework.remoting.nioinvoker.io;

import hr.sting.niocommunicator.channel.PacketChannel;
import org.springframework.remoting.nioinvoker.context.RmiTargetContext;

import java.io.IOException;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 23:39:23
 */
public interface RmiPacketTarget {
    /**
     * Reads a remote invocation from the request, executes it,
     * and writes the remote invocation result to the response.
     */
    void handlePacket(PacketChannel<RmiPacket> packetChannel, RmiPacket rmiPacket) throws IOException;

    void setPacketChannel(PacketChannel<RmiPacket> packetChannel);

    RmiTargetContext getRmiTargetContext();

    boolean accepts(PacketChannel<RmiPacket> packetChannel, RmiPacket packet);
}
