-- ============================================
-- AI-Gateway MCP Gateway Tables
-- Version: V25
-- Description: MCP service management, auth keys, statistics, tool mapping
-- ============================================

-- MCP 服务表
CREATE TABLE IF NOT EXISTS `mcp_services` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `service_id`      VARCHAR(100) NOT NULL COMMENT '服务唯一标识',
    `name`            VARCHAR(200) NOT NULL COMMENT '服务名称',
    `description`     TEXT         COMMENT '服务描述',
    `endpoint`        VARCHAR(500) NOT NULL COMMENT '服务端点URL',
    `service_type`    VARCHAR(32)  NOT NULL DEFAULT 'TRANSPARENT' COMMENT '服务类型：TRANSPARENT(透明代理) / PROTOCOL_PARSE(协议解析)',
    `status`          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '服务状态：ACTIVE/INACTIVE/MAINTENANCE/DEPRECATED',
    `max_qps`         INT          NOT NULL DEFAULT 1000 COMMENT '最大QPS限制',
    `health_check_url` VARCHAR(500) COMMENT '健康检查URL',
    `documentation`   TEXT         COMMENT '服务文档',
    `nacos_service_id` VARCHAR(200) COMMENT 'Nacos注册的服务ID（可选，用于自动发现）',
    `version_no`      BIGINT       NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_service_id_deleted` (`service_id`, `deleted`),
    KEY `idx_status_deleted` (`status`, `deleted`),
    KEY `idx_nacos_service_id` (`nacos_service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP 服务表';

-- MCP 认证密钥表
CREATE TABLE IF NOT EXISTS `mcp_auth_keys` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `key_hash`        VARCHAR(500) NOT NULL COMMENT '密钥值（HMAC-SHA256）',
    `key_prefix`      VARCHAR(20)  COMMENT '密钥前缀（用于展示）',
    `user_id`         VARCHAR(100) NOT NULL COMMENT '用户ID',
    `service_id`      VARCHAR(100) NOT NULL COMMENT '关联的MCP服务ID',
    `expires_at`      DATETIME     NULL COMMENT '过期时间，NULL表示永不过期',
    `is_active`       BIT(1)       NOT NULL DEFAULT b'1' COMMENT '是否激活',
    `version_no`      BIGINT       NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_used_at`    DATETIME     NULL COMMENT '最后使用时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key_hash_deleted` (`key_hash`, `deleted`),
    KEY `idx_user_id_deleted` (`user_id`, `deleted`),
    KEY `idx_service_id_deleted` (`service_id`, `deleted`),
    KEY `idx_user_service_deleted` (`user_id`, `service_id`, `deleted`),
    KEY `idx_active_expires` (`is_active`, `expires_at`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP 认证密钥表';

-- MCP API 调用日志表
CREATE TABLE IF NOT EXISTS `mcp_api_call_logs` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`         VARCHAR(100) NOT NULL COMMENT '用户ID',
    `service_id`      VARCHAR(100) NOT NULL COMMENT '服务ID',
    `auth_key_id`     BIGINT       COMMENT '使用的认证密钥ID',
    `request_path`    VARCHAR(500) COMMENT '请求路径',
    `request_method`  VARCHAR(10)  COMMENT '请求方法',
    `client_ip`       VARCHAR(45)  COMMENT '客户端IP',
    `user_agent`      VARCHAR(500) COMMENT 'User-Agent',
    `status_code`     INT          COMMENT '响应状态码',
    `response_time_ms` INT         COMMENT '响应时间(毫秒)',
    `error_message`   TEXT         COMMENT '错误信息',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id_deleted` (`user_id`, `deleted`),
    KEY `idx_service_id_time` (`service_id`, `create_time`, `deleted`),
    KEY `idx_auth_key_id_deleted` (`auth_key_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP API 调用日志表';

-- MCP 服务统计表（按日聚合）
CREATE TABLE IF NOT EXISTS `mcp_service_statistics` (
    `id`                  BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `service_id`          VARCHAR(100) NOT NULL COMMENT '服务ID',
    `date_key`            DATE     NOT NULL COMMENT '统计日期',
    `total_calls`         INT      NOT NULL DEFAULT 0 COMMENT '总调用次数',
    `success_calls`       INT      NOT NULL DEFAULT 0 COMMENT '成功调用次数',
    `failed_calls`        INT      NOT NULL DEFAULT 0 COMMENT '失败调用次数',
    `avg_response_time_ms` INT     COMMENT '平均响应时间(毫秒)',
    `max_response_time_ms` INT     COMMENT '最大响应时间(毫秒)',
    `unique_users`        INT      NOT NULL DEFAULT 0 COMMENT '独立用户数',
    `version_no`          BIGINT   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)   NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_service_date_deleted` (`service_id`, `date_key`, `deleted`),
    KEY `idx_date_key` (`date_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP 服务统计表';

-- REST->MCP 工具映射表
CREATE TABLE IF NOT EXISTS `mcp_tool_mapping` (
    `id`                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `service_id`              VARCHAR(100) NOT NULL COMMENT '关联的MCP服务ID',
    `tool_name`               VARCHAR(200) NOT NULL COMMENT 'MCP工具名称',
    `tool_description`        TEXT         COMMENT '工具描述（展示给LLM）',
    `input_schema_json`       JSON         NOT NULL COMMENT 'JSON Schema 定义工具参数',
    `rest_endpoint`           VARCHAR(500) NOT NULL COMMENT '下游REST API地址',
    `rest_method`             VARCHAR(10)  NOT NULL DEFAULT 'POST' COMMENT 'HTTP方法',
    `rest_headers_json`       JSON         COMMENT '静态请求头',
    `rest_param_mapping_json` JSON         COMMENT '参数映射：MCP参数名 -> REST参数位置(query/path/body)',
    `response_mapping_json`   JSON         COMMENT '响应映射：REST响应 -> MCP结果格式',
    `enabled`                 BIT(1)       NOT NULL DEFAULT b'1' COMMENT '是否启用',
    `version_no`              BIGINT       NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                 BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_service_tool_deleted` (`service_id`, `tool_name`, `deleted`),
    KEY `idx_service_enabled_deleted` (`service_id`, `enabled`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='REST-to-MCP 工具映射表';
