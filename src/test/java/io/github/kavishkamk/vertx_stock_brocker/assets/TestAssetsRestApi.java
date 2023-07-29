package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {

    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    webClient.get("/assets")
      .send().onComplete(testContext.succeeding(response -> {
        JsonArray result = response.bodyAsJsonArray();
        System.out.println("Result: " + result);
        assertEquals("[{\"symbol\":\"AAPL\"},{\"symbol\":\"AMZN\"},{\"symbol\":\"NFLX\"},{\"symbol\":\"TSLA\"}]", result.encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }
}
