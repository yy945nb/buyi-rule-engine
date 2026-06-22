package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.value.model.Parameter;
import com.ymware.engine.domain.workflow.type.RefType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数解析器 - 统一处理工作流节点参数的解析和验证
 * 从 Chain 中提取，消除 parseOutputResult / getParametersData 的重复逻辑
 */
public final class ParameterResolver {

    private ParameterResolver() {
    }

    /**
     * 解析输出参数
     *
     * @param outputParameters 输出参数定义列表
     * @param execute          执行结果数据
     * @return 解析后的参数映射
     * @throws RuntimeException 当必填参数缺失时
     */
    public static Map<String, Object> resolve(List<Parameter> outputParameters, Map<String, Object> execute) {
        if (outputParameters == null) {
            return execute != null ? execute : new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        List<String> validParameters = new ArrayList<>();

        for (Parameter parameter : outputParameters) {
            Object value = resolveParameterValue(parameter, execute);

            if (parameter.isRequire() && value == null) {
                validParameters.add("参数 " + parameter.getName() + " 缺失");
            }
            result.put(parameter.getName(), value);
        }

        if (!validParameters.isEmpty()) {
            throw new RuntimeException("参数验证失败：" + String.join(",", validParameters));
        }

        return result;
    }

    /**
     * 解析节点参数数据（从内存上下文中）
     *
     * @param parameters  参数定义列表
     * @param memory      工作流内存上下文
     * @return 解析后的参数映射
     * @throws RuntimeException 当必填参数缺失时
     */
    public static Map<String, Object> resolveFromMemory(List<Parameter> parameters, Map<String, Object> memory) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        List<String> validParameters = new ArrayList<>();

        for (Parameter parameter : parameters) {
            Object value = null;

            if (parameter.getRefType() == RefType.REF) {
                List<String> refValue = parameter.getRefValue();
                if (refValue != null && refValue.size() >= 2) {
                    Object nodeResult = memory.get(refValue.get(0));
                    if (nodeResult instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> nodeResultMap = (Map<String, Object>) nodeResult;
                        value = nodeResultMap.get(refValue.get(1));
                    }
                }
            }

            if (value == null) {
                value = parameter.getDefaultValue();
            }

            if (parameter.isRequire() && value == null) {
                validParameters.add("参数 " + parameter.getName() + " 缺失");
            }
            result.put(parameter.getName(), value);
        }

        if (!validParameters.isEmpty()) {
            throw new RuntimeException("参数验证失败：" + String.join(",", validParameters));
        }

        return result;
    }

    private static Object resolveParameterValue(Parameter parameter, Map<String, Object> execute) {
        if (parameter.getRefType() == RefType.REF) {
            List<String> refValue = parameter.getRefValue();
            if (refValue != null && refValue.size() >= 2) {
                Object nodeResult = execute.get(refValue.get(0));
                if (nodeResult instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nodeResultMap = (Map<String, Object>) nodeResult;
                    return nodeResultMap.get(refValue.get(1));
                }
            } else if (refValue != null) {
                return execute.getOrDefault(String.join(".", refValue), parameter.getDefaultValue());
            }
        }
        return parameter.getDefaultValue();
    }
}
