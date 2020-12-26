package com.df4j.xcframework.boot.datasource;

import com.df4j.xcframework.base.exception.XcException;
import com.df4j.xcframework.boot.utils.BinderUtils;
import com.df4j.xcframework.jdbc.datasource.DataSourceNodeManager;
import com.df4j.xcframework.jdbc.datasource.DataSourceType;
import com.df4j.xcframework.jdbc.datasource.DefaultDynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static com.df4j.xcframework.boot.datasource.DynamicDatasourceBeanFactoryPostProcessor.DEFAULT_DATASOURCE_PREFIX;

@Component
@ConditionalOnProperty(prefix = DEFAULT_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
public class DynamicDatasourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private Logger logger = LoggerFactory.getLogger(DynamicDatasourceBeanFactoryPostProcessor.class);

    private static final String SPRING_DATASOURCE_PREFIX = "spring.datasource";

    public static final String DEFAULT_DATASOURCE_PREFIX = "df4j.xcframework.datasource";

    private String dynamicDatasourcePropertiesPrefix;

    public DynamicDatasourceBeanFactoryPostProcessor() {
        this(DEFAULT_DATASOURCE_PREFIX);
    }

    public DynamicDatasourceBeanFactoryPostProcessor(String dynamicDatasourcePropertiesPrefix) {
        this.dynamicDatasourcePropertiesPrefix = dynamicDatasourcePropertiesPrefix;
    }

    public String getDynamicDatasourcePropertiesPrefix() {
        return dynamicDatasourcePropertiesPrefix;
    }

    public void setDynamicDatasourcePropertiesPrefix(String dynamicDatasourcePropertiesPrefix) {
        this.dynamicDatasourcePropertiesPrefix = dynamicDatasourcePropertiesPrefix;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("动态数据源开启，开始进行动态多数据源配置");
        DefaultDynamicDataSource dynamicDataSource = null;
        try {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
            Environment environment = beanFactory.getBean(Environment.class);
            DynamicDatasourceProperties dynamicDatasourceProperties =
                    BinderUtils.binder(environment, DEFAULT_DATASOURCE_PREFIX, DynamicDatasourceProperties.class);
            Map<String, DynamicDatasourceProperties.DynamicDatasourceNode> datasources = dynamicDatasourceProperties.getDatasources();
            if(!ObjectUtils.isEmpty(datasources)){
                logger.info("动态数据源由下列数据源组成:{}", datasources.keySet());
                dynamicDataSource = new DefaultDynamicDataSource();
                Map<Object, Object> targetDataSources = new HashMap<>();
                for (String datasourceKey : datasources.keySet()) {
                    DynamicDatasourceProperties.DynamicDatasourceNode datasource = datasources.get(datasourceKey);
                    Map<String, Map<String, Object>> nodes = datasource.getNodes();
                    Map<String, String> nodeBeanKeys = new HashMap<>();
                    for (String nodeKey : nodes.keySet()) {
                        logger.debug("开始配置{}数据源{}节点", datasourceKey, nodeKey);
                        String beanName = datasourceKey + com.df4j.xcframework.base.util.StringUtils.objectNameToClassName(nodeKey) + "DataSource";
                        DataSource dataSource = null;
                        try {
                            dataSource = this.initDataSource(environment, datasource.getType(), datasourceKey, nodeKey);
                            nodeBeanKeys.put(nodeKey, beanName);
                            factory.registerSingleton(beanName, dataSource);
                            logger.info("配置{}数据源{}节点完成", datasourceKey, nodeKey);
                            targetDataSources.put(beanName, dataSource);
                        }catch (Exception e) {
                            String msg = String.format("初始化多数据源节点错误,datasourceKey:%s, nodeKey:%s", datasourceKey, nodeKey);
                            logger.error(msg, e);
                        }
                    }
                    DataSourceNodeManager.addDataSource(datasourceKey, datasource.getMaster(), nodeBeanKeys);
                }
                DataSourceNodeManager.setDefaultDataSourceKey(dynamicDatasourceProperties.getDefaultKey());
                dynamicDataSource.setDefaultTargetDataSource(beanFactory.getBean(DataSourceNodeManager.getDefaultDataSourceKey()));
                dynamicDataSource.setTargetDataSources(targetDataSources);
                dynamicDataSource.afterPropertiesSet();
                factory.registerSingleton("dataSource", dynamicDataSource);
                logger.info("配置自定义多数据源完成，使用动态数据源代替spring-boot自动配置的数据源");
            } else {
                logger.error("多数源开启，但是未找到有效的多数据源配置，将按照spring boot auto configuration 的机制尝试注入默认数据源");
            }
        } catch (Exception e) {
            logger.error("初始话动态数据源出现异常", e);
        }
    }

    private DataSource initDataSource(Environment environment, String type, String datasourceKey, String nodeKey) {
        // 用属性绑定，此处只能绑定基础的配置，所以选用改两部分配置合并
        String[] binderTypes = new String[]{
                SPRING_DATASOURCE_PREFIX,
                DEFAULT_DATASOURCE_PREFIX + ".datasources." + datasourceKey + ".nodes." + nodeKey};
        DataSourceProperties dataSourceProperties = BinderUtils.binder(environment, binderTypes, DataSourceProperties.class);
        DataSource dataSource = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(this.getDatasourceClass(type))
                .build();
        // 再次绑定属性
        // 主要再次绑定数据源的配置信息
        binderTypes = new String[]{
                SPRING_DATASOURCE_PREFIX + "." + type,
                DEFAULT_DATASOURCE_PREFIX + ".datasources." + datasourceKey + ".nodes." + nodeKey};
        BinderUtils.binder(environment, binderTypes, dataSource);
        // 如果需要初始化，则初始化数据源
        if (StringUtils.hasText(DataSourceType.getDataSourceType(type).getInitMethodName())) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(dataSource.getClass(), DataSourceType.getDataSourceType(type).getInitMethodName()), dataSource);
        }
        return dataSource;
    }

    private Class<? extends DataSource> getDatasourceClass(String name) {
        DataSourceType dataSourceType = DataSourceType.getDataSourceType(name);
        String className = dataSourceType.getFullName();
        try {
            return (Class<? extends DataSource>) Class.forName(className);
        } catch (Exception e) {
            throw new XcException("未找到类" + className, e);
        }
    }
}
