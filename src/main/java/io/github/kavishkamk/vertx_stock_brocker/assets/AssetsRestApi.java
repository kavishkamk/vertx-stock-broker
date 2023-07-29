package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.domain.Asset;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

public class AssetsRestApi {

  public static void attach(Router router) {

    router.get("/assets").handler(context -> {
      final JsonArray response = new JsonArray()
        .add(new Asset("AAPL"))
        .add(new Asset("AMZN"))
        .add(new Asset("NFLX"))
        .add(new Asset("TSLA"));
      System.out.println("Context path: " + context.normalizedPath() + ", " + response.encode());
      context.response().end(response.toBuffer());

    });

  }

}
