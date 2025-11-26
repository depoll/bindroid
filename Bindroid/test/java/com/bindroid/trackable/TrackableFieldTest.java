package com.bindroid.trackable;

import org.junit.Test;

import static org.junit.Assert.*;

public class TrackableFieldTest {
    private static class CountingTracker implements Tracker { int updates=0; @Override public void update(){updates++;}}

    @Test
    public void get_tracks_and_set_updates_on_change() {
        TrackableField<Integer> field = new TrackableField<>(1);
        CountingTracker tracker = new CountingTracker();

        Integer value = Trackable.track(tracker, field::get);
        assertEquals(Integer.valueOf(1), value);
        assertEquals(0, tracker.updates);

        field.set(1);
        assertEquals(0, tracker.updates);

        field.set(2);
        assertEquals(1, tracker.updates);
    }
}

