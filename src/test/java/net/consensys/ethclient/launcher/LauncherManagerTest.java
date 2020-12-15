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
package net.consensys.ethclient.launcher;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;

import de.codeshelf.consoleui.elements.ConfirmChoice;
import de.codeshelf.consoleui.prompt.CheckboxResult;
import de.codeshelf.consoleui.prompt.ConfirmResult;
import de.codeshelf.consoleui.prompt.InputResult;
import de.codeshelf.consoleui.prompt.ListResult;
import net.consensys.ethclient.launcher.command.CommandClassTest;
import net.consensys.ethclient.launcher.config.ImmutableLauncherConfig;
import net.consensys.ethclient.launcher.exception.LauncherException;
import net.consensys.ethclient.launcher.network.Apis;
import net.consensys.ethclient.launcher.network.NetworkName;
import net.consensys.ethclient.launcher.util.IdGenerator;
import net.consensys.ethclient.launcher.util.TestPrompt;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@SuppressWarnings({"rawtypes", "unchecked", "ResultOfMethodCallIgnored"})
public class LauncherManagerTest {

  private static final String CONFIG_FILE_NAME = "config.toml";

  private static final String ID = "default";

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @BeforeClass
  public static void setup() {
    IdGenerator.setIDGenerator(ID);
  }

  @Test
  public void shouldCreateConfigFileInTheCorrectFolder() throws LauncherException, IOException {

    final ArrayDeque mockedResult = new ArrayDeque();
    mockedResult.offer(Pair.of("network", new ListResult(NetworkName.MAINNET.name())));
    mockedResult.offer(
        Pair.of("rpc-http-enabled", new ConfirmResult(ConfirmChoice.ConfirmationValue.NO)));
    mockedResult.offer(Pair.of("data-path", new InputResult(folder.getRoot().getAbsolutePath())));

    final ImmutableLauncherConfig immutableLauncherConfig =
        ImmutableLauncherConfig.builder()
            .launcherScript(prepareScript("launcher.json"))
            .addCommandClasses(new CommandClassTest())
            .customConsolePrompt(new TestPrompt(mockedResult))
            .build();
    final LauncherManager launcherManager = new LauncherManager(immutableLauncherConfig);

    final File config = launcherManager.run();

    assertThat(config.getAbsolutePath())
        .isEqualTo(folder.getRoot().getAbsolutePath() + File.separator + CONFIG_FILE_NAME);
  }

  @Test
  public void shouldUseConfigFileIfAlreadyCreated()
      throws LauncherException, IOException, URISyntaxException {

    final ArrayDeque mockedResult = new ArrayDeque();
    mockedResult.offer(
        Pair.of("rpc-http-enabled", new ConfirmResult(ConfirmChoice.ConfirmationValue.NO)));

    final String configFilePath =
        Paths.get(LauncherManagerTest.class.getResource("config-test.toml").toURI())
            .toAbsolutePath()
            .toString();

    final ImmutableLauncherConfig immutableLauncherConfig =
        ImmutableLauncherConfig.builder()
            .launcherScript(prepareScript("launcher.json", Optional.of(configFilePath)))
            .addCommandClasses(new CommandClassTest())
            .customConsolePrompt(new TestPrompt(mockedResult))
            .build();
    final LauncherManager launcherManager = new LauncherManager(immutableLauncherConfig);

    final File config = launcherManager.run();
    final FileInputStream fis = new FileInputStream(config);
    byte[] data = new byte[(int) config.length()];
    fis.read(data);
    fis.close();
    assertThat(new String(data, StandardCharsets.UTF_8)).doesNotContain("rpc-http-enabled");
  }

