package com.ymware.engine.compute.function;

import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;
import com.ymware.engine.annotation.Param;
import com.ymware.engine.domain.rule.service.RuleSqlExecutor;
import com.ymware.engine.domain.rule.model.DataRow;
import com.ymware.engine.domain.rule.model.DataColumn;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL data query function.
 */
@Slf4j
@Function("sqlExecutorFunction")
public class SqlFunction {

    @Resource
    private RuleSqlExecutor ruleSqlExecutor;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Executor
    public String executor(@Param(value = "sql") String sql,
                           @Param(value = "dataSourceCode", required = false) String dataSourceCode,
                           @Param(value = "params", required = false) Map<String, Object> params) {
        String dsCode = (dataSourceCode == null || dataSourceCode.trim().isEmpty()) ? "DEFAULT_DS" : dataSourceCode;
        if (log.isDebugEnabled()) {
            log.debug("Executing SQL function on DS: {}, SQL: {}, params: {}", dsCode, sql, params);
        }
        Map<String, Object> sqlParams = params != null ? params : new HashMap<>();
        try {
            List<DataRow> rows = ruleSqlExecutor.executeSql(sql, dsCode, sqlParams);
            // If the query returns a single value (1 row, 1 column), return it directly.
            if (rows.size() == 1) {
                DataRow firstRow = rows.get(0);
                if (firstRow.getColumnMap() != null && firstRow.getColumnMap().size() == 1) {
                    DataColumn col = firstRow.getColumnMap().values().iterator().next();
                    return col.getColValue() != null ? col.getColValue().toString() : null;
                }
            }
            
            // Format list of rows as Map list for JSON serialization
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (DataRow row : rows) {
                Map<String, Object> rowMap = new HashMap<>();
                if (row.getColumnMap() != null) {
                    for (Map.Entry<String, DataColumn> entry : row.getColumnMap().entrySet()) {
                        rowMap.put(entry.getKey(), entry.getValue().getColValue());
                    }
                }
                resultList.add(rowMap);
            }
            return OBJECT_MAPPER.writeValueAsString(resultList);
        } catch (Exception e) {
            log.error("SQL execution function failed", e);
            throw new RuntimeException("SQL execution failed: " + e.getMessage(), e);
        }
    }
}
