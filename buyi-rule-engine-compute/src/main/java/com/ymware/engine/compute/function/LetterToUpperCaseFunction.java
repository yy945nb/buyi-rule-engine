package com.ymware.engine.compute.function;

import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;
import com.ymware.engine.annotation.Param;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 * 字母转大写
 *
 * @author 丁乾文
 * @date 2020/12/24
 * @since 1.0.0
 */
@Function
public class LetterToUpperCaseFunction {

    @Executor
    public String executor(@Param(value = "letter",required = false) String letter) {
        if (letter == null) {
            return null;
        }
        return letter.toUpperCase();
    }

}
