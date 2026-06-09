package com.ymware.engine.compute.function.json;

import cn.hutool.core.lang.Validator;
import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;
import com.ymware.engine.annotation.Param;
import com.ymware.engine.domain.rule.service.compare.BooleanCompare;
import com.ymware.engine.exception.ValueException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2020/12/13
 * @since 1.0.0
 */
@Slf4j
@Function
public class ParseJsonBooleanFunction implements JsonEval {

    @Executor
    public Boolean executor(@Param(value = "jsonString", required = false) String jsonString,
                            @Param(value = "jsonValuePath", required = false) String jsonValuePath) {
        Object value = this.eval(jsonString, jsonValuePath);
        // 返回null 而不是String.valueOf后的null字符
        if (Validator.isEmpty(value)) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (Objects.equals(String.valueOf(value), BooleanCompare.TRUE)) {
            return true;
        } else if (Objects.equals(String.valueOf(value), BooleanCompare.FALSE)) {
            return false;
        }
        throw new ValueException("{}只能是Boolean类型", value);
    }

}
