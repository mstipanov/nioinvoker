package org.springframework.remoting.nioinvoker.io;

import org.springframework.remoting.support.RemoteInvocation;

/**
 * @author mstipanov
 * @since 14.10.10. 21:54
 */
public interface RemoteInvocationContainer {
    long getId();

    RemoteInvocation getRemoteInvocation();
}
