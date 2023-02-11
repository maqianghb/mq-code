package com.example.mq.data.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class DataSourceRouter extends AbstractRoutingDataSource {

    private Map<Object, Object> dataSourceMap;

    //根据dataSourceKey选择对应的dataSource
    @Override
    protected String determineCurrentLookupKey() {
        return HotDsKeyHolder.getDBKey();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Assert.notNull(dataSourceMap, "dataSourceMap is null.");
        String dataSourceKey =null;
        if(StringUtils.isEmpty(dataSourceKey =determineCurrentLookupKey())
                || dataSourceMap.get(dataSourceKey) ==null){
            log.error("dataSourceKey or dataSource is null, key:{}", dataSourceKey);
            throw new IllegalArgumentException("data source do not exist, dsKey:" + dataSourceKey);
        }
        return super.determineTargetDataSource();
    }

    public void setDataSourceMap(Map<Object,Object> dataSourceMap){
        this.dataSourceMap = dataSourceMap;
        super.setTargetDataSources(dataSourceMap);
    }
}
