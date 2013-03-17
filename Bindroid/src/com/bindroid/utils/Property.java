package com.bindroid.utils;

/**
 * Represents a property (a getter and setter pair).
 * 
 * @param <T>
 *          the type of the property.
 */
public class Property<T> {
  protected Action<T> setter;

  protected Function<T> getter;

  protected Class<?> propertyType;

  protected Property() {
  }

  /**
   * Creates a property from a getter and setter. The type will be assumed to be {@link Object}.
   * 
   * @param getter
   *          the getter for the property.
   * @param setter
   *          the setter for the property.
   */
  public Property(Function<T> getter, Action<T> setter) {
    this(getter, setter, Object.class);
  }

  /**
   * Creates a property from a getter and setter.
   * 
   * @param getter
   *          the getter for the property.
   * @param setter
   *          the setter for the property.
   * @param type
   *          the type of the property.
   */
  public Property(Function<T> getter, Action<T> setter, Class<?> type) {
    this.getter = getter;
    this.setter = setter;
    this.propertyType = type;
  }

  /**
   * @return the getter for the property.
   */
  public Function<T> getGetter() {
    return this.getter;
  }

  /**
   * @return the setter for the property.
   */
  public Action<T> getSetter() {
    return this.setter;
  }

  /**
   * @return the type of the property.
   */
  public Class<?> getType() {
    return this.propertyType;
  }

  /**
   * Invokes the getter.
   * 
   * @return the value returned by the getter.
   */
  public final T getValue() {
    return this.getter.evaluate();
  }

  /**
   * Invokes the setter with the given value.
   * 
   * @param value
   *          the value to pass to the setter.
   */
  public final void setValue(T value) {
    this.setter.invoke(value);
  }
}