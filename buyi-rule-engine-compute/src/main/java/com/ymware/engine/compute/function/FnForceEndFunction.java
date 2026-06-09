package com.ymware.engine.compute.function;

import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.AbstractSimpleFunction;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基础的函数定义
 *
 * @author liukaixiong
 * @date 2024/8/16 - 13:56
 */
@Component
public class FnForceEndFunction extends AbstractSimpleFunction {

    @Override
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {
        env.forceEnd();
        return true;
    }

    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.END_FORCE;
    }
}
