package org.springframework.remoting.nioinvoker.io;

import org.springframework.remoting.support.RemoteInvocation;

import java.io.Serializable;

/**
 * @author mstipanov
 * @since 14.10.10. 21:54
 */
public class RemoteInvocationWrapper implements RemoteInvocationContainer, ContextPathContainer, Serializable {
    private long id;
    private RemoteInvocation remoteInvocation;
    private String contextPath;

    public RemoteInvocationWrapper() {
    }

    public RemoteInvocationWrapper(long id, RemoteInvocation remoteInvocation, String contextPath) {
        this.id = id;
        this.remoteInvocation = remoteInvocation;
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public RemoteInvocation getRemoteInvocation() {
        return remoteInvocation;
    }

    public void setRemoteInvocation(RemoteInvocation remoteInvocation) {
        this.remoteInvocation = remoteInvocation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
