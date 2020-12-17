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
        this.getMsg("starting");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        this.getMsg("environmentPrepared");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        this.getMsg("contextPrepared");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        this.getMsg("contextLoaded");
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        this.getMsg("started");
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        this.getMsg("running");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        this.getMsg("failed");
    }

    private String getMsg(String type) {
        return String.format("[非异常,用来打印SpringBoot程序启动声明周期关键节点信息]当前程序%s", type);
    }
}
