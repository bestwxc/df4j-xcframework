package com.df4j.xcframework.web.config;

import com.df4j.xcframework.web.advice.ExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ExceptionHandler.class)
public class ExceptionHandlerAutoConfiguration {

}
