package net.consensys.ethclient.launcher.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LauncherScript {

  @JsonProperty("config-file-name")
  private String configFileName;

  @JsonProperty("steps")
  private Step[] steps;

  public String getConfigFileName() {
    return configFileName;
  }

  public Step[] getSteps() {
    return steps;
  }
}
