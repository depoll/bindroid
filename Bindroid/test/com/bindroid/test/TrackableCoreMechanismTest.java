package com.bindroid.test;

import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.TrackableField;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Unit tests specifically for the core Trackable tracking mechanism.
 * These tests focus on the static tracking methods and the tracking lifecycle.
 */
public class TrackableCoreMechanismTest {

    // ==================== Basic Trackable Tests ====================

    @Test
    public void testRawTrackableTrack() {
        Trackable trackable = new Trackable();
        final AtomicBoolean updated = new AtomicBoolean(false);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updated.set(true);
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                trackable.track();
            }
        });
        
        assertFalse(updated.get());
        
        trackable.updateTrackers();
        assertTrue(updated.get());
    }

    @Test
    public void testTrackableOnlyNotifiesOnce() {
        Trackable trackable = new Trackable();
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
                trackable.track();
                trackable.track();
                trackable.track();
            }
        });
        
        trackable.updateTrackers();
        // Should only notify once, not three times
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testUpdateTrackersClearsTrackers() {
        Trackable trackable = new Trackable();
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
                trackable.track();
            }
        });
        
        trackable.updateTrackers();
        assertEquals(1, updateCount.get());
        
        // Second update should not notify because tracker was cleared
        trackable.updateTrackers();
        assertEquals(1, updateCount.get());
    }

    // ==================== Static Track Method Tests ====================

    @Test
    public void testStaticTrackWithAction() {
        final TrackableField<String> field = new TrackableField<String>("initial");
        final AtomicReference<String> result = new AtomicReference<String>();
        
        Trackable.track(new Tracker() {
            @Override
            public void update() {
                result.set("updated");
            }
        }, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                result.set(field.get());
            }
        });
        
        assertEquals("initial", result.get());
        
        field.set("changed");
        assertEquals("updated", result.get());
    }

    @Test
    public void testStaticTrackWithFunction() {
        final TrackableField<Integer> field = new TrackableField<Integer>(5);
        
        Integer result = Trackable.track(new Tracker() {
            @Override
            public void update() {
                // Not used
            }
        }, new Function<Integer>() {
            @Override
            public Integer evaluate() {
                return field.get() * 2;
            }
        });
        
        assertEquals(Integer.valueOf(10), result);
    }

    @Test
    public void testStaticTrackFunctionReturnsValue() {
        String result = Trackable.track(new Tracker() {
            @Override
            public void update() {
            }
        }, new Function<String>() {
            @Override
            public String evaluate() {
                return "test value";
            }
        });
        
        assertEquals("test value", result);
    }

    @Test
    public void testStaticTrackFunctionReturnsNull() {
        String result = Trackable.track(new Tracker() {
            @Override
            public void update() {
            }
        }, new Function<String>() {
            @Override
            public String evaluate() {
                return null;
            }
        });
        
        assertNull(result);
    }

    // ==================== Nested Tracking Tests ====================

    @Test
    public void testNestedTrackingActions() {
        final TrackableField<Integer> field1 = new TrackableField<Integer>(1);
        final TrackableField<Integer> field2 = new TrackableField<Integer>(2);
        final AtomicInteger outerUpdates = new AtomicInteger(0);
        final AtomicInteger innerUpdates = new AtomicInteger(0);
        
        Trackable.track(new Tracker() {
            @Override
            public void update() {
                outerUpdates.incrementAndGet();
            }
        }, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field1.get();
                Trackable.track(new Tracker() {
                    @Override
                    public void update() {
                        innerUpdates.incrementAndGet();
                    }
                }, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        field2.get();
                    }
                });
            }
        });
        
        // Updating field1 should trigger outer tracker
        field1.set(10);
        assertEquals(1, outerUpdates.get());
        assertEquals(0, innerUpdates.get());
        
        // Updating field2 should trigger inner tracker
        field2.set(20);
        assertEquals(1, outerUpdates.get());
        assertEquals(1, innerUpdates.get());
    }

    @Test
    public void testDeeplyNestedTracking() {
        final TrackableField<Integer> level1 = new TrackableField<Integer>(1);
        final TrackableField<Integer> level2 = new TrackableField<Integer>(2);
        final TrackableField<Integer> level3 = new TrackableField<Integer>(3);
        final AtomicInteger updates1 = new AtomicInteger(0);
        final AtomicInteger updates2 = new AtomicInteger(0);
        final AtomicInteger updates3 = new AtomicInteger(0);
        
        Trackable.track(new Tracker() {
            @Override
            public void update() {
                updates1.incrementAndGet();
            }
        }, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                level1.get();
                Trackable.track(new Tracker() {
                    @Override
                    public void update() {
                        updates2.incrementAndGet();
                    }
                }, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        level2.get();
                        Trackable.track(new Tracker() {
                            @Override
                            public void update() {
                                updates3.incrementAndGet();
                            }
                        }, new Action<Void>() {
                            @Override
                            public void invoke(Void o) {
                                level3.get();
                            }
                        });
                    }
                });
            }
        });
        
        level3.set(30);
        assertEquals(0, updates1.get());
        assertEquals(0, updates2.get());
        assertEquals(1, updates3.get());
        
        level2.set(20);
        assertEquals(0, updates1.get());
        assertEquals(1, updates2.get());
        
        level1.set(10);
        assertEquals(1, updates1.get());
    }

    // ==================== Tracker Wrapper Tests ====================

    @Test
    public void testTrackerOnlyCalledOnce() {
        final Trackable trackable = new Trackable();
        final AtomicInteger updateCount = new AtomicInteger(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
            }
        };
        
        // Track the trackable
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                trackable.track();
            }
        });
        
        // Multiple update calls should only trigger tracker once
        trackable.updateTrackers();
        assertEquals(1, updateCount.get());
        
        // Re-track
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                trackable.track();
            }
        });
        
        trackable.updateTrackers();
        assertEquals(2, updateCount.get());
    }

    // ==================== Exception Handling Tests ====================

    @Test
    public void testExceptionInActionStillPopsFrame() {
        final TrackableField<Integer> field = new TrackableField<Integer>(0);
        final AtomicInteger updates = new AtomicInteger(0);
        
        try {
            Trackable.track(new Tracker() {
                @Override
                public void update() {
                    updates.incrementAndGet();
                }
            }, new Action<Void>() {
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
        
        // New tracking should work after exception
        final AtomicInteger newUpdates = new AtomicInteger(0);
        Trackable.track(new Tracker() {
            @Override
            public void update() {
                newUpdates.incrementAndGet();
            }
        }, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
        
        field.set(1);
        assertEquals(1, newUpdates.get());
    }

    @Test
    public void testExceptionInFunctionStillPopsFrame() {
        final TrackableField<Integer> field = new TrackableField<Integer>(0);
        
        try {
            Trackable.track(new Tracker() {
                @Override
                public void update() {
                }
            }, new Function<Integer>() {
                @Override
                public Integer evaluate() {
                    field.get();
                    throw new RuntimeException("Function exception");
                }
            });
            fail("Should have thrown exception");
        } catch (RuntimeException e) {
            assertEquals("Function exception", e.getMessage());
        }
        
        // Verify we can still track after exception
        final AtomicInteger updates = new AtomicInteger(0);
        Trackable.track(new Tracker() {
            @Override
            public void update() {
                updates.incrementAndGet();
            }
        }, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
        
        field.set(1);
        assertEquals(1, updates.get());
    }

    // ==================== Multiple Trackables Tests ====================

    @Test
    public void testTrackingMultipleTrackables() {
        final TrackableField<Integer> field1 = new TrackableField<Integer>(1);
        final TrackableField<Integer> field2 = new TrackableField<Integer>(2);
        final TrackableField<Integer> field3 = new TrackableField<Integer>(3);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updates.incrementAndGet();
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        field1.get();
                        field2.get();
                        field3.get();
                    }
                });
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field1.get();
                field2.get();
                field3.get();
            }
        });
        
        field1.set(10);
        assertEquals(1, updates.get());
        
        field2.set(20);
        assertEquals(2, updates.get());
        
        field3.set(30);
        assertEquals(3, updates.get());
    }

    // ==================== Tracking Without Tracker Tests ====================

    @Test
    public void testTrackWithoutActiveTracker() {
        Trackable trackable = new Trackable();
        
        // This should not throw
        trackable.track();
        
        // This should also not throw even with no trackers
        trackable.updateTrackers();
    }

    @Test
    public void testUpdateTrackersWithNoTrackers() {
        Trackable trackable = new Trackable();
        
        // Should not throw
        trackable.updateTrackers();
        trackable.updateTrackers();
        trackable.updateTrackers();
    }
}
