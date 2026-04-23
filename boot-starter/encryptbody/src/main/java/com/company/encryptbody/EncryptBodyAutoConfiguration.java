package com.company.encryptbody;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.company.encryptbody.annotation.EnableEncryptBody;

//@Configuration 使用org.springframework.boot.autoconfigure.AutoConfiguration.imports装配bean
@EnableEncryptBody
@ConditionalOnProperty(prefix = "encrypt.body", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EncryptBodyAutoConfiguration {
}
