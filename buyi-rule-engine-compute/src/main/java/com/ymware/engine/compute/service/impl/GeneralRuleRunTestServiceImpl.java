package com.ymware.engine.compute.service.impl;

import com.ymware.engine.compute.service.RunTestService;
import com.ymware.engine.entity.RuleEngineGeneralRulePublish;
import com.ymware.engine.service.RuleEngineGeneralRulePublishManager;
import com.ymware.engine.domain.rule.model.RunTestRequest;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.domain.rule.service.DefaultInput;
import com.ymware.engine.domain.rule.service.Container;
import com.ymware.engine.domain.rule.service.GeneralRuleEngine;
import com.ymware.engine.exception.ValidException;
import com.ymware.engine.domain.rule.service.GeneralRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/26
 * @since 1.0.0
 */
@Primary
@Slf4j
@Service
public class GeneralRuleRunTestServiceImpl implements RunTestService {

    @Resource
    private RuleEngineConfiguration ruleEngineConfiguration;
    @Resource
    private RuleEngineGeneralRulePublishManager ruleEngineGeneralRulePublishManager;

    /**
     * 规则模拟运行
     *
     * @param runTestRequest 规则参数信息
     * @return result
     */
    @Override
    public Object run(RunTestRequest runTestRequest) {
        log.info("模拟运行规则：{}", runTestRequest.getCode());
        RuleEngineGeneralRulePublish rulePublish = this.ruleEngineGeneralRulePublishManager.lambdaQuery()
                .eq(RuleEngineGeneralRulePublish::getVersion, runTestRequest.getVersion())
                .eq(RuleEngineGeneralRulePublish::getGeneralRuleCode, runTestRequest.getCode())
                .eq(RuleEngineGeneralRulePublish::getWorkspaceCode, runTestRequest.getWorkspaceCode())
                .one();
        if (rulePublish == null) {
            throw new ValidException("找不到可运行的规则数据:{},{},{}", runTestRequest.getWorkspaceCode(), runTestRequest.getCode(), runTestRequest.getVersion());
        }
        Input input = new DefaultInput(runTestRequest.getParam());
        log.info("初始化规则引擎");
        RuleEngineConfiguration ruleEngineConfiguration = new RuleEngineConfiguration();
        Container.Body<GeneralRule> generalRuleContainer = ruleEngineConfiguration.getGeneralRuleContainer();
        GeneralRule rule = GeneralRule.buildRule(rulePublish.getData());
        generalRuleContainer.add(rule);
        GeneralRuleEngine engine = new GeneralRuleEngine(ruleEngineConfiguration);
        // 加载变量
        engine.getConfiguration().setEngineVariable(this.ruleEngineConfiguration.getEngineVariable());
        return engine.execute(input, runTestRequest.getWorkspaceCode(), runTestRequest.getCode());
    }

}
