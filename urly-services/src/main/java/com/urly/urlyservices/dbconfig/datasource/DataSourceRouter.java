package com.urly.urlyservices.dbconfig.datasource;

import com.urly.urlyservices.dbconfig.properties.DBProperties;
import com.urly.urlyservices.dbconfig.properties.ShardDBPropertiesManager;
import com.urly.urlyservices.enums.DBShard;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DataSourceRouter extends AbstractRoutingDataSource {

    private static final Map<Object, Object> dataSourceMap = new HashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }

    public void initDataSources(Map<DBShard, DBProperties> configurations) {
        for (DBShard shard : DBShard.values()) {
            dataSourceMap.put(shard, new HikariDataSource(hikariConfig(configurations.get(shard))));
        }
        setDefaultTargetDataSource(getDataSource(DBShard.url0));
        setTargetDataSources(dataSourceMap);

    }

    public DataSource getDataSource(DBShard dbShard) {
        return (DataSource) dataSourceMap.get(dbShard);
    }

    private HikariConfig hikariConfig(DBProperties configuration) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl(configuration.getUrl());
        hikariConfig.setUsername(configuration.getUsername());
        hikariConfig.setPassword(configuration.getPassword());

        return hikariConfig;
    }
}
