package io.github.kavishkamk.vertx_stock_brocker.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;

public class ConfigLoader {


  public static final String SERVER_PORT = "SERVER_PORT";
  public static final String DB_HOST = "DB_HOST";
  public static final String DB_PORT = "DB_PORT";
  public static final String DB_DATABASE = "DB_DATABASE";
  public static final String DB_USER = "DB_USER";
  public static final String DB_PASSWORD = "DB_PASSWORD";
  public static final String APPLICATION_YAML = "application.yaml";
  static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Arrays.asList(
    SERVER_PORT, DB_PORT, DB_DATABASE, DB_HOST, DB_USER, DB_PASSWORD);

  public static Future<BrokerConfig> load(Vertx vertx) {

    final JsonArray exposedKeys = new JsonArray();
    EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
    System.out.println("Exposed keys :" + exposedKeys.encode());

    ConfigStoreOptions envStore = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));

    ConfigStoreOptions propertyStore = new ConfigStoreOptions()
      .setType("sys")
      .setConfig(new JsonObject().put("cast", false));

    ConfigStoreOptions yamlStore = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setConfig(new JsonObject().put("path", APPLICATION_YAML));


    var retrever = ConfigRetriever.create(vertx,
      new ConfigRetrieverOptions()
        .addStore(yamlStore)
        .addStore(propertyStore)
        .addStore(envStore));

    return retrever.getConfig().map(BrokerConfig::from);
  }

}
