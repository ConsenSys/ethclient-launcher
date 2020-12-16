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
package net.consensys.quorum.mainnet.launcher.util;

import static org.assertj.core.api.Assertions.assertThat;

import net.consensys.quorum.mainnet.launcher.options.TestLauncherOptions;
import org.junit.Test;

public class ParseArgsHelperTest {

  @Test
  public void shouldKeepDefaultValuesWhenNoArgs() {
    final TestLauncherOptions options = new TestLauncherOptions();
    ParseArgsHelper.getLauncherOptions(options);
    assertThat(options.isLauncherMode()).isFalse();
    assertThat(options.isLauncherModeForced()).isFalse();
  }

  @Test
  public void shouldNotThrowWithUnknownArgs() {
    final TestLauncherOptions options = new TestLauncherOptions();
    ParseArgsHelper.getLauncherOptions(options, "Unknown");
    assertThat(options.isLauncherMode()).isFalse();
    assertThat(options.isLauncherModeForced()).isFalse();
  }

  @Test
  public void shouldUpdateOptionCorrectly() {
    final TestLauncherOptions options = new TestLauncherOptions();
    ParseArgsHelper.getLauncherOptions(options, "--launcher", "--launcher-force");
    assertThat(options.isLauncherMode()).isTrue();
    assertThat(options.isLauncherModeForced()).isTrue();
  }
}
