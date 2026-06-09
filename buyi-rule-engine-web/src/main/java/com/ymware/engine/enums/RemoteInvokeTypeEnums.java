package com.ymware.engine.enums;

import com.ymware.engine.components.html.DataRender;

public enum RemoteInvokeTypeEnums implements DataRender {

    HTTP("http调用"),
    INTERNAL("内部调用");

    private String label;

    RemoteInvokeTypeEnums(String describe) {
        this.label = describe;
    }

    @Override
    public Object getCode() {
        return this.name();
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
