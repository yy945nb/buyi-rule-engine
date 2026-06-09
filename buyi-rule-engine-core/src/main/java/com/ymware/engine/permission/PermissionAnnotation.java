package com.ymware.engine.permission;

import java.lang.annotation.*;

/**
 * MP数据权限注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PermissionAnnotation {

    /**
     */
    String tenantAlias() default "";

    String tenantIdColumnName() default "shop_id";

    String userAlias() default "";

    String userIdColumnName() default "create_by";

}

