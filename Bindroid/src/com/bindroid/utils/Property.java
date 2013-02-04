package com.bindroid.utils;

public class Property<T> {
  protected Action<T> setter;

  protected Function<T> getter;

  protected Class<?> propertyType;

  protected Property() {
  }

  public Property(Function<T> getter, Action<T> setter) {
    this(getter, setter, Object.class);
  }

  public Property(Function<T> getter, Action<T> setter, Class<?> type) {
    this.getter = getter;
    this.setter = setter;
    this.propertyType = type;
  }

  public Function<T> getGetter() {
    return this.getter;
  }

  public Action<T> getSetter() {
    return this.setter;
  }

  public Class<?> getType() {
    return this.propertyType;
  }

  public final T getValue() {
    return this.getter.evaluate();
  }

  public final void setValue(T value) {
    this.setter.invoke(value);
  }
}