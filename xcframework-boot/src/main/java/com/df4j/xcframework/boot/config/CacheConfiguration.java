package com.df4j.xcframework.boot.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Conditional(SpringBootCacheCondition.class)
public class CacheConfiguration {
    // 根据属性配置启用spring-cache无需在主类上加入@EnableCaching
}
