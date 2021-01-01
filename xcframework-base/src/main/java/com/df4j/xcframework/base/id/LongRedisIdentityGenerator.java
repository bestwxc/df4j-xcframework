package com.df4j.xcframework.base.id;

import com.df4j.xcframework.base.lock.RedisDistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ObjectUtils;

public class LongRedisIdentityGenerator implements IdentityGenerator<Long> {

    private Logger logger = LoggerFactory.getLogger(LongRedisIdentityGenerator.class);

    private final String sql = "select max(id) from %S";

    private StringRedisTemplate stringRedisTemplate;

    private RedisDistributedLock redisDistributedLock;

    private JdbcTemplate jdbcTemplate;

    public LongRedisIdentityGenerator() {
    }

    public LongRedisIdentityGenerator(StringRedisTemplate stringRedisTemplate, RedisDistributedLock redisDistributedLock, JdbcTemplate jdbcTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisDistributedLock = redisDistributedLock;
        this.jdbcTemplate = jdbcTemplate;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public RedisDistributedLock getRedisDistributedLock() {
        return redisDistributedLock;
    }

    public void setRedisDistributedLock(RedisDistributedLock redisDistributedLock) {
        this.redisDistributedLock = redisDistributedLock;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long generate(String keyGroup, String keyName) {
        String key = this.getKey(keyGroup, keyName);
        Long value = stringRedisTemplate.opsForValue().increment(key);
        boolean checked = this.validate(value);
        if (checked) {
            return value;
        } else {
            synchronized (this) {
                logger.warn("当前未初始化该主键生成方式，keyGroup, keyName:{}");
                String lockId = String.valueOf(System.currentTimeMillis());
                boolean flag = redisDistributedLock.doInLock(keyGroup, keyName, lockId, 30000, () -> {
                    String redisKey = redisDistributedLock.getKey(keyGroup, keyName);
                    Long tmp = stringRedisTemplate.opsForValue().increment(redisKey, 0);
                    boolean checkTemp = this.validate(tmp);
                    logger.info("查询到当前的值,keyGroup:{}, keyName:{}, tmp:{}, checked:{}",
                            keyGroup, keyName, tmp, checked);
                    if (checkTemp) return true;
                    Long id = jdbcTemplate.queryForObject(String.format(sql, keyName), Long.class);
                    id = ObjectUtils.isEmpty(id) ? 1000L : id + 1;
                    stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(id));
                    tmp = stringRedisTemplate.opsForValue().increment(redisKey, 0);
                    logger.info("重新初始化，查询到当前的值,keyGroup:{}, keyName:{}, tmp:{}, checked:{}",
                            keyGroup, keyName, tmp, checked);
                    return this.validate(tmp);
                });
                logger.info("更新后的值为,keyGroup:{}, keyName:{}, tmp:{}, flag:{}",
                        keyGroup, keyName, flag);
                return this.generate(keyGroup, keyName);
            }
        }
    }

    @Override
    public boolean validate(Long value) {
        return value != null && value >= 1000;
    }

    private String getKey(String keyGroup, String keyName) {
        return ("IDENTITY:" + keyGroup + ":" + keyName).toUpperCase();
    }
}
