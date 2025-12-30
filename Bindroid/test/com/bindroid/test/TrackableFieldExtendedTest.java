package com.bindroid.test;

import com.bindroid.trackable.ComparingTrackableField;
import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.TrackableField;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Action;
import com.bindroid.utils.EqualityComparer;
import com.bindroid.utils.Function;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Extended unit tests for TrackableField and related classes.
 * These tests focus on edge cases and scenarios not covered by the existing tests.
 */
public class TrackableFieldExtendedTest {

    private AtomicInteger updateCount;

    @Before
    public void setUp() {
        updateCount = new AtomicInteger(0);
    }

    // ==================== TrackableField Tests ====================

    @Test
    public void testTrackableFieldDefaultConstructor() {
        TrackableField<String> field = new TrackableField<String>();
        assertNull(field.get());
    }

    @Test
    public void testTrackableFieldWithNullInitialValue() {
        TrackableField<String> field = new TrackableField<String>(null);
        assertNull(field.get());
    }

    @Test
    public void testTrackableFieldSetNullValue() {
        TrackableField<String> field = new TrackableField<String>("initial");
        assertEquals("initial", field.get());
        
        final AtomicReference<String> trackedValue = new AtomicReference<String>();
        beginTracking(field, trackedValue);
        
        field.set(null);
        assertNull(field.get());
        assertNull(trackedValue.get());
    }

