package org.springframework.remoting.nioinvoker;

import org.springframework.remoting.nioinvoker.context.RmiTargetContext;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

/**
 * @author Marko Stipanov
 * @since 09.10.2010. 00:07:35
 */
public interface NioInvokerRequestExecutor {
    RemoteInvocationResult executeRequest(RmiTargetContext rmiTargetContext, RemoteInvocation remoteInvocation) throws Exception;
}
