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

import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class IdGenerator {

  private static Optional<String> fixedValue = Optional.empty();

  private IdGenerator() {}

  public static String generateID() {
    return fixedValue.orElse(Long.toString(System.nanoTime()));
  }

  @VisibleForTesting
  public static void setIDGenerator(final String fixedValue) {
    IdGenerator.fixedValue = Optional.of(fixedValue);
  }
}
