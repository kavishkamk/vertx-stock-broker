package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.domain.Asset;
import io.github.kavishkamk.vertx_stock_brocker.domain.WatchList;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi extends AbstractRestApiTest {

  @Test
  void add_and_return_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
    UUID accountId = UUID.randomUUID();

    webClient.put("/account/watchlist/" + accountId)
      .sendJsonObject(new WatchList(
        Arrays.asList(
          new Asset("AMZN"),
          new Asset("APPL")
        )).toJsonObject())
      .onComplete(testContext.succeeding(response -> {
        JsonObject result = response.bodyAsJsonObject();
        System.out.println("Result: " + result);
        assertEquals(200, response.statusCode());
        assertEquals("{\"assets\":[{\"symbol\":\"AMZN\"},{\"symbol\":\"APPL\"}]}", result.encode());
      }))
      .compose(next -> {
        webClient.get("/account/watchlist/" + accountId)
          .send()
          .onComplete(testContext.succeeding(response -> {
            JsonObject result = response.bodyAsJsonObject();
            System.out.println("Result: " + result);
            assertEquals(200, response.statusCode());
            assertEquals("{\"assets\":[{\"symbol\":\"AMZN\"},{\"symbol\":\"APPL\"}]}", result.encode());
            testContext.completeNow();
          }));
        return Future.succeededFuture();
      });
  }

  @Test
  void add_and_delete_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
    UUID accountId = UUID.randomUUID();

    webClient.put("/account/watchlist/" + accountId)
      .sendJsonObject(new WatchList(
        Arrays.asList(
          new Asset("AMZN"),
          new Asset("APPL")
        )).toJsonObject())
      .onComplete(testContext.succeeding(response -> {
        JsonObject result = response.bodyAsJsonObject();
        System.out.println("Result: " + result);
        assertEquals(200, response.statusCode());
        assertEquals("{\"assets\":[{\"symbol\":\"AMZN\"},{\"symbol\":\"APPL\"}]}", result.encode());
      }))
      .compose(next -> {
        webClient.delete("/account/watchlist/" + accountId)
          .send()
          .onComplete(testContext.succeeding(response -> {
            JsonObject result = response.bodyAsJsonObject();
            System.out.println("Result: " + result);
            assertEquals(200, response.statusCode());
            assertEquals("{\"assets\":[{\"symbol\":\"AMZN\"},{\"symbol\":\"APPL\"}]}", result.encode());
            testContext.completeNow();
          }));
        return Future.succeededFuture();
      });
  }
}
