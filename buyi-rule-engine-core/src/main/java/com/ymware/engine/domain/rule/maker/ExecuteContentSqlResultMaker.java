package com.ymware.engine.domain.rule.maker;

import cn.hutool.core.collection.CollectionUtil;
import com.ymware.engine.model.response.TagRuleExecutionLogResponse;
import com.ymware.engine.domain.rule.model.RuleCheckContext;
import com.ymware.engine.domain.rule.service.RuleSqlExecutor;
import com.ymware.engine.domain.rule.model.DataRow;
import com.ymware.engine.domain.rule.model.TagExecuteRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * SQL结果生产器
 */
@Slf4j
@Service("executeContentSqlResultMaker")
public class ExecuteContentSqlResultMaker extends RuleExecuteContentMaker {

    @Resource
    private RuleSqlExecutor sqlExecutor;


    @Override
    public TagRuleExecutionLogResponse execute(RuleCheckContext ruleCheckContext, TagExecuteRule tagExecuteRule) {
        TagRuleExecutionLogResponse response = new TagRuleExecutionLogResponse();
        List<DataRow> sdRowList = sqlExecutor.executeSql(tagExecuteRule.getSqlExpress(), tagExecuteRule.getDatasourceCode(), ruleCheckContext.getCheckRuleData());
        if (CollectionUtil.isNotEmpty(sdRowList)) {

        }
        return response;
    }
}
