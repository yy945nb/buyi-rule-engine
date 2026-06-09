package com.ymware.engine.compute.api.configurability;

import com.ymware.engine.compute.api.ExpressionNodeExecutorFilter;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.common.enums.EnginCacheKeyEnums;
import com.ymware.engine.compute.enums.ExpressionConfigurabilitySwitchEnum;
import com.ymware.engine.compute.helper.ConfigurabilityHelper;
import com.ymware.engine.compute.log.LogEventEnum;
import com.ymware.engine.compute.log.LogHelper;
import com.ymware.engine.compute.process.ExpressionNodeFilterChain;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * redisson 全局锁
 *
 * @author liukaixiong
 * @date 2025/3/25 - 11:35
 */
public class RedissonLockExpressionConfigurabilityProcessor implements ExpressionNodeExecutorFilter {
    private final RedissonClient redissonClient;

    public RedissonLockExpressionConfigurabilityProcessor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void doExpressionNodeFilter(ExpressionBaseRequest baseRequest, ExpressionEnvContext envContext, ExpressionConfigInfo configInfo, ExpressionConfigTreeModel treeModel, Object execute, ExpressionNodeFilterChain chain) {
        if (ConfigurabilityHelper.isEnableExpressionConfigurability(treeModel.getConfigurabilityMap(), ExpressionConfigurabilitySwitchEnum.enableGlobalLock)) {
            final String lockKey = EnginCacheKeyEnums.EXPRESSION_LOCK.generateKey(treeModel.getExpressionId() + "", execute.toString());
            LogHelper.trace(baseRequest, LogEventEnum.EXPRESSION_CALL, "表达式编号:{} , 启用全局锁:{}", treeModel.getExpressionId(), lockKey);
            final RLock lock = redissonClient.getLock(lockKey);
            lock.lock();
            try {
                chain.doFilter(baseRequest, envContext, configInfo, treeModel, execute);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }
}
