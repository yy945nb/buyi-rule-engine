package com.ymware.engine.service;


import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.entity.RuleEngineCondition;
import com.ymware.engine.entity.RuleEngineInputParameter;
import com.ymware.engine.entity.RuleEngineVariable;
import com.ymware.engine.vo.condition.*;

import java.util.Collection;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/14
 * @since 1.0.0
 */
public interface ConditionService {

    /**
     * 保存条件
     *
     * @param addConditionRequest 条件配置信息
     * @return 条件id
     */
    Long save(AddConditionRequest addConditionRequest);

    /**
     * 条件名称是否存在
     *
     * @param name 条件名称
     * @return true存在
     */
    Boolean conditionNameIsExists(String name);

    /**
     * 根绝id查询条件信息
     *
     * @param id 条件id
     * @return ConditionResponse
     */
    ConditionBody getById(Long id);

    /**
     * 条件转换
     *
     * @param engineCondition engineCondition
     * @return ConditionResponse
     */
    ConditionBody getConditionResponse(RuleEngineCondition engineCondition);

    /**
     * 条件转换
     *
     * @param engineCondition   engineCondition
     * @param variableMap       条件用到的变量
     * @param inputParameterMap 条件用到的规则参数
     * @return ConditionResponse
     */
    ConditionBody getConditionResponse(RuleEngineCondition engineCondition, Map<Long, RuleEngineVariable> variableMap, Map<Long, RuleEngineInputParameter> inputParameterMap);

    /**
     * 条件列表
     *
     * @param pageRequest 分页查询信息
     * @return page
     */
    PageResult<ListConditionResponse> list(PageRequest<ListConditionRequest> pageRequest);

    /**
     * 获取条件中的变量
     *
     * @param ruleEngineConditions 条件信息
     * @return map
     */
    Map<Long, RuleEngineVariable> getConditionVariableMap(Collection<RuleEngineCondition> ruleEngineConditions);

    /**
     * 获取条件中的规则参数
     *
     * @param ruleEngineConditions 条件信息
     * @return map
     */
    Map<Long, RuleEngineInputParameter> getConditionInputParameterMap(Collection<RuleEngineCondition> ruleEngineConditions);

    /**
     * 更新条件
     *
     * @param updateConditionRequest 更新条件
     * @return true
     */
    Boolean update(UpdateConditionRequest updateConditionRequest);

    /**
     * 删除条件
     *
     * @param id 条件id
     * @return true：删除成功
     */
    Boolean delete(Long id);


}
