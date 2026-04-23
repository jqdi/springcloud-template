package com.company.framework.globalresponse;

/**
 * 异常工具类（建议使用该工具类抛出业务异常，方便灵活切换异常实现类）
 *
 * @author JQ棣
 */
public class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static void throwException(String code, String message) {
        throw new BusinessException(code, message); // 不带参数的业务异常
//        throw new ArgsBusinessException(code, message); // 带参数的业务异常【不建议使用】
    }

    public static void throwException(String message) {
        throw new BusinessException(message); // 不带参数的业务异常
//        throw new ArgsBusinessException(message); // 带参数的业务异常【不建议使用】
    }

    public static void throwException(String code, String message, Object... args) {
//        throw new BusinessException(code, message); // 不带参数的业务异常【不建议使用】
        throw new ArgsBusinessException(code, message, args); // 带参数的业务异常
    }

    public static void throwException(String message, Object... args) {
//        throw new BusinessException(message); // 不带参数的业务异常【不建议使用】
        throw new ArgsBusinessException(message, args); // 带参数的业务异常
    }
}
