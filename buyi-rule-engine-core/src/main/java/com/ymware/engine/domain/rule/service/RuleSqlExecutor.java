package com.ymware.engine.domain.rule.service;

import com.ymware.engine.config.DataSourceFactory;

import com.ymware.engine.domain.rule.model.DataColumn;
import com.ymware.engine.domain.rule.model.DataRow;
import com.ymware.engine.domain.rule.model.ResourceNotFoundException;
import com.ymware.engine.exception.EngineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class RuleSqlExecutor {

    @Resource
    private DataSourceFactory dataSourceFactory;

    /**
     * 查询数据
     *
     * @param sql
     * @return
     */
    public List<DataRow> executeSql(String sql, String dataSourceCode, Map<String, Object> paramsMap) {
        List<DataRow> sdRowList = new ArrayList<DataRow>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (dataSourceCode == null || dataSourceCode.isEmpty()) {
                throw new IllegalArgumentException("dataSourceCode is null");
            }
            DataSource dataSource = dataSourceFactory.getDataSource(dataSourceCode);
            if (dataSource == null) {
                throw new ResourceNotFoundException("dataSource", dataSourceCode);
            }
            NamedParamSql named = parseNamedParams(sql);
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(named.getSqlWithPlaceholders());
            bindParams(stmt, named.getParamNamesInOrder(), paramsMap);
            rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                DataRow row = new DataRow();
                Map<String, DataColumn> columnMap = new HashMap<String, DataColumn>();
                row.setColumnMap(columnMap);
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    int colIndex = i + 1;
                    DataColumn column = new DataColumn();
                    column.setColIndex(colIndex);
                    column.setColName(metaData.getColumnName(colIndex));
                    column.setColType(metaData.getColumnTypeName(colIndex));
                    column.setColValue(rs.getObject(colIndex));
                    columnMap.put(column.getColName(), column);
                }
                sdRowList.add(row);
            }
        } catch (SQLException e) {
            log.error("executeSql error", e);
            throw new EngineException("executeSql failed: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            rs = null;
            closeStatement(stmt);
            stmt = null;
            closeConnection(conn);
            conn = null;
        }
        return sdRowList;
    }

    /**
     * 执行更新（INSERT/UPDATE/DELETE）
     *
     * @param sql 带命名参数的SQL
     * @param dataSourceCode 数据源编码
     * @param paramsMap 参数映射
     * @return 受影响行数
     */
    public int executeUpdate(String sql, String dataSourceCode, Map<String, Object> paramsMap) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            if (dataSourceCode == null || dataSourceCode.isEmpty()) {
                throw new IllegalArgumentException("dataSourceCode is null");
            }
            DataSource dataSource = dataSourceFactory.getDataSource(dataSourceCode);
            if (dataSource == null) {
                throw new ResourceNotFoundException("dataSource", dataSourceCode);
            }
            NamedParamSql named = parseNamedParams(sql);
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(named.getSqlWithPlaceholders());
            bindParams(stmt, named.getParamNamesInOrder(), paramsMap);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("executeUpdate error", e);
            throw new EngineException("executeUpdate failed: " + e.getMessage(), e);
        } finally {
            closeStatement(stmt);
            closeConnection(conn);
        }
    }

    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException var2) {
                log.debug("Could not close JDBC ResultSet", var2);
            } catch (Exception var3) {
                log.debug("Unexpected exception on closing JDBC ResultSet", var3);
            }
        }

    }

    private static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException var2) {
                log.debug("Could not close JDBC Statement", var2);
            } catch (Exception var3) {
                log.debug("Unexpected exception on closing JDBC Statement", var3);
            }
        }

    }

    private static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException var2) {
                log.debug("Could not close JDBC Connection", var2);
            } catch (Exception var3) {
                log.debug("Unexpected exception on closing JDBC Connection", var3);
            }
        }
    }


    private static void bindParams(PreparedStatement stmt, List<String> paramNamesInOrder, Map<String, Object> paramsMap)
            throws SQLException {
        if (paramNamesInOrder == null || paramNamesInOrder.isEmpty()) {
            return;
        }
        Map<String, Object> safe = (paramsMap == null) ? Collections.emptyMap() : paramsMap;

        for (int i = 0; i < paramNamesInOrder.size(); i++) {
            String name = paramNamesInOrder.get(i);
            Object val = safe.get(name);
            int idx = i + 1;

            if (!safe.containsKey(name)) {
                throw new IllegalArgumentException("missing param: " + name);
            }
            if (val == null) {
                stmt.setObject(idx, null);
            } else if (val instanceof java.util.Date) {
                stmt.setTimestamp(idx, new Timestamp(((java.util.Date) val).getTime()));
            } else {
                stmt.setObject(idx, val);
            }
        }
    }

    private static final Pattern NAMED_PARAM = Pattern.compile(":(\\w+)");

    private static NamedParamSql parseNamedParams(String sql) {
        Matcher m = NAMED_PARAM.matcher(sql);
        StringBuffer sb = new StringBuffer();
        List<String> names = new ArrayList<>();

        while (m.find()) {
            String name = m.group(1);
            names.add(name);
            m.appendReplacement(sb, "?");
        }
        m.appendTail(sb);

        return new NamedParamSql(sb.toString(), names);
    }

    private static class NamedParamSql {
        private final String sqlWithPlaceholders;
        private final List<String> paramNamesInOrder;

        private NamedParamSql(String sqlWithPlaceholders, List<String> paramNamesInOrder) {
            this.sqlWithPlaceholders = sqlWithPlaceholders;
            this.paramNamesInOrder = paramNamesInOrder;
        }

        public String getSqlWithPlaceholders() {
            return sqlWithPlaceholders;
        }

        public List<String> getParamNamesInOrder() {
            return paramNamesInOrder;
        }
    }
}
