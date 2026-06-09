package com.ymware.engine.domain.rule.maker;

import com.ymware.engine.domain.rule.model.RuleExecuteResultTypeEnum;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 通知内容生成器工厂
 */
@Component
public class ExecuteContentMakerFactory {

    @Resource
    private ExecuteContentJavaExpressMaker executeContentJavaExpressMaker;

    @Resource
    private ExecuteContentSqlResultMaker executeContentSqlResultMaker;

    /**
     * 获取标签生成器
     *
     * @param type
     * @return
     */
    public RuleExecuteContentMaker getRuleContentMaker(String type) {
        if (RuleExecuteResultTypeEnum.JAVA_EXPRESS.getCode().equals(type)) {
            return executeContentJavaExpressMaker;
        } else if (RuleExecuteResultTypeEnum.SQL_RESULT.getCode().equals(type)) {
            return executeContentSqlResultMaker;
        }

        return null;
    }
}
