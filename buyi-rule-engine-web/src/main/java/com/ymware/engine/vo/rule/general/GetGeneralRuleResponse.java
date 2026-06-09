package com.ymware.engine.vo.rule.general;

import com.ymware.engine.vo.condition.ConditionGroupConfig;
import com.ymware.engine.vo.condition.ConfigValue;
import lombok.Data;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/24
 * @since 1.0.0
 */
@Data
public class GetGeneralRuleResponse {

    private Long id;

    private String name;

    private String code;

    private String currentVersion;

    private String publishVersion;

    private String description;

    private Long workspaceId;

    private String workspaceCode;

    private Long ruleId;

    private List<ConditionGroupConfig> conditionGroup;

    private ConfigValue action;

    private DefaultAction defaultAction;

}
