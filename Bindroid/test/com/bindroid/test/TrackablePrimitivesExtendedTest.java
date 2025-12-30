package com.bindroid.test;

import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.TrackableBoolean;
import com.bindroid.trackable.TrackableByte;
import com.bindroid.trackable.TrackableChar;
import com.bindroid.trackable.TrackableDouble;
import com.bindroid.trackable.TrackableFloat;
import com.bindroid.trackable.TrackableInt;
import com.bindroid.trackable.TrackableLong;
import com.bindroid.trackable.TrackableShort;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Action;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extended unit tests for primitive trackable types.
 * Tests cover edge cases, boundary values, and additional scenarios.
 */
public class TrackablePrimitivesExtendedTest {

    // ==================== TrackableInt Extended Tests ====================

    @Test
    public void testTrackableIntDefaultConstructor() {
        TrackableInt field = new TrackableInt();
        assertEquals(0, field.get());
    }

    @Test
    public void testTrackableIntBoundaryValues() {
        TrackableInt field = new TrackableInt();
        
        field.set(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, field.get());
        
        field.set(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, field.get());
    }

    @Test
    public void testTrackableIntToString() {
        TrackableInt field = new TrackableInt(42);
        assertEquals("42", field.toString());
    }

    @Test
    public void testTrackableIntNoUpdateOnSameValue() {
        TrackableInt field = new TrackableInt(100);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = createCountingTracker(updates);
        trackInt(field, tracker);
        
        field.set(100);
        assertEquals(0, updates.get());
        
        field.set(200);
        assertEquals(1, updates.get());
    }

    // ==================== TrackableLong Extended Tests ====================

    @Test
    public void testTrackableLongDefaultConstructor() {
        TrackableLong field = new TrackableLong();
        assertEquals(0L, field.get());
    }

    @Test
    public void testTrackableLongBoundaryValues() {
        TrackableLong field = new TrackableLong();
        
        field.set(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, field.get());
        
        field.set(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, field.get());
    }

    @Test
    public void testTrackableLongToString() {
        TrackableLong field = new TrackableLong(1234567890123L);
        assertEquals("1234567890123", field.toString());
    }

    @Test
    public void testTrackableLongNoUpdateOnSameValue() {
        TrackableLong field = new TrackableLong(100L);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = createCountingTracker(updates);
        trackLong(field, tracker);
        
        field.set(100L);
        assertEquals(0, updates.get());
        
        field.set(200L);
        assertEquals(1, updates.get());
    }

    // ==================== TrackableDouble Extended Tests ====================

    @Test
    public void testTrackableDoubleDefaultConstructor() {
        TrackableDouble field = new TrackableDouble();
        assertEquals(0.0, field.get(), 0.0001);
    }

    @Test
    public void testTrackableDoubleSpecialValues() {
        TrackableDouble field = new TrackableDouble();
        
        field.set(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, field.get(), 0.0001);
        
        field.set(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, field.get(), 0.0001);
        
        field.set(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, field.get(), 0.0001);
        
        field.set(Double.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, field.get(), 0.0001);
    }

    @Test
    public void testTrackableDoubleNaN() {
        TrackableDouble field = new TrackableDouble(Double.NaN);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = createCountingTracker(updates);
        trackDouble(field, tracker);
        
        // NaN != NaN, so setting to NaN should trigger update
        field.set(Double.NaN);
        // Due to NaN comparison behavior, this might trigger an update
        assertTrue(Double.isNaN(field.get()));
    }

    @Test
    public void testTrackableDoubleToString() {
        TrackableDouble field = new TrackableDouble(3.14159);
        assertEquals("3.14159", field.toString());
    }

    @Test
    public void testTrackableDoubleNoUpdateOnSameValue() {
        TrackableDouble field = new TrackableDouble(1.5);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = createCountingTracker(updates);
        trackDouble(field, tracker);
        
        field.set(1.5);
        assertEquals(0, updates.get());
        
        field.set(2.5);
        assertEquals(1, updates.get());
    }

    // ==================== TrackableFloat Extended Tests ====================

    @Test
    public void testTrackableFloatDefaultConstructor() {
        TrackableFloat field = new TrackableFloat();
        assertEquals(0.0f, field.get(), 0.0001f);
    }

    @Test
    public void testTrackableFloatSpecialValues() {
        TrackableFloat field = new TrackableFloat();
        
        field.set(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, field.get(), 0.0001f);
        
        field.set(Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, field.get(), 0.0001f);
        
        field.set(Float.POSITIVE_INFINITY);
        assertEquals(Float.POSITIVE_INFINITY, field.get(), 0.0001f);
    }

    @Test
    public void testTrackableFloatToString() {
        TrackableFloat field = new TrackableFloat(2.718f);
        assertTrue(field.toString().startsWith("2.718"));
    }

    // ==================== TrackableBoolean Extended Tests ====================

    @Test
    public void testTrackableBooleanDefaultConstructor() {
        TrackableBoolean field = new TrackableBoolean();
        assertFalse(field.get());
    }

