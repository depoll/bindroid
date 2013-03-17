package com.bindroid.test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import android.os.Handler;
import android.os.Looper;
import android.test.InstrumentationTestCase;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bindroid.BindingMode;
import com.bindroid.converters.ToStringConverter;
import com.bindroid.trackable.TrackableField;
import com.bindroid.ui.CompoundButtonCheckedProperty;
import com.bindroid.ui.EditTextTextProperty;
import com.bindroid.ui.UiBinder;

public class UiBindingTests extends InstrumentationTestCase {
  private TrackableField<Boolean> boolValue = new TrackableField<Boolean>(false);
  private TrackableField<String> stringValue = new TrackableField<String>();

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setStringValue("Hello, World!");
    setBoolValue(false);
  }

  public boolean getBoolValue() {
    return boolValue.getValue();
  }

  public void setBoolValue(boolean value) {
    boolValue.setValue(value);
  }

  public String getStringValue() {
    return stringValue.getValue();
  }

  public void setStringValue(String value) {
    stringValue.setValue(value);
  }

  public void testTextViewBinding() throws Exception {
    final AtomicReference<Runnable> gcTest = new AtomicReference<Runnable>();
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        TextView tv = new TextView(getInstrumentation().getTargetContext());
        tv.setId(1);
        UiBinder.bind(tv, 1, "Text", UiBindingTests.this, "StringValue", BindingMode.ONE_WAY);
        assertEquals("Hello, World!", tv.getText());
        setStringValue("Bonjour, Monde!");
        assertEquals("Bonjour, Monde!", tv.getText());

        gcTest.set(GCTestUtils.watchPointers(Arrays.asList(tv)));
      }
    });
    getInstrumentation().waitForIdleSync();
    gcTest.get().run();
  }

  public void testTextViewBooleanBinding() throws Exception {
    final AtomicReference<Runnable> gcTest = new AtomicReference<Runnable>();
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        TextView tv = new TextView(getInstrumentation().getTargetContext());
        tv.setId(1);
        UiBinder.bind(tv, 1, "Text", UiBindingTests.this, "BoolValue", BindingMode.ONE_WAY,
            new ToStringConverter());
        assertEquals("false", tv.getText());
        setBoolValue(true);
        assertEquals("true", tv.getText());
        setBoolValue(false);
        assertEquals("false", tv.getText());

        gcTest.set(GCTestUtils.watchPointers(Arrays.asList(tv)));
      }
    });
    getInstrumentation().waitForIdleSync();
    gcTest.get().run();
  }

  public void testEditTextTextBinding() throws Exception {
    final AtomicReference<Runnable> gcTest = new AtomicReference<Runnable>();
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        EditText et = new EditText(getInstrumentation().getTargetContext());
        et.setId(1);
        UiBinder.bind(new EditTextTextProperty(et), UiBindingTests.this, "StringValue",
            BindingMode.TWO_WAY);
        assertEquals("Hello, World!", et.getText().toString());
        setStringValue("Bonjour, Monde!");
        assertEquals("Bonjour, Monde!", et.getText().toString());
        et.setText("Sweet!");
        assertEquals("Sweet!", getStringValue());

        gcTest.set(GCTestUtils.watchPointers(Arrays.asList(et)));
      }
    });
    getInstrumentation().waitForIdleSync();
    gcTest.get().run();
  }

  public void testCheckBoxBinding() throws Exception {
    final AtomicReference<Runnable> gcTest = new AtomicReference<Runnable>();
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        CheckBox cb = new CheckBox(getInstrumentation().getTargetContext());
        cb.setId(1);
        UiBinder.bind(new CompoundButtonCheckedProperty(cb), UiBindingTests.this, "BoolValue",
            BindingMode.TWO_WAY);
        assertFalse(cb.isChecked());
        setBoolValue(true);
        assertTrue(cb.isChecked());
        cb.setChecked(false);
        assertFalse(getBoolValue());

        gcTest.set(GCTestUtils.watchPointers(Arrays.asList(cb)));
      }
    });
    getInstrumentation().waitForIdleSync();
    gcTest.get().run();
  }
}
