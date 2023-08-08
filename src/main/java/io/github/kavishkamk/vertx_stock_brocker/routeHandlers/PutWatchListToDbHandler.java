package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.github.kavishkamk.vertx_stock_brocker.assets.WatchListRestApi;
import io.github.kavishkamk.vertx_stock_brocker.domain.WatchList;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

public class PutWatchListToDbHandler implements Handler<RoutingContext> {

  private final PgPool pool;
  public PutWatchListToDbHandler(PgPool pgPool) {
    this.pool = pgPool;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String accountId = routingContext.pathParam("accountId");
    System.out.println("request reserved to: " + routingContext.normalizedPath());

    JsonObject jsonObject = routingContext.body().asJsonObject();
    WatchList watchList = jsonObject.mapTo(WatchList.class);

    watchList.getAssets().forEach(asset -> {
      final HashMap<String, Object> parameters = new HashMap<>();

      parameters.put("account_id", accountId);
      parameters.put("asset", asset.getSymbol());

      SqlTemplate.forUpdate(pool, "INSERT INTO broker.watchList VALUES(#{account_id}, #{asset})")
        .execute(parameters)
        .onFailure(error -> {
          System.err.println("Error occurred: " + error.getCause());
          routingContext.response()
            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(new JsonObject()
              .put("Error", "Unknown Error:  " + error)
              .put("path", routingContext.normalizedPath())
              .toBuffer()
            );
        })
        .onSuccess(result -> {
          if(!routingContext.response().ended()) {
            routingContext.response()
              .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
              .end();
          }
        });
    });
  }
}
