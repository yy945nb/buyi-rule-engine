package com.ymware.engine.compute.function;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.FailureStrategy;
import com.ymware.engine.annotation.Function;
import com.ymware.engine.annotation.Param;
import lombok.extern.slf4j.Slf4j;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2021/2/9
 * @since 1.0.0
 */
@Slf4j
@Function
public class SubstringFunction {

    @Executor(failureFor = StringIndexOutOfBoundsException.class)
    public String executor(@Param(value = "string", required = false) String string,
                           @Param(value = "beginIndex") Integer beginIndex,
                           @Param(value = "endIndex", required = false) Integer endIndex) {
        if (StrUtil.isBlank(string)) {
            return string;
        }
        if (Validator.isEmpty(endIndex)) {
            return string.substring(beginIndex);
        }
        return string.substring(beginIndex, endIndex);
    }

    @FailureStrategy
    public String failureStrategy(@Param(value = "string", required = false) String string,
                                  @Param(value = "beginIndex") Integer beginIndex,
                                  @Param(value = "endIndex", required = false) Integer endIndex) {
        log.error("字符串索引超出范围异常：{},{},{}", string, beginIndex, endIndex);
        return string;
    }

}
