package io.github.kavishkamk.vertx_stock_brocker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
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
    vertx.deployVerticle(RestApiVerticle.class.getName(),
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
