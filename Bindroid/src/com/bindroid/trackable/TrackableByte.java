package com.bindroid.trackable;

/**
 * Provides a {@link TrackableField}-like implementation for byte primitives,
 * allowing you to avoid the cost of boxing and unboxing values.
 */
public class TrackableByte extends Trackable {
  protected byte value;

  /**
   * Constructs a new TrackableField, initialized to 0.
   */
  public TrackableByte() {
    this((byte) 0);
  }

  /**
   * Constructs a new TrackableField with the given initial value.
   * 
   * @param initialValue
   *          The initial value of the field.
   */
  public TrackableByte(byte initialValue) {
    this.value = initialValue;
  }

  /**
   * Gets the value of the TrackableField and calls {@link #track()}.
   * 
   * @return The value of the TrackableField.
   */
  public byte get() {
    this.track();
    return this.value;
  }

  /**
   * Sets the value of the TrackableField and calls {@link #updateTrackers()}.
   * 
   * @param value
   *          The new value.
   */
  public void set(byte value) {
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
