package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.domain.WatchList;
import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.DeleteWatchListHandler;
import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.GetWatchListHandler;
import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.PutWatchListHandler;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.UUID;

public class WatchListRestApi {

  public static void attach(Router router) {
    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
    final String path = "/account/watchlist/:accountId";

    router.get(path).handler(new GetWatchListHandler(watchListPerAccount));

    router.put(path).handler(new PutWatchListHandler(watchListPerAccount));

    router.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));

  }

}
