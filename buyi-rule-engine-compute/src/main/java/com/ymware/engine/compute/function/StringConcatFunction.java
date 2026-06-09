package com.ymware.engine.compute.function;

import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;
import com.ymware.engine.annotation.Param;
import lombok.extern.slf4j.Slf4j;


/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 * <p>
 * 字符串拼接
 *
 * @author dingqianwen
 * @date 2021/2/9
 * @since 1.0.0
 */
@Slf4j
@Function
public class StringConcatFunction {

    @Executor
    public String executor(@Param(value = "source") String source,
                           @Param(value = "target", required = false) String target) {
        return source.concat(target);
    }

}
