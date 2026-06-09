package com.ymware.engine.permission;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PermissionProperties.class)
@ConditionalOnClass(IPermissionDefine.class)
@ConditionalOnProperty(prefix = "mybatis.tenant.plugin", name = "enable", havingValue = "true")
public class PermissionPluginAutoConfiguration {

    @Autowired
    private PermissionProperties tenantProperties;

    @Bean
    @ConditionalOnMissingBean(IPermissionDefine.class)
    public PermissionPluginContext tenantPluginContext() {
        return new PermissionPluginContext(tenantProperties);
    }


    @Bean
    public PermissionPluginInterceptor tenantPluginInterceptor() {
        return new PermissionPluginInterceptor(new IPermissionDefine() {
            @Override
            public Expression getTenantId() {
                return new StringValue(PermissionPluginContext.getShopIds());
            }
        });
    }
}
