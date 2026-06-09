package com.ymware.engine.common.listener.body;

import lombok.Data;

import java.io.Serializable;


@Data
public class VariableMessageBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private Type type;

    private Long id;

    public enum Type {
        /**
         * 规则加载，以及移除
         */
        LOAD, 
        REMOVE, 
        UPDATE
    }
}
