package com.urly.urlyservices.dbconfig.datasource;

import com.urly.urlyservices.dbconfig.properties.DBProperties;
import com.urly.urlyservices.dbconfig.properties.Shard1DBProperties;
import com.urly.urlyservices.dbconfig.properties.Shard2DBProperties;
import com.urly.urlyservices.enums.DBShard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class DBMigrationConfig {

    @Bean
    public Shard1DBProperties shard1DBProperties() {
        return new Shard1DBProperties();
    }

    @Bean
    public Shard2DBProperties shard2DBProperties() {
        return new Shard2DBProperties();
    }

    @Bean
    public DataSourceRouter dataSourceRouter() {
        Map<DBShard, DBProperties> configurations = new HashMap<>();
        configurations.put(DBShard.url0, shard1DBProperties());
        configurations.put(DBShard.url1, shard2DBProperties());

        DataSourceRouter routingDataSource = new DataSourceRouter();
        routingDataSource.initDataSources(configurations);
        return routingDataSource;
    }

    @Bean(initMethod = "migrate")
    @DependsOn(value = "dataSourceRouter")
    public FlywayMigrationInitializer flywayMigrationInitializer() {
        return new FlywayMigrationInitializer();
    }
}
