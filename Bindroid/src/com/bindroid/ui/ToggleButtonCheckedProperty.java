package com.bindroid.ui;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.bindroid.Trackable;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.Property;

public class ToggleButtonCheckedProperty extends Property<Boolean> {
  private Trackable trackable = new Trackable();

  public ToggleButtonCheckedProperty(final ToggleButton button) {
    button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ToggleButtonCheckedProperty.this.trackable.updateTrackers();
      }
    });
    this.getter = new Function<Boolean>() {
      @Override
      public Boolean evaluate() {
        ToggleButtonCheckedProperty.this.trackable.track();
        return button.isChecked();
      }
    };
    this.setter = new Action<Boolean>() {
      @Override
      public void invoke(Boolean parameter) {
        button.setChecked(parameter);
      }
    };
  }

  @Override
  public Class<?> getType() {
    return Boolean.TYPE;
  }
}
