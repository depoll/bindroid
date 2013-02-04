package com.bindroid.converters;

import com.bindroid.ValueConverter;

public class ToStringConverter extends ValueConverter {
  private String stringFormat;

  public ToStringConverter() {
    this.setStringFormat(null);
  }

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

  public String getStringFormat() {
    return this.stringFormat;
  }

  public void setStringFormat(String value) {
    this.stringFormat = value;
  }
}
