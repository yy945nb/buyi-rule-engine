package com.ymware.engine.controller.api;

import com.ymware.engine.domain.rule.model.DataColumn;
import com.ymware.engine.domain.rule.model.DataRow;
import com.ymware.engine.domain.rule.service.RuleSqlExecutor;
import com.ymware.engine.model.response.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * SQL执行API - 对外暴露数据查询/更新能力
 */
@Slf4j
@Tag(name = "SQL执行")
@RestController
@RequestMapping("api/sql")
public class SqlController {

    @Resource
    private RuleSqlExecutor ruleSqlExecutor;

    /**
     * 执行查询SQL（SELECT）
     *
     * @param request 包含 sql, dataSourceCode, params
     * @return 查询结果行列表
     */
    @Operation(summary = "执行查询SQL")
    @PostMapping("query")
    public RestResult<Map<String, Object>> query(@RequestBody SqlRequest request) {
        try {
            List<DataRow> rows = ruleSqlExecutor.executeSql(
                    request.getSql(), request.getDataSourceCode(), request.getParams());

            List<Map<String, Object>> rowList = new ArrayList<>();
            for (DataRow row : rows) {
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (Map.Entry<String, DataColumn> entry : row.getColumnMap().entrySet()) {
                    rowMap.put(entry.getKey(), entry.getValue().getColValue());
                }
                rowList.add(rowMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("rows", rowList);
            data.put("rowCount", rowList.size());
            return RestResult.ok(data);
        } catch (Exception e) {
            log.error("SQL query failed", e);
            return RestResult.failed(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 执行更新SQL（INSERT/UPDATE/DELETE）
     *
     * @param request 包含 sql, dataSourceCode, params
     * @return 受影响行数
     */
    @Operation(summary = "执行更新SQL")
    @PostMapping("update")
    public RestResult<Map<String, Object>> update(@RequestBody SqlRequest request) {
        try {
            int affectedRows = ruleSqlExecutor.executeUpdate(
                    request.getSql(), request.getDataSourceCode(), request.getParams());

            Map<String, Object> data = new HashMap<>();
            data.put("affectedRows", affectedRows);
            return RestResult.ok(data);
        } catch (Exception e) {
            log.error("SQL update failed", e);
            return RestResult.failed(500, "执行失败: " + e.getMessage());
        }
    }

    /**
     * SQL请求体
     */
    @lombok.Data
    public static class SqlRequest {
        /** SQL语句（支持 :paramName 命名参数） */
        private String sql;
        /** 数据源编码 */
        private String dataSourceCode;
        /** 参数映射 */
        private Map<String, Object> params;
    }
}
