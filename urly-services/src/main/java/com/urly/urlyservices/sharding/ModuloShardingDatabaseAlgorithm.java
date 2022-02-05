package com.urly.urlyservices.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;
import java.util.Optional;

@Slf4j
public class ModuloShardingDatabaseAlgorithm implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(
            Collection<String> databaseNames, PreciseShardingValue<String> shardingValue) {

        String databaseName = "";

        if ("LONG_TO_SHORT".equalsIgnoreCase(shardingValue.getLogicTableName())
                || "LONG_TO_SEQUENCE_ID".equalsIgnoreCase(shardingValue.getLogicTableName())
                || "USER".equalsIgnoreCase(shardingValue.getLogicTableName())) {

            String shardValue = Optional.of(shardingValue.getValue()).orElse("");

            if (StringUtils.isNotBlank(shardValue)) {
                log.info("shard value: " + shardValue);
                databaseName =
                        findDatabaseName(shardValue.hashCode() % databaseNames.size() + "", databaseNames);
            }
        }

        log.info("databaseName:{}", databaseName);
        if (StringUtils.isNotEmpty(databaseName)) {
            return databaseName;
        }
        return "url0";
    }

    private String findDatabaseName(String dbIndexStr, Collection<String> databaseNames) {
        String databaseName = "";

        for (String each : databaseNames) {
            if (each.endsWith(dbIndexStr)) {
                databaseName = each;
                break;
            }
        }

        return databaseName;
    }

}
