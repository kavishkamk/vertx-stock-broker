package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;

public class DeleteWatchListFromDbHandler implements Handler<RoutingContext> {

  private final Pool pool;
  public DeleteWatchListFromDbHandler(PgPool pgPool) {
    this.pool = pgPool;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String accountId = routingContext.pathParam("accountId");
    System.out.println("request reserved to: " + routingContext.normalizedPath());

    SqlTemplate.forUpdate(pool, "DELETE FROM broker.watchList WHERE account_id=#{account_id}")
      .execute(Collections.singletonMap("account_id", accountId))
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
      .onSuccess(response -> {
        System.out.println("Deleted count: " + response.rowCount());
        routingContext.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
  }
}
