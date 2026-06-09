package com.ymware.engine.domain.rule.service;

import com.ymware.engine.cache.RuleExecuteContextCache;

import com.ymware.engine.model.dto.TagBusinessRuleDto;
import com.ymware.engine.domain.rule.model.*;
import com.ymware.engine.domain.rule.maker.ExecuteContentMakerFactory;
import com.ymware.engine.domain.rule.maker.RuleExecuteContentMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规则校验核心类
 */
@Service
@Slf4j
public class RuleCheckService {

    @Resource
    private RuleSqlExecutor sqlExecutor;

    @Resource
    private RuleEngine ruleEngine;

    @Resource
    private ExecuteContentMakerFactory ruleContentMakerFactory;

    @Resource
    private RuleExecuteContextCache ruleExecuteContentCache;

    /**
     * 校验规则
     *
     * @param ruleCode
     */
    public void checkRule(String ruleCode) {
        //获取监控规则
        TagBusinessRuleDto businessRuleDto = ruleExecuteContentCache.get(ruleCode);
        if (businessRuleDto == null) {
            log.info("businessRule is null");
            return;
        }
        //构建监控指标
        List<RuleMetric> ruleMetricList = makeMetrics(businessRuleDto);

        checkRule(businessRuleDto, ruleMetricList);

        //校验监控规则
    }

    /**
     * 校验监控规则
     *
     * @param tagBusinessRuleDto
     * @param ruleMetricList
     */
    public void checkRule(TagBusinessRuleDto tagBusinessRuleDto, List<RuleMetric> ruleMetricList) {
        //1、构建校验上下文
        RuleCheckContext ruleCheckContext = new RuleCheckContext();
        Map<String, Object> busData = new HashMap<String, Object>();
        for (RuleMetric metric : ruleMetricList) {
            busData.put(metric.getMetricCode(), metric.getMetricData());
        }
        ruleCheckContext.setCheckRuleData(busData);
        //校验规则
        Boolean checkResult = ruleEngine.checkRule(ruleCheckContext, tagBusinessRuleDto.getConditionExpression());
        if (checkResult && tagBusinessRuleDto.getActionExpression() != null) {
            //规则命中
            TagExecuteRule tagExecuteRule = new TagExecuteRule();
            tagExecuteRule.setRuleCode(tagBusinessRuleDto.getRuleCode());
            tagExecuteRule.setRuleExpress(tagBusinessRuleDto.getConditionExpression());
            tagExecuteRule.setSqlExpress(tagBusinessRuleDto.getActionExpression());
            tagExecuteRule.setDatasourceCode("DEFAULT_DS");

            RuleExecuteContentMaker maker = ruleContentMakerFactory.getRuleContentMaker(tagBusinessRuleDto.getRuleType() == 1 ? "SQL_RESULT" : "JAVA_EXPRESS");
            maker.execute(ruleCheckContext, tagExecuteRule);
        }
    }

    /**
     * 构建监控指标
     */
    public List<RuleMetric> makeMetrics(TagBusinessRuleDto tagExecuteRule) {
        //获取监控指标对应的数据
        String sqlExpress = tagExecuteRule.getActionExpression();
        if (sqlExpress != null && !sqlExpress.isEmpty()) {
            List<String> sqlList = sqlExpressLinesToList(sqlExpress);
            if (CollectionUtils.isEmpty(tagExecuteRule.getSqlContextList())) {
                tagExecuteRule.setSqlContextList(new ArrayList<>());
            }
            sqlList.forEach(m -> {
                SqlContext sqlContext = new SqlContext();
                sqlContext.setSqlExpress(m);
                sqlContext.setDataSourceCode(tagExecuteRule.getDataSourceCode());
                sqlContext.setParams(tagExecuteRule.getParams());
                tagExecuteRule.getSqlContextList().add(sqlContext);
            });
        }
        List<SqlContext> sqlContextList = tagExecuteRule.getSqlContextList();
        if (CollectionUtils.isEmpty(sqlContextList)) {
            log.error("sdSqlContentList is null");
            throw new RuntimeException("sdSqlContentList is not null");
        }
        List<RuleMetric> metricList = new ArrayList<RuleMetric>();
        //构建指标
        for (SqlContext context : sqlContextList) {
            String sql = context.getSqlExpress();
            if (context.getParams() == null) {
                context.setParams(new HashMap<String, Object>());
            }
            //获取监控来源数据
            List<DataRow> rowList = sqlExecutor.executeSql(sql, context.getDataSourceCode(),context.getParams());
            List<RuleMetric> metrics = makeMetricList(rowList);
            //设置监控指标对应的参数
            metrics.forEach(m -> {
                if (!context.getParams().containsKey(m.getMetricCode())) {
                    context.getParams().put(m.getMetricCode(), m.getMetricData());
                }
            });
            //设置监控指标对应的数据
            metricList.addAll(metrics);
        }

        return metricList;
    }


    /**
     * 根据查询结果构建监控指标
     *
     * @param dataRowList
     */
    public List<RuleMetric> makeMetricList(List<DataRow> dataRowList) {
        List<RuleMetric> metricList = new ArrayList<RuleMetric>();
        if (CollectionUtils.isEmpty(dataRowList)) {
            log.info("dataRowList is null");
            return metricList;
        }
        DataRow sdRow = dataRowList.get(0);
        for (DataColumn sdColumn : sdRow.getColumnMap().values()) {
            RuleMetric sdMetric = new RuleMetric();
            sdMetric.setMetricCode(sdColumn.getColName());
            if(sdColumn.getColName().contains("spu_id")){
                sdMetric.setMetricType(1);
            }
            sdMetric.setMetricData(sdColumn.getColValue());
            metricList.add(sdMetric);
        }
        return metricList;
    }

    /**
     * 将sql表达式转换为sql列表
     *
     * @param sqlExpress
     * @return
     */
    private List<String> sqlExpressLinesToList(String sqlExpress) {
        List<String> sqlList = new ArrayList<String>();
        String[] sqlLines = sqlExpress.split(";");
        for (String sqlLine : sqlLines) {
            if (sqlLine != null && !sqlLine.isEmpty()) {
                sqlList.add(sqlLine);
            }
        }
        return sqlList;
    }
}
