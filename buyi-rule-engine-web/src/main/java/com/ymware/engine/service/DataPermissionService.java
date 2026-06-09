package com.ymware.engine.service;

import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.annotation.DataPermission;
import com.ymware.engine.vo.permission.data.ListDataPermissionRequest;
import com.ymware.engine.vo.permission.data.ListDataPermissionResponse;
import com.ymware.engine.vo.permission.data.UpdateDataPermissionRequest;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2020/12/13
 * @since 1.0.0
 */
public interface DataPermissionService {

    /**
     * 校验数据权限
     *
     * @param id             数据id
     * @param dataPermission dataPermission
     * @return true有权限
     */
    Boolean validDataPermission(Serializable id, DataPermission dataPermission);

    /**
     * 数据权限列表
     *
     * @param pageRequest p
     * @return r
     */
    PageResult<ListDataPermissionResponse> list(PageRequest<ListDataPermissionRequest> pageRequest);


    /**
     * 保存或者更新数据权限
     *
     * @param updateRequest u
     * @return Integer
     */
    Boolean saveOrUpdateDataPermission(UpdateDataPermissionRequest updateRequest);
}
