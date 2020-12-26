package com.df4j.xcframework.boot.listner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class DefaultSpringApplicationRunListener implements SpringApplicationRunListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SpringApplication application;
    private String[] args;

    public DefaultSpringApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting() {
        logger.error(this.getMsg("starting"));
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        logger.error(this.getMsg("environmentPrepared"));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        logger.error(this.getMsg("contextPrepared"));
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        logger.error(this.getMsg("contextLoaded"));
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        logger.error(this.getMsg("started"));
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        logger.error(this.getMsg("running"));
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        logger.error(this.getMsg("failed"));
    }

    private String getMsg(String type) {
        return String.format("[非异常,用来打印SpringBoot程序启动声明周期关键节点信息]当前程序%s", type);
    }
}
