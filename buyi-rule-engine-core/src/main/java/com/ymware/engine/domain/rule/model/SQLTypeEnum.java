package com.ymware.engine.domain.rule.model;

public enum SQLTypeEnum {

    CHAR("CHAR", String.class),
    BLOB("BLOB", Byte[].class),
    VARCHAR("VARCHAR", String.class),
    INTEGER("INTEGER", Long.class),
    TINYINT("TINYINT", Integer.class),
    SMALLINT("SMALLINT", Integer.class),
    MEDIUMINT("MEDIUMINT", Integer.class),
    BIT("BIT", Boolean.class),
    BIGINT("BIGINT", Long.class),
    FLOAT("FLOAT", Float.class),
    DOUBLE("DOUBLE", Double.class),
    DECIMAL("DECIMAL",java.math.BigDecimal.class),
    BOOLEAN("BOOLEAN", Boolean.class),
    DATE("DATE",java.sql.Date.class),
    TIME("TIME",java.sql.Time.class),
    DATETIME("DATETIME",java.sql.Timestamp.class),
    TIMESTAMP("TIMESTAMP",java.sql.Timestamp.class),
    YEAR("YEAR",java.sql.Date.class);

    private String code;
    private Class cls;

    private SQLTypeEnum(String code, Class cls) {
        this.code = code;
        this.cls = cls;
    }

    public static SQLTypeEnum getEnumByCode(String code) {
        for (SQLTypeEnum an : SQLTypeEnum.values()) {
            if (an.getCode().equals(code))
                return an;
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }
}
