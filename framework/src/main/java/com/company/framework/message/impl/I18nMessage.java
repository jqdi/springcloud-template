//package com.company.framework.message.impl;
//
//import java.util.Locale;
//
//import org.springframework.context.MessageSource;
//
//public class I18nMessage implements IMessage {
//    private final IMessage parent;
//    private final MessageSource messageSource;
//
//    public I18nMessage(IMessage parent, MessageSource messageSource) {
//        this.parent = parent;
//        this.messageSource = messageSource;
//    }
//
//    @Override
//    public String getMessage(Locale locale, String code, Object... args) {
////        String defaultMessage = code;
////        if (args != null && args.length > 0) {
////            MessageFormat messageFormat = new MessageFormat(code, locale);
////            defaultMessage = messageFormat.format(args);
////        }
//        String message = messageSource.getMessage(code, args, null, locale);
//        if (message != null) {
//            return message;
//        }
//        if (parent == null) {
//            return code;
//        }
//        return parent.getMessage(code, args, locale);
//    }
//}
