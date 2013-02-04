package com.bindroid.test;

import com.bindroid.TrackableField;

public class Nestable {
  private TrackableField<Nestable> child = new TrackableField<Nestable>();
  private TrackableField<String> value = new TrackableField<String>();

  public Nestable getChild() {
    return this.child.getValue();
  }

  public String getValue() {
    return this.value.getValue();
  }

  public void setChild(Nestable value) {
    this.child.setValue(value);
  }

  public void setValue(String newVal) {
    this.value.setValue(newVal);
  }
}
