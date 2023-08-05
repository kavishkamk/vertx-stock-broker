package io.github.kavishkamk.vertx_stock_brocker.db.migration;

import io.github.kavishkamk.vertx_stock_brocker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;

import java.util.Arrays;

public class FlywayMigration {
  public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {

    return vertx.<Void>executeBlocking(promise -> {
      execute(dbConfig);
      promise.complete();
    }).onFailure(err -> System.out.println("Failed migration: " + err));

  }

  private static void execute(DbConfig dbConfig) {
    final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
      dbConfig.getHost(),
      dbConfig.getPort(),
      dbConfig.getDatabase());
    System.out.println("Migration db schema: " + jdbcUrl);

    final Flyway flyway = Flyway.configure()
      .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();

    System.out.println("flyway info: " + flyway.info().current());

    System.out.println("pending migration: " + Arrays.toString(flyway.info().pending()));
    flyway.migrate();
  }
}
