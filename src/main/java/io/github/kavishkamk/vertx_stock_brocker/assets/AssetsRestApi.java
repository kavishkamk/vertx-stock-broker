package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.GetAssetsHandler;
import io.vertx.ext.web.Router;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  public final static List<String> ASSETS = Arrays.asList("APLE", "AMZN", "NFLX", "TSLA");
  public static void attach(Router router) {

    router.get("/assets").handler(new GetAssetsHandler());

  }

}
