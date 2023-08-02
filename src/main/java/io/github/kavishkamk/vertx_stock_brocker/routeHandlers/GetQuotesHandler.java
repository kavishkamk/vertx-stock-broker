package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.github.kavishkamk.vertx_stock_brocker.domain.Quote;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Optional;

public class GetQuotesHandler implements Handler<RoutingContext> {

  private final HashMap<String, Quote> cashedQuotes;

  public GetQuotesHandler(HashMap<String, Quote> cashedQuotes) {
    this.cashedQuotes = cashedQuotes;
  }

  @Override
  public void handle(RoutingContext context) {
    final String asset = context.pathParam("assets");
    System.out.println("asset: " + asset);

    final Optional<Quote> quote = Optional.ofNullable(cashedQuotes.get(asset));

    if (quote.isEmpty()) {
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message", "not found Quate for " + asset)
          .put("path", context.normalizedPath())
          .toBuffer()
        );
      return;
    }

    final JsonObject response = quote.get().toJsonObject();

    System.out.println("Context path: " + context.normalizedPath() + ", " + response.encode());
    context.response().end(response.toBuffer());
  }
}
