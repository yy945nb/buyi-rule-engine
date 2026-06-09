package com.ymware.engine.cache;

import com.ymware.engine.domain.rule.model.DataSourceConfig;
import com.ymware.engine.domain.rule.model.DataSourceProperties;
import com.ymware.engine.config.DataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源缓存
 */
@Slf4j
@Component
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfigCache extends AbstractCache<DataSourceConfig> {

    private final DataSourceFactory dataSourceFactory;
    private final DataSourceProperties dataSourceProperties;

    public DataSourceConfigCache(DataSourceFactory dataSourceFactory,
                                 DataSourceProperties dataSourceProperties) {
        this.dataSourceFactory = dataSourceFactory;
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public synchronized void update(String code, DataSourceConfig value) {
        if (cacheMap.containsKey(value.getCode())) {
            //删除原来的数据库连接
            dataSourceFactory.getDataSourceMap().remove(value.getCode());
        }
        //更新数据源配置
        cacheMap.put(value.getCode(), value);
    }

    /**
     * 删除
     *
     * @param code
     */
    public synchronized void delete(String code) {
        if (StringUtils.isEmpty(code)) {
            log.info("code is null");
            return;
        }
        //删除原来的数据库连接
        dataSourceFactory.getDataSourceMap().remove(code);
        //删除数据源配置
        cacheMap.remove(code);
    }

    /**
     * 获取所有数据源配置
     */
    public List<DataSourceConfig> getAllConfigs() {
        return new ArrayList<>(cacheMap.values());
    }

    @Override
    public void init() {
        //1、从库中加载数据库初始数据
        DataSourceFactory.defaultConfig.setUrl(dataSourceProperties.getUrl());
        DataSourceFactory.defaultConfig.setUsername(dataSourceProperties.getUsername());
        DataSourceFactory.defaultConfig.setPassword(dataSourceProperties.getPassword());
        DataSourceFactory.defaultConfig.setDriverClass(dataSourceProperties.getDriverClass());

        cacheMap.put(DataSourceFactory.defaultConfig.getCode(), DataSourceFactory.defaultConfig);
    }
}
