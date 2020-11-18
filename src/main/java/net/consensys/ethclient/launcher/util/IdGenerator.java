package net.consensys.ethclient.launcher.util;

import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class IdGenerator {

  private static Optional<String> fixedValue = Optional.empty();

  public static String generateID() {
    return fixedValue.orElse(Long.toString(System.nanoTime()));
  }

  @VisibleForTesting
  public static void setIDGenerator(final String fixedValue) {
    IdGenerator.fixedValue = Optional.of(fixedValue);
  }
}
