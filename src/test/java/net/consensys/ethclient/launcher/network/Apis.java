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
package net.consensys.ethclient.launcher.network;

import java.util.Arrays;
import java.util.List;

public class Apis {

  public static final Api ETH = new Api("ETH");
  public static final Api DEBUG = new Api("DEBUG");

  public static final List<Api> ALL_APIS = Arrays.asList(ETH, DEBUG);

  public static class Api {
    private final String value;

    public Api(final String cliValue) {
      this.value = cliValue;
    }

    public String getValue() {
      return value;
    }
  }
}
