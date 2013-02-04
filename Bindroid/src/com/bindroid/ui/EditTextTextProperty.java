package com.bindroid.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.bindroid.Trackable;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.ObjectUtilities;
import com.bindroid.utils.Property;

public class EditTextTextProperty extends Property<String> {
  private EditText target;
  private Trackable notifier = new Trackable();

  public EditTextTextProperty(EditText target) {
    this.target = target;
    this.propertyType = String.class;
    target.addTextChangedListener(new TextWatcher() {

      @Override
      public void afterTextChanged(Editable s) {
        EditTextTextProperty.this.notifier.updateTrackers();
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });
    this.getter = new Function<String>() {
      @Override
      public String evaluate() {
        EditTextTextProperty.this.notifier.track();
        return EditTextTextProperty.this.target.getText().toString();
      }
    };
    this.setter = new Action<String>() {
      @Override
      public void invoke(String parameter) {
        if (!ObjectUtilities.equals(parameter, EditTextTextProperty.this.target.getText()
            .toString())) {
          EditTextTextProperty.this.target.setText(parameter);
        }
      }

    };
  }
}
