/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.consensys.quorum.mainnet.launcher.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeFinal"})
public class Step {

  @JsonProperty("prompt-type")
  private InputType promptType;

  @JsonProperty("question")
  private String question;

  @JsonProperty("config-key")
  private String configKey;

  @JsonProperty("regex")
  private String regex;

  @JsonProperty("available-options")
  private String availableOptions;

  @JsonProperty("default-option")
  private String defaultOption = "";

  @JsonProperty("sub-questions")
  private List<Step> subQuestions = new ArrayList<>();

  @JsonProperty("additional-flag")
  private Map<String, String> additionalFlag = new HashMap<>();

  @JsonProperty("is-config-file-location")
  private boolean isConfigFileLocation;

  public InputType getPromptType() {
    return promptType;
  }

  public String getQuestion() {
    return question;
  }

  public String getConfigKey() {
    return configKey;
  }

  public String getRegex() {
    return regex;
  }

  public String getAvailableOptions() {
    return availableOptions;
  }

  public String getDefaultOption() {
    return defaultOption;
  }

  public List<Step> getSubQuestions() {
    return subQuestions;
  }

  public Map<String, String> getAdditionalFlag() {
    return additionalFlag;
  }

  public boolean isConfigFileLocation() {
    return isConfigFileLocation;
  }
}
