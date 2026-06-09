package com.ymware.engine.domain.value.model;

import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.rule.service.RuleSqlExecutor;
import com.ymware.engine.domain.rule.model.DataRow;
import com.ymware.engine.domain.rule.model.DataColumn;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A dynamic SQL-driven variable value that implements the rule-engine-open Value interface.
 */
public class SqlVariable implements Value {

    private final String sql;
    private final String dataSourceCode;
    private final ValueType valueType;
    private RuleSqlExecutor ruleSqlExecutor;

    public SqlVariable(String sql, String dataSourceCode, ValueType valueType) {
        this.sql = sql;
        this.dataSourceCode = (dataSourceCode == null || dataSourceCode.trim().isEmpty()) ? "DEFAULT_DS" : dataSourceCode;
        this.valueType = valueType;
    }

    @Override
    public Object getValue(Input input, RuleEngineConfiguration configuration) {
        if (ruleSqlExecutor == null) {
            // Retrieve RuleSqlExecutor bean from ApplicationContext
            ApplicationContext ctx = getApplicationContext();
            if (ctx != null) {
                ruleSqlExecutor = ctx.getBean(RuleSqlExecutor.class);
            }
        }

        if (ruleSqlExecutor == null) {
            throw new IllegalStateException("RuleSqlExecutor is not available in the Spring context");
        }

        Map<String, Object> params = input != null ? input.getAll() : new HashMap<>();
        List<DataRow> rows = ruleSqlExecutor.executeSql(sql, dataSourceCode, params);
        if (rows != null && !rows.isEmpty()) {
            DataRow firstRow = rows.get(0);
            if (firstRow.getColumnMap() != null && !firstRow.getColumnMap().isEmpty()) {
                DataColumn col = firstRow.getColumnMap().values().iterator().next();
                return col.getColValue();
            }
        }
        return null;
    }

    @Override
    public ValueType getValueType() {
        return this.valueType;
    }

    private ApplicationContext getApplicationContext() {
        try {
            Class<?> contextClass = Class.forName("com.ymware.engine.config.Context");
            return (ApplicationContext) contextClass.getMethod("getApplicationContext").invoke(null);
        } catch (Exception e) {
            return null;
        }
    }
}
