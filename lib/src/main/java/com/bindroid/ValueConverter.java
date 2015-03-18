package com.bindroid;

/**
 * Allows conversion between the source and target values in a {@link Binding}.
 */
public class ValueConverter {
  private static final ValueConverter defaultConverter;

  static {
    defaultConverter = new ValueConverter();
  }

  /**
   * @return A ValueConverter that performs no conversion.
   */
  public static ValueConverter getDefaultConverter() {
    return ValueConverter.defaultConverter;
  }

  /**
   * Converts a target value into a source value. This is called whenever the target property's
   * value is being applied to the source property in a {@link BindingMode#ONE_WAY_TO_SOURCE} or
   * {@link BindingMode#TWO_WAY} {@link Binding}.
   * 
   * @param targetValue
   *          The value of the target property.
   * @param sourceType
   *          The type of the source property.
   * @return The value to apply to the target.
   */
  public Object convertToSource(Object targetValue, Class<?> sourceType) {
    return targetValue;
  }

  /**
   * Converts a source value into a target value. This is called whenever the source property's
   * value is being applied to the target property in a {@link BindingMode#ONE_WAY} or
   * {@link BindingMode#TWO_WAY} {@link Binding}.
   * 
   * @param sourceValue
   *          The value of the source property.
   * @param targetType
   *          The type of the target property.
   * @return The value to apply to the target.
   */
  public Object convertToTarget(Object sourceValue, Class<?> targetType) {
    return sourceValue;
  }
}
