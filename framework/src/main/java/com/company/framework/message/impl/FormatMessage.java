package com.company.framework.message.impl;

import com.company.framework.message.IMessage;

import java.text.MessageFormat;

public class FormatMessage implements IMessage {

    @Override
    public String getMessage(String code, Object... args) {
        if (args == null || args.length == 0) {
            return code;
        }
        MessageFormat messageFormat = new MessageFormat(code);
        return messageFormat.format(args);
    }
}
