package guru.qa.niffler.config;

import org.jetbrains.annotations.NotNull;

public interface Config {

  static Config getInstance() {
     return "docker".equals(System.getProperty("test.env"))
            ? DockerConfig.INSTANCE
            : LocalConfig.INSTANCE;
  }
  @NotNull
  String frontUrl();

  @NotNull
  String spendUrl();

  @NotNull
  String spendJdbcUrl();

  @NotNull
  String authUrl();

  @NotNull
  String authJdbcUrl();

  @NotNull
  default String ghUrl() {
    return "https://api.github.com/";
  }

  @NotNull
  String gatewayUrl();

  @NotNull
  String userdataUrl();

  @NotNull
  String userdataJdbcUrl();

  @NotNull
  String currencyJdbcUrl();
}
