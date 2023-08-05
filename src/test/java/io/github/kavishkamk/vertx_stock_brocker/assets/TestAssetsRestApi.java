package io.github.kavishkamk.vertx_stock_brocker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi extends AbstractRestApiTest{

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {

    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
    webClient.get("/assets")
      .send().onComplete(testContext.succeeding(response -> {
        JsonArray result = response.bodyAsJsonArray();
        System.out.println("Result: " + result);
        assertEquals("[{\"symbol\":\"APLE\"},{\"symbol\":\"AMZN\"},{\"symbol\":\"NFLX\"},{\"symbol\":\"TSLA\"}]", result.encode());
        assertEquals(200, response.statusCode());
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        assertEquals("my-value", response.getHeader("my-header"));
        testContext.completeNow();
      }));
  }
}