    @Test
    public void testTrackableBooleanToggle() {
        TrackableBoolean field = new TrackableBoolean(false);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = createCountingTracker(updates);
        trackBoolean(field, tracker);
        
        field.set(true);
        assertEquals(1, updates.get());
        assertTrue(field.get());
        
        field.set(false);
        assertEquals(2, updates.get());
        assertFalse(field.get());
    }

    @Test
    public void testTrackableBooleanToString() {
        TrackableBoolean trueField = new TrackableBoolean(true);
        TrackableBoolean falseField = new TrackableBoolean(false);
        
        assertEquals("true", trueField.toString());
        assertEquals("false", falseField.toString());
    }

    @Test
    public void testTrackableBooleanNoUpdateOnSameValue() {
        TrackableBoolean field = new TrackableBoolean(true);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = createCountingTracker(updates);
        trackBoolean(field, tracker);
        
        field.set(true);
        assertEquals(0, updates.get());
        
        field.set(false);
        assertEquals(1, updates.get());
    }

    // ==================== TrackableByte Extended Tests ====================

    @Test
    public void testTrackableByteDefaultConstructor() {
        TrackableByte field = new TrackableByte();
        assertEquals(0, field.get());
    }

    @Test
    public void testTrackableByteBoundaryValues() {
        TrackableByte field = new TrackableByte();
        
        field.set(Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, field.get());
        
        field.set(Byte.MIN_VALUE);
        assertEquals(Byte.MIN_VALUE, field.get());
    }

    @Test
    public void testTrackableByteToString() {
        TrackableByte field = new TrackableByte((byte) 42);
        assertEquals("42", field.toString());
    }

    @Test
    public void testTrackableByteNoUpdateOnSameValue() {
        TrackableByte field = new TrackableByte((byte) 100);
        final AtomicInteger updates = new AtomicInteger(0);
        
        Tracker tracker = createCountingTracker(updates);
        trackByte(field, tracker);
        
        field.set((byte) 100);
        assertEquals(0, updates.get());
        
        field.set((byte) 50);
        assertEquals(1, updates.get());
    }

    // ==================== TrackableShort Extended Tests ====================

    @Test
    public void testTrackableShortDefaultConstructor() {
        TrackableShort field = new TrackableShort();
        assertEquals(0, field.get());
    }

    @Test
    public void testTrackableShortBoundaryValues() {
        TrackableShort field = new TrackableShort();
        
        field.set(Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, field.get());
        
        field.set(Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, field.get());
    }

    @Test
    public void testTrackableShortToString() {
        TrackableShort field = new TrackableShort((short) 12345);
        assertEquals("12345", field.toString());
    }

    // ==================== TrackableChar Extended Tests ====================

    @Test
    public void testTrackableCharDefaultConstructor() {
        TrackableChar field = new TrackableChar();
        assertEquals('\0', field.get());
    }

    @Test
    public void testTrackableCharBoundaryValues() {
        TrackableChar field = new TrackableChar();
        
        field.set(Character.MAX_VALUE);
        assertEquals(Character.MAX_VALUE, field.get());
        
        field.set(Character.MIN_VALUE);
        assertEquals(Character.MIN_VALUE, field.get());
    }

    @Test
    public void testTrackableCharUnicodeCharacters() {
        TrackableChar field = new TrackableChar();
        
        field.set('A');
        assertEquals('A', field.get());
        
        field.set('中');  // Chinese character
        assertEquals('中', field.get());
        
        field.set('\n');  // Newline
        assertEquals('\n', field.get());
    }

    @Test
    public void testTrackableCharToString() {
        TrackableChar field = new TrackableChar('X');
        assertEquals("X", field.toString());
    }

    // ==================== Cross-Type Tracking Tests ====================

    @Test
    public void testMultiplePrimitivesTracking() {
        final TrackableInt intField = new TrackableInt(1);
        final TrackableBoolean boolField = new TrackableBoolean(true);
        final TrackableDouble doubleField = new TrackableDouble(1.0);
        
        final AtomicInteger totalUpdates = new AtomicInteger(0);
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                totalUpdates.incrementAndGet();
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        intField.get();
                        boolField.get();
                        doubleField.get();
                    }
                });
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                intField.get();
                boolField.get();
                doubleField.get();
            }
        });
        
        intField.set(2);
        assertEquals(1, totalUpdates.get());
        
        boolField.set(false);
        assertEquals(2, totalUpdates.get());
        
        doubleField.set(2.0);
        assertEquals(3, totalUpdates.get());
    }

    // ==================== Helper Methods ====================

    private Tracker createCountingTracker(final AtomicInteger counter) {
        return new Tracker() {
            @Override
            public void update() {
                counter.incrementAndGet();
            }
        };
    }

    private void trackInt(final TrackableInt field, final Tracker tracker) {
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
    }

    private void trackLong(final TrackableLong field, final Tracker tracker) {
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
    }

    private void trackDouble(final TrackableDouble field, final Tracker tracker) {
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
    }

    private void trackBoolean(final TrackableBoolean field, final Tracker tracker) {
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
    }

    private void trackByte(final TrackableByte field, final Tracker tracker) {
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                field.get();
            }
        });
    }
}
