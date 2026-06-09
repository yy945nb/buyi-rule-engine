package com.ymware.engine.compute.feature;

import com.ymware.engine.compute.api.ExpressionFunctionFilter;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.FunctionFilterChain;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import com.ymware.engine.model.FunctionApiModel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * 表达式函数名称过滤能力支持
 * 开启能力可通过: ExpressionEnvContext#enableExpressionFunctionNameSkipFilter(Set)
 *
 * @author liukaixiong
 * @date 2024/8/23 - 10:59
 */
@Component
public class ExpressionFunctionNameFilterSupport implements ExpressionFunctionFilter {

    @Override
    public Object doFunctionFilter(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, FunctionApiModel functionInfo, List<Object> funcArgs, FunctionFilterChain chain) {
        final Set<String> expressionFunctionNameSkipFilterList = env.getExpressionFunctionNameSkipFilterList();
        if (!CollectionUtils.isEmpty(expressionFunctionNameSkipFilterList)) {
            if (expressionFunctionNameSkipFilterList.contains(functionInfo.getName())) {
                env.recordTraceDebugContent(functionInfo.getName(), "function_name_skip", "触发函数名称跳过能力,默认true!");
                return true;
            }
        }
        return chain.doFilter(env, configTreeModel, request, functionInfo, funcArgs);
    }
}
