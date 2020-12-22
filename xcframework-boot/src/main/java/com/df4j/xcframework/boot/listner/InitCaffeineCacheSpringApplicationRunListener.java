package com.df4j.xcframework.boot.listner;

import com.df4j.xcframework.base.util.JsonUtils;
import com.df4j.xcframework.boot.utils.FactoryConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

public class InitCaffeineCacheSpringApplicationRunListener implements SpringApplicationRunListener {

    private static String CAFFEINE_CACHE_NAMES = "com.df4j.xcframework.boot.cache.caffeine.cacheNames";

    private Logger logger = LoggerFactory.getLogger(InitCaffeineCacheSpringApplicationRunListener.class);

    private SpringApplication application;
    private String[] args;

    public InitCaffeineCacheSpringApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        CaffeineCacheManager caffeineCacheManager = null;
        if (context instanceof AnnotationConfigServletWebServerApplicationContext) {
            try {
                caffeineCacheManager = context.getBeanFactory().getBean(CaffeineCacheManager.class);
                if (ObjectUtils.isEmpty(caffeineCacheManager)) {
                    logger.info("获取不到caffeineCacheManager，请检查是否启用了caffeine cache");
                }
                List<String> cacheNames = FactoryConfigUtils.loadFactoryNames(CAFFEINE_CACHE_NAMES);
                Environment environment = context.getEnvironment();
                final String applicationName = environment.getProperty("spring.cache.type");
                if (!ObjectUtils.isEmpty(cacheNames)) {
                    logger.info("即将在caffeineCacheManager增加applicatino中配置的缓存类型,applicationName:{}, keys:{}",
                            applicationName, JsonUtils.stringify(cacheNames));
                    List<String> handledCacheNames = cacheNames.stream()
                            .map(x -> {
                                String tmp = x;
                                if (x.contains("@")) {
                                    tmp = x.substring(0, x.indexOf("@"));
                                }
                                return tmp + "@" + applicationName;
                            }).collect(Collectors.toList());
                    caffeineCacheManager.setCacheNames(handledCacheNames);
                    logger.info("在caffeineCacheManager增加applicatino中配置的缓存类型成功,applicationName:{}, keys:{}",
                            applicationName, JsonUtils.stringify(handledCacheNames));
                } else {
                    logger.info("程序中未配置缓存初始化,applicationName:{}", applicationName);
                }
            } catch (Exception e) {
                logger.error("初始化缓存出现错误", e);
            }
            logger.info("程序中所有初始化的缓存类型为:{}", JsonUtils.stringify(caffeineCacheManager.getCacheNames()));
        } else {
            logger.info("context类型为{}，跳过当前应用的缓存初始化", context.getClass().getName());
        }
    }
}
