package com.bindroid;

import com.bindroid.utils.EqualityComparer;
import com.bindroid.utils.ObjectUtilities;

public class TrackableField<T> extends Trackable {
  protected T value;
  private static EqualityComparer<Object> comparer;

  static {
    TrackableField.comparer = ObjectUtilities.<Object> getDefaultComparer();
  }

  public TrackableField() {
    this(null);
  }

  public TrackableField(T initialValue) {
    this.value = initialValue;
  }

  public T getValue() {
    this.track();
    return this.value;
  }

  public void setValue(T value) {
    if (!TrackableField.comparer.equals(this.value, value)) {
      this.value = value;
      this.updateTrackers();
    }
  }

  @Override
  public String toString() {
    return "" + this.getValue();
  }
}
