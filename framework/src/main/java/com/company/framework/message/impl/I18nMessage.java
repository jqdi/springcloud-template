package com.company.framework.message.impl;

import com.company.framework.message.IMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.Locale;

public class I18nMessage implements IMessage {

    private MessageSource messageSource;

    public I18nMessage(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        String defaultMessage = code;
        if (args != null && args.length > 0) {
            MessageFormat messageFormat = new MessageFormat(code, locale);
            defaultMessage = messageFormat.format(args);
        }
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
}
