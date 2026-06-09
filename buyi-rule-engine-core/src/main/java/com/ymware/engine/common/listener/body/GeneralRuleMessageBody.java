package com.ymware.engine.common.listener.body;

import lombok.Data;

import java.io.Serializable;


@Data
public class GeneralRuleMessageBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private Type type;

    private String workspaceCode;

    private Long workspaceId;

    private String ruleCode;

    public enum Type {
        /**
         * 规则加载，以及移除
         */
        LOAD, 
        UPDATE, 
        REMOVE
    }
}
