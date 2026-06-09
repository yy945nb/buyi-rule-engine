package com.ymware.engine.config;

import com.ymware.engine.domain.rule.model.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DataSourceFactory {
    /**
     * 配置读取类
     */
    private ConcurrentHashMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();

    public static DataSourceConfig defaultConfig = new DataSourceConfig();

    static {
        defaultConfig.setCode("DEFAULT_DS");
        defaultConfig.setMaxIdleTime(3600 * 2);
        defaultConfig.setMinPoolSize(1);
        defaultConfig.setMaxPoolSize(3);
    }


    public ConcurrentHashMap<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public DataSource getDataSource(String dataSourceCode) {
        return dataSourceMap.get(dataSourceCode);
    }


    public DataSource register(DataSourceConfig cfg) {
        Objects.requireNonNull(cfg, "cfg");
        Objects.requireNonNull(cfg.getCode(), "cfg.code");

        return dataSourceMap.computeIfAbsent(cfg.getCode(), k -> {
            HikariConfig hc = new HikariConfig();
            hc.setJdbcUrl(cfg.getUrl());
            hc.setUsername(cfg.getUsername());
            hc.setPassword(cfg.getPassword());
            hc.setDriverClassName(cfg.getDriverClass());

            hc.setMinimumIdle(cfg.getMinPoolSize());
            hc.setMaximumPoolSize(cfg.getMaxPoolSize());
            hc.setIdleTimeout((long) cfg.getMaxIdleTime() * 1000);

            return new HikariDataSource(hc);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initDefault() {
        if (defaultConfig.getUrl() == null) {
            log.warn("default datasource config url is null, skip init");
            return;
        }
        register(defaultConfig);
        log.info("default datasource registered, code={}", defaultConfig.getCode());
    }

}
