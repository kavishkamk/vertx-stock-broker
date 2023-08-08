package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.domain.WatchList;
import io.github.kavishkamk.vertx_stock_brocker.routeHandlers.*;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;

import java.util.HashMap;
import java.util.UUID;

public class WatchListRestApi {

  public static void attach(Router router, PgPool pgPool) {
    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
    final String path = "/account/watchlist/:accountId";

    router.get(path).handler(new GetWatchListHandler(watchListPerAccount));

    router.put(path).handler(new PutWatchListHandler(watchListPerAccount));

    router.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));

    final String pgPath = "/db/account/watchlist/:accountId";

    router.get(pgPath).handler(new GetWatchListFromDbHandler(pgPool));

    router.put(pgPath).handler(new PutWatchListToDbHandler(pgPool));

    router.delete(pgPath).handler(new DeleteWatchListFromDbHandler(pgPool));

  }

}
