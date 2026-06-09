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
 * 将字符串对象转换成json对象
 *
 * @author liukaixiong
 * @date 2025/3/18 - 13:50
 */
@Component
public class FnStrToJsonFunction extends AbstractSimpleFunction {
    @Override
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {
        String jsonStr = getArgsIndexValue(funArgs, 0);
        env.recordTraceDebugContent(getName(), "record", String.format("jsonStr=%s", jsonStr));
        return Jsons.parseMap(jsonStr);
    }

    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.OBJECT_STR_TO_JSON;
    }
}
