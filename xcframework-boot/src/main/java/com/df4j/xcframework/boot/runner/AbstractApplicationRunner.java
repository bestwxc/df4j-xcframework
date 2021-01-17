package com.df4j.xcframework.boot.runner;

import com.df4j.xcframework.base.concurrent.AbstractRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 抽象的ApplicationRunner,用于提供一般的异常捕捉机制
 */
public abstract class AbstractApplicationRunner extends AbstractRunnable implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(AbstractApplicationRunner.class);

    private ApplicationArguments args;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.setArgs(args);
        this.run();
    }

    public ApplicationArguments getArgs() {
        return args;
    }

    public void setArgs(ApplicationArguments args) {
        this.args = args;
    }
}
