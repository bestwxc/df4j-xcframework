package com.df4j.xcframework.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DefaultDynamicDataSource extends AbstractRoutingDataSource {

    private Logger logger = LoggerFactory.getLogger(DefaultDynamicDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String key = DataSourceNodeManager.getDataSourceKey();
        logger.debug("从[{}]数据源获取连接", key);
        return key;
    }
}
