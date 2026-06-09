package com.ymware.engine.domain.rule.maker;

import com.ymware.engine.model.response.TagRuleExecutionLogResponse;
import com.ymware.engine.domain.rule.model.RuleCheckContext;
import com.ymware.engine.domain.rule.model.TagExecuteRule;

/**
 * 规则执行器
 */
public abstract class RuleExecuteContentMaker {

    public abstract TagRuleExecutionLogResponse execute(RuleCheckContext ruleCheckContext,
                                                        TagExecuteRule tagExecuteRule);
}
