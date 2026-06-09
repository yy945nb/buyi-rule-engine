package com.ymware.engine.compute.function;

import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.AbstractSimpleFunction;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取变量
 *
 * @author liukaixiong
 * @date 2024/9/24 - 17:41
 */
@Component
public class FnEnvGetFunction extends AbstractSimpleFunction {
    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.ENV_GET_VALUE;
    }
    @Override
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {
        String key = getConvertValue(funArgs, 0, String.class);
        final Object objectValue = env.getObjectValue(key);
        env.recordTraceDebugContent(getName(), "debug", String.format("上下文中获取值: %s = %s", key, objectValue));
        return objectValue;
    }
}
