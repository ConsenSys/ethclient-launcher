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
package net.consensys.ethclient.launcher.util;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;

import de.codeshelf.consoleui.elements.PromptableElementIF;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings({"rawtypes", "ConstantConditions", "unchecked"})
public class TestPrompt extends ConsolePrompt {

  private final ArrayDeque<Pair> mockedResult;

  public TestPrompt(final ArrayDeque<Pair> mockedResult) {
    super();
    this.mockedResult = mockedResult;
  }

  @Override
  public HashMap<String, ? extends PromtResultItemIF> prompt(
      final List<PromptableElementIF> promptableElementList) {
    final Pair result = mockedResult.poll();
    final HashMap map = new HashMap();
    map.put(result.getKey(), result.getValue());
    return map;
  }
}
