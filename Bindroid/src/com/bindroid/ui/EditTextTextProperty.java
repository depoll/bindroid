package com.bindroid.ui;

import java.lang.ref.WeakReference;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.bindroid.trackable.Trackable;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.ObjectUtilities;
import com.bindroid.utils.Property;

/**
 * Represents the "Text" property of an {@link EditText}, allowing for two-way bindings.
 */
public class EditTextTextProperty extends Property<String> {
  private Trackable notifier = new Trackable();
  private String lastValue = null;

  /**
   * Constructs an EditTextTextProperty for an {@link EditText}.
   * 
   * @param target
   *          the text box being bound.
   */
  public EditTextTextProperty(EditText target) {
    final WeakReference<EditText> weakTarget = new WeakReference<EditText>(target);
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
        EditText target = weakTarget.get();
        if (target != null) {
          EditTextTextProperty.this.notifier.track();
          return EditTextTextProperty.this.lastValue = target.getText().toString();
        } else {
          return EditTextTextProperty.this.lastValue;
        }
      }
    };
    this.setter = new Action<String>() {
      @Override
      public void invoke(String parameter) {
        EditText target = weakTarget.get();
        if (target != null) {
          if (!ObjectUtilities.equals(parameter, target.getText().toString())) {
            target.setText(parameter);
            EditTextTextProperty.this.lastValue = parameter;
          }
        }
      }
    };
  }
}
