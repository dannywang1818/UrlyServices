package com.urly.urlyservices.dbconfig.datasource;

import com.urly.urlyservices.enums.DBShard;
import com.urly.urlyservices.sharding.ModuloShardingDatabaseAlgorithm;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    @Resource
    DataSourceRouter dataSourceRouter;

    @Bean
    @DependsOn("flywayMigrationInitializer")
    public DataSource userShardingDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

        TableRuleConfiguration longToShortShardingRule = new TableRuleConfiguration("LONG_TO_SHORT", "url${0..1}.LONG_TO_SHORT");
        longToShortShardingRule.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("long_url",
                new ModuloShardingDatabaseAlgorithm()));

        TableRuleConfiguration longToSequenceIdShardingRule = new TableRuleConfiguration("LONG_TO_SEQUENCE_ID", "url${0..1}.LONG_TO_SEQUENCE_ID");
        longToSequenceIdShardingRule.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("long_url",
                new ModuloShardingDatabaseAlgorithm()));

        TableRuleConfiguration userShardingRule = new TableRuleConfiguration("USER", "url${0..1}.USER");
        userShardingRule.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("email",
                new ModuloShardingDatabaseAlgorithm()));

        shardingRuleConfig.getTableRuleConfigs().add(longToShortShardingRule);
        shardingRuleConfig.getTableRuleConfigs().add(longToSequenceIdShardingRule);
        shardingRuleConfig.getTableRuleConfigs().add(userShardingRule);

        Map<String, DataSource> dbMap = new HashMap<>(2);
        dbMap.put(DBShard.url0.name(), dataSourceRouter.getDataSource(DBShard.url0));
        dbMap.put(DBShard.url1.name(), dataSourceRouter.getDataSource(DBShard.url1));

        Properties properties = new Properties();
        properties.put("sql.show", true);

        return ShardingDataSourceFactory.createDataSource(dbMap, shardingRuleConfig, properties);
    }
}
