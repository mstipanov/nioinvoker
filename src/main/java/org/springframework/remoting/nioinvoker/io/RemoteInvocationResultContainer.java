package org.springframework.remoting.nioinvoker.io;

import org.springframework.remoting.support.RemoteInvocationResult;

/**
 * @author mstipanov
 * @since 14.10.10. 23:35
 */
public interface RemoteInvocationResultContainer {
    long getId();

    RemoteInvocationResult getRemoteInvocationResult();
}
