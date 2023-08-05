package io.github.kavishkamk.vertx_stock_brocker;

import io.github.kavishkamk.vertx_stock_brocker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class VersionInfoVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        System.out.println("Current application version is: " + configuration.getVersion());
        startPromise.complete();
      });
  }
}
