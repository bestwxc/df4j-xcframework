package com.df4j.xcframework.base.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * 基于REDIS的分布式锁
 */
public class RedisDistributedLock extends AbstractDistributedLock {

    private Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

    private StringRedisTemplate stringRedisTemplate;

    private final static String UNLOCK_REDIS_SCRIPT
            = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";


    public RedisDistributedLock() {
    }

    public RedisDistributedLock(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean lock(String lockGroup, String lockName, String lockId, long ttl) {
        String key = this.getKey(lockGroup, lockName);
        // 使用set key value NX EX 100 一条命令的原子操作实现
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, lockId, ttl, TimeUnit.MILLISECONDS);
        if (ObjectUtils.isEmpty(flag)) {
            logger.error("当前模式stringRedisTemplate.opsForValue().setIfAbsent返回为null,不能正常加分布式锁！！！, " +
                    "lockGroup: {}, lockName:{}, lockId:{}", lockGroup, lockName, lockId);
        }
        return flag;
    }

    @Override
    public boolean unlock(String lockGroup, String lockName, String lockId) {
        String key = this.getKey(lockGroup, lockName);
        RedisScript<String> redisScript = new DefaultRedisScript(UNLOCK_REDIS_SCRIPT);
        // 使用一个lua脚本，保证原子性
        Object res = stringRedisTemplate.execute((RedisConnection connection)
                -> connection.eval(redisScript.getScriptAsString().getBytes(),
                ReturnType.INTEGER, 1, key.getBytes(), lockId.getBytes()));
        if (logger.isDebugEnabled()) {
            logger.info("解锁返回的结果为lockGroup: {}, lockName:{}, lockId:{}, res:{}", lockGroup, lockName, lockId, res);
        }
        return "1".equals(String.valueOf(res));
    }

    public String getKey(String lockGroup, String lockName) {
        return ("DISTRIBUTED_LOCK:" + lockGroup + ":" + lockName).toUpperCase();
    }
}
