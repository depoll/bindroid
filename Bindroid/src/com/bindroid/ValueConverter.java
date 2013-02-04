package com.bindroid;

public class ValueConverter {
  private static final ValueConverter defaultConverter;

  static {
    defaultConverter = new ValueConverter();
  }

  public static ValueConverter getDefaultConverter() {
    return ValueConverter.defaultConverter;
  }

  public Object convertToSource(Object targetValue, Class<?> sourceType) {
    return targetValue;
  }

  public Object convertToTarget(Object sourceValue, Class<?> targetType) {
    return sourceValue;
  }
}
