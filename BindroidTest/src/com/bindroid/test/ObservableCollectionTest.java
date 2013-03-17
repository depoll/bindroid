package com.bindroid.test;

import java.util.Arrays;

import junit.framework.TestCase;

import com.bindroid.Binding;
import com.bindroid.BindingMode;
import com.bindroid.ObservableCollection;
import com.bindroid.trackable.TrackableField;
import com.bindroid.utils.Function;
import com.bindroid.utils.Property;
import com.bindroid.utils.ReflectedProperty;

public class ObservableCollectionTest extends TestCase {
  private TrackableField<Object> value = new TrackableField<Object>();

  public Object getValue() {
    return value.getValue();
  }

  public void setValue(Object value) {
    this.value.setValue(value);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setValue(null);
  }

  public void testSizeObservation() {
    final ObservableCollection<Integer> coll = new ObservableCollection<Integer>();
    new Binding(new ReflectedProperty(this, "Value"), new Property<Integer>(
        new Function<Integer>() {
          @Override
          public Integer evaluate() {
            return coll.size();
          }
        }, null), BindingMode.ONE_WAY);
    assertEquals(0, getValue());
    coll.add(1);
    assertEquals(1, getValue());
    coll.addAll(Arrays.asList(2, 3, 4, 5));
    assertEquals(5, getValue());
    coll.remove(0);
    assertEquals(4, getValue());
    coll.clear();
    assertEquals(0, getValue());
  }

  public void testContainsObservation() {
    final ObservableCollection<Integer> coll = new ObservableCollection<Integer>();
    new Binding(new ReflectedProperty(this, "Value"), new Property<Boolean>(
        new Function<Boolean>() {
          @Override
          public Boolean evaluate() {
            return coll.contains(1);
          }
        }, null), BindingMode.ONE_WAY);
    assertEquals(false, getValue());
    coll.add(1);
    assertEquals(true, getValue());
    coll.addAll(Arrays.asList(2, 3, 4, 5));
    assertEquals(true, getValue());
    coll.remove(0);
    assertEquals(false, getValue());
    coll.add(1);
    assertEquals(true, getValue());
    coll.clear();
    assertEquals(false, getValue());
  }

  public void testGetObservation() {
    final ObservableCollection<Integer> coll = new ObservableCollection<Integer>();
    new Binding(new ReflectedProperty(this, "Value"), new Property<Integer>(
        new Function<Integer>() {
          @Override
          public Integer evaluate() {
            return coll.get(0);
          }
        }, null), BindingMode.ONE_WAY);
    assertEquals(null, getValue());
    coll.add(1);
    assertEquals(1, getValue());
    coll.addAll(Arrays.asList(2, 3, 4, 5));
    assertEquals(1, getValue());
    coll.remove(0);
    assertEquals(2, getValue());
    coll.add(0, 6);
    assertEquals(6, getValue());
    coll.set(0, 7);
    assertEquals(7, getValue());
  }
}
