package com.ymware.engine.compute.function;

import cn.hutool.core.util.StrUtil;
import com.ymware.engine.annotation.Executor;
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
public class ChineseCharacterToPinyinFunction {

    @Executor
    public String executor(@Param(value = "string", required = false) String string,
                           @Param(value = "separator", required = false) String separator) {
        if (StrUtil.isBlank(string)) {
            return string;
        }
        // TODO: 2021/2/9
        return null;
    }

}
