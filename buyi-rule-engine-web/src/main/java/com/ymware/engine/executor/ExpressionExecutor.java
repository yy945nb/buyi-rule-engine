package com.ymware.engine.executor;

import com.ymware.engine.common.enums.EnginCacheKeyEnums;
import com.ymware.engine.enums.ExpressionTypeEnum;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import com.ymware.engine.service.ExpressionExecutorService;
import com.ymware.engine.utils.Jsons;
import com.ymware.engine.components.TraceLogHelper;
import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.enums.TraceStageEnums;
import com.ymware.engine.exception.Throws;
import com.ymware.engine.model.response.ExpressionExecutorBaseDTO;
import com.ymware.engine.model.dto.response.ExpressionExecutorDetailConfigDTO;
import com.ymware.engine.service.ExpressionConfigService;
import com.ymware.engine.service.ExpressionExecutorConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 表达式执行器
 * 负责接收上游发送过来的业务编码进行执行逻辑处理
 */
@Component
public class ExpressionExecutor implements ExpressionExecutorService {

    private final Logger log = getLogger(ExpressionExecutor.class);
    private final List<ExpressionTypeEnum> invokeTypeList = Arrays.asList(ExpressionTypeEnum.ACTION, ExpressionTypeEnum.CONDITION, ExpressionTypeEnum.TRIGGER, ExpressionTypeEnum.CALLBACK);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ExpressionExecutorConfigService expressionExecutorConfigService;
    @Autowired
    private ExpressionConfigService expressionConfigService;

    public ExpressionConfigInfo getBusinessConfigInfo(String serviceName, String businessCode, String executorCode) {
        // 1. 根据项目和业务编码查询配置的表达式配置组
        ExpressionExecutorBaseDTO expressionExecutorBase = expressionExecutorConfigService.queryExecutorInfo(serviceName, businessCode, executorCode);
        Throws.check(expressionExecutorBase == null, "未查询到执行器[" + serviceName + "|" + businessCode + "|" + executorCode + "]配置信息!");
        List<ExpressionConfigTreeModel> deepConfigInfo = getDeepConfigInfo(expressionExecutorBase.getId());
        Map<String, Object> configurabilityMap = new HashMap<>();
        if (StringUtils.isNotEmpty(expressionExecutorBase.getConfigurabilityJson())) {
            configurabilityMap = Jsons.parseMap(expressionExecutorBase.getConfigurabilityJson());
        }
        return ExpressionConfigInfo.builder().timestamp(System.currentTimeMillis()).executorId(expressionExecutorBase.getId()).configurabilityMap(configurabilityMap).executorCode(expressionExecutorBase.getExecutorCode()).executorName(expressionExecutorBase.getExecutorDescription()).serviceName(serviceName).businessCode(businessCode).varDefinition(expressionExecutorBase.getVarDefinition()).configTreeModelList(deepConfigInfo).build();
    }


    public ExpressionConfigInfo queryBusinessConfigInfo(String serviceName, String businessCode, String executorCode) {
        String cacheKey = EnginCacheKeyEnums.EXECUTOR_REFRESH_KEY.generateKey(serviceName, businessCode, executorCode);
        Object configInfoObject = redisTemplate.opsForValue().get(cacheKey);
        if (configInfoObject != null) {
            return Jsons.parseObject(configInfoObject.toString(), ExpressionConfigInfo.class);
        }
        return getRefreshBusinessConfigInfo(serviceName, businessCode, executorCode);
    }

    public ExpressionConfigInfo getRefreshBusinessConfigInfo(String serviceName, String businessCode, String executorCode) {
        ExpressionConfigInfo businessConfigInfo = getBusinessConfigInfo(serviceName, businessCode, executorCode);
        String cacheKey = EnginCacheKeyEnums.EXECUTOR_REFRESH_KEY.generateKey(serviceName, businessCode, executorCode);
        if (businessConfigInfo != null) {
            redisTemplate.opsForValue().set(cacheKey, Objects.requireNonNull(Jsons.toJsonString(businessConfigInfo)));
        }
        return businessConfigInfo;
    }

    /**
     * 获取表达式配置表中的父子关系集合
     *
     * @param executorId 执行器编号
     * @return
     */
    private List<ExpressionConfigTreeModel> getDeepConfigInfo(Long executorId) {
        return getDeepConfigInfo(executorId, 0L);
    }

    /**
     * 递归获取数据库中相应的表达式层级目录结构
     *
     * @param executorId 上级编号
     * @param parentId   上级编号
     * @return
     */
    private List<ExpressionConfigTreeModel> getDeepConfigInfo(Long executorId, Long parentId) {
        // 获取该编号的下级表达式集合
        List<ExpressionExecutorDetailConfigDTO> nodeExpressionInfo = expressionConfigService.getNodeExpressionInfo(executorId, parentId);

        if (CollectionUtils.isEmpty(nodeExpressionInfo)) {
            return null;
        }

        List<ExpressionConfigTreeModel> deepList = new ArrayList<>();
        for (ExpressionExecutorDetailConfigDTO configDTO : nodeExpressionInfo) {
            // 启用的表达式才会应用deepList = {ArrayList@17114}  size = 2
            if (BaseConstants.BASE_VALID_STATUS == configDTO.getExpressionStatus()) {
                // 根据当前表达式配置,获取下级表达式内容
                List<ExpressionConfigTreeModel> deepConfigInfo = getDeepConfigInfo(configDTO.getExecutorId(), configDTO.getId());
                ExpressionConfigTreeModel configTreeModel = ExpressionConfigTreeModel.builder()
                        .executorId(configDTO.getExecutorId())
                        .expressionCode(configDTO.getExpressionCode())
                        .expressionId(configDTO.getId())
                        .expressionType(configDTO.getExpressionType())
                        .expression(configDTO.getExpressionContent())
                        .title(configDTO.getExpressionTitle())
                        .configurabilityMap(Jsons.parseMap(configDTO.getConfigurabilityJson()))
                        .nodeExpression(deepConfigInfo).build();
                deepList.add(configTreeModel);
            }
        }

        return deepList;
    }


    /**
     * 表达式触发
     *
     * @param serviceName
     * @param businessCode
     * @param request
     */
    @Override
    public Map<String, Object> invoke(String serviceName, String businessCode, String eventName, String unionId, String traceId, Object request) {
        return null;
    }

}
