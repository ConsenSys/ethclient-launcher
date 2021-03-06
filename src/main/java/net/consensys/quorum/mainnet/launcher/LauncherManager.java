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
package net.consensys.quorum.mainnet.launcher;

import static de.codeshelf.consoleui.elements.ConfirmChoice.ConfirmationValue.YES;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import de.codeshelf.consoleui.elements.ConfirmChoice.ConfirmationValue;
import de.codeshelf.consoleui.prompt.CheckboxResult;
import de.codeshelf.consoleui.prompt.ConfirmResult;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.CheckboxItemBuilder;
import de.codeshelf.consoleui.prompt.builder.CheckboxPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.InputValueBuilder;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import net.consensys.quorum.mainnet.launcher.config.ImmutableLauncherConfig;
import net.consensys.quorum.mainnet.launcher.exception.LauncherException;
import net.consensys.quorum.mainnet.launcher.model.LauncherScript;
import net.consensys.quorum.mainnet.launcher.model.Step;
import net.consensys.quorum.mainnet.launcher.util.IdGenerator;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;

public class LauncherManager {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  final ImmutableLauncherConfig launcherConfig;

  private String configFilePath;

  private final Map<String, String> additionalFlag;

  public LauncherManager(final ImmutableLauncherConfig launcherConfig) {
    this.launcherConfig = launcherConfig;
    this.additionalFlag = new HashMap<>();
  }

  public File run() throws LauncherException {
    AnsiConsole.systemInstall();

    try {
      final String resource =
          new String(launcherConfig.launcherScript().readAllBytes(), StandardCharsets.UTF_8);
      final Map<String, PromtResultItemIF> configuration = new HashMap<>();
      final LauncherScript script = MAPPER.readValue(resource, LauncherScript.class);

      if (Strings.isNullOrEmpty(script.getConfigFileName())) {
        throw new LauncherException("config file name is missing");
      }
      configFilePath = script.getConfigFileName();

      // config file already exist
      final File configFile = new File(configFilePath);
      if (!launcherConfig.isLauncherForced() && configFile.exists()) {
        return configFile;
      }

      for (Step stepFound : script.getSteps()) {
        configuration.putAll(createInput(stepFound));
      }
      return createConfigFile(configuration);
    } catch (Exception e) {
      throw new LauncherException(e.getMessage());
    }
  }

