package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;

public class GetAssetsFromDbHandler implements Handler<RoutingContext> {

  private final PgPool pgPool;
  public GetAssetsFromDbHandler(PgPool pgPool) {
    this.pgPool = pgPool;
  }

  @Override
  public void handle(RoutingContext routingContext) {

    pgPool.query("SELECT a.value FROM broker.assets a")
      .execute()
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
        JsonArray response = new JsonArray();

        result.forEach(row -> {
          response.add(row.getValue("value"));
        });
        System.out.println("Path: " + routingContext.normalizedPath() + " , response with: " + response.encode());
        routingContext.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