    @Test
    public void testTrackableFieldSetNullToNull() {
        TrackableField<String> field = new TrackableField<String>(null);
        
        final AtomicInteger updateCount = new AtomicInteger(0);
        Tracker tracker = createCountingTracker(field, updateCount);
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                // Just tracking
            }
        });
        
        field.set(null);
        // Should not trigger update since value hasn't changed
        assertEquals(0, updateCount.get());
    }

    @Test
    public void testTrackableFieldToString() {
        TrackableField<Integer> field = new TrackableField<Integer>(42);
        assertEquals("42", field.toString());
    }

    @Test
    public void testTrackableFieldToStringWithNull() {
        TrackableField<String> field = new TrackableField<String>(null);
        assertEquals("null", field.toString());
    }

    @Test
    public void testTrackableFieldMultipleTrackers() {
        final TrackableField<Integer> field = new TrackableField<Integer>(0);
        final AtomicReference<Integer> value1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> value2 = new AtomicReference<Integer>();
        
        // Create two trackers that will both be notified
        Tracker tracker1 = new Tracker() {
            @Override
            public void update() {
                value1.set(field.get());
            }
        };
        Tracker tracker2 = new Tracker() {
            @Override
            public void update() {
                value2.set(field.get());
            }
        };
        
        Trackable.track(tracker1, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                value1.set(field.get());
            }
        });
        Trackable.track(tracker2, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                value2.set(field.get());
            }
        });
        
        assertEquals(Integer.valueOf(0), value1.get());
        assertEquals(Integer.valueOf(0), value2.get());
        
        field.set(100);
        assertEquals(Integer.valueOf(100), value1.get());
        assertEquals(Integer.valueOf(100), value2.get());
    }

    @Test
    public void testTrackableFieldNestedTracking() {
        final TrackableField<Integer> field1 = new TrackableField<Integer>(0);
        final TrackableField<Integer> field2 = new TrackableField<Integer>(0);
        final AtomicReference<Integer> sum = new AtomicReference<Integer>(0);
        
        // Create a tracker that depends on both fields
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        sum.set(field1.get() + field2.get());
                    }
                });
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                sum.set(field1.get() + field2.get());
            }
        });
        
        assertEquals(Integer.valueOf(0), sum.get());
        
        field1.set(10);
        assertEquals(Integer.valueOf(10), sum.get());
        
        field2.set(20);
        assertEquals(Integer.valueOf(30), sum.get());
    }

    @Test
    public void testTrackableFieldWithFunction() {
        final TrackableField<Integer> field = new TrackableField<Integer>(5);
        
        Integer result = Trackable.track(new Tracker() {
            @Override
            public void update() {
                // Not used in this test
            }
        }, new Function<Integer>() {
            @Override
            public Integer evaluate() {
                return field.get() * 2;
            }
        });
        
        assertEquals(Integer.valueOf(10), result);
    }

    // ==================== ComparingTrackableField Tests ====================

    @Test
    public void testComparingTrackableFieldNullSafe() {
        EqualityComparer<String> nullSafeComparer = new EqualityComparer<String>() {
            @Override
            public boolean equals(String obj1, String obj2) {
                if (obj1 == null && obj2 == null) return true;
                if (obj1 == null || obj2 == null) return false;
                return obj1.equals(obj2);
            }
        };
        
        TrackableField<String> field = new ComparingTrackableField<String>(null, nullSafeComparer);
        final AtomicInteger updateCount = new AtomicInteger(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                // Track initial state
            }
        });
        
        // Set to same null - should not trigger
        field.set(null);
        assertEquals(0, updateCount.get());
        
        // Set to non-null - should trigger
        field.set("value");
        assertEquals(1, updateCount.get());
        
        // Set to null - should trigger
        field.set(null);
        assertEquals(2, updateCount.get());
    }

    @Test
    public void testComparingTrackableFieldCaseInsensitive() {
        EqualityComparer<String> caseInsensitiveComparer = new EqualityComparer<String>() {
            @Override
            public boolean equals(String obj1, String obj2) {
                if (obj1 == null && obj2 == null) return true;
                if (obj1 == null || obj2 == null) return false;
                return obj1.equalsIgnoreCase(obj2);
            }
        };
        
        TrackableField<String> field = new ComparingTrackableField<String>("Hello", caseInsensitiveComparer);
        final AtomicInteger updateCount = new AtomicInteger(0);
        final AtomicReference<String> lastValue = new AtomicReference<String>();
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
                lastValue.set(field.get());
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                lastValue.set(field.get());
            }
        });
        
        // Same value (case insensitive) - should not trigger
        field.set("HELLO");
        assertEquals(0, updateCount.get());
        assertEquals("Hello", lastValue.get()); // Value doesn't change because comparer says equal
        
        // Different value - should trigger
        field.set("World");
        assertEquals(1, updateCount.get());
        assertEquals("World", lastValue.get());
    }

    @Test
    public void testComparingTrackableFieldWithToleranceComparer() {
        EqualityComparer<Double> toleranceComparer = new EqualityComparer<Double>() {
            @Override
            public boolean equals(Double obj1, Double obj2) {
                if (obj1 == null && obj2 == null) return true;
                if (obj1 == null || obj2 == null) return false;
                return Math.abs(obj1 - obj2) < 0.01;
            }
        };
        
        TrackableField<Double> field = new ComparingTrackableField<Double>(1.0, toleranceComparer);
        final AtomicInteger updateCount = new AtomicInteger(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                // Track initial state
            }
        });
        
        // Value within tolerance - should not trigger
        field.set(1.005);
        assertEquals(0, updateCount.get());
        
        // Value outside tolerance - should trigger
        field.set(1.5);
        assertEquals(1, updateCount.get());
    }

    // ==================== Trackable.track Static Method Tests ====================

    @Test
    public void testTrackWithNestedActions() {
        final TrackableField<Integer> outerField = new TrackableField<Integer>(1);
        final TrackableField<Integer> innerField = new TrackableField<Integer>(2);
        final AtomicInteger outerUpdates = new AtomicInteger(0);
        final AtomicInteger innerUpdates = new AtomicInteger(0);
        
        Tracker outerTracker = new Tracker() {
            @Override
            public void update() {
                outerUpdates.incrementAndGet();
            }
        };
        
        Tracker innerTracker = new Tracker() {
            @Override
            public void update() {
                innerUpdates.incrementAndGet();
            }
        };
        
        // Nested tracking
        Trackable.track(outerTracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                outerField.get();
                Trackable.track(innerTracker, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        innerField.get();
                    }
                });
            }
        });
        
        // Updating outer field should notify outer tracker
        outerField.set(10);
        assertEquals(1, outerUpdates.get());
        
        // Updating inner field should notify inner tracker
        innerField.set(20);
        assertEquals(1, innerUpdates.get());
    }

    @Test
    public void testTrackerOnlyNotifiedOnce() {
        final TrackableField<Integer> field = new TrackableField<Integer>(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
            }
        };
        
        // Track multiple times with same tracker
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
                field.get(); // Get multiple times
                field.get();
            }
        });
        
        // Only one notification should occur
        field.set(1);
        assertEquals(1, updateCount.get());
    }

    // ==================== Edge Case Tests ====================

    @Test
    public void testTrackableFieldWithObjectEquality() {
        // Test with objects that have custom equals
        final TrackableField<TestObject> field = new TrackableField<TestObject>(new TestObject("a", 1));
        final AtomicInteger updateCount = new AtomicInteger(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
        
        // Set to equal object - should not trigger
        field.set(new TestObject("a", 1));
        assertEquals(0, updateCount.get());
        
        // Set to different object - should trigger
        field.set(new TestObject("b", 2));
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testTrackableFieldRapidUpdates() {
        final TrackableField<Integer> field = new TrackableField<Integer>(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
        final AtomicReference<Integer> lastValue = new AtomicReference<Integer>();
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
                lastValue.set(field.get());
                // Re-register tracker
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        field.get();
                    }
                });
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                lastValue.set(field.get());
            }
        });
        
        // Rapid updates
        for (int i = 1; i <= 100; i++) {
            field.set(i);
        }
        
        assertEquals(100, updateCount.get());
        assertEquals(Integer.valueOf(100), lastValue.get());
    }

    @Test
    public void testTrackingWithExceptionInAction() {
        final TrackableField<Integer> field = new TrackableField<Integer>(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                // Should still work after exception
            }
        };
        
        try {
            Trackable.track(tracker, new Action<Void>() {
                @Override
                public void invoke(Void o) {
                    field.get();
                    throw new RuntimeException("Test exception");
                }
            });
            fail("Should have thrown exception");
        } catch (RuntimeException e) {
            assertEquals("Test exception", e.getMessage());
        }
        
        // The tracking frame should be popped even after exception
        // New tracking should work
        final AtomicInteger updateCount = new AtomicInteger(0);
        Tracker newTracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
            }
        };
        
        Trackable.track(newTracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
        
        field.set(1);
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testTrackableFieldChainedDependencies() {
        final TrackableField<Integer> source = new TrackableField<Integer>(1);
        final TrackableField<Integer> derived1 = new TrackableField<Integer>(0);
        final TrackableField<Integer> derived2 = new TrackableField<Integer>(0);
        
        // derived1 = source * 2
        Tracker tracker1 = new Tracker() {
            @Override
            public void update() {
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        derived1.set(source.get() * 2);
                    }
                });
            }
        };
        
        // derived2 = derived1 + 10
        Tracker tracker2 = new Tracker() {
            @Override
            public void update() {
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        derived2.set(derived1.get() + 10);
                    }
                });
            }
        };
        
        // Initialize
        Trackable.track(tracker1, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                derived1.set(source.get() * 2);
            }
        });
        Trackable.track(tracker2, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                derived2.set(derived1.get() + 10);
            }
        });
        
        assertEquals(Integer.valueOf(2), derived1.get());
        assertEquals(Integer.valueOf(12), derived2.get());
        
        // Update source
        source.set(5);
        assertEquals(Integer.valueOf(10), derived1.get());
        assertEquals(Integer.valueOf(20), derived2.get());
    }

    // ==================== Helper Methods ====================

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

    private <T> Tracker createCountingTracker(final TrackableField<T> field, final AtomicInteger counter) {
        return new Tracker() {
            @Override
            public void update() {
                counter.incrementAndGet();
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        field.get();
                    }
                });
            }
        };
    }

    // ==================== Helper Classes ====================

    private static class TestObject {
        private String name;
        private int value;
        
        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestObject)) return false;
            TestObject other = (TestObject) obj;
            return this.name.equals(other.name) && this.value == other.value;
        }
        
        @Override
        public int hashCode() {
            return name.hashCode() + value;
        }
    }
}
