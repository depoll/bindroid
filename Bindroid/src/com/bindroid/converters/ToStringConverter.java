package com.bindroid.converters;

import com.bindroid.BindingMode;
import com.bindroid.ValueConverter;

/**
 * A {@link ValueConverter} for converting to String values in {@link BindingMode#ONE_WAY} bindings.
 */
public class ToStringConverter extends ValueConverter {
  private String stringFormat;

  /**
   * Constructs a ToStringConverter that simply gets the default string value of an object.
   */
  public ToStringConverter() {
    this.setStringFormat(null);
  }

  /**
   * Constructs a ToStringConverter that calls {@link String#format(String, Object...)} with the
   * source value as a parameter in order to generate a string for the object.
   * 
   * @param format
   *          The format to use when calling {@link String#format(String, Object...)}.
   */
  public ToStringConverter(String format) {
    this.setStringFormat(format);
  }

  @Override
  public Object convertToTarget(Object sourceValue, Class<?> targetType) {
    try {
      if (this.getStringFormat() == null) {
        return "" + sourceValue;
      }
      return String.format(this.getStringFormat(), sourceValue);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getStringFormat() {
    return this.stringFormat;
  }

  private void setStringFormat(String value) {
    this.stringFormat = value;
  }
}
