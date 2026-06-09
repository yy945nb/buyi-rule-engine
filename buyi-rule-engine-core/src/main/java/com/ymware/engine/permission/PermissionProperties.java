package com.ymware.engine.permission;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mybatis.permission.plugin")
public class PermissionProperties {

    //插件是否启用
    private Boolean enable = false;

    //租户字段
    private String column = "shop_id";

    //忽略的表以,分割
    private String ignoreTables;

    // 仅仅包含的表
    private String includeTables;

    //mapper文件位置以,分割
    private String mapperLocations;

    private String entityLocations;

}
