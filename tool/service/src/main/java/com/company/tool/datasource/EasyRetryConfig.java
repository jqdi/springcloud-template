package com.company.tool.datasource;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class EasyRetryConfig {
    @Bean(name = "easyRetryMybatisDataSource", initMethod = "init", destroyMethod = "close")
    public DataSource easyRetryMybatisDataSource() {
        DruidDataSource tds = new DruidDataSource();
        tds.setUrl("jdbc:mysql://127.0.0.1:3306/template?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true");
        tds.setUsername("root");
        tds.setPassword("12345678");
        return tds;
    }
}
