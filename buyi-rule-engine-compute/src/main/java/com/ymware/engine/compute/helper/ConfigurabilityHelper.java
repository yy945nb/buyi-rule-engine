package com.ymware.engine.compute.helper;

import com.ymware.engine.compute.enums.ExecutorConfigurabilitySwitchEnum;
import com.ymware.engine.compute.enums.ExpressionConfigurabilitySwitchEnum;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 能力使用帮助类
 *
 * @author liukaixiong
 * @date 2025/1/15 - 11:08
 */
public class ConfigurabilityHelper {

    /**
     * 是否开启执行器配置能力
     * @param configurabilityMap
     * @param key
     * @return
     */
    public static boolean isEnableExecutorConfigurability(Map<String, Object> configurabilityMap, ExecutorConfigurabilitySwitchEnum key) {
        return isEnableConfigurability(configurabilityMap, key.name());
    }

    /**
     * 是否开启表达式配置能力
     * @param configurabilityMap
     * @param key
     * @return
     */
    public static boolean isEnableExpressionConfigurability(Map<String, Object> configurabilityMap, ExpressionConfigurabilitySwitchEnum key) {
        return isEnableConfigurability(configurabilityMap, key.name());
    }

    /**
     * 是否开启配置能力
     *
     * @param configurabilityMap    能力配置信息
     * @param key                   配置key
     * @return
     */
    public static boolean isEnableConfigurability(Map<String, Object> configurabilityMap, String key) {
        if (configurabilityMap != null) {
            List<String> list = (List<String>) configurabilityMap.getOrDefault("enableSwitch", Collections.emptyList());
            return !list.isEmpty() && list.contains(key);
        }
        return false;
    }

}
