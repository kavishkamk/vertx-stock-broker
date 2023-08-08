package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;

public class GetWatchListFromDbHandler implements Handler<RoutingContext> {

  private final Pool pool;
  public GetWatchListFromDbHandler(PgPool pgPath) {
    this.pool = pgPath;
  }

  @Override
  public void handle(RoutingContext context) {
    String accountId = context.pathParam("accountId");
    System.out.println("request reserved to: " + context.normalizedPath());

    SqlTemplate.forQuery(pool, "SELECT w.asset FROM broker.watchList w WHERE w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(error -> {
        System.err.println("Error occurred: " + error.getCause());
        context.response()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(new JsonObject()
            .put("Error", "Unknown Error:  " + error)
            .put("path", context.normalizedPath())
            .toBuffer()
          );
      })
      .onSuccess(assets -> {
        if (!assets.iterator().hasNext()) {
          context.response()
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject()
              .put("message", "not found watchlist for " + accountId)
              .put("path",context.normalizedPath())
              .toBuffer()
            );
          return;
        }

        var response = new JsonArray();
        assets.forEach(response::add);
        context.response().end(response.toBuffer());
      });
  }
}
