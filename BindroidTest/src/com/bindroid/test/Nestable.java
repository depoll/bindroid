package com.bindroid.test;

import com.bindroid.trackable.TrackableField;

public class Nestable {
  private TrackableField<Nestable> child = new TrackableField<Nestable>();
  private TrackableField<String> value = new TrackableField<String>();

  public Nestable getChild() {
    return this.child.get();
  }

  public String getValue() {
    return this.value.get();
  }

  public void setChild(Nestable value) {
    this.child.set(value);
  }

  public void setValue(String newVal) {
    this.value.set(newVal);
  }
}
