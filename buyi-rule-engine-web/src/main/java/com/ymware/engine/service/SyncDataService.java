package com.ymware.engine.service;

import com.ymware.engine.enums.SyncDataEnums;

/**
 * @author liukaixiong
 * @date 2024/2/20
 */
public interface SyncDataService<T> {

    public SyncDataEnums syncType();

    public boolean importData(T data);

    public T export(Long id);

}
