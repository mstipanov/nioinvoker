package org.springframework.remoting.nioinvoker.io;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.remoting.support.RemoteInvocationResult;

import java.io.Serializable;

/**
 * @author mstipanov
 * @since 14.10.10. 21:54
 */
public class RemoteInvocationResultWrapper implements RemoteInvocationResultContainer, ContextPathContainer, Serializable {
    private long id;
    private Object result;
    private Throwable exception;
    private String contextPath;

    public RemoteInvocationResultWrapper() {
    }

    public RemoteInvocationResultWrapper(long id, Object result, Throwable exception, String contextPath) {
        this.id = id;
        this.result = result;
        this.exception = exception;
        this.contextPath = contextPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @JsonIgnore
    public RemoteInvocationResult getRemoteInvocationResult() {
        if (null != exception)
            return new RemoteInvocationResult(exception);

        return new RemoteInvocationResult(result);
    }
}
