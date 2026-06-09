package com.ymware.engine.service;

import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.vo.function.GetFunctionResponse;
import com.ymware.engine.vo.function.ListFunctionRequest;
import com.ymware.engine.vo.function.ListFunctionResponse;

/**
 * 函数处理
 */
public interface FunctionService {

    /**
     * 函数列表
     *
     * @param pageRequest param
     * @return list
     */
    PageResult<ListFunctionResponse> list(PageRequest<ListFunctionRequest> pageRequest);

    /**
     * 查询函数详情
     *
     * @param id 函数id
     * @return 函数信息
     */
    GetFunctionResponse get(Long id);

}
