package com.ymware.engine.vo.rule;

import com.ymware.engine.vo.condition.ConditionGroupConfig;
import com.ymware.engine.vo.condition.ConfigValue;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author liqian
 * @date 2021/1/15
 */
@Data
public class RuleBody {
    /**
     * 规则id
     */
    private Long id;
    /**
     * 规则名称
     */
    @NotBlank
    private String name;
    /**
     * 规则集是有序的，默认循序执行规则集
     */
    private Integer orderNo;

    /**
     * 规则条件组
     */
    @Valid
    private List<ConditionGroupConfig> conditionGroup = new ArrayList<>();

    /**
     * 规则结果
     */
    @Valid
    private ConfigValue action = new ConfigValue();

}
