package com.ymware.engine.cache;

import com.ymware.engine.domain.rule.service.RuleCheckService;

import cn.hutool.core.util.ObjectUtil;
import com.ymware.engine.entity.TagBusinessRule;
import com.ymware.engine.model.dto.TagBusinessRuleDto;
import com.ymware.engine.mapper.TagBusinessRuleMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 监控规则缓存 - 使用Spring TaskScheduler
 */
@Slf4j
@Component
public class RuleExecuteContextCache extends AbstractCache<TagBusinessRuleDto> {

    @Resource
    private TaskScheduler taskScheduler;

    @Resource
    private RuleCheckService ruleCheckService;

    /**
     * 存储调度任务的Future，用于取消任务
     */
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    public synchronized void update(String code, TagBusinessRuleDto value) {
        TagBusinessRuleDto lastBusinessRule = cacheMap.get(value.getRuleCode());
        //修改调度时间时才需要重新生成调度任务
        if (lastBusinessRule == null) {
            //创建调度任务
            scheduleTask(value);
        } else if ("N".equals(value.getIsEnable())) {
            //停止旧的调度任务
            stopTask(value.getRuleCode());
        } else {
            //更新
            if (!value.getScheduleExpress().equals(lastBusinessRule.getScheduleExpress()) ||
                    !value.getActive().equals(lastBusinessRule.getActive())) {
                //停止旧的调度任务
                stopTask(value.getRuleCode());
                //创建新的调度任务
                scheduleTask(value);
            }
        }
        cacheMap.put(value.getRuleCode(), value);
    }

    /**
     * 调度任务
     */
    private void scheduleTask(TagBusinessRuleDto value) {
        if (ObjectUtil.isNull(value.getScheduleExpress())) {
            value.setScheduleExpress("0 0 3/12 * * *");
        }

        String ruleCode = value.getRuleCode();
        Runnable task = () -> {
            try {
                ruleCheckService.checkRule(ruleCode);
            } catch (Exception e) {
                log.error("Failed to execute rule check for: {}", ruleCode, e);
            }
        };

        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(value.getScheduleExpress()));
        scheduledTasks.put(ruleCode, future);
        log.info("Scheduled task for rule: {} with cron: {}", ruleCode, value.getScheduleExpress());
    }

    /**
     * 停止任务
     */
    private void stopTask(String ruleCode) {
        ScheduledFuture<?> future = scheduledTasks.remove(ruleCode);
        if (future != null) {
            future.cancel(false);
            log.info("Stopped scheduled task for rule: {}", ruleCode);
        }
    }

    /**
     * 删除规则
     */
    public synchronized void delete(String ruleCode) {
        if (StringUtils.isEmpty(ruleCode)) {
            log.info("ruleCode is null");
            return;
        }
        //停止旧的调度任务
        stopTask(ruleCode);
        cacheMap.remove(ruleCode);
    }

    @Resource
    private TagBusinessRuleMapper tagBusinessRuleMapper;

    private TagBusinessRuleDto convertToDto(TagBusinessRule rule) {
        TagBusinessRuleDto dto = new TagBusinessRuleDto();
        dto.setId(rule.getId());
        dto.setRuleName(rule.getRuleName());
        dto.setRuleCode(rule.getRuleCode());
        dto.setCronExpression(rule.getCronExpression());
        dto.setScheduleExpress(rule.getScheduleExpress());
        dto.setRuleSql(rule.getRuleSql());
        dto.setDataSourceCode(rule.getDataSourceCode() != null ? rule.getDataSourceCode() : "DEFAULT_DS");
        dto.setDescription(rule.getDescription());
        dto.setRuleType(rule.getRuleType());
        dto.setConditionExpression(rule.getConditionExpression());
        dto.setActionExpression(rule.getActionExpression());
        dto.setTargetTagId(rule.getTargetTagId());
        dto.setPriority(rule.getPriority());
        dto.setEffectiveTime(rule.getEffectiveTime());
        dto.setExpiryTime(rule.getExpiryTime());
        dto.setActive(rule.getActive() != null && rule.getActive() == 1);
        dto.setExecutionCount(rule.getExecutionCount());

        dto.setIsEnable(dto.getActive());
        if (dto.getScheduleExpress() == null) {
            dto.setScheduleExpress(dto.getCronExpression() != null ? dto.getCronExpression() : "0 0 3/12 * * *");
        }
        return dto;
    }

    @Override
    public void init() {
        log.info("Loading active tag business rules from database...");
        try {
            List<TagBusinessRule> rules = tagBusinessRuleMapper.selectList(
                    new QueryWrapper<TagBusinessRule>().eq("is_active", 1)
            );
            if (rules != null) {
                log.info("Found {} active tag business rules to load", rules.size());
                for (TagBusinessRule rule : rules) {
                    try {
                        TagBusinessRuleDto dto = convertToDto(rule);
                        this.update(dto.getRuleCode(), dto);
                        log.info("Successfully loaded and scheduled tag rule: {}", dto.getRuleCode());
                    } catch (Exception e) {
                        log.error("Failed to load tag business rule: {}", rule.getRuleCode(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch tag business rules from database", e);
        }
    }
}
