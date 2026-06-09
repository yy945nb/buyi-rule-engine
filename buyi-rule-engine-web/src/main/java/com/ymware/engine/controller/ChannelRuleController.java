package com.ymware.engine.controller;

import com.ymware.engine.common.vo.BaseResult;
import com.ymware.engine.common.vo.PlainResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ymware.engine.entity.RuleEngineFlow;
import com.ymware.engine.entity.TagBusinessRule;
import com.ymware.engine.entity.TagRuleExecutionLog;
import com.ymware.engine.domain.rule.service.RuleEngineService;
import com.ymware.engine.cache.RuleExecuteContextCache;
import com.ymware.engine.model.dto.TagBusinessRuleDto;
import com.ymware.engine.mapper.RuleEngineFlowMapper;
import com.ymware.engine.mapper.TagBusinessRuleMapper;
import com.ymware.engine.mapper.TagRuleExecutionLogMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Controller to configure and manage TagBusinessRules and RuleEngineFlows.
 */
@Tag(name = "通道/标签规则与工作流控制器")
@RestController
@RequestMapping("ruleEngine/channel")
public class ChannelRuleController {

    @Resource
    private TagBusinessRuleMapper tagBusinessRuleMapper;

    @Resource
    private TagRuleExecutionLogMapper tagRuleExecutionLogMapper;

    @Resource
    private RuleEngineFlowMapper ruleEngineFlowMapper;

    @Resource
    private RuleEngineService ruleFlowService;

    @Resource
    private RuleExecuteContextCache ruleExecuteContextCache;

    @Operation(summary = "保存/修改标签规则")
    @PostMapping("tagRule/save")
    public BaseResult saveTagRule(@RequestBody TagBusinessRule tagRule) {
        PlainResult<Boolean> result = new PlainResult<>();
        if (tagRule.getId() == null) {
            tagRule.setCreateTime(new Date());
            tagRule.setUpdateTime(new Date());
            tagBusinessRuleMapper.insert(tagRule);
        } else {
            tagRule.setUpdateTime(new Date());
            tagBusinessRuleMapper.updateById(tagRule);
        }

        // Convert and update cache
        TagBusinessRule fresh = tagBusinessRuleMapper.selectById(tagRule.getId());
        TagBusinessRuleDto dto = convertToDto(fresh);
        if (fresh.getActive() != null && fresh.getActive() == 1) {
            ruleExecuteContextCache.update(dto.getRuleCode(), dto);
        } else {
            ruleExecuteContextCache.delete(dto.getRuleCode());
        }

        result.setData(true);
        return result;
    }

    @Operation(summary = "列表查询标签规则")
    @PostMapping("tagRule/list")
    public BaseResult listTagRules() {
        PlainResult<List<TagBusinessRule>> result = new PlainResult<>();
        result.setData(tagBusinessRuleMapper.selectList(new QueryWrapper<>()));
        return result;
    }

    @Operation(summary = "删除标签规则")
    @PostMapping("tagRule/delete")
    public BaseResult deleteTagRule(@RequestParam Long id) {
        PlainResult<Boolean> result = new PlainResult<>();
        TagBusinessRule rule = tagBusinessRuleMapper.selectById(id);
        if (rule != null) {
            tagBusinessRuleMapper.deleteById(id);
            ruleExecuteContextCache.delete(rule.getRuleCode());
        }
        result.setData(true);
        return result;
    }

    @Operation(summary = "保存/修改工作流（DAG流）")
    @PostMapping("flow/save")
    public BaseResult saveFlow(@RequestBody RuleEngineFlow flow) {
        PlainResult<Boolean> result = new PlainResult<>();
        ruleFlowService.saveFlow(flow);
        result.setData(true);
        return result;
    }

    @Operation(summary = "列表查询工作流")
    @PostMapping("flow/list")
    public BaseResult listFlows() {
        PlainResult<List<RuleEngineFlow>> result = new PlainResult<>();
        result.setData(ruleEngineFlowMapper.selectList(new QueryWrapper<>()));
        return result;
    }

    @Operation(summary = "发布工作流")
    @PostMapping("flow/publish")
    public BaseResult publishFlow(@RequestParam String flowCode) {
        PlainResult<Boolean> result = new PlainResult<>();
        ruleFlowService.publishFlow(flowCode);
        result.setData(true);
        return result;
    }

    @Operation(summary = "删除工作流")
    @PostMapping("flow/delete")
    public BaseResult deleteFlow(@RequestParam Long id) {
        PlainResult<Boolean> result = new PlainResult<>();
        RuleEngineFlow flow = ruleEngineFlowMapper.selectById(id);
        if (flow != null) {
            ruleEngineFlowMapper.deleteById(id);
            ruleFlowService.saveFlow(flow.setDeleted(1));
        }
        result.setData(true);
        return result;
    }

    @Operation(summary = "列表查询执行日志")
    @PostMapping("logs/list")
    public BaseResult listLogs() {
        PlainResult<List<TagRuleExecutionLog>> result = new PlainResult<>();
        result.setData(tagRuleExecutionLogMapper.selectList(new QueryWrapper<>()));
        return result;
    }

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
            dto.setScheduleExpress(dto.getCronExpression() != null ? dto.getCronExpression() : "0 0 3/12 * * * ");
        }
        return dto;
    }
}
