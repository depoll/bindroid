package com.bindroid.utils;

import java.lang.ref.WeakReference;

public class WeakenedProperty<T> extends Property<T> {
  public static <T> WeakenedProperty<T> weaken(Property<T> property) {
    return new WeakenedProperty<T>(property);
  }

  private WeakReference<Property<T>> baseProperty;

  private WeakenedProperty(Property<T> prop) {
    this.baseProperty = new WeakReference<Property<T>>(prop);
  }

  @Override
  public Function<T> getGetter() {
    Property<T> prop = this.baseProperty.get();
    if (prop != null) {
      return prop.getGetter();
    }
    return null;
  }

  @Override
  public Action<T> getSetter() {
    Property<T> prop = this.baseProperty.get();
    if (prop != null) {
      return prop.getSetter();
    }
    return null;
  }

  @Override
  public Class<?> getType() {
    Property<T> prop = this.baseProperty.get();
    if (prop != null) {
      return prop.getType();
    }
    return Object.class;
  }
}
