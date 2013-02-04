package com.bindroid.ui;

import android.app.Activity;
import android.view.View;

import com.bindroid.Binding;
import com.bindroid.BindingMode;
import com.bindroid.ValueConverter;
import com.bindroid.utils.Property;
import com.bindroid.utils.ReflectedProperty;
import com.bindroid.utils.WeakReflectedProperty;

public final class UiBinder {

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty) {
    return UiBinder.bind(activity, targetId, targetProperty, sourceProperty, BindingMode.OneWay,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty, BindingMode mode) {
    return UiBinder.bind(activity, targetId, targetProperty, activity, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty, BindingMode mode, ValueConverter converter) {
    return bind(activity, targetId, targetProperty, activity, sourceProperty, mode, converter);
  }

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty, ValueConverter converter) {
    return UiBinder.bind(activity, targetId, targetProperty, activity, sourceProperty,
        BindingMode.OneWay, converter);
  }

  public static Binding bind(Activity activity, Property<?> targetProperty, String sourceProperty,
      BindingMode mode) {
    return bind(targetProperty, activity, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty) {
    return UiBinder.bind(view, targetId, targetProperty, view, sourceProperty, BindingMode.OneWay,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty,
      BindingMode mode) {
    return UiBinder.bind(view, targetId, targetProperty, view, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty,
      BindingMode mode, ValueConverter converter) {
    return bind(view, targetId, targetProperty, view, sourceProperty, mode, converter);
  }

  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty,
      ValueConverter converter) {
    return UiBinder.bind(view, targetId, targetProperty, view, sourceProperty, BindingMode.OneWay,
        converter);
  }

  public static Binding bind(View view, Property<?> targetProperty, String sourceProperty,
      BindingMode mode) {
    return bind(targetProperty, view, sourceProperty, mode, ValueConverter.getDefaultConverter());
  }

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty) {
    return UiBinder.bind(activity, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.OneWay, ValueConverter.getDefaultConverter());
  }

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty, BindingMode mode) {
    return UiBinder.bind(activity, targetId, targetProperty, sourceObject, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty, BindingMode mode, ValueConverter converter) {
    Binding b = new Binding(UiProperty.make(new WeakReflectedProperty(activity
        .findViewById(targetId), targetProperty)), new ReflectedProperty(sourceObject,
        sourceProperty), mode, converter);
    return b;
  }

  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty, ValueConverter converter) {
    return UiBinder.bind(activity, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.OneWay, converter);
  }

  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty) {
    return UiBinder.bind(view, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.OneWay, ValueConverter.getDefaultConverter());
  }

  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode) {
    return UiBinder.bind(view, targetId, targetProperty, sourceObject, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode, ValueConverter converter) {
    return bind(new WeakReflectedProperty(view.findViewById(targetId), targetProperty),
        new ReflectedProperty(sourceObject, sourceProperty), mode, converter);
  }

  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty, ValueConverter converter) {
    return UiBinder.bind(view, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.OneWay, converter);
  }

  public static Binding bind(Property<?> targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode) {
    return bind(targetProperty, new ReflectedProperty(sourceObject, sourceProperty), mode,
        ValueConverter.getDefaultConverter());
  }

  public static Binding bind(Property<?> targetProperty, Property<?> sourceProperty,
      BindingMode mode) {
    return bind(targetProperty, sourceProperty, mode, ValueConverter.getDefaultConverter());
  }

  public static Binding bind(Property<?> targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode, ValueConverter converter) {
    return bind(targetProperty, new ReflectedProperty(sourceObject, sourceProperty), mode,
        converter);
  }

  public static Binding bind(Property<?> targetProperty, Property<?> sourceProperty,
      BindingMode mode, ValueConverter converter) {
    return new Binding(UiProperty.make(targetProperty), sourceProperty, mode, converter);
  }

  private UiBinder() {
  }
}
