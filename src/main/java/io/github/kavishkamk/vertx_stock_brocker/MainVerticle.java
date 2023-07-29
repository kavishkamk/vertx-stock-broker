package io.github.kavishkamk.vertx_stock_brocker;

import io.github.kavishkamk.vertx_stock_brocker.assets.AssetsRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      System.err.println("error: " + error.getMessage());
    });
    vertx.deployVerticle(new MainVerticle(), response -> {
      if (response.failed()) {
        System.err.println("error: " + response.cause());
        return;
      }
      System.out.println("Deployment success");
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final Router restApi = Router.router(vertx);

    restApi.route().failureHandler(routeFailerHandler());

    AssetsRestApi.attach(restApi);

    vertx.createHttpServer().requestHandler(restApi)
      .exceptionHandler(error -> System.err.println("Error: " + error.getCause()))
      .listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private static Handler<RoutingContext> routeFailerHandler() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        return;
      }
      System.out.println("error: " + errorContext.failure());
      errorContext.response().setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong").toBuffer());
    };
  }
}
