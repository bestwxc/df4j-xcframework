package com.df4j.xcframework.base.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public abstract class AbstractDistributedLock implements DistributedLock {

    private Logger logger = LoggerFactory.getLogger(AbstractDistributedLock.class);


    @Override
    public boolean doInLock(String lockGroup, String lockName, String lockId, long ttl, Callable<Boolean> callable) {
        boolean res = false;
        boolean flag = this.lock(lockGroup, lockName, lockId, ttl);
        if (flag) {
            try {
                res = callable.call();
                if (logger.isDebugEnabled()) {
                    logger.debug("解锁逻辑返回false,lockGroup:{}, lockName:{}, lockId:{}, res:{}",
                            lockGroup, lockName, lockId, res);
                }
            } catch (Exception e) {
                String msg = String.format("解锁逻辑返回false,lockGroup:%s, lockName:%s, lockId:%s",
                        lockGroup, lockName, lockId);
                logger.error(msg, e);
            } finally {
                boolean unlockFlag = this.unlock(lockGroup, lockName, lockId);
                if (!unlockFlag) {
                    logger.error("解锁逻辑返回false,lockGroup:{}, lockName:{}, lockId:{}",
                            lockGroup, lockName, lockId);
                }
            }
        }
        return res;
    }
}
