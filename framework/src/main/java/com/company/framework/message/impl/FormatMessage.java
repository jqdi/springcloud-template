//package com.company.framework.message.impl;
//
//import java.text.MessageFormat;
//import java.util.Locale;
//
//public class FormatMessage implements IMessage {
//
//    @Override
//    public String getMessage(Locale locale, String code, Object... args) {
//        if (args == null || args.length == 0) {
//            return code;
//        }
//        MessageFormat messageFormat = new MessageFormat(code, locale);
//        return messageFormat.format(args);
//    }
//}
