package com.df4j.xcframework.boot.config;

import com.df4j.xcframework.base.exception.XcException;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SpringBootCacheCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String type = environment.getProperty("spring.cache.type");
        if (StringUtils.hasText(type)) {
            CacheType cacheType = null;
            try {
                cacheType = CacheType.valueOf(type.toUpperCase());
            } catch (Exception e) {
                throw new XcException("spring.cache.type值不合法,value:" + type, e);
            }
            return !ObjectUtils.isEmpty(cacheType)
                    && ObjectUtils.nullSafeEquals(CacheType.NONE, cacheType);
        }
        return false;
    }
}
