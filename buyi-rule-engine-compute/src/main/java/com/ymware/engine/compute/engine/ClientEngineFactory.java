package com.ymware.engine.compute.engine;

import com.ymware.engine.compute.api.ClientEngineInvokeService;
import com.ymware.engine.compute.enums.EngineCallType;
import com.ymware.engine.model.request.ClientExpressionSubmitRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 引擎工厂
 *
 */
public class ClientEngineFactory implements InitializingBean {

    private final Map<EngineCallType, ClientEngineInvokeService> callMap = new HashMap<>();
    @Autowired
    private List<ClientEngineInvokeService> engineInvokeServiceList;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (ClientEngineInvokeService engineInvokeService : engineInvokeServiceList) {
            callMap.put(engineInvokeService.type(), engineInvokeService);
        }
    }

    /**
     * 本地执行规则，不建议使用
     * 建议使用： {@link ClientEngineFactory#invoke(ClientExpressionSubmitRequest, ExpressionEnvContext)}
     *
     * @param request    业务编码
     * @param envContext 上下文变量
     * @return
     */
    public Boolean invoke(ClientExpressionSubmitRequest request, Map<String, Object> envContext) {
        return (Boolean) callMap.get(EngineCallType.LOCAL).invoke(request, envContext);
    }

    /**
     * 本地规则执行
     * @param request
     * @param envContext
     * @return
     */
    public Map<String, Object> invoke(ClientExpressionSubmitRequest request, ExpressionEnvContext envContext) {
        callMap.get(EngineCallType.LOCAL).invoke(request, envContext);
        return envContext.getResultContext();
    }

    /**
     * 远端任务提交
     *
     * @param request    请求信息
     * @param envContext 上下文变量
     * @return
     */
    @Deprecated
    public Boolean submit(ClientExpressionSubmitRequest request, Map<String, Object> envContext) {
        return (Boolean) callMap.get(EngineCallType.REMOTE_ASYNC).invoke(request, envContext);
    }


    /**
     * 兼容老版本的改动，不建议使用
     *
     * @param request    业务编码
     * @param envContext 上下文变量
     * @return
     */
    @Deprecated
    public Object syncGetResult(ClientExpressionSubmitRequest request, Map<String, Object> envContext) {
        return callMap.get(EngineCallType.REMOTE_SYNC).invoke(request, envContext);
    }

}
