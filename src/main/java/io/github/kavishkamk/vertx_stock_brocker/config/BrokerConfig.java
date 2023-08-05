package io.github.kavishkamk.vertx_stock_brocker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Value
@Builder
@ToString
public class BrokerConfig {

  int serverPort;
  String version;

  public static BrokerConfig from(JsonObject config) {

    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);

    if (Objects.isNull(serverPort)) {
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured");
    }

    final String version = config.getString("version");

    if (Objects.isNull(version)) {
      throw new RuntimeException("version is not found in the config file");
    }

    return BrokerConfig.builder()
      .serverPort(config.getInteger(ConfigLoader.SERVER_PORT))
      .version(config.getString("version"))
      .build();
  }

}
