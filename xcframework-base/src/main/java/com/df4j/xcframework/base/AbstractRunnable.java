package com.df4j.xcframework.base;

import com.df4j.xcframework.base.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(AbstractRunnable.class);

    private boolean throwException = false;

    public boolean isThrowException() {
        return throwException;
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    private String getName() {
        return this.getClass().getName();
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        long beginTime = System.currentTimeMillis();
        try {
            logger.info("Task-{}[{}] start.",
                    this.getName(), threadName);
            this.run0();
            logger.info("Task-{}[{}] send, cost {} ms.",
                    this.getName(), threadName, System.currentTimeMillis() - beginTime);
        } catch (Throwable t) {
            String errorMsg = String.format("Task-{%s}[%s] error.", this.getName(), threadName);
            if (throwException) {
                BusinessException exception = t instanceof BusinessException ? (BusinessException) t : new BusinessException(null, null, null, t);
                throw exception;
            } else {
                logger.error(errorMsg, t);
            }
        }
    }


    abstract void run0();
}
