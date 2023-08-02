package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.domain.Asset;
import io.github.kavishkamk.vertx_stock_brocker.domain.Quote;
import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.GetQuotesHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  public static void attach(Router router) {

    final HashMap<String, Quote> cashedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> cashedQuotes.put(symbol, getQuoteBuilder(symbol)));

    router.get("/quotes/:assets")
      .handler(new GetQuotesHandler(cashedQuotes));

  }

  private static Quote getQuoteBuilder(String asset) {
    return Quote.builder()
      .asset(new Asset(asset))
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }

}
