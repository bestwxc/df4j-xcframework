package com.df4j.xcframework.base.lock;

import java.util.concurrent.Callable;

/**
 * 分布式锁
 */
public interface DistributedLock{
    /**
     * 锁定
     * @param lockGroup 锁分组
     * @param lockName 锁名称
     * @param lockId 锁ID
     * @param ttl 过期时间 单位为毫秒
     * @return
     */
    boolean lock(String lockGroup, String lockName, String lockId, long ttl);

    /**
     * 解锁
     * @param lockGroup 锁分组
     * @param lockName 锁名称
     * @param lockId 锁ID
     * @return
     */
    boolean unlock(String lockGroup, String lockName, String lockId);

    /**
     * 在锁中执行逻辑
     * @param lockGroup
     * @param lockName
     * @param lockId
     * @param ttl
     * @param callable
     * @return
     */
    boolean doInLock(String lockGroup, String lockName, String lockId, long ttl,Callable<Boolean> callable);
}
