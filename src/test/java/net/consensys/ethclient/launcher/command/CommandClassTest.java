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
package net.consensys.ethclient.launcher.command;

import net.consensys.ethclient.launcher.network.NetworkName;
import picocli.CommandLine;

public class CommandClassTest {

  @CommandLine.Option(
      names = {"--data-path"},
      description = "The path to data directory (default: ${DEFAULT-VALUE})")
  private final String dataPath = "default";

  @CommandLine.Option(
      names = {"--network"},
      description =
          "Synchronize against the indicated network, possible values are ${COMPLETION-CANDIDATES}."
              + " (default: MAINNET)")
  private final NetworkName network = null;

  private final String otherField = "";
}