  @Test
  public void shouldCreateValidContentForConfigFile() throws LauncherException, IOException {

    final ArrayDeque mockedResult = new ArrayDeque();
    mockedResult.offer(Pair.of("network", new ListResult(NetworkName.MAINNET.name())));
    mockedResult.offer(
        Pair.of("rpc-http-enabled", new ConfirmResult(ConfirmChoice.ConfirmationValue.YES)));
    mockedResult.offer(Pair.of(ID, new ConfirmResult(ConfirmChoice.ConfirmationValue.YES)));
    mockedResult.offer(Pair.of("rpc-http-host", new InputResult("127.0.0.1")));
    mockedResult.offer(Pair.of("rpc-http-port", new InputResult("8080")));
    mockedResult.offer(
        Pair.of(
            "rpc-http-apis",
            new CheckboxResult(new HashSet<>(asList(Apis.DEBUG.getValue(), Apis.ETH.getValue())))));
    mockedResult.offer(Pair.of("data-path", new InputResult(folder.getRoot().getAbsolutePath())));

    final ImmutableLauncherConfig immutableLauncherConfig =
        ImmutableLauncherConfig.builder()
            .launcherScript(prepareScript("launcher.json"))
            .addCommandClasses(new CommandClassTest())
            .customConsolePrompt(new TestPrompt(mockedResult))
            .build();
    final LauncherManager launcherManager = new LauncherManager(immutableLauncherConfig);
    final File config = launcherManager.run();

    final FileInputStream fis = new FileInputStream(config);
    byte[] data = new byte[(int) config.length()];
    fis.read(data);
    fis.close();

    final String expectedConfigFile =
        "rpc-http-host=\"127.0.0.1\"\n"
            + "rpc-http-enabled=true\n"
            + "rpc-http-apis=[\"ETH\", \"DEBUG\"]\n"
            + "data-path=\""
            + folder.getRoot().getAbsolutePath()
            + "\"\n"
            + "network=\"MAINNET\"\n"
            + "rpc-http-port=\"8080\"\n";

    assertThat(new String(data, StandardCharsets.UTF_8)).isEqualTo(expectedConfigFile);
  }

  @Test
  public void shouldDetectInvalidScript() {
    final ImmutableLauncherConfig immutableLauncherConfig =
        ImmutableLauncherConfig.builder()
            .customConsolePrompt(new TestPrompt(new ArrayDeque<>()))
            .launcherScript(
                new InputStream() {
                  @Override
                  public int read() {
                    return 0;
                  }

                  @Override
                  public byte[] readAllBytes() {
                    return new byte[] {0x00};
                  }
                })
            .build();
    final LauncherManager launcherManager = new LauncherManager(immutableLauncherConfig);
    assertThatThrownBy(launcherManager::run).isInstanceOf(LauncherException.class);
  }

  @Test
  public void shouldDetectInvalidScriptOptions() throws IOException {
    final ImmutableLauncherConfig immutableLauncherConfig =
        ImmutableLauncherConfig.builder()
            .customConsolePrompt(new TestPrompt(new ArrayDeque<>()))
            .launcherScript(prepareScript("launcher-invalid.json"))
            .build();
    final LauncherManager launcherManager = new LauncherManager(immutableLauncherConfig);
    assertThatThrownBy(launcherManager::run)
        .isInstanceOf(LauncherException.class)
        .hasMessageContaining("invalid default option for rpc-http-apis");
  }

  @Test
  public void shouldDetectMissingConfigFileLocation() throws IOException {
    final ArrayDeque mockedResult = new ArrayDeque();
    mockedResult.offer(Pair.of("data-path", new InputResult(folder.getRoot().getAbsolutePath())));

    final ImmutableLauncherConfig immutableLauncherConfig =
        ImmutableLauncherConfig.builder()
            .customConsolePrompt(new TestPrompt(mockedResult))
            .launcherScript(prepareScript("launcher-missing-config-file.json"))
            .build();
    final LauncherManager launcherManager = new LauncherManager(immutableLauncherConfig);
    assertThatThrownBy(launcherManager::run)
        .isInstanceOf(LauncherException.class)
        .hasMessageContaining("config file name is missing");
  }

  @Test
  public void shouldDetectInvalidConfigFileLocation() throws IOException {
    final ArrayDeque mockedResult = new ArrayDeque();
    mockedResult.offer(Pair.of("data-path", new InputResult(folder.getRoot().getAbsolutePath())));

    final ImmutableLauncherConfig immutableLauncherConfig =
        ImmutableLauncherConfig.builder()
            .customConsolePrompt(new TestPrompt(mockedResult))
            .launcherScript(
                prepareScript(
                    "launcher-simple.json", Optional.of("bad" + File.separator + CONFIG_FILE_NAME)))
            .build();
    final LauncherManager launcherManager = new LauncherManager(immutableLauncherConfig);
    assertThatThrownBy(launcherManager::run)
        .isInstanceOf(LauncherException.class)
        .hasMessageContaining("bad/config.toml (No such file or directory)");
  }

  private InputStream prepareScript(final String scriptName) throws IOException {
    return prepareScript(scriptName, Optional.empty());
  }

  private InputStream prepareScript(final String scriptName, final Optional<String> path)
      throws IOException {
    final InputStream resourceAsStream = LauncherManagerTest.class.getResourceAsStream(scriptName);
    final String configFilePath =
        path.orElse(folder.getRoot().getAbsolutePath() + File.separator + CONFIG_FILE_NAME);
    final String script =
        new String(resourceAsStream.readAllBytes()).replace(CONFIG_FILE_NAME, configFilePath);
    return new ByteArrayInputStream(script.getBytes());
  }
}
