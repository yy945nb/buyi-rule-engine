package com.ymware.engine.core.condition;

import com.ymware.engine.condition.Operator;
import com.ymware.engine.domain.rule.service.compare.StringCompare;
import org.junit.jupiter.api.Test;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2025/11/17
 * @since 1.0.0
 */
public class StringCompareTest {

    @Test
    public void test() {
        {
            StringCompare instance = StringCompare.getInstance();
            boolean compare = instance.compare("[北京市,上海市,深圳市]", Operator.CONTAIN, "深圳");
            System.out.println(compare);
        }
        {
            StringCompare instance = StringCompare.getInstance();
            boolean compare = instance.compare("[北京市,上海市,深圳市]", Operator.NOT_CONTAIN, "河南");
            System.out.println(compare);
        }
    }

}
