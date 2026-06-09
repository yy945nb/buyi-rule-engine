package com.ymware.engine.compute.function;

import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.AbstractSimpleFunction;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 描述: 执行完成当前，不继续往下走，而是返回上层分支继续
 *
 * @author liukx
 * @date 2025/2/13 14:22
 */
@Component
public class FnReturnFunction extends AbstractSimpleFunction {

    @Override
    protected boolean isAllowedCache() {
        return false;
    }

    @Override
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {
        env.returnEnd();
        return true;
    }

    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.END_RETURN;
    }
}
