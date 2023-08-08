package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.github.kavishkamk.vertx_stock_brocker.domain.QuoteEntity;
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

public class GetQuotesFromDbHandler implements Handler<RoutingContext> {

  private final Pool pool;
  public GetQuotesFromDbHandler(PgPool pgPool) {
    this.pool = pgPool;
  }

  @Override
  public void handle(RoutingContext context) {
    final String asset = context.pathParam("assets");
    System.out.println("asset: " + asset);

    SqlTemplate.forQuery(pool, "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume From broker.quotes q WHERE asset = #{asset}" )
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset", asset))
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
      .onSuccess(quotes -> {
        if(!quotes.iterator().hasNext()) {
          context.response()
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject()
              .put("message", "not found Quate for " + asset)
              .put("path", context.normalizedPath())
              .toBuffer()
            );
          return;
        }
        var response = quotes.iterator().next().toJsonObject();
        System.out.println("Context path: " + context.normalizedPath() + ", " + response.encode());
        context.response().end(response.toBuffer());
      });
  }
}
