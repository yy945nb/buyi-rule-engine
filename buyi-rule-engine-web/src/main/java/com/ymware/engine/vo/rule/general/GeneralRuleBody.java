package com.ymware.engine.vo.rule.general;

import com.ymware.engine.vo.condition.ConditionGroupConfig;
import com.ymware.engine.vo.condition.ConfigValue;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2021/1/23
 * @since 1.0.0
 */
@Data
public class GeneralRuleBody {

    @NotNull
    private Long id;

    @Valid
    private List<ConditionGroupConfig> conditionGroup = new ArrayList<>(1);

    @NotNull
    @Valid
    private ConfigValue action;

    private DefaultAction defaultAction;

}
