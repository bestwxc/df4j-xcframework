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
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "df.boot.datasource", name = "enabled", havingValue = "true")
public class DynamicDatasourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private Logger logger = LoggerFactory.getLogger(DynamicDatasourceBeanFactoryPostProcessor.class);

    private static String SPRING_DATASOURCE_PREFIX = "spring.datasource";

    private static String DEFAULT_DATASOURCE_PREFIX = "com.df4j.xcframework.datasource.datasources";

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
        Environment environment = beanFactory.getBean(Environment.class);
        DynamicDatasourceProperties dynamicDatasourceProperties =
                BinderUtils.binder(environment, DEFAULT_DATASOURCE_PREFIX, DynamicDatasourceProperties.class);
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
        Map<String, DynamicDatasourceProperties.DynamicDatasourceNode> datasources = dynamicDatasourceProperties.getDatasources();
        logger.info("动态数据源由下列数据源组成:{}", datasources.keySet());
        DefaultDynamicDataSource dynamicDataSource = new DefaultDynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String datasourceKey : datasources.keySet()) {
            DynamicDatasourceProperties.DynamicDatasourceNode datasource = datasources.get(datasourceKey);
            Map<String, Map<String, Object>> nodes = datasource.getNodes();
            Map<String, String> nodeBeanKeys = new HashMap<>();
            for (String nodeKey : nodes.keySet()) {
                logger.debug("开始配置{}数据源{}节点", datasourceKey, nodeKey);
                String beanName = datasourceKey + com.df4j.xcframework.base.util.StringUtils.objectNameToClassName(nodeKey) + "DataSource";
                DataSource dataSource = this.initDataSource(environment, datasource.getType(), datasourceKey, nodeKey);
                nodeBeanKeys.put(nodeKey, beanName);
                factory.registerSingleton(beanName, dataSource);
                logger.info("配置{}数据源{}节点完成", datasourceKey, nodeKey);
                targetDataSources.put(beanName, dataSource);
            }
            DataSourceNodeManager.addDataSource(datasourceKey, datasource.getMaster(), nodeBeanKeys);
        }
        DataSourceNodeManager.setDefaultDataSourceKey(dynamicDatasourceProperties.getDefaultKey());
        dynamicDataSource.setDefaultTargetDataSource(beanFactory.getBean(DataSourceNodeManager.getDefaultDataSourceKey()));
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.afterPropertiesSet();
        factory.registerSingleton("dataSource", dynamicDataSource);
        logger.debug("配置自定义多数据源完成");
    }

    private DataSource initDataSource(Environment environment, String type, String datasourceKey, String nodeKey) {
        String[] binderTypes = new String[]{
                SPRING_DATASOURCE_PREFIX,
                SPRING_DATASOURCE_PREFIX + "." + type,
                DEFAULT_DATASOURCE_PREFIX + "." + datasourceKey + ".nodes." + nodeKey};
        // 用属性生成对象
        DataSourceProperties dataSourceProperties = BinderUtils.binder(environment, binderTypes, DataSourceProperties.class);
        DataSource dataSource = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(this.getDatasourceClass(type))
                .build();
        // 再次绑定属性
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
