package com.bindroid.test;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import com.bindroid.ComparingTrackableField;
import com.bindroid.Trackable;
import com.bindroid.TrackableField;
import com.bindroid.Tracker;
import com.bindroid.utils.Action;
import com.bindroid.utils.EqualityComparer;

public class TrackableTest extends TestCase {
  private <T> void beginTracking(final TrackableField<T> field, final AtomicReference<T> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.getValue());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.getValue();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.getValue());
      }
    });
  }

  public void testPersistentTrackableField() {
    TrackableField<Integer> field = new TrackableField<Integer>(Integer.MIN_VALUE);
    AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.getValue().intValue());
    beginTracking(field, toSet);
    assertEquals(field.getValue(), toSet.get());
    field.setValue(150);
    assertEquals(150, field.getValue().intValue());
    assertEquals(150, toSet.get().intValue());
    field.setValue(300);
    assertEquals(300, field.getValue().intValue());
    assertEquals(300, toSet.get().intValue());
  }

  public void testSetSameValue() {
    TrackableField<Integer> field = new TrackableField<Integer>(Integer.MIN_VALUE);
    AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.getValue().intValue());
    beginTracking(field, toSet);
    assertEquals(field.getValue(), toSet.get());
    field.setValue(150);
    assertEquals(150, field.getValue().intValue());
    assertEquals(150, toSet.get().intValue());
    field.setValue(300);
    assertEquals(300, field.getValue().intValue());
    assertEquals(300, toSet.get().intValue());
    toSet.set(Integer.MIN_VALUE);
    field.setValue(300);
    assertEquals(300, field.getValue().intValue());
    assertEquals(Integer.MIN_VALUE, toSet.get().intValue());
    field.setValue(600);
    assertEquals(600, field.getValue().intValue());
    assertEquals(600, toSet.get().intValue());
  }

  public void testComparingTrackableField() {
    TrackableField<Integer> field = new ComparingTrackableField<Integer>(150,
        new EqualityComparer<Integer>() {
          @Override
          public boolean equals(Integer obj1, Integer obj2) {
            // Returns true if both values are odd or even.
            return obj1.intValue() % 2 == obj2.intValue() % 2;
          }
        });
    AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    beginTracking(field, toSet);
    assertEquals(150, field.getValue().intValue());
    assertEquals(150, toSet.get().intValue());
    field.setValue(300);
    assertEquals(150, field.getValue().intValue());
    assertEquals(150, toSet.get().intValue());
    field.setValue(151);
    assertEquals(151, field.getValue().intValue());
    assertEquals(151, toSet.get().intValue());
    field.setValue(601);
    assertEquals(151, field.getValue().intValue());
    assertEquals(151, toSet.get().intValue());
  }

  public void testSimpleTrackableField() {
    final TrackableField<Integer> field = new TrackableField<Integer>(Integer.MIN_VALUE);
    final AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.getValue().intValue());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.getValue());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.getValue());
      }
    });
    assertEquals(field.getValue(), toSet.get());
    field.setValue(150);
    assertEquals(150, field.getValue().intValue());
    assertEquals(150, toSet.get().intValue());
    field.setValue(300);
    assertEquals(300, field.getValue().intValue());
    assertEquals(150, toSet.get().intValue());
  }

}
