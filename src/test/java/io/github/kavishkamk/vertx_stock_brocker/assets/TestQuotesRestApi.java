package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.github.kavishkamk.vertx_stock_brocker.MainVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void return_quotes_for_assets(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    webClient.get("/quotes/APLE")
      .send().onComplete(testContext.succeeding(response -> {
        JsonObject result = response.bodyAsJsonObject();
        System.out.println("Result: " + result);
        assertEquals("{\"symbol\":\"APLE\"}", result.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void return_not_found_for_unknown_assets(Vertx vertx, VertxTestContext testContext) {

    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    webClient.get("/quotes/UNKNOWN")
      .send().onComplete(testContext.succeeding(response -> {
        JsonObject result = response.bodyAsJsonObject();
        System.out.println("Result: " + result);
        assertEquals(HttpResponseStatus.NOT_FOUND.code(), response.statusCode());
        assertEquals("{\"message\":\"not found Quate for UNKNOWN\",\"path\":\"/quotes/UNKNOWN\"}", result.encode());
        testContext.completeNow();
      }));

  }
}