  private Map<String, PromtResultItemIF> createInput(final Step step) throws LauncherException {
    try {
      final ConsolePrompt prompt =
          Optional.ofNullable(launcherConfig.customConsolePrompt()).orElse(new ConsolePrompt());
      switch (step.getPromptType()) {
        case LIST:
          additionalFlag.putAll(step.getAdditionalFlag());
          return processList(prompt, step);
        case CHECKBOX:
          return processCheckbox(prompt, step);
        case INPUT:
          return processInput(prompt, step);
        case CONFIRM:
          return processConfirm(prompt, step);
        default:
          throw new LauncherException("invalid input type");
      }
    } catch (Exception e) {
      throw new LauncherException(e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, PromtResultItemIF> processList(final ConsolePrompt prompt, final Step step)
      throws LauncherException {
    final PromptBuilder promptBuilder = prompt.getPromptBuilder();
    final ListPromptBuilder list = promptBuilder.createListPrompt();
    list.name(step.getConfigKey()).message(step.getQuestion());
    try {
      formatOptions(step)
          .forEach(value -> list.newItem().text(value.toString().toLowerCase()).add());
      list.addPrompt();
      final Map<String, PromtResultItemIF> prompt1 =
          (Map<String, PromtResultItemIF>) prompt.prompt(promptBuilder.build());
      return prompt1;
    } catch (Exception e) {
      throw new LauncherException("invalid default option for " + step.getConfigKey());
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, PromtResultItemIF> processCheckbox(
      final ConsolePrompt prompt, final Step step) throws LauncherException, IOException {
    final PromptBuilder promptBuilder = prompt.getPromptBuilder();
    final CheckboxPromptBuilder checkbox = promptBuilder.createCheckboxPrompt();
    checkbox.name(step.getConfigKey()).message(step.getQuestion());
    formatOptions(step)
        .forEach(
            value -> {
              final CheckboxItemBuilder checkboxItemBuilder = checkbox.newItem();
              checkboxItemBuilder.text(value.toString().toLowerCase());
              if (step.getDefaultOption().contains(value.toString())) {
                checkboxItemBuilder.check();
              }
              checkboxItemBuilder.add();
            });
    checkbox.addPrompt();
    return (Map<String, PromtResultItemIF>) prompt.prompt(promptBuilder.build());
  }

  @SuppressWarnings("unchecked")
  private Map<String, PromtResultItemIF> processInput(final ConsolePrompt prompt, final Step step)
      throws IOException {
    Map<String, PromtResultItemIF> response;
    boolean isValidResponse;
    do {
      isValidResponse = true;
      final PromptBuilder promptBuilder = prompt.getPromptBuilder();
      final InputValueBuilder inputPrompt = promptBuilder.createInputPrompt();
      setDefaultValue(step.getConfigKey(), inputPrompt);
      inputPrompt.name(step.getConfigKey()).message(step.getQuestion()).addPrompt();
      response = (Map<String, PromtResultItemIF>) prompt.prompt(promptBuilder.build());
      final String input = ((InputResult) response.get(step.getConfigKey())).getInput();
      if (Strings.isNullOrEmpty(input)) {
        isValidResponse = false;
      } else if (!Strings.isNullOrEmpty(step.getRegex())) {
        final Pattern pattern = Pattern.compile(step.getRegex());
        isValidResponse = pattern.matcher(input).find();
      }
    } while (!isValidResponse);
    return response;
  }

  private Map<String, PromtResultItemIF> processConfirm(final ConsolePrompt prompt, final Step step)
      throws IOException, LauncherException {
    final PromptBuilder promptBuilder = prompt.getPromptBuilder();
    final Map<String, PromtResultItemIF> configuration = new HashMap<>();
    final String name = Optional.ofNullable(step.getConfigKey()).orElse(IdGenerator.generateID());
    promptBuilder
        .createConfirmPromp()
        .name(name)
        .message(step.getQuestion())
        .defaultValue(ConfirmationValue.valueOf(step.getDefaultOption().toUpperCase()))
        .addPrompt();
    final HashMap<String, ? extends PromtResultItemIF> result =
        prompt.prompt(promptBuilder.build());
    final ConfirmationValue confirmed = ((ConfirmResult) result.get(name)).getConfirmed();
    if (step.getConfigKey() != null && !step.getConfigKey().isEmpty()) {
      configuration.putAll(result);
    }
    if (confirmed.equals(YES)) {
      for (Step subStep : step.getSubQuestions()) {
        configuration.putAll(createInput(subStep));
      }
    }
    return configuration;
  }

  @SuppressWarnings("unchecked")
  private List<Object> formatOptions(final Step step) throws LauncherException {
    try {
      final List<String> split = Splitter.on('$').splitToList(step.getAvailableOptions());
      if (split.size() > 1) {
        return (List<Object>) Class.forName(split.get(0)).getField(split.get(1)).get(null);
      } else {
        return Arrays.asList(Class.forName(step.getAvailableOptions()).getEnumConstants());
      }
    } catch (Exception e) {
      throw new LauncherException("invalid default option for " + step.getConfigKey());
    }
  }

  private void setDefaultValue(final String key, final InputValueBuilder inputPrompt) {
    try {
      for (Object o : launcherConfig.commandClasses()) {
        for (Field f : o.getClass().getDeclaredFields()) {
          if (f.isAnnotationPresent(CommandLine.Option.class)) {
            final CommandLine.Option annotation = f.getAnnotation(CommandLine.Option.class);
            if (Arrays.toString(annotation.names()).contains(key)) {
              inputPrompt.defaultValue(readField(f, o, true).toString());
              break;
            }
          }
        }
      }
    } catch (Exception e) {
      // ignore
    }
  }

  private File createConfigFile(final Map<String, PromtResultItemIF> configuration)
      throws LauncherException {
    final StringBuilder config = new StringBuilder();

    for (Map.Entry<String, ? extends PromtResultItemIF> entry : configuration.entrySet()) {
      String key = entry.getKey();
      PromtResultItemIF value = entry.getValue();
      if (value instanceof ConfirmResult) {
        config.append(
            String.format(
                "%s=%s%n", key, ((ConfirmResult) value).getConfirmed() == YES ? "true" : "false"));
      } else if (value instanceof InputResult) {
        String input = ((InputResult) value).getInput();
        config.append(String.format("%s=\"%s\"%n", key, input));
      } else if (value instanceof CheckboxResult) {
        config.append(
            String.format(
                "%s=%s%n",
                key,
                ((CheckboxResult) value)
                    .getSelectedIds().stream()
                        .map(String::toUpperCase)
                        .map(elt -> String.format("\"%s\"", elt))
                        .collect(Collectors.toList())));
      } else if (value instanceof ListResult) {
        final String selectedItem = ((ListResult) value).getSelectedId();
        if (additionalFlag.containsKey(selectedItem)) {
          config.append(String.format("%s%n", additionalFlag.get(selectedItem)));
        }
        config.append(String.format("%s=\"%s\"%n", key, selectedItem.toUpperCase()));
      }
    }
    final File file = new File(configFilePath);
    try (final PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
      out.print(config.toString());
    } catch (Exception e) {
      throw new LauncherException(String.format("error creating config file :%s", e.getMessage()));
    }
    return file;
  }
}
