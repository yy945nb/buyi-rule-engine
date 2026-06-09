package com.ymware.engine.domain.rule.model;

/**
 * 规则执行结果类型
 */
public enum RuleExecuteResultTypeEnum {

    JAVA_EXPRESS("JAVA_EXPRESS", "Java表达式"),
    SQL_RESULT("SQL_RESULT", "SQL结果");

    private String code;
    private String name;

    private RuleExecuteResultTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RuleExecuteResultTypeEnum getEnumByCode(String code) {
        for (RuleExecuteResultTypeEnum an : RuleExecuteResultTypeEnum.values()) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
