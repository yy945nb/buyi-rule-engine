package com.ymware.engine.common.config.rabbit;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ Topic 常量定义
 */
@Slf4j
public class RabbitTopicConstants {

    /**
     * 接收规则相关消息
     */
    public static final String RULE_EXCHANGE = "boot_engine_rule_exchange";
    final public static String RULE_TOPIC_ROUTING_KEY = "boot_engine_rule_topic_routingKey";

    /**
     * 接收规则集相关消息
     */
    public static final String RULE_SET_EXCHANGE = "boot_engine_rule_set_exchange";
    final public static String RULE_SET_TOPIC_ROUTING_KEY = "boot_engine_rule_set_topic_routingKey";

    /**
     * 接收决策表相关消息
     */
    public static final String DECISION_TABLE_EXCHANGE = "boot_engine_decision_table_exchange";
    final public static String DECISION_TABLE_TOPIC_ROUTING_KEY = "boot_engine_decision_table_topic_routingKey";

    /**
     * 接收变量相关消息
     */
    public static final String VAR_EXCHANGE = "boot_engine_var_exchange";
    final public static String VAR_TOPIC_ROUTING_KEY = "boot_engine_var_topic_routingKey";


}
