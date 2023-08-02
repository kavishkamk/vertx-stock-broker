package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.github.kavishkamk.vertx_stock_brocker.domain.Asset;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

import static io.github.kavishkamk.vertx_stock_brocker.assets.AssetsRestApi.ASSETS;

public class GetAssetsHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext context) {
    final JsonArray response = new JsonArray();
    ASSETS.stream().map(Asset::new).forEach(response::add);
    System.out.println("Context path: " + context.normalizedPath() + ", " + response.encode());
    context
      .response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("my-header", "my-value")
      .end(response.toBuffer());
  }
}
