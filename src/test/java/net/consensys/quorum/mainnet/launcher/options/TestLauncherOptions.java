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
package net.consensys.quorum.mainnet.launcher.options;

import picocli.CommandLine;

public class TestLauncherOptions implements Options {

  private static final String LAUNCHER_OPTION_NAME = "--launcher";
  private static final String LAUNCHER_OPTION_NAME_FORCE = "--launcher-force";

  @SuppressWarnings({"FieldCanBeFinal", "FieldMayBeFinal"})
  @CommandLine.Option(
      hidden = true,
      names = {LAUNCHER_OPTION_NAME},
      description = "Activate the launcher if no configuration file is present",
      arity = "0..1")
  private Boolean isLauncherMode = Boolean.FALSE;

  @SuppressWarnings({"FieldCanBeFinal", "FieldMayBeFinal"})
  @CommandLine.Option(
      hidden = true,
      names = {LAUNCHER_OPTION_NAME_FORCE},
      description = "Force to activate the launcher even if a configuration file is present",
      arity = "0..1")
  private Boolean isLauncherModeForced = Boolean.FALSE;

  public static TestLauncherOptions create() {
    return new TestLauncherOptions();
  }

  public boolean isLauncherMode() {
    return isLauncherMode;
  }

  public boolean isLauncherModeForced() {
    return isLauncherModeForced;
  }
}
