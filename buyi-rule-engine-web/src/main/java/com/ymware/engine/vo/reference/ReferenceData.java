package com.ymware.engine.vo.reference;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2021/1/23
 * @since 1.0.0
 */
@Data
public class ReferenceData {

    private Set<Long> inputParameterIds = new HashSet<>();
    private Set<Long> variableIds = new HashSet<>();
    /**
     * 引用的表达式
     */
    private Set<Long> formulaIds = new HashSet<>();
    /**
     * 引用的普通规则
     * 目前规则集可以引用
     */
    private Set<Long> generalRuleIds = new HashSet<>();


    public void addVariableId(Long id) {
        this.variableIds.add(id);
    }

    public void addInputParameterId(Long id) {
        this.inputParameterIds.add(id);
    }

    public void addGeneralRuleId(Long id) {
        this.generalRuleIds.add(id);
    }

    public void addFormulaId(Long id) {
        this.formulaIds.add(id);
    }

    /**
     * 转为json
     *
     * @return json
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }


    public void append(ReferenceData rd) {
        this.inputParameterIds.addAll(rd.getInputParameterIds());
        this.variableIds.addAll(rd.getVariableIds());
        this.formulaIds.addAll(rd.getFormulaIds());
        this.generalRuleIds.addAll(rd.getGeneralRuleIds());
    }

}
