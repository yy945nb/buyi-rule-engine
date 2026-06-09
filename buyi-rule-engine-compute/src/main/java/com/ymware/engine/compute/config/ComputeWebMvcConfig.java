/*
 * Copyright (c) 2020 dingqianwen (761945125@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ymware.engine.compute.config;


import com.ymware.engine.utils.TraceInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * 〈一句话功能简述〉<br>
 * 〈mvc Interceptor〉
 *
 * @author 丁乾文
 * @date 2021/6/17
 * @since 1.0.0
 */
public class ComputeWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private TraceInterceptor traceInterceptor;

    /**
     * 静态资源不拦截
     */
    private static final List<String> STATIC_RESOURCE = Arrays.asList(
            "/swagger-ui.html/**", "/swagger-resources/**", "/webjars/**", "/v2/**", "/csrf/**", "/doc.html/**",
            "/druid/**",
            "/error/**");

    /**
     * @param registry 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.traceInterceptor).addPathPatterns("/**")
                .excludePathPatterns(STATIC_RESOURCE);
        ;
    }

}
