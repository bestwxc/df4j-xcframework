package com.df4j.xcframework.boot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SpringBootCacheCondition extends SpringBootCondition {

    private Logger logger = LoggerFactory.getLogger(SpringBootCacheCondition.class);

    public boolean countMatche(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String type = environment.getProperty("spring.cache.type");
        if (StringUtils.hasText(type)) {
            CacheType cacheType = null;
            try {
                cacheType = CacheType.valueOf(type.toUpperCase());
            } catch (Exception e) {
                logger.error("spring.cache.type值不合法,value:" + type, e);
            }
            return !ObjectUtils.isEmpty(cacheType)
                    && !ObjectUtils.nullSafeEquals(CacheType.NONE, cacheType);
        }
        return false;
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean match = this.countMatche(context, metadata);
        if (match) {
            return ConditionOutcome.match("spring.cache.type不为空且不为null");
        } else {
            return ConditionOutcome.noMatch("spring.cache.type的值为none或无法转化为有效的类型");
        }

    }
}
