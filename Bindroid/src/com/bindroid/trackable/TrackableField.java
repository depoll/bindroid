package com.bindroid.trackable;

import com.bindroid.utils.EqualityComparer;
import com.bindroid.utils.ObjectUtilities;

/**
 * Implements a {@link Trackable} that stores a value. A TrackableField calls {@link #track()} when
 * {@link #get()} is called and {@link #updateTrackers()} when {@link #set(Object)} is called and
 * its value has changed. As a result, TrackableFields are ideal for implementing properties -- most
 * of the time, getters and setters can simply delegate directly to the TrackableField, and the
 * TrackableField replaces the field that would otherwise have been private on the object.
 * TrackableFields are rarely exposed as a part of a public API.
 * <p>
 * TrackableFields are meant to be as lightweight as possible in order to avoid overhead when using
 * them for all of the fields of an object. For this reason, if you wish to specify a custom
 * {@link EqualityComparer}, you should use a {@link ComparingTrackableField}, which has the extra
 * field (and thus extra overhead) to store the comparer.
 *
 * @param <T> The type of the field.
 */
public class TrackableField<T> extends Trackable {
    protected T value;
    private static EqualityComparer<Object> comparer;

    static {
        TrackableField.comparer = ObjectUtilities.getDefaultComparer();
    }

    /**
     * Constructs a new TrackableField, initialized to null.
     */
    public TrackableField() {
        this(null);
    }

    /**
     * Constructs a new TrackableField with the given initial value.
     *
     * @param initialValue The initial value of the field.
     */
    public TrackableField(T initialValue) {
        this.value = initialValue;
    }

    /**
     * Gets the value of the TrackableField and calls {@link #track()}.
     *
     * @return The value of the TrackableField.
     */
    public T get() {
        this.track();
        return this.value;
    }

    /**
     * Sets the value of the TrackableField and calls {@link #updateTrackers()}.
     *
     * @param value The new value.
     */
    public void set(T value) {
        boolean update = !TrackableField.comparer.equals(this.value, value);
        this.value = value;
        if (update) {
            this.updateTrackers();
        }
    }

    @Override
    public String toString() {
        return "" + this.get();
    }
}
