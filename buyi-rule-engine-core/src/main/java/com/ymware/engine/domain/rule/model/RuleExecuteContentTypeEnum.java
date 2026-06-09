package com.ymware.engine.domain.rule.model;

/**
 * 执行上下文类型
 */
public enum RuleExecuteContentTypeEnum {

    SQL("sql", "sql查询"),
    JAVA_CODE("java", "java代码");

    private String code;
    private String name;

    private RuleExecuteContentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RuleExecuteContentTypeEnum getEnumByCode(String code) {
        for (RuleExecuteContentTypeEnum an : RuleExecuteContentTypeEnum.values()) {
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
