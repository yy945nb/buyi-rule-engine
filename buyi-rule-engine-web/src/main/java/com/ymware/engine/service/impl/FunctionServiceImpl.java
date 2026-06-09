package com.ymware.engine.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import com.ymware.engine.entity.PageBase;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.common.vo.Rows;
import com.ymware.engine.common.enums.ErrorCodeEnum;
import com.ymware.engine.common.exception.ApiException;
import com.ymware.engine.service.FunctionService;
import com.ymware.engine.entity.RuleEngineFunction;
import com.ymware.engine.entity.RuleEngineFunctionParam;
import com.ymware.engine.service.RuleEngineFunctionManager;
import com.ymware.engine.service.RuleEngineFunctionParamManager;
import com.ymware.engine.util.PageUtils;
import com.ymware.engine.vo.function.FunctionParam;
import com.ymware.engine.vo.function.GetFunctionResponse;
import com.ymware.engine.vo.function.ListFunctionRequest;
import com.ymware.engine.vo.function.ListFunctionResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/27
 * @since 1.0.0
 */
@Service
public class FunctionServiceImpl implements FunctionService {

    @Resource
    private RuleEngineFunctionManager ruleEngineFunctionManager;
    @Resource
    private RuleEngineFunctionParamManager ruleEngineFunctionParamManager;
    @Resource
    private ApplicationContext applicationContext;

    private static final String FUNCTION_PACKAGE = "com.ymware.engine.function";

    /**
     * 函数列表
     *
     * @param pageRequest param
     * @return list
     */
    @Override
    public PageResult<ListFunctionResponse> list(PageRequest<ListFunctionRequest> pageRequest) {
        List<PageRequest.OrderBy> orders = pageRequest.getOrders();
        PageBase page = pageRequest.getPage();

        QueryWrapper<RuleEngineFunction> wrapper = new QueryWrapper<>();
        PageUtils.defaultOrder(orders, wrapper);
        ListFunctionRequest query = pageRequest.getQuery();
        if (Validator.isNotEmpty(query.getName())) {
            wrapper.lambda().like(RuleEngineFunction::getName, query.getName());
        }
        // 函数返回值
        if (Validator.isNotEmpty(query.getValueType())) {
            wrapper.lambda().eq(RuleEngineFunction::getReturnValueType, query.getValueType());
        }
        IPage<RuleEngineFunction> functionPage = ruleEngineFunctionManager.page(new Page<>(page.getPageIndex(), page.getPageSize()), wrapper);

        List<RuleEngineFunction> records = functionPage.getRecords();

        // 获取本次请求用到的所有函数参数
        Map<Long, List<RuleEngineFunctionParam>> functionParamMap = Optional.of(records)
                .filter(CollUtil::isNotEmpty)
                .map(m -> {
                    List<Long> functionIds = m.stream().map(RuleEngineFunction::getId).collect(Collectors.toList());
                    return this.ruleEngineFunctionParamManager.lambdaQuery().in(RuleEngineFunctionParam::getFunctionId, functionIds).list();
                })
                .filter(CollUtil::isNotEmpty)
                .map(m -> m.stream().collect(Collectors.groupingBy(RuleEngineFunctionParam::getFunctionId))).orElse(Collections.emptyMap());

        PageResult<ListFunctionResponse> pageResult = new PageResult<>();
        List<ListFunctionResponse> responseList = records.stream().map(m -> {
            ListFunctionResponse functionResponse = new ListFunctionResponse();
            functionResponse.setId(m.getId());
            functionResponse.setName(m.getName());
            functionResponse.setExecutor(m.getExecutor());
            functionResponse.setReturnValueType(m.getReturnValueType());
            functionResponse.setCreateTime(m.getCreateTime());
            // 处理方法参数
            functionResponse.setParams(this.getFunctionParam(functionParamMap.get(m.getId())));
            return functionResponse;
        }).collect(Collectors.toList());
        pageResult.setData(new Rows<>(responseList, PageUtils.getPageResponse(functionPage)));
        return pageResult;
    }

    /**
     * 查询函数详情
     *
     * @param id 函数id
     * @return 函数信息
     */
    @Override
    public GetFunctionResponse get(Long id) {
        RuleEngineFunction ruleEngineFunction = this.ruleEngineFunctionManager.getById(id);
        if (ruleEngineFunction == null) {
            throw new ApiException(ErrorCodeEnum.RULE9999404.getCode(),"不存在函数：{}", id);
        }
        GetFunctionResponse functionResponse = new GetFunctionResponse();
        functionResponse.setId(ruleEngineFunction.getId());
        functionResponse.setName(ruleEngineFunction.getName());
        functionResponse.setDescription(ruleEngineFunction.getDescription());
        functionResponse.setExecutor(ruleEngineFunction.getExecutor());
        functionResponse.setReturnValueType(ruleEngineFunction.getReturnValueType());
        // 处理方法参数
        List<RuleEngineFunctionParam> functionParamList = this.ruleEngineFunctionParamManager.lambdaQuery().eq(RuleEngineFunctionParam::getFunctionId, id).list();
        functionResponse.setParams(this.getFunctionParam(functionParamList));
        return functionResponse;
    }


    private void saveFunctionParam(List<FunctionParam> param, Long functionId) {
        if (CollUtil.isNotEmpty(param)) {
            List<RuleEngineFunctionParam> functionParamList = param.stream().map(m -> {
                RuleEngineFunctionParam ruleEngineFunctionParam = new RuleEngineFunctionParam();
                ruleEngineFunctionParam.setFunctionId(functionId);
                ruleEngineFunctionParam.setParamName(m.getName());
                ruleEngineFunctionParam.setParamCode(m.getCode());
                ruleEngineFunctionParam.setValueType(m.getValueType());
                return ruleEngineFunctionParam;
            }).collect(Collectors.toList());
            this.ruleEngineFunctionParamManager.saveBatch(functionParamList);
        }
    }


    /**
     * 处理函数参数
     *
     * @param functionParamList 函数参数列表
     * @return list
     */
    private List<FunctionParam> getFunctionParam(List<RuleEngineFunctionParam> functionParamList) {
        List<FunctionParam> params = new ArrayList<>();
        if (CollUtil.isNotEmpty(functionParamList)) {
            params = functionParamList.stream().map(m -> {
                FunctionParam param = new FunctionParam();
                param.setName(m.getParamName());
                param.setCode(m.getParamCode());
                param.setValueType(m.getValueType());
                return param;
            }).collect(Collectors.toList());
        }
        return params;
    }
}
