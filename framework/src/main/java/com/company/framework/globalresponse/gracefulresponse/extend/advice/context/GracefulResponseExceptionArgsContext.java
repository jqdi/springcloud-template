package com.company.framework.globalresponse.gracefulresponse.extend.advice.context;

public class GracefulResponseExceptionArgsContext {
    private static final ThreadLocal<Object[]> ARGS_THREAD_LOCAL = new ThreadLocal<>();

    public static void setArgs(Object[] args) {
        ARGS_THREAD_LOCAL.set(args);
    }

    public static Object[] getAndRemoveArgs() {
        Object[] args = ARGS_THREAD_LOCAL.get();
        ARGS_THREAD_LOCAL.remove();
        return args;
    }

}
