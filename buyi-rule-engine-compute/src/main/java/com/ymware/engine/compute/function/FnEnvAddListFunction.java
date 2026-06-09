package com.ymware.engine.compute.function;

import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.AbstractSimpleFunction;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 设置集合到上下文变量中
 *
 * @author liukaixiong
 * @date 2024/9/24 - 17:41
 */
@Component
public class FnEnvAddListFunction extends AbstractSimpleFunction {
    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.ENV_ADD_LIST;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {
        String key = getConvertValue(funArgs, 0, String.class);
        Object value = getConvertValue(funArgs, 1, Object.class);
        final Set<Object> code = (Set<Object>) env.getSourceMap().computeIfAbsent(key, k -> new HashSet<>());
        if (value instanceof Collection<?>) {
            code.addAll((Collection<?>) value);
        } else {
            code.add(value);
        }
        env.recordTraceDebugContent(getName(), "debug", String.format("新增属性到上下文中: %s = %s", key, code));
        return true;
    }
}
