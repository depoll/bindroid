package com.bindroid.utils;

import java.lang.ref.WeakReference;

public class WeakenedProperty<T> extends Property<T> {
  @Override
  public Function<T> getGetter() {
    Property<T> prop = baseProperty.get();
    if (prop != null)
      return prop.getGetter();
    return null;
  }

  @Override
  public Action<T> getSetter() {
    Property<T> prop = baseProperty.get();
    if (prop != null)
      return prop.getSetter();
    return null;
  }

  @Override
  public Class<?> getType() {
    Property<T> prop = baseProperty.get();
    if (prop != null)
      return prop.getType();
    return Object.class;
  }

  private WeakReference<Property<T>> baseProperty;

  private WeakenedProperty(Property<T> prop) {
    this.baseProperty = new WeakReference<Property<T>>(prop);
  }
  
  public static <T> WeakenedProperty<T> weaken(Property<T> property) {
    return new WeakenedProperty<T>(property);
  }
}
