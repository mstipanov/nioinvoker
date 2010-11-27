package org.springframework.remoting.nioinvoker.io.monitor;

import org.springframework.remoting.nioinvoker.io.RmiPacketTarget;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author mstipanov
 * @since 09.10.10. 13:05
 */
public class RmiPacketTargetContext {
    private InetSocketAddress inetSocketAddress;
    private ConcurrentLinkedQueue<RmiPacketTarget> rmiPacketTargets = new ConcurrentLinkedQueue<RmiPacketTarget>();

    public RmiPacketTargetContext(InetSocketAddress inetSocketAddress, RmiPacketTarget rmiPacketTarget) {
        this.inetSocketAddress = inetSocketAddress;
        this.rmiPacketTargets.add(rmiPacketTarget);
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public ConcurrentLinkedQueue<RmiPacketTarget> getRmiPacketTargets() {
        return rmiPacketTargets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RmiPacketTargetContext)) return false;

        RmiPacketTargetContext that = (RmiPacketTargetContext) o;

        if (inetSocketAddress != null ? !inetSocketAddress.equals(that.inetSocketAddress) : that.inetSocketAddress != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return inetSocketAddress != null ? inetSocketAddress.hashCode() : 0;
    }

    public boolean contains(RmiPacketTarget rmiPacketTarget) {
        return rmiPacketTargets.contains(rmiPacketTarget);
    }

    public void add(RmiPacketTarget rmiPacketTarget) {
        rmiPacketTargets.add(rmiPacketTarget);
    }
}
