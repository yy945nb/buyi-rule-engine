package com.ymware.engine.domain.rule.service;

import com.ymware.engine.domain.rule.model.RuleCheckContext;
import com.ymware.engine.exception.EngineException;
import com.ymware.engine.expression.JexlEngineFactory;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymware.engine.entity.TagRuleExecutionLog;
import com.ymware.engine.model.dto.TagBusinessRuleDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类BaseEngine.java的实现描述：规则引擎（自动结案、复核、转人工审核）
 */
@Slf4j
@Component
public class RuleEngine {

    /**
     * JSON对象转换器
     */
    private ObjectMapper objectMapper;

    /**
     * 表达式执行引擎
     */
    protected JexlEngine engine;

    public RuleEngine() {
        engine = JexlEngineFactory.createDefault();
        objectMapper = new ObjectMapper();
    }

    /**
     * 执行业务规则
     *
     * @param ruleCheckContext
     * @param ruleExpression
     * @return
     */
    public Boolean checkRule(RuleCheckContext ruleCheckContext, String ruleExpression) {
        //根据模型创建规则校验上下文环境
        JexlContext context = new MapContext();
        setMapContextData(context, ruleCheckContext);
        //把变量直接获取出来，这样表达式中使用变量时不用带busData前缀
        if (ruleCheckContext.getCheckRuleData() != null) {
            for (String key : ruleCheckContext.getCheckRuleData().keySet()) {
                context.set(key, ruleCheckContext.getCheckRuleData().get(key));
            }
        }
        JexlScript jexlScript = engine.createScript(ruleExpression);
        Object result = jexlScript.execute(context);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result == null) {
            return false;
        }
        // Coerce non-boolean results
        if (result instanceof Number) {
            return ((Number) result).doubleValue() != 0.0;
        }
        String str = result.toString();
        return !str.isEmpty() && !str.equalsIgnoreCase("false");
    }

    /**
     * 执行业务规则
     *
     * @param busRuleCheckContext
     * @param ruleExpression
     * @return
     */
    public String executeScript(RuleCheckContext busRuleCheckContext, String ruleExpression) {
        //根据模型创建规则校验上下文环境
        JexlContext context = getContext(busRuleCheckContext, null);
        JexlScript jexlScript = engine.createScript(ruleExpression);
        String evaluateResult = (String) jexlScript.execute(context);
        return evaluateResult;
    }


    /**
     * 根据参数创建上下文环境
     *
     * @param model
     * @param args
     * @return
     */
    private <T> JexlContext getContext(T model, Object... args) {
        JexlContext context = null;
        if (args != null && args.length >= 1) {
            //如果使用除模型对象外的其他参数，使用MapContext,在写表达式时，必须写上变量名，变量名默认是首字母小写的类名；
            context = new MapContext();
            setMapContextData(context, model);
            for (Object arg : args) {
                setMapContextData(context, arg);
            }
        } else {
            context = new ObjectContext<T>(engine, model);
        }
        return context;
    }

    /**
     * 初始化对象设值
     *
     * @param context
     * @param obj
     */
    private void setMapContextData(JexlContext context, Object obj) {
        Field srcFields[] = obj.getClass().getDeclaredFields();
        if (srcFields == null || srcFields.length == 0) {
            return;
        }
        for (int i = 0; i < srcFields.length; i++) {
            Field srcField = srcFields[i];
            try {
                srcField.setAccessible(true);
                Object property = srcField.get(obj);
                if (srcField.getName().equals("serialVersionUID") || property == null) {
                    continue;
                }
                context.set(srcField.getName(), property);
            } catch (Exception e) {
                log.warn("initMapContext:{} Exception erro,e:{}", srcField.getName(), e);
            }
        }
    }

    /**
     * 执行jexl表达式
     *
     * @param jexlExp 表达式
     * @param context 上下文环境
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T evaluate(JexlExpression jexlExp, JexlContext context, Class<T> cls) {
        return (T) jexlExp.evaluate(context);
    }

    /**
     * 字符串解析成表达式
     *
     * @param expression
     */
    public JexlExpression parseExpression(String expression) {
        return engine.createExpression(expression);
    }


    /**
     * 解析规则并生成SQL查询
     */
    public String parseRuleToSQL(TagBusinessRuleDto rule) {
        try {
            Map<String, Object> conditionMap = objectMapper.readValue(rule.getConditionExpression(), Map.class);
            String whereClause = buildWhereClause(conditionMap);
            return String.format(rule.getRuleSql() + " WHERE %s", whereClause);
        } catch (Exception e) {
            throw new EngineException("规则解析失败", e);
        }
    }

    /**
     * 构建WHERE子句
     */
    private String buildWhereClause(Map<String, Object> conditionMap) {
        List<String> conditions = new ArrayList<>();
        parseConditions(conditionMap, conditions);
        return String.join(" AND ", conditions);
    }

    /**
     * 解析条件
     */
    private void parseConditions(Map<String, Object> condition, List<String> conditions) {
        String type = (String) condition.get("type");
        switch (type) {
            case "and":
                List<Map<String, Object>> andRules = (List<Map<String, Object>>) condition.get("rules");
                for (Map<String, Object> rule : andRules) {
                    parseConditions(rule, conditions);
                }
                break;

            case "or":
                List<Map<String, Object>> orRules = (List<Map<String, Object>>) condition.get("rules");
                List<String> orConditions = new ArrayList<>();
                for (Map<String, Object> r : orRules) {
                    List<String> temp = new ArrayList<>();
                    parseConditions(r, temp);
                    orConditions.addAll(temp);
                }
                conditions.add("(" + String.join(" OR ", orConditions) + ")");
                break;

            case "comparison":
                String field = (String) condition.get("field");
                String operator = (String) condition.get("operator");
                Object value = condition.get("value");
                conditions.add(buildComparison(field, operator, value));
                break;

            case "range":
                String rangeField = (String) condition.get("field");
                Object min = condition.get("min");
                Object max = condition.get("max");
                conditions.add(buildRange(rangeField, min, max));
                break;

            default:
                throw new IllegalArgumentException("未知条件类型: " + type);
        }
    }

    /**
     * 构建比较条件
     */
    private String buildComparison(String field, String operator, Object value) {
        String sqlOperator = getSqlOperator(operator);
        String formattedValue = formatValue(value, operator);
        return String.format("%s %s %s", field, sqlOperator, formattedValue);
    }

    /**
     * 构建范围条件
     */
    private String buildRange(String field, Object min, Object max) {
        return String.format("%s BETWEEN %s AND %s", field, formatValue(min, null), formatValue(max, null));
    }

    /**
     * 获取SQL操作符
     */
    private String getSqlOperator(String operator) {
        switch (operator) {
            case "equal":
                return "=";
            case "not_equal":
                return "!=";
            case "greater_than":
                return ">";
            case "less_than":
                return "<";
            case "greater_than_equal":
                return ">=";
            case "less_than_equal":
                return "<=";
            case "contains":
                return "LIKE";
            default:
                throw new IllegalArgumentException("未知操作符: " + operator);
        }
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value, String operator) {
        if (value instanceof String) {
            // Escape single quotes to prevent SQL injection
            String escaped = ((String) value).replace("'", "''");
            if ("contains".equals(operator)) {
                return "'%" + escaped + "%'";
            }
            return "'" + escaped + "'";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            String values = collection.stream()
                    .map(v -> formatValue(v, operator))
                    .collect(Collectors.joining(", "));
            return "(" + values + ")";
        } else {
            return value.toString();
        }
    }

    /**
     * 执行规则并分配标签
     */
    public TagRuleExecutionLog executeRule(TagBusinessRuleDto rule, List<Map<String, Object>> mapList) {
        TagRuleExecutionLog result = new TagRuleExecutionLog();
        result.setRuleId(rule.getId());
        result.setStartTime(LocalDateTime.now());

        try {
            int affectedUsers = 0;

            for (Map<String, Object> userData : mapList) {
                boolean conditionMet = evaluateCondition(rule, userData);
                if (conditionMet) {
                    boolean actionSuccess = executeAction(rule, userData);
                    if (actionSuccess) {
                        affectedUsers++;
                    }
                }

                result.setAffectedUsers(affectedUsers);
                result.setExecutionStatus("SUCCESS");
            }
        } catch (Exception e) {
            result.setExecutionStatus("FAILED");
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 评估条件
     */
    private boolean evaluateCondition(TagBusinessRuleDto rule, Map<String,Object> params) {
        try {
            Map<String, Object> conditionMap = objectMapper.readValue(rule.getConditionExpression(), Map.class);
            return evaluateConditionMap(conditionMap, params);
        } catch (Exception e) {
            throw new EngineException("条件评估失败", e);
        }
    }

    /**
     * 评估条件映射
     */
    private boolean evaluateConditionMap(Map<String, Object> condition, Map<String, Object> paramsData) {
        String type = (String) condition.get("type");
        switch (type) {
            case "and":
                List<Map<String, Object>> rules = (List<Map<String, Object>>) condition.get("rules");
                for (Map<String, Object> rule : rules) {
                    if (!evaluateConditionMap(rule, paramsData)) {
                        return false;
                    }
                }
                return true;

            case "or":
                List<Map<String, Object>> orRules = (List<Map<String, Object>>) condition.get("rules");
                for (Map<String, Object> r : orRules) {
                    if (evaluateConditionMap(r, paramsData)) {
                        return true;
                    }
                }
                return false;

            case "comparison":
                String field = (String) condition.get("field");
                String operator = (String) condition.get("operator");
                Object value = condition.get("value");
                return evaluateComparison(field, operator, value, paramsData);

            case "range":
                String rangeField = (String) condition.get("field");
                Object min = condition.get("min");
                Object max = condition.get("max");
                return evaluateRange(rangeField, min, max, paramsData);

            default:
                throw new IllegalArgumentException("未知条件类型: " + type);
        }
    }

    /**
     * 评估比较条件
     */
    private boolean evaluateComparison(String field, String operator, Object value, Map<String, Object> userData) {
        Object userValue = userData.get(field);

        if (userValue == null) {
            return false;
        }

        switch (operator) {
            case "equal":
                return ObjectUtil.equals(userValue, value);
            case "not_equal":
                return !ObjectUtil.equals(userValue, value);
            case "greater_than":
                return Double.parseDouble(userValue.toString()) > Double.parseDouble(value.toString());
            case "less_than":
                return Double.parseDouble(userValue.toString()) < Double.parseDouble(value.toString());
            case "contains":
                return userValue.toString().contains(value.toString());
            default:
                return false;
        }
    }

    /**
     * 评估范围条件
     */
    private boolean evaluateRange(String field, Object min, Object max, Map<String, Object> userData) {
        Object userValue = userData.get(field);

        if (userValue == null) {
            return false;
        }

        double userNum = Double.parseDouble(userValue.toString());
        double minNum = Double.parseDouble(min.toString());
        double maxNum = Double.parseDouble(max.toString());

        return userNum >= minNum && userNum <= maxNum;
    }

    /**
     * 执行动作
     */
    private boolean executeAction(TagBusinessRuleDto rule, Map<String, Object> objectMap) {
        try {
            Map<String, Object> actionMap = objectMapper.readValue(rule.getActionExpression(), Map.class);
            String actionType = (String) actionMap.get("type");
            switch (actionType) {
                case "assign_tag":
                    return assignTag(rule, objectMap);
                default:
                    return false;
            }
        } catch (Exception e) {
            throw new EngineException("动作执行失败", e);
        }
    }

    /**
     * 分配标签
     */
    private boolean assignTag(TagBusinessRuleDto tag, Map<String, Object> userData) {
        log.info("为用户分配标签: {}", tag.getRuleCode());
        return true;
    }


}
