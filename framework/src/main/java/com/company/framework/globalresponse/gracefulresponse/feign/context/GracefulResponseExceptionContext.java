package com.company.framework.globalresponse.gracefulresponse.feign.context;

import com.feiniaojin.gracefulresponse.GracefulResponseException;

public class GracefulResponseExceptionContext {
    private static final ThreadLocal<GracefulResponseException> EXCEPTION_THREAD_LOCAL = new ThreadLocal<>();

    public static void setException(GracefulResponseException exception) {
        EXCEPTION_THREAD_LOCAL.set(exception);
    }

    public static GracefulResponseException getAndRemoveException() {
        GracefulResponseException gracefulResponseException = EXCEPTION_THREAD_LOCAL.get();
        EXCEPTION_THREAD_LOCAL.remove();
        return gracefulResponseException;
    }

}
