package com.ymware.engine.model.dto.sync;

import com.ymware.engine.enums.SyncDataEnums;

/**
 * 数据同步基础数据结构
 */
public class BaseSyncData {

    private SyncDataEnums type;
    private Object data;

    public BaseSyncData(SyncDataEnums type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public SyncDataEnums getType() {
        return type;
    }

    public void setType(SyncDataEnums type) {
        this.type = type;
    }
}
