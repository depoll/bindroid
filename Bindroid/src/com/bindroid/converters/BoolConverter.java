package com.bindroid.converters;

import java.util.List;

import android.view.View;

import com.bindroid.ValueConverter;

public class BoolConverter extends ValueConverter {
  private static ValueConverter normal = new BoolConverter();
  private static ValueConverter inverted = new BoolConverter(true);
  private static ValueConverter invertedZeroFalse = new BoolConverter(true, true);
  private static ValueConverter normalZeroFalse = new BoolConverter(false, true);

  public static ValueConverter get() {
    return BoolConverter.get(false, false);
  }

  public static ValueConverter get(boolean invert) {
    return BoolConverter.get(invert, false);
  }

  public static ValueConverter get(boolean invert, boolean zeroLengthArrayIsFalse) {
    if (zeroLengthArrayIsFalse) {
      if (invert) {
        return BoolConverter.invertedZeroFalse;
      } else {
        return BoolConverter.normalZeroFalse;
      }
    } else if (invert) {
      return BoolConverter.inverted;
    } else {
      return BoolConverter.normal;
    }
  }

  private boolean invert;

  private boolean zeroLengthArrayIsFalse;

  public BoolConverter() {
    this(false);
  }

  public BoolConverter(boolean invert) {
    this(invert, false);
  }

  public BoolConverter(boolean invert, boolean zeroLengthArrayIsFalse) {
    this.setInvert(invert);
    this.setZeroLengthArrayIsFalse(zeroLengthArrayIsFalse);
  }

  @Override
  public Object convertToSource(Object targetValue, Class<?> sourceType) {
    return super.convertToSource(targetValue, sourceType);
  }

  @Override
  public Object convertToTarget(Object sourceValue, Class<?> targetType) {
    try {
      Object value = sourceValue;
      if (value == null) {
        value = Boolean.valueOf(false);
      }
      if (this.getZeroLengthArrayIsFalse() && value instanceof List) {
        if (((List<?>) value).size() == 0) {
          value = Boolean.valueOf(false);
        } else {
          value = Boolean.valueOf(true);
        }
      }
      if ((value instanceof Integer || value.getClass().equals(Integer.TYPE))
          && Integer.valueOf(0).equals(value)) {
        value = Boolean.valueOf(false);
      }
      if (!(value instanceof Boolean) && !value.getClass().equals(Boolean.TYPE)) {
        value = Boolean.valueOf(true);
      }
      boolean realValue = ((Boolean) value).booleanValue();
      if (this.getInvert()) {
        realValue = !realValue;
      }
      if (targetType.equals(Integer.class) || targetType.equals(Integer.TYPE)) {
        return Integer.valueOf(realValue ? View.VISIBLE : View.GONE);
      }
      if (targetType.equals(Boolean.class) || targetType.equals(Boolean.TYPE)
          || targetType.equals(Object.class)) {
        return Boolean.valueOf(realValue);
      }
      return super.convertToTarget(sourceValue, targetType);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean getInvert() {
    return this.invert;
  }

  public boolean getZeroLengthArrayIsFalse() {
    return this.zeroLengthArrayIsFalse;
  }

  private void setInvert(boolean value) {
    this.invert = value;
  }

  private void setZeroLengthArrayIsFalse(boolean value) {
    this.zeroLengthArrayIsFalse = value;
  }

}
