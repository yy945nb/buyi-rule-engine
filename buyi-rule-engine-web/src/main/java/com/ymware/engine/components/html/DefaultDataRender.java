package com.ymware.engine.components.html;


public class DefaultDataRender implements DataRender {

    private Object code;
    private String label;

    public DefaultDataRender(Object code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public Object getCode() {
        return this.code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
