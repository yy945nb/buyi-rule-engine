package com.ymware.engine.domain.value.model;

import com.ymware.engine.domain.rule.service.DefaultInput;
import com.ymware.engine.domain.rule.service.Input;
import org.junit.jupiter.api.Test;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2021/2/6
 * @since 1.0.0
 */
public class FormulaTest {

    @Test
    public void test() {
        Formula formula = new Formula("(#input1 - #input2) * 3", ValueType.NUMBER);
        Input input = new DefaultInput();
        input.put("input1", 3);
        input.put("input2", 1);
        System.out.println(formula.getInputParameterCodes());
        Object value = formula.getValue(input, null);
        System.out.println(value);
    }

    @Test
    public void test2() {
        Formula formula = new Formula("#input1 + ' 你好'", ValueType.STRING);
        Input input = new DefaultInput();
        input.put("input1", "小丁");
        System.out.println(formula.getInputParameterCodes());
        Object value = formula.getValue(input, null);
        System.out.println(value);
    }

}
