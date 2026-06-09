package com.ymware.engine.compute.function;

import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.AbstractSimpleFunction;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 变量方法执行器
 * 当变量为一个对象，内部有一些特殊方法需要执行时，可以调用
 * <code>fn_env_invoke_method(currentDate,'toLocalDate') // LocalDateTime 转 LocalDate</code>
 * <code>fn_env_invoke_method(startDate2,'minusDays',seq.list(1)) // 日期减一天</code>
 * @author liukaixiong
 * @date 2024/9/24 - 17:41
 */
@Component
public class FnEnvMethodExecutorFunction extends AbstractSimpleFunction {
    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.ENV_INVOKE_METHOD;
    }

    @Override
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {
        Object object = getArgsIndexValue(funArgs, 0);
        String methodName = getConvertValue(funArgs, 1, String.class);
        List<Object> methodArgs = getArgsIndexValue(funArgs, 2, Collections.emptyList());
        try {
            final Object value = MethodUtils.invokeMethod(object, true, methodName, methodArgs.toArray());
            env.recordTraceDebugContent(getName(), "invoke", String.format("value=%s", value));
            return value;
        } catch (Exception e) {
            env.recordTraceDebugContent(getName(), "error", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
