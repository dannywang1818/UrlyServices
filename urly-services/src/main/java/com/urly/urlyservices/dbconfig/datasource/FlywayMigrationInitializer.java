package com.urly.urlyservices.dbconfig.datasource;

import com.urly.urlyservices.enums.DBShard;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class FlywayMigrationInitializer {

    @Autowired
    private DataSourceRouter dataSourceRouter;

    public void migrate() {

        String scriptLocation = "db/migration";

        for (DBShard shard : DBShard.values()) {

            Flyway flyway = Flyway.configure()
                    .locations(scriptLocation)
                    .baselineOnMigrate(Boolean.TRUE)
                    .dataSource(dataSourceRouter.getDataSource(shard))
                    .schemas(shard.name())
                    .load();
            flyway.migrate();
        }
    }
}
