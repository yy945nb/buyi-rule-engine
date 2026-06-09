package com.ymware.engine.annotation;

import java.lang.annotation.*;

/**
 * 标注动态变量的 Key
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VariableKey {
    /**
     * 变量名称
     */
    String value() default "";
}
