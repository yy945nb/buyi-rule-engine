package com.ymware.engine.domain.rule.maker;

import com.ymware.engine.model.dto.TagBusinessRuleDto;
import com.ymware.engine.model.dto.TagDefinitionDto;
import com.ymware.engine.model.response.TagRuleExecutionLogResponse;
import com.ymware.engine.domain.rule.model.RuleCheckContext;
import com.ymware.engine.domain.rule.service.JexlRuleEvaluator;
import com.ymware.engine.domain.rule.model.TagExecuteRule;
import com.ymware.engine.expression.ExpressionEvaluator;
import com.ymware.engine.domain.rule.model.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java结果生产器 - 使用JEXL表达式引擎
 */
@Slf4j
@Service("executeContentJavaExpressMaker")
public class ExecuteContentJavaExpressMaker extends RuleExecuteContentMaker {

    @Resource
    private JexlRuleEvaluator ruleEngine;

    @Resource
    private ExpressionEvaluator expressionEvaluator;

    /**
     * 匹配${}中的内容
     */
    private static final String REGEX = "(?<=\\$\\{)[^}]*(?=\\})";

    public void makeContent(RuleCheckContext ruleCheckContext,
                            TagDefinitionDto definitionDto) {
        //处理占位符，把${}中的占位符替换成变量值
        String content = handlePlaceholder(definitionDto.getTagBusinessRules(), ruleCheckContext);
    }

    /**
     * 处理占位符（把${}中的占位符替换成变量值）
     */
    private String handlePlaceholder(List<TagBusinessRuleDto> tagBusinessRules, RuleCheckContext ruleCheckContext) {
        Pattern p = Pattern.compile(REGEX);
        StringBuffer contentBuffer = new StringBuffer();
        tagBusinessRules.forEach(rule -> {
            String actionExpression = rule.getActionExpression();
            Matcher matcher = p.matcher(actionExpression);
            int lastIndex = 0;
            while (matcher.find()) {
                String variable = actionExpression.substring(matcher.start(), matcher.end());
                if (ruleCheckContext.getCheckRuleData().containsKey(variable)) {
                    Object data = ruleCheckContext.getCheckRuleData().get(variable);
                    //-2是为了去除${
                    contentBuffer.append(actionExpression.substring(lastIndex, matcher.start() - 2)).append(data.toString());
                    //+1是为了去除}
                    lastIndex = matcher.end() + 1;
                }
            }
            //截取最后一节
            contentBuffer.append(actionExpression.substring(lastIndex, actionExpression.length()));
        });
        return contentBuffer.toString();
    }

    @Override
    public TagRuleExecutionLogResponse execute(RuleCheckContext ruleCheckContext,
                                               TagExecuteRule tagExecuteRule) {
        TagRuleExecutionLogResponse executionLogDto = new TagRuleExecutionLogResponse();
        String expression = tagExecuteRule.getRuleExpress();
        Map<String, Object> bindings = ruleCheckContext.getCheckRuleData();

        // 使用JEXL表达式引擎替代自定义表达式解析器
        boolean triggered = false;
        try {
            ExecutionContext context = new ExecutionContext();
            bindings.forEach(context::setVariable);
            triggered = expressionEvaluator.evaluateBoolean(expression, context);
        } catch (Exception e) {
            log.error("Failed to evaluate expression: {}", expression, e);
        }

        if (triggered) {
            log.info("Rule triggered for expression: {}", expression);
        } else {
            log.info("Rule not triggered for expression: {}", expression);
        }
        executionLogDto.setExecutionStatus(triggered);
        return executionLogDto;
    }
}
