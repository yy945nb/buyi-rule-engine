package com.ymware.engine.listener;

import com.ymware.engine.common.config.rabbit.RabbitQueueConstants;
import com.ymware.engine.common.config.rabbit.RabbitTopicConstants;
import com.ymware.engine.common.listener.body.GeneralRuleMessageBody;
import com.ymware.engine.common.listener.body.VariableMessageBody;
import com.ymware.engine.listener.event.*;
import com.ymware.engine.entity.RuleEngineSystemLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import jakarta.annotation.Resource;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2020-12-23 17:50
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnBean(RabbitTemplate.class)
@Component
public class EventPublisherListener {


    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 事物结束后，发送mq消息通知需要加载或者移出的规则
     *
     * @param generalRuleEvent 规则事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = GeneralRuleEvent.class)
    public void generalRuleEvent(GeneralRuleEvent generalRuleEvent) {
        GeneralRuleMessageBody ruleMessageBody = generalRuleEvent.getRuleMessageBody();
        this.rabbitTemplate.convertAndSend(RabbitTopicConstants.RULE_EXCHANGE, RabbitTopicConstants.RULE_TOPIC_ROUTING_KEY, ruleMessageBody);
    }

    /**
     * 事物结束后，对变量的操作
     *
     * @param variableEvent 变量事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = VariableEvent.class)
    public void variableEvent(VariableEvent variableEvent) {
        log.info("发送变量消息：" + variableEvent.getVariableMessageBody());
        VariableMessageBody variableMessageBody = variableEvent.getVariableMessageBody();
        this.rabbitTemplate.convertAndSend(RabbitTopicConstants.VAR_EXCHANGE, RabbitTopicConstants.VAR_TOPIC_ROUTING_KEY, variableMessageBody);
    }

    /**
     * 日志消息
     *
     * @param systemLogEvent 日志消息
     */
    @Async
    @EventListener(SystemLogEvent.class)
    public void systemLogEvent(SystemLogEvent systemLogEvent) {
        RuleEngineSystemLog ruleEngineSystemLog = systemLogEvent.getRuleEngineSystemLog();
        this.rabbitTemplate.convertAndSend(RabbitQueueConstants.SYSTEM_LOG_QUEUE, ruleEngineSystemLog);
    }

}
