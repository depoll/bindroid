package com.bindroid.converters;

import android.view.View;

import com.bindroid.BindingMode;
import com.bindroid.ValueConverter;

import java.util.List;

/**
 * A {@link ValueConverter} for converting to boolean values or visibilities in
 * {@link BindingMode#ONE_WAY} bindings.
 */
public class BoolConverter extends ValueConverter {
    private static final ValueConverter normal = new BoolConverter();
    private static final ValueConverter inverted = new BoolConverter(true);
    private static final ValueConverter invertedZeroFalse = new BoolConverter(true, true);
    private static final ValueConverter normalZeroFalse = new BoolConverter(false, true);

    /**
     * @return A BoolConverter that treats {@code null}, {@code 0}, and {@code false} as false, while
     * everything else is considered true.
     */
    public static ValueConverter get() {
        return BoolConverter.get(false, false);
    }

    /**
     * Gets a standard BoolConverter, optionally inverting the values it returns.
     *
     * @param invert Whether to invert the standard truthiness of the values.
     * @return A BoolConverter properly configured.
     */
    public static ValueConverter get(boolean invert) {
        return BoolConverter.get(invert, false);
    }

    /**
     * Gets a standard BoolConverter, optionally inverting the values it returns, and optionally
     * considering a zero-length list to be a falsey value.
     *
     * @param invert                Whether to invert the standard truthiness of the values.
     * @param zeroLengthListIsFalse Whether a zero-length list should be considered falsey.
     * @return A BoolConverter properly configured.
     */
    public static ValueConverter get(boolean invert, boolean zeroLengthListIsFalse) {
        if (zeroLengthListIsFalse) {
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

    private boolean zeroLengthListIsFalse;

    public BoolConverter() {
        this(false);
    }

    public BoolConverter(boolean invert) {
        this(invert, false);
    }

    public BoolConverter(boolean invert, boolean zeroLengthListIsFalse) {
        this.setInvert(invert);
        this.setZeroLengthListIsFalse(zeroLengthListIsFalse);
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
            if (this.getZeroLengthListIsFalse() && value instanceof List) {
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

    private boolean getInvert() {
        return this.invert;
    }

    private boolean getZeroLengthListIsFalse() {
        return this.zeroLengthListIsFalse;
    }

    private void setInvert(boolean value) {
        this.invert = value;
    }

    private void setZeroLengthListIsFalse(boolean value) {
        this.zeroLengthListIsFalse = value;
    }

}
