package com.df4j.xcframework.boot.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Conditional(SpringBootCacheCondition.class)
@ConditionalOnClass({org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class})
@AutoConfigureBefore(org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class)
public class CacheAutoConfiguration {
    // 根据属性配置启用spring-cache无需在主类上加入@EnableCaching
}
