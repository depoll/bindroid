package com.bindroid;

import com.bindroid.utils.EqualityComparer;

public class ComparingTrackableField<T> extends TrackableField<T> {
  private EqualityComparer<? super T> comparer;
  
  public ComparingTrackableField(T initialValue, EqualityComparer<? super T> comparer) {
    super(initialValue);
    this.comparer = comparer;
  }

  @Override
  public void setValue(T value) {
    if (!this.comparer.equals(this.value, value)) {
      this.value = value;
      this.updateTrackers();
    }
  }
}
