package com.ymware.engine.domain.rule.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {

    private String code;

    private String driverClass = "com.mysql.cj.jdbc.Driver";

    private String url;

    private String username;

    private String password;

    private Integer minPoolSize;

    private Integer maxPoolSize;

    private Integer maxIdleTime;
}
