package com.df4j.xcframework.boot.config;

import com.df4j.xcframework.boot.datasource.DynamicDatasourceBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.df4j.xcframework.boot.datasource.DynamicDatasourceBeanFactoryPostProcessor.DEFAULT_DATASOURCE_PREFIX;

@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@ConditionalOnProperty(prefix = DEFAULT_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
public class DynamicDataSourceAutoConfiguration {

    @Bean
    DynamicDatasourceBeanFactoryPostProcessor dynamicDatasourceBeanFactoryPostProcessor() {
        return new DynamicDatasourceBeanFactoryPostProcessor();
    }

}
