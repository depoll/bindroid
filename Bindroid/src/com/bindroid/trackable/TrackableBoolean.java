package com.bindroid.trackable;

/**
 * Provides a {@link TrackableField}-like implementation for boolean primitives,
 * allowing you to avoid the cost of boxing and unboxing values.
 */
public class TrackableBoolean extends Trackable {
  protected boolean value;

  /**
   * Constructs a new TrackableField, initialized to false.
   */
  public TrackableBoolean() {
    this(false);
  }

  /**
   * Constructs a new TrackableField with the given initial value.
   * 
   * @param initialValue
   *          The initial value of the field.
   */
  public TrackableBoolean(boolean initialValue) {
    this.value = initialValue;
  }

  /**
   * Gets the value of the TrackableField and calls {@link #track()}.
   * 
   * @return The value of the TrackableField.
   */
  public boolean getValue() {
    this.track();
    return this.value;
  }

  /**
   * Sets the value of the TrackableField and calls {@link #updateTrackers()}.
   * 
   * @param value
   *          The new value.
   */
  public void setValue(boolean value) {
    if (this.value != value) {
      this.value = value;
      this.updateTrackers();
    }
  }

  @Override
  public String toString() {
    return "" + this.getValue();
  }
}
