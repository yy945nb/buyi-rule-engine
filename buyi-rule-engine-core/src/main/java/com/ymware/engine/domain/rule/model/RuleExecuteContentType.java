package com.ymware.engine.domain.rule.model;

/**
 * 执行上下文类型
 */
public enum RuleExecuteContentType {

    SQL("sql", "sql查询"),
    JAVA_CODE("java", "java代码");

    private String code;
    private String name;

    private RuleExecuteContentType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RuleExecuteContentType getEnumByCode(String code) {
        for (RuleExecuteContentType an : RuleExecuteContentType.values()) {
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
