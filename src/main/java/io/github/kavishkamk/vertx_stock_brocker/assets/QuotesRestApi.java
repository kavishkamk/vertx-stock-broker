package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.domain.Asset;
import io.github.kavishkamk.vertx_stock_brocker.domain.Quote;
import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.GetQuotesFromDbHandler;
import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.GetQuotesHandler;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  public static void attach(Router router, PgPool pgPool) {

    final HashMap<String, Quote> cashedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> cashedQuotes.put(symbol, getQuoteBuilder(symbol)));

    router.get("/quotes/:assets")
      .handler(new GetQuotesHandler(cashedQuotes));
    router.get("/pg/quotes/:assets")
      .handler(new GetQuotesFromDbHandler(pgPool));

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
