package com.urly.urlyservices.dbconfig.properties;

import com.urly.urlyservices.enums.DBShard;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ShardDBPropertiesManager {

    private static final Map<DBShard, DBProperties> propertiesMap = new HashMap<>();

    public void load(DBShard dbShard, DBProperties dbProperties) {
        propertiesMap.put(dbShard, dbProperties);
    }

    public DBProperties getProperties(DBShard dbShard) {
        return propertiesMap.get(dbShard);
    }
}
