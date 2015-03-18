package com.bindroid.test;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import com.bindroid.trackable.ComparingTrackableField;
import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.TrackableBoolean;
import com.bindroid.trackable.TrackableByte;
import com.bindroid.trackable.TrackableChar;
import com.bindroid.trackable.TrackableDouble;
import com.bindroid.trackable.TrackableField;
import com.bindroid.trackable.TrackableFloat;
import com.bindroid.trackable.TrackableInt;
import com.bindroid.trackable.TrackableLong;
import com.bindroid.trackable.TrackableShort;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Action;
import com.bindroid.utils.EqualityComparer;

public class TrackableTest extends TestCase {
  private <T> void beginTracking(final TrackableField<T> field, final AtomicReference<T> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableField() {
    TrackableField<Integer> field = new TrackableField<Integer>(Integer.MIN_VALUE);
    AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.get().intValue());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get());
    field.set(150);
    assertEquals(150, field.get().intValue());
    assertEquals(150, toSet.get().intValue());
    field.set(300);
    assertEquals(300, field.get().intValue());
    assertEquals(300, toSet.get().intValue());
  }

  public void testSetSameValue() {
    TrackableField<Integer> field = new TrackableField<Integer>(Integer.MIN_VALUE);
    AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.get().intValue());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get());
    field.set(150);
    assertEquals(150, field.get().intValue());
    assertEquals(150, toSet.get().intValue());
    field.set(300);
    assertEquals(300, field.get().intValue());
    assertEquals(300, toSet.get().intValue());
    toSet.set(Integer.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get().intValue());
    assertEquals(Integer.MIN_VALUE, toSet.get().intValue());
    field.set(600);
    assertEquals(600, field.get().intValue());
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
    assertEquals(150, field.get().intValue());
    assertEquals(150, toSet.get().intValue());
    field.set(300);
    assertEquals(150, field.get().intValue());
    assertEquals(150, toSet.get().intValue());
    field.set(151);
    assertEquals(151, field.get().intValue());
    assertEquals(151, toSet.get().intValue());
    field.set(601);
    assertEquals(151, field.get().intValue());
    assertEquals(151, toSet.get().intValue());
  }

  public void testSimpleTrackableField() {
    final TrackableField<Integer> field = new TrackableField<Integer>(Integer.MIN_VALUE);
    final AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.get().intValue());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get());
    field.set(150);
    assertEquals(150, field.get().intValue());
    assertEquals(150, toSet.get().intValue());
    field.set(300);
    assertEquals(300, field.get().intValue());
    assertEquals(150, toSet.get().intValue());
  }

  private void beginTracking(final TrackableInt field, final AtomicReference<Integer> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableInt() {
    TrackableInt field = new TrackableInt(Integer.MIN_VALUE);
    AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().intValue());
    field.set(150);
    assertEquals(150, field.get());
    assertEquals(150, toSet.get().intValue());
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(300, toSet.get().intValue());
  }

  public void testSetSameValueInt() {
    TrackableInt field = new TrackableInt(Integer.MIN_VALUE);
    AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().intValue());
    field.set(150);
    assertEquals(150, field.get());
    assertEquals(150, toSet.get().intValue());
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(300, toSet.get().intValue());
    toSet.set(Integer.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(Integer.MIN_VALUE, toSet.get().intValue());
    field.set(600);
    assertEquals(600, field.get());
    assertEquals(600, toSet.get().intValue());
  }

  public void testSimpleTrackableInt() {
    final TrackableInt field = new TrackableInt(Integer.MIN_VALUE);
    final AtomicReference<Integer> toSet = new AtomicReference<Integer>();
    assertEquals(Integer.MIN_VALUE, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().intValue());
    field.set(150);
    assertEquals(150, field.get());
    assertEquals(150, toSet.get().intValue());
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(150, toSet.get().intValue());
  }

  private void beginTracking(final TrackableBoolean field, final AtomicReference<Boolean> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableBoolean() {
    TrackableBoolean field = new TrackableBoolean(false);
    AtomicReference<Boolean> toSet = new AtomicReference<Boolean>();
    assertEquals(false, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().booleanValue());
    field.set(true);
    assertEquals(true, field.get());
    assertEquals(true, toSet.get().booleanValue());
    field.set(false);
    assertEquals(false, field.get());
    assertEquals(false, toSet.get().booleanValue());
  }

  public void testSetSameValueBoolean() {
    TrackableBoolean field = new TrackableBoolean(false);
    AtomicReference<Boolean> toSet = new AtomicReference<Boolean>();
    assertEquals(false, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().booleanValue());
    field.set(true);
    assertEquals(true, field.get());
    assertEquals(true, toSet.get().booleanValue());
    toSet.set(false);
    field.set(true);
    assertEquals(true, field.get());
    assertEquals(false, toSet.get().booleanValue());
    field.set(false);
    assertEquals(false, field.get());
    assertEquals(false, toSet.get().booleanValue());
  }

  public void testSimpleTrackableBoolean() {
    final TrackableBoolean field = new TrackableBoolean(false);
    final AtomicReference<Boolean> toSet = new AtomicReference<Boolean>();
    assertEquals(false, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().booleanValue());
    field.set(true);
    assertEquals(true, field.get());
    assertEquals(true, toSet.get().booleanValue());
    field.set(false);
    assertEquals(false, field.get());
    assertEquals(true, toSet.get().booleanValue());
  }

  private void beginTracking(final TrackableByte field, final AtomicReference<Byte> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableByte() {
    TrackableByte field = new TrackableByte(Byte.MIN_VALUE);
    AtomicReference<Byte> toSet = new AtomicReference<Byte>();
    assertEquals(Byte.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().byteValue());
    field.set((byte) 150);
    assertEquals((byte) 150, field.get());
    assertEquals((byte) 150, toSet.get().byteValue());
    field.set((byte) 300);
    assertEquals((byte) 300, field.get());
    assertEquals((byte) 300, toSet.get().byteValue());
  }

  public void testSetSameValueByte() {
    TrackableByte field = new TrackableByte(Byte.MIN_VALUE);
    AtomicReference<Byte> toSet = new AtomicReference<Byte>();
    assertEquals(Byte.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().byteValue());
    field.set((byte) 150);
    assertEquals((byte) 150, field.get());
    assertEquals((byte) 150, toSet.get().byteValue());
    field.set((byte) 300);
    assertEquals((byte) 300, field.get());
    assertEquals((byte) 300, toSet.get().byteValue());
    toSet.set(Byte.MIN_VALUE);
    field.set((byte) 300);
    assertEquals((byte) 300, field.get());
    assertEquals(Byte.MIN_VALUE, toSet.get().byteValue());
    field.set((byte) 600);
    assertEquals((byte) 600, field.get());
    assertEquals((byte) 600, toSet.get().byteValue());
  }

  public void testSimpleTrackableByte() {
    final TrackableByte field = new TrackableByte(Byte.MIN_VALUE);
    final AtomicReference<Byte> toSet = new AtomicReference<Byte>();
    assertEquals(Byte.MIN_VALUE, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().byteValue());
    field.set((byte) 150);
    assertEquals((byte) 150, field.get());
    assertEquals((byte) 150, toSet.get().byteValue());
    field.set((byte) 300);
    assertEquals((byte) 300, field.get());
    assertEquals((byte) 150, toSet.get().byteValue());
  }

  private void beginTracking(final TrackableChar field, final AtomicReference<Character> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableChar() {
    TrackableChar field = new TrackableChar(Character.MIN_VALUE);
    AtomicReference<Character> toSet = new AtomicReference<Character>();
    assertEquals(Character.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().charValue());
    field.set((char) 150);
    assertEquals((char) 150, field.get());
    assertEquals((char) 150, toSet.get().charValue());
    field.set((char) 300);
    assertEquals((char) 300, field.get());
    assertEquals((char) 300, toSet.get().charValue());
  }

  public void testSetSameValueChar() {
    TrackableChar field = new TrackableChar(Character.MIN_VALUE);
    AtomicReference<Character> toSet = new AtomicReference<Character>();
    assertEquals(Character.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().charValue());
    field.set((char) 150);
    assertEquals((char) 150, field.get());
    assertEquals((char) 150, toSet.get().charValue());
    field.set((char) 300);
    assertEquals((char) 300, field.get());
    assertEquals((char) 300, toSet.get().charValue());
    toSet.set(Character.MIN_VALUE);
    field.set((char) 300);
    assertEquals((char) 300, field.get());
    assertEquals(Character.MIN_VALUE, toSet.get().charValue());
    field.set((char) 600);
    assertEquals((char) 600, field.get());
    assertEquals((char) 600, toSet.get().charValue());
  }

  public void testSimpleTrackableChar() {
    final TrackableChar field = new TrackableChar(Character.MIN_VALUE);
    final AtomicReference<Character> toSet = new AtomicReference<Character>();
    assertEquals(Character.MIN_VALUE, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().charValue());
    field.set((char) 150);
    assertEquals((char) 150, field.get());
    assertEquals((char) 150, toSet.get().charValue());
    field.set((char) 300);
    assertEquals((char) 300, field.get());
    assertEquals((char) 150, toSet.get().charValue());
  }

  private void beginTracking(final TrackableDouble field, final AtomicReference<Double> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableDouble() {
    TrackableDouble field = new TrackableDouble(Double.MIN_VALUE);
    AtomicReference<Double> toSet = new AtomicReference<Double>();
    assertEquals(Double.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().doubleValue());
    field.set(150);
    assertEquals(150, field.get(), Double.MIN_VALUE);
    assertEquals(150, toSet.get().doubleValue(), Double.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Double.MIN_VALUE);
    assertEquals(300, toSet.get().doubleValue(), Double.MIN_VALUE);
  }

  public void testSetSameValueDouble() {
    TrackableDouble field = new TrackableDouble(Double.MIN_VALUE);
    AtomicReference<Double> toSet = new AtomicReference<Double>();
    assertEquals(Double.MIN_VALUE, field.get(), Double.MIN_VALUE);
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().doubleValue(), Double.MIN_VALUE);
    field.set(150);
    assertEquals(150, field.get(), Double.MIN_VALUE);
    assertEquals(150, toSet.get().doubleValue(), Double.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Double.MIN_VALUE);
    assertEquals(300, toSet.get().doubleValue(), Double.MIN_VALUE);
    toSet.set(Double.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Double.MIN_VALUE);
    assertEquals(Double.MIN_VALUE, toSet.get().doubleValue(), Double.MIN_VALUE);
    field.set(600);
    assertEquals(600, field.get(), Double.MIN_VALUE);
    assertEquals(600, toSet.get().doubleValue(), Double.MIN_VALUE);
  }

  public void testSimpleTrackableDouble() {
    final TrackableDouble field = new TrackableDouble(Double.MIN_VALUE);
    final AtomicReference<Double> toSet = new AtomicReference<Double>();
    assertEquals(Double.MIN_VALUE, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().doubleValue(), Double.MIN_VALUE);
    field.set(150);
    assertEquals(150, field.get(), Double.MIN_VALUE);
    assertEquals(150, toSet.get().doubleValue(), Double.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Double.MIN_VALUE);
    assertEquals(150, toSet.get().doubleValue(), Double.MIN_VALUE);
  }

  private void beginTracking(final TrackableFloat field, final AtomicReference<Float> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableFloat() {
    TrackableFloat field = new TrackableFloat(Float.MIN_VALUE);
    AtomicReference<Float> toSet = new AtomicReference<Float>();
    assertEquals(Float.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().floatValue());
    field.set(150);
    assertEquals(150, field.get(), Float.MIN_VALUE);
    assertEquals(150, toSet.get().floatValue(), Float.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Float.MIN_VALUE);
    assertEquals(300, toSet.get().floatValue(), Float.MIN_VALUE);
  }

  public void testSetSameValueFloat() {
    TrackableFloat field = new TrackableFloat(Float.MIN_VALUE);
    AtomicReference<Float> toSet = new AtomicReference<Float>();
    assertEquals(Float.MIN_VALUE, field.get(), Float.MIN_VALUE);
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().floatValue(), Float.MIN_VALUE);
    field.set(150);
    assertEquals(150, field.get(), Float.MIN_VALUE);
    assertEquals(150, toSet.get().floatValue(), Float.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Float.MIN_VALUE);
    assertEquals(300, toSet.get().floatValue(), Float.MIN_VALUE);
    toSet.set(Float.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Float.MIN_VALUE);
    assertEquals(Float.MIN_VALUE, toSet.get().floatValue(), Float.MIN_VALUE);
    field.set(600);
    assertEquals(600, field.get(), Float.MIN_VALUE);
    assertEquals(600, toSet.get().floatValue(), Float.MIN_VALUE);
  }

  public void testSimpleTrackableFloat() {
    final TrackableFloat field = new TrackableFloat(Float.MIN_VALUE);
    final AtomicReference<Float> toSet = new AtomicReference<Float>();
    assertEquals(Float.MIN_VALUE, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().floatValue(), Float.MIN_VALUE);
    field.set(150);
    assertEquals(150, field.get(), Float.MIN_VALUE);
    assertEquals(150, toSet.get().floatValue(), Float.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get(), Float.MIN_VALUE);
    assertEquals(150, toSet.get().floatValue(), Float.MIN_VALUE);
  }

  private void beginTracking(final TrackableLong field, final AtomicReference<Long> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableLong() {
    TrackableLong field = new TrackableLong(Long.MIN_VALUE);
    AtomicReference<Long> toSet = new AtomicReference<Long>();
    assertEquals(Long.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().longValue());
    field.set(150);
    assertEquals(150, field.get());
    assertEquals(150, toSet.get().longValue());
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(300, toSet.get().longValue());
  }

  public void testSetSameValueLong() {
    TrackableLong field = new TrackableLong(Long.MIN_VALUE);
    AtomicReference<Long> toSet = new AtomicReference<Long>();
    assertEquals(Long.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().longValue());
    field.set(150);
    assertEquals(150, field.get());
    assertEquals(150, toSet.get().longValue());
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(300, toSet.get().longValue());
    toSet.set(Long.MIN_VALUE);
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(Long.MIN_VALUE, toSet.get().longValue());
    field.set(600);
    assertEquals(600, field.get());
    assertEquals(600, toSet.get().longValue());
  }

  public void testSimpleTrackableLong() {
    final TrackableLong field = new TrackableLong(Long.MIN_VALUE);
    final AtomicReference<Long> toSet = new AtomicReference<Long>();
    assertEquals(Long.MIN_VALUE, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().longValue());
    field.set(150);
    assertEquals(150, field.get());
    assertEquals(150, toSet.get().longValue());
    field.set(300);
    assertEquals(300, field.get());
    assertEquals(150, toSet.get().longValue());
  }

  private void beginTracking(final TrackableShort field, final AtomicReference<Short> toSet) {
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
        Trackable.track(this, new Action<Void>() {
          @Override
          public void invoke(Void o) {
            field.get();
          }
        });
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
  }

  public void testPersistentTrackableShort() {
    TrackableShort field = new TrackableShort(Short.MIN_VALUE);
    AtomicReference<Short> toSet = new AtomicReference<Short>();
    assertEquals(Short.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().shortValue());
    field.set((short) 150);
    assertEquals((short) 150, field.get());
    assertEquals((short) 150, toSet.get().shortValue());
    field.set((short) 300);
    assertEquals((short) 300, field.get());
    assertEquals((short) 300, toSet.get().shortValue());
  }

  public void testSetSameValueShort() {
    TrackableShort field = new TrackableShort(Short.MIN_VALUE);
    AtomicReference<Short> toSet = new AtomicReference<Short>();
    assertEquals(Short.MIN_VALUE, field.get());
    beginTracking(field, toSet);
    assertEquals(field.get(), toSet.get().shortValue());
    field.set((short) 150);
    assertEquals((short) 150, field.get());
    assertEquals((short) 150, toSet.get().shortValue());
    field.set((short) 300);
    assertEquals((short) 300, field.get());
    assertEquals((short) 300, toSet.get().shortValue());
    toSet.set(Short.MIN_VALUE);
    field.set((short) 300);
    assertEquals((short) 300, field.get());
    assertEquals(Short.MIN_VALUE, toSet.get().shortValue());
    field.set((short) 600);
    assertEquals((short) 600, field.get());
    assertEquals((short) 600, toSet.get().shortValue());
  }

  public void testSimpleTrackableShort() {
    final TrackableShort field = new TrackableShort(Short.MIN_VALUE);
    final AtomicReference<Short> toSet = new AtomicReference<Short>();
    assertEquals(Short.MIN_VALUE, field.get());
    Tracker t = new Tracker() {
      @Override
      public void update() {
        toSet.set(field.get());
      }
    };
    Trackable.track(t, new Action<Void>() {
      @Override
      public void invoke(Void o) {
        toSet.set(field.get());
      }
    });
    assertEquals(field.get(), toSet.get().shortValue());
    field.set((short) 150);
    assertEquals((short) 150, field.get());
    assertEquals((short) 150, toSet.get().shortValue());
    field.set((short) 300);
    assertEquals((short) 300, field.get());
    assertEquals((short) 150, toSet.get().shortValue());
  }
}
