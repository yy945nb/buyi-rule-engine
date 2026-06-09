package com.ymware.engine.controller.api;

import com.ymware.engine.cache.DataSourceConfigCache;
import com.ymware.engine.config.DataSourceFactory;
import com.ymware.engine.domain.rule.model.DataSourceConfig;
import com.ymware.engine.model.response.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据源管理API - 对外暴露数据源CRUD能力
 */
@Slf4j
@Tag(name = "数据源管理")
@RestController
@RequestMapping("api/datasource")
public class DataSourceController {

    @Resource
    private DataSourceConfigCache dataSourceConfigCache;

    @Resource
    private DataSourceFactory dataSourceFactory;

    /**
     * 获取所有数据源列表
     */
    @Operation(summary = "获取数据源列表")
    @GetMapping("list")
    public RestResult<List<Map<String, Object>>> list() {
        List<Map<String, Object>> list = dataSourceConfigCache.getAllConfigs().stream()
                .map(cfg -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("code", cfg.getCode());
                    m.put("driverClass", cfg.getDriverClass());
                    m.put("url", cfg.getUrl());
                    m.put("username", cfg.getUsername());
                    m.put("minPoolSize", cfg.getMinPoolSize());
                    m.put("maxPoolSize", cfg.getMaxPoolSize());
                    // 不返回密码
                    return m;
                })
                .collect(Collectors.toList());
        return RestResult.ok(list);
    }

    /**
     * 获取单个数据源配置
     */
    @Operation(summary = "获取数据源详情")
    @GetMapping("get/{code}")
    public RestResult<Map<String, Object>> get(@PathVariable String code) {
        DataSourceConfig cfg = dataSourceConfigCache.get(code);
        if (cfg == null) {
            return RestResult.failed(404, "数据源不存在: " + code);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", cfg.getCode());
        m.put("driverClass", cfg.getDriverClass());
        m.put("url", cfg.getUrl());
        m.put("username", cfg.getUsername());
        m.put("minPoolSize", cfg.getMinPoolSize());
        m.put("maxPoolSize", cfg.getMaxPoolSize());
        m.put("maxIdleTime", cfg.getMaxIdleTime());
        m.put("idleConnectionTestPeriod", cfg.getIdleConnectionTestPeriod());
        return RestResult.ok(m);
    }

    /**
     * 新增数据源
     */
    @Operation(summary = "新增数据源")
    @PostMapping("add")
    public RestResult<Void> add(@RequestBody DataSourceConfig config) {
        if (config.getCode() == null || config.getCode().isBlank()) {
            return RestResult.failed(400, "数据源编码不能为空");
        }
        if (dataSourceConfigCache.get(config.getCode()) != null) {
            return RestResult.failed(400, "数据源编码已存在: " + config.getCode());
        }
        try {
            // 注册连接池
            dataSourceFactory.register(config);
            // 缓存配置
            dataSourceConfigCache.update(config.getCode(), config);
            log.info("DataSource added: {}", config.getCode());
            return RestResult.ok();
        } catch (Exception e) {
            log.error("Failed to add datasource: {}", config.getCode(), e);
            return RestResult.failed(500, "新增失败: " + e.getMessage());
        }
    }

    /**
     * 更新数据源配置
     */
    @Operation(summary = "更新数据源")
    @PostMapping("update")
    public RestResult<Void> update(@RequestBody DataSourceConfig config) {
        if (config.getCode() == null || config.getCode().isBlank()) {
            return RestResult.failed(400, "数据源编码不能为空");
        }
        try {
            // 移除旧连接池
            dataSourceFactory.getDataSourceMap().remove(config.getCode());
            // 注册新连接池
            dataSourceFactory.register(config);
            // 更新缓存
            dataSourceConfigCache.update(config.getCode(), config);
            log.info("DataSource updated: {}", config.getCode());
            return RestResult.ok();
        } catch (Exception e) {
            log.error("Failed to update datasource: {}", config.getCode(), e);
            return RestResult.failed(500, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除数据源
     */
    @Operation(summary = "删除数据源")
    @PostMapping("delete/{code}")
    public RestResult<Void> delete(@PathVariable String code) {
        if (DataSourceFactory.defaultConfig.getCode().equals(code)) {
            return RestResult.failed(400, "不能删除默认数据源");
        }
        dataSourceConfigCache.delete(code);
        log.info("DataSource deleted: {}", code);
        return RestResult.ok();
    }

    /**
     * 测试数据源连接
     */
    @Operation(summary = "测试数据源连接")
    @PostMapping("test/{code}")
    public RestResult<Map<String, Object>> testConnection(@PathVariable String code) {
        DataSource ds = dataSourceFactory.getDataSource(code);
        if (ds == null) {
            return RestResult.failed(404, "数据源不存在: " + code);
        }
        try (var conn = ds.getConnection()) {
            Map<String, Object> data = new HashMap<>();
            data.put("valid", conn.isValid(5));
            data.put("url", conn.getMetaData().getURL());
            data.put("productName", conn.getMetaData().getDatabaseProductName());
            data.put("productVersion", conn.getMetaData().getDatabaseProductVersion());
            return RestResult.ok(data);
        } catch (Exception e) {
            return RestResult.failed(500, "连接失败: " + e.getMessage());
        }
    }
}
