package com.ymware.engine.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ymware.engine.config.Context;
import com.ymware.engine.common.enums.ErrorCodeEnum;
import com.ymware.engine.common.exception.ApiException;
import com.ymware.engine.service.ConditionGroupService;
import com.ymware.engine.service.RuleService;
import com.ymware.engine.entity.RuleEngineRule;
import com.ymware.engine.store.manager.RuleEngineRuleManager;
import com.ymware.engine.mapper.RuleEngineRuleMapper;
import com.ymware.engine.vo.condition.ConditionGroupConfig;
import com.ymware.engine.vo.condition.ConfigValue;
import com.ymware.engine.vo.rule.general.SaveActionRequest;
import com.ymware.engine.vo.rule.RuleBody;
import com.ymware.engine.vo.user.UserData;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 〈RuleServiceImpl〉
 *
 * @author 丁乾文
 * @date 2021/7/28 1:08 下午
 * @since 1.0.0
 */
@Service
public class RuleServiceImpl implements RuleService {

    @Resource
    private RuleEngineRuleManager ruleEngineRuleManager;
    @Resource
    private ConditionGroupService conditionGroupService;
    @Resource
    private RuleEngineRuleMapper ruleEngineRuleMapper;

    /**
     * 保存结果
     *
     * @param saveActionRequest 保存结果
     * @return 保存结果
     */
    @Override
    public Boolean saveAction(SaveActionRequest saveActionRequest) {
        Long ruleId = saveActionRequest.getRuleId();
        ConfigValue configValue = saveActionRequest.getConfigValue();
        RuleEngineRule ruleEngineRule = this.ruleEngineRuleManager.getById(ruleId);
        if (ruleEngineRule == null) {
            throw new ApiException(ErrorCodeEnum.RULE9999404.getCode(), "不存在规则:{}", ruleId);
        }
        ruleEngineRule.setId(ruleId);
        ruleEngineRule.setActionType(configValue.getType());
        ruleEngineRule.setActionValueType(configValue.getValueType());
        ruleEngineRule.setActionValue(configValue.getValue());
        this.ruleEngineRuleMapper.updateRuleById(ruleEngineRule);
        return true;
    }


    /**
     * 保存规则并返回规则id
     *
     * @param ruleBody 规则体
     * @return 规则id
     */
    @Override
    public Long saveOrUpdateRule(RuleBody ruleBody) {
        RuleEngineRule ruleEngineRule = new RuleEngineRule();
        ruleEngineRule.setId(ruleBody.getId());
        ruleEngineRule.setName(ruleBody.getName());
        ConfigValue action = ruleBody.getAction();
        if (action != null) {
            ruleEngineRule.setActionType(action.getType());
            ruleEngineRule.setActionValueType(action.getValueType());
            ruleEngineRule.setActionValue(action.getValue());
        }
        if (ruleBody.getId() == null) {
            UserData user = Context.getCurrentUser();
            ruleEngineRule.setCreateUserId(user.getId());
            ruleEngineRule.setCreateUserName(user.getUsername());
        }
        this.ruleEngineRuleManager.saveOrUpdate(ruleEngineRule);
        List<ConditionGroupConfig> conditionGroup = ruleBody.getConditionGroup();
        if (CollUtil.isNotEmpty(conditionGroup)) {
            this.conditionGroupService.saveConditionGroup(ruleEngineRule.getId(), conditionGroup);
        }
        return ruleEngineRule.getId();
    }


}
