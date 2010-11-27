package org.springframework.remoting.nioinvoker.context;

import java.net.InetSocketAddress;

import static org.springframework.util.Assert.notNull;

/**
 * @author mstipanov
 * @since 14.10.10. 20:54
 */
public class RmiTargetContext {
    private String path;
    private InetSocketAddress inetSocketAddress;

    public RmiTargetContext() {
    }

    public RmiTargetContext(InetSocketAddress inetSocketAddress, String path) {
        this.inetSocketAddress = inetSocketAddress;
        this.path = path;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void check() {
        notNull(inetSocketAddress);
        notNull(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RmiTargetContext)) return false;

        RmiTargetContext that = (RmiTargetContext) o;

        if (inetSocketAddress != null ? !getAddress().equals(that.getAddress()) : that.getAddress() != null)
            return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    public String getAddress() {
        return null == inetSocketAddress ? null : inetSocketAddress.toString();
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RmiTargetContext");
        sb.append("{inetSocketAddress=").append(inetSocketAddress);
        sb.append(", path='").append(path).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
