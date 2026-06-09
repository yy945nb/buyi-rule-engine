package com.ymware.engine.service;


import com.ymware.engine.domain.rule.model.ConditionSet;
import com.ymware.engine.vo.condition.ConditionGroupConfig;

import java.util.List;


public interface ConditionSetService {


    /**
     * 获取规则配置条件集，懒得写的，待优化
     *
     * @param conditionGroup 条件组配置
     * @return 条件集
     */
    ConditionSet loadConditionSet(List<ConditionGroupConfig> conditionGroup);

    /**
     * 获取规则配置条件集，懒得写的，待优化
     *
     * @param ruleId 规则id
     * @return 条件集
     */
    ConditionSet loadConditionSet(Long ruleId);

}

