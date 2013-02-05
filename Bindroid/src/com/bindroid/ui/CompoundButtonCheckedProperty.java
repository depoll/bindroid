package com.bindroid.ui;

import java.lang.ref.WeakReference;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.bindroid.Trackable;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.Property;

public class CompoundButtonCheckedProperty extends Property<Boolean> {
  private Trackable trackable = new Trackable();
  private boolean lastValue = false;

  public CompoundButtonCheckedProperty(CompoundButton button) {
    final WeakReference<CompoundButton> weakButton = new WeakReference<CompoundButton>(button);
    button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CompoundButtonCheckedProperty.this.trackable.updateTrackers();
      }
    });
    this.getter = new Function<Boolean>() {
      @Override
      public Boolean evaluate() {
        CompoundButton button = weakButton.get();
        if (button != null) {
          CompoundButtonCheckedProperty.this.trackable.track();
          return lastValue = button.isChecked();
        } else {
          return lastValue;
        }
      }
    };
    this.setter = new Action<Boolean>() {
      @Override
      public void invoke(Boolean parameter) {
        CompoundButton button = weakButton.get();
        if (button != null) {
          button.setChecked(parameter);
          lastValue = parameter;
        }
      }
    };
    this.propertyType = Boolean.TYPE;
  }
}
