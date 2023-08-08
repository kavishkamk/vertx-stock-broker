package io.github.kavishkamk.vertx_stock_brocker;

import io.github.kavishkamk.vertx_stock_brocker.assets.AssetsRestApi;
import io.github.kavishkamk.vertx_stock_brocker.assets.QuotesRestApi;
import io.github.kavishkamk.vertx_stock_brocker.assets.WatchListRestApi;
import io.github.kavishkamk.vertx_stock_brocker.config.BrokerConfig;
import io.github.kavishkamk.vertx_stock_brocker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class RestApiVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {

    ConfigLoader.load(vertx)
        .onFailure(startPromise::fail)
          .onSuccess(configuration -> {
            System.out.println("loaded config: " + configuration);
            startRestApiAndAttachRoutes(startPromise, configuration);
          });
  }

  private void startRestApiAndAttachRoutes(Promise<Void> startPromise,
                                           BrokerConfig config) {

    PgPool pgPool = getPgPool(config);

    final Router restApi = Router.router(vertx);

    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(routeFailerHandler());

    AssetsRestApi.attach(restApi, pgPool);
    QuotesRestApi.attach(restApi, pgPool);
    WatchListRestApi.attach(restApi, pgPool);

    vertx.createHttpServer().requestHandler(restApi)
      .exceptionHandler(error -> System.err.println("Error: " + error.getCause()))
      .listen(config.getServerPort(), http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port: " + config.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private PgPool getPgPool(BrokerConfig config) {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setHost(config.getDbConfig().getHost())
      .setPort(config.getDbConfig().getPort())
      .setDatabase(config.getDbConfig().getDatabase())
      .setUser(config.getDbConfig().getUser())
      .setPassword(config.getDbConfig().getPassword());

    PoolOptions poolOptions = new PoolOptions().setMaxSize(4);

    return PgPool.pool(vertx, connectOptions, poolOptions);
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
