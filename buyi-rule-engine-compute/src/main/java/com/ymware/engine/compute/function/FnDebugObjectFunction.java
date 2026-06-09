package com.ymware.engine.compute.function;

import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.AbstractSimpleFunction;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import com.ymware.engine.utils.Jsons;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 打印调试信息
 *
 * @author liukaixiong
 * @date 2024/8/23 - 15:41
 */
@Component
public class FnDebugObjectFunction extends AbstractSimpleFunction {

    @Override
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {
        final Object envContextKey = getArgsIndexValue(funArgs, 0, env.getRequest());
        env.recordTraceDebugContent(getName(), "debug_object", envContextKey == null ? "" : Jsons.objToMap(envContextKey));
        return true;
    }

    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.DEBUG_OBJECT;
    }
}
