package org.springframework.remoting.nioinvoker.io;

import hr.sting.niocommunicator.channel.Packet;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 21:46:54
 */
public class RmiPacket implements Packet {

    private byte[] bytes;
    private Object object;
    private String contextPath;
    private long id;

    public RmiPacket(byte[] bytes, Object object) {
        this.bytes = bytes;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
