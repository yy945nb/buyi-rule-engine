package com.ymware.engine.compute.function;

import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;
import com.ymware.engine.annotation.Param;

import java.util.Date;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2021/2/24
 * @since 1.0.0
 */
@Function
public class DateToTimestampFunction {

    @Executor
    public Long executor(@Param(value = "date") Date date) {
        return date.getTime();
    }

}
