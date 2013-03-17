package com.bindroid.trackable;

import com.bindroid.utils.EqualityComparer;

/**
 * A {@link TrackableField} that uses an {@link EqualityComparer} to determine whether a value has
 * changed when calling {@link #set(Object)}. If the value has not changed, no tracking notification
 * is raised.
 * 
 * @param <T>
 *          The type of the field.
 */
public class ComparingTrackableField<T> extends TrackableField<T> {
  private EqualityComparer<? super T> comparer;

  public ComparingTrackableField(T initialValue, EqualityComparer<? super T> comparer) {
    super(initialValue);
    this.comparer = comparer;
  }

  @Override
  public void set(T value) {
    if (!this.comparer.equals(this.value, value)) {
      this.value = value;
      this.updateTrackers();
    }
  }
}
