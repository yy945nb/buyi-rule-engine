package com.ymware.engine.service;

import com.ymware.engine.domain.rule.service.RuleParameter;
import com.ymware.engine.vo.reference.ReferenceData;
import com.ymware.engine.vo.reference.ReferenceDataMap;
import com.ymware.engine.vo.rule.general.GeneralRuleBody;

import java.util.Collection;
import java.util.Map;

/**
 * 〈DataReferenceService〉
 *
 * @author 丁乾文
 * @date 2021/7/27 2:21 下午
 * @since 1.0.0
 */
public interface DataReferenceService {

    /**
     * 是否有引用这个数据
     *
     * @param type      元素、变量、条件、规则等
     * @param refDataId 元素id...
     */
    void validDataReference(Integer type, Long refDataId);

    /**
     * 保存规则引用的基础数据
     *
     * @param generalRuleBody g
     * @param version         版本
     */
    void saveDataReference(GeneralRuleBody generalRuleBody, String version);


    /**
     * 引用的参数
     *
     * @param referenceData r
     * @return map
     */
    Map<String, RuleParameter> referenceInputParamList(ReferenceData referenceData);


    /**
     * 更新到开发状态
     *
     * @param dataType d
     * @param dataId   id
     */
    void updateToDevStatus(Integer dataType, Long dataId);

    /**
     * 缓存数据到ReferenceDataMap
     *
     * @param dataType 数据类型
     * @param dataId   id
     * @param version  版本号
     * @return r
     */
    ReferenceDataMap getReferenceDataMap(Integer dataType, Long dataId, String version);

    /**
     * 获取规则集请求参数
     *
     * @param id      规则集id
     * @param version 版本
     * @return param
     */
    Collection<RuleParameter> getRuleSetParameters(Long id, String version);

    /**
     * 获取规则请求参数
     *
     * @param id      规则集id
     * @param version 版本
     * @return param
     */
    Collection<RuleParameter> getGeneralRuleParameters(Long id, String version);
}
