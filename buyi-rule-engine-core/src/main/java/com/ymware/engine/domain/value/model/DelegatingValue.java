package com.ymware.engine.domain.value.model;

import com.ymware.engine.domain.rule.service.Container;
import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.exception.EngineException;
import com.ymware.engine.exception.ValueException;
import com.ymware.engine.domain.rule.service.GeneralRule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 〈Execute〉
 * <p>
 * 执行规则
 * <p>
 * 目前可以执行基本规则
 *
 * @author 丁乾文
 * @date 2021/7/22 10:10 上午
 * @since 1.0.0
 */
@Slf4j
public class DelegatingValue implements Value {

    @Getter
    private Long id;
    @Getter
    private String code;
    @Getter
    private String workspaceCode;

    private ValueType valueType;

    /**
     * json 反序列化使用
     */
    DelegatingValue() {

    }

    public DelegatingValue(String workspaceCode, Long id, String code, ValueType valueType) {
        this.workspaceCode = Objects.requireNonNull(workspaceCode);
        this.code = Objects.requireNonNull(code);
        this.id = Objects.requireNonNull(id);
        this.valueType = Objects.requireNonNull(valueType);
    }

    @Override
    public Object getValue(Input input, RuleEngineConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("执行规则:{}-{}", this.workspaceCode, this.code);
        }
        Container.Body<GeneralRule> generalRuleContainer = configuration.getGeneralRuleContainer();
        GeneralRule generalRule = generalRuleContainer.get(this.workspaceCode, this.code);
        if (generalRule == null) {
            throw new EngineException("no general rule:{}", code);
        }
        Object action = generalRule.execute(input, configuration);
        // 如果执行结果为空，则不再校验类型，直接返回，进去后续逻辑判断
        if (action == null) {
            return null;
        }
        Class<?> classType = action.getClass();
        if (!valueType.getClassType().isAssignableFrom(classType)) {
            throw new ValueException("基础规则执行结果与配置结果类型不同：{}-{}", classType, valueType.getClassType());
        }
        return action;
    }

    @Override
    public ValueType getValueType() {
        return this.valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DelegatingValue executor = (DelegatingValue) o;
        return Objects.equals(id, executor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
