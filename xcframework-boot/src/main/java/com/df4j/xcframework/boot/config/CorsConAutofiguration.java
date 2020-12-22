package com.df4j.xcframework.boot.config;

import com.df4j.xcframework.base.constant.Constants;
import com.df4j.xcframework.boot.utils.BinderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@ConditionalOnProperty(prefix = Constants.PROPERTIES_PREFIX + ".web.cors", name = "enabled", havingValue = "true")
public class CorsConAutofiguration {
    @Autowired
    private Environment environment;

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setMaxAge(3600l);
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        String debug = environment.getProperty(Constants.PROPERTIES_PREFIX + ".web.cors.debug");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = null;
        String paths = environment.getProperty(Constants.PROPERTIES_PREFIX + ".web.cors.paths");
        String[] pathArray = null;
        if (StringUtils.hasText(paths)) {
            pathArray = paths.split(",");
        } else {
            pathArray = new String[]{"/**"};
        }
        if ("true".equals(debug)) {
            corsConfiguration = new CorsConfiguration();
            corsConfiguration.setMaxAge(3600l);
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.addAllowedOrigin("*");
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addAllowedMethod("*");
        } else {
            corsConfiguration = BinderUtils.binder(environment, Constants.PROPERTIES_PREFIX + ".web.cors", CorsConfiguration.class);
        }
        for (int i = 0; i < pathArray.length; i++) {
            source.registerCorsConfiguration(pathArray[i], corsConfiguration);
        }
        return new CorsFilter(source);
    }
}
