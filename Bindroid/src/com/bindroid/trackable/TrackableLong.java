package com.bindroid.trackable;

/**
 * Provides a {@link TrackableField}-like implementation for long primitives,
 * allowing you to avoid the cost of boxing and unboxing values.
 */
public class TrackableLong extends Trackable {
  protected long value;

  /**
   * Constructs a new TrackableField, initialized to 0.
   */
  public TrackableLong() {
    this(0L);
  }

  /**
   * Constructs a new TrackableField with the given initial value.
   * 
   * @param initialValue
   *          The initial value of the field.
   */
  public TrackableLong(long initialValue) {
    this.value = initialValue;
  }

  /**
   * Gets the value of the TrackableField and calls {@link #track()}.
   * 
   * @return The value of the TrackableField.
   */
  public long getValue() {
    this.track();
    return this.value;
  }

  /**
   * Sets the value of the TrackableField and calls {@link #updateTrackers()}.
   * 
   * @param value
   *          The new value.
   */
  public void setValue(long value) {
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
