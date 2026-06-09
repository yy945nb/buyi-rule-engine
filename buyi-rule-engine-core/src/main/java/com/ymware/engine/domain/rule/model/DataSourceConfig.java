package com.ymware.engine.domain.rule.model;

import lombok.Data;

@Data
public class DataSourceConfig {

    private String code;

    private String driverClass;

    private String url;

    private String username;

    private String password;

    private Integer minPoolSize;

    private Integer maxPoolSize;

    private Integer maxIdleTime;

    private Integer idleConnectionTestPeriod;

}
