package com.ymware.engine.domain.value.model;

import com.ymware.engine.domain.workflow.type.DataType;
import com.ymware.engine.domain.workflow.type.RefType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Parameter {
    private String name;
    private boolean require;
    private DataType type;
    private String description;
    private RefType refType;
    //默认值
    private Object defaultValue;
    //ref 链接
    private List<String> refValue;
    private List<Parameter> children;
}
