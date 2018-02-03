package com.bindroid.utils;

import java.lang.ref.WeakReference;

/**
 * A ReflectedProperty that keeps a weak reference to its source.
 */
public class WeakReflectedProperty extends ReflectedProperty {
    /**
     * Creates a WeakReflectedProperty for the given source and path.
     *
     * @param source
     * @param path
     */
    public WeakReflectedProperty(Object source, String path) {
        super(new WeakReference<Object>(source), path);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getSource() {
        return ((WeakReference<Object>) super.getSource()).get();
    }
}
