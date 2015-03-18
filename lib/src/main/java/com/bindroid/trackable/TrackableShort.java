package com.bindroid.trackable;

/**
 * Provides a {@link TrackableField}-like implementation for short primitives, allowing you to avoid
 * the cost of boxing and unboxing values.
 */
public class TrackableShort extends Trackable {
  protected short value;

  /**
   * Constructs a new TrackableField, initialized to 0.
   */
  public TrackableShort() {
    this((short) 0);
  }

  /**
   * Constructs a new TrackableField with the given initial value.
   * 
   * @param initialValue
   *          The initial value of the field.
   */
  public TrackableShort(short initialValue) {
    this.value = initialValue;
  }

  /**
   * Gets the value of the TrackableField and calls {@link #track()}.
   * 
   * @return The value of the TrackableField.
   */
  public short get() {
    this.track();
    return this.value;
  }

  /**
   * Sets the value of the TrackableField and calls {@link #updateTrackers()}.
   * 
   * @param value
   *          The new value.
   */
  public void set(short value) {
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
