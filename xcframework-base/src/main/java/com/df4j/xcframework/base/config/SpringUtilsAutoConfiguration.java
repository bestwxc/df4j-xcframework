package com.df4j.xcframework.base.config;

import com.df4j.xcframework.base.util.SpringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringUtilsAutoConfiguration {
    @Bean(name = "xcSpringUtils")
    public SpringUtils springUtils() {
        return new SpringUtils();
    }
}
