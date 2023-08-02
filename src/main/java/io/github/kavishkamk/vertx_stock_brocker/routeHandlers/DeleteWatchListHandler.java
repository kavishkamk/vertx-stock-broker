package io.github.kavishkamk.vertx_stock_brocker.routeHandlers;

import io.github.kavishkamk.vertx_stock_brocker.domain.WatchList;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {

  private final HashMap<UUID, WatchList> watchListPerAccount;

  public DeleteWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String accountId = routingContext.pathParam("accountId");
    System.out.println("request reserved to: " + routingContext.normalizedPath());
    final WatchList watchList = watchListPerAccount.remove(UUID.fromString(accountId));
    routingContext.response()
      .end(watchList.toJsonObject().toBuffer());
  }
}
