package com.company.tool.datasource;

import org.springframework.stereotype.Component;

import com.company.datasource.CurrentUserProvider;
import com.company.framework.context.HeaderContextUtil;

@Component
public class CustomCurrentUserProvider implements CurrentUserProvider {
    @Override
    public Object currentUser() {
        return HeaderContextUtil.currentUserId();
    }
}
