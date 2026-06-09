package com.ymware.engine.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ymware.engine.infrastructure.persistence.mapper")
public class MybatisConfig implements InitializingBean {

    private final SqlSessionFactory sqlSessionFactory;

    public MybatisConfig(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
        typeHandlerRegistry.register(LocalDateTimeHandler.class);
    }
}
