package com.bindroid.trackable;

/**
 * Provides a {@link TrackableField}-like implementation for double primitives, allowing you to
 * avoid the cost of boxing and unboxing values.
 */
public class TrackableDouble extends Trackable {
  protected double value;

  /**
   * Constructs a new TrackableField, initialized to 0.
   */
  public TrackableDouble() {
    this(0);
  }

  /**
   * Constructs a new TrackableField with the given initial value.
   * 
   * @param initialValue
   *          The initial value of the field.
   */
  public TrackableDouble(double initialValue) {
    this.value = initialValue;
  }

  /**
   * Gets the value of the TrackableField and calls {@link #track()}.
   * 
   * @return The value of the TrackableField.
   */
  public double get() {
    this.track();
    return this.value;
  }

  /**
   * Sets the value of the TrackableField and calls {@link #updateTrackers()}.
   * 
   * @param value
   *          The new value.
   */
  public void set(double value) {
    if (this.value != value) {
      this.value = value;
      this.updateTrackers();
    }
  }

  @Override
  public String toString() {
    return "" + this.get();
  }
}
