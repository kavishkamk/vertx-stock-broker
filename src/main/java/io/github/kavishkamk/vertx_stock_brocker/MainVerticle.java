package io.github.kavishkamk.vertx_stock_brocker;

import io.github.kavishkamk.vertx_stock_brocker.config.ConfigLoader;
import io.github.kavishkamk.vertx_stock_brocker.db.migration.FlywayMigration;
import io.vertx.core.*;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
//    System.setProperty(ConfigLoader.SERVER_PORT, "9000");
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      System.err.println("error: " + error.getMessage());
    });
    vertx.deployVerticle(new MainVerticle())
      .onFailure(error -> System.err.println("error: " + error))
      .onSuccess(id -> {
        System.out.println("Deployed " + MainVerticle.class.getName() + " with id " + id);
      });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(VersionInfoVerticle.class.getName())
      .onFailure(startPromise::fail)
      .onSuccess(id ->
        System.out.println("Deployed " + VersionInfoVerticle.class.getName() + " with id " + id)
      )
      .compose(next -> migrateDatabase())
      .onFailure(startPromise::fail)
      .onSuccess(id ->
        System.out.println("DB migration success with id: " + id)
      )
      .compose(next ->
        startRestApiVerticle(startPromise)
      );
  }

  private Future<Void> migrateDatabase() {
    return ConfigLoader.load(vertx)
      .compose(config -> FlywayMigration.migrate(vertx, config.getDbConfig()));
  }

  private Future<String> startRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
        new DeploymentOptions()
          .setInstances(getAvailableProcessors())
      )
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        System.out.println("Deployed " + RestApiVerticle.class.getName() + " with id " + id);
        startPromise.complete();
      });
  }

  private static int getAvailableProcessors() {
    return Math.max(1, Runtime.getRuntime().availableProcessors());
  }


}
