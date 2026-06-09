-- ============================================
-- AI-Gateway Routing Rule Engine Tables
-- Version: V26
-- Description: Routing rules, service capabilities for intelligent request routing
-- ============================================

-- 路由规则表
CREATE TABLE IF NOT EXISTS `mcp_routing_rules` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `rule_name`           VARCHAR(200) NOT NULL COMMENT '规则名称',
    `description`         TEXT         COMMENT '规则描述',
    `priority`            INT          NOT NULL DEFAULT 0 COMMENT '优先级，数值越大越优先',
    `match_tool_pattern`  VARCHAR(500) COMMENT '工具名匹配模式，支持 * 通配符，逗号分隔多值',
    `match_keywords`      VARCHAR(500) COMMENT '意图关键词匹配，逗号分隔',
    `match_service_type`  VARCHAR(32)  DEFAULT 'ALL' COMMENT '服务类型过滤：TRANSPARENT/PROTOCOL_PARSE/ALL',
    `match_arg_path`      VARCHAR(500) COMMENT '参数路径匹配，格式：jsonPath=value',
    `targets_json`        JSON         NOT NULL COMMENT '目标服务列表：[{serviceId, weight, capabilityTag, fallback}]',
    `enabled`             BIT(1)       NOT NULL DEFAULT b'1' COMMENT '是否启用',
    `version_no`          BIGINT       NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_priority_enabled_deleted` (`priority` DESC, `enabled`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP 路由规则表';

-- 服务能力注册表
CREATE TABLE IF NOT EXISTS `mcp_service_capabilities` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `service_id`          VARCHAR(100) NOT NULL COMMENT '服务ID',
    `capability_tag`      VARCHAR(100) NOT NULL COMMENT '能力标签：file/image/database/order/product/...',
    `description`         TEXT         COMMENT '能力描述',
    `max_concurrent`      INT          DEFAULT 100 COMMENT '最大并发数',
    `weight`              INT          NOT NULL DEFAULT 100 COMMENT '默认权重',
    `health_status`       BIT(1)       NOT NULL DEFAULT b'1' COMMENT '当前健康状态',
    `avg_response_time_ms` BIGINT      COMMENT '平均响应时间（运行时更新）',
    `version_no`          BIGINT       NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_service_capability_deleted` (`service_id`, `capability_tag`, `deleted`),
    KEY `idx_capability_tag_deleted` (`capability_tag`, `deleted`),
    KEY `idx_health_deleted` (`health_status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP 服务能力注册表';
