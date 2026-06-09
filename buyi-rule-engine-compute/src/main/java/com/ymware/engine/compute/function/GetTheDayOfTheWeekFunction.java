package com.ymware.engine.compute.function;

import cn.hutool.core.date.DateUtil;
import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;


/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2021/2/24
 * @since 1.0.0
 */
@Function(name = "获取今天星期几")
public class GetTheDayOfTheWeekFunction {

    @Executor
    public String executor() {
        return DateUtil.thisDayOfWeekEnum().toChinese();
    }

}
