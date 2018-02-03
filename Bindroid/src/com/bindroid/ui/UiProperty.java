package com.bindroid.ui;

import android.os.Handler;
import android.os.Looper;

import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.Property;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A property that wraps another property, delegating calls to the UI thread.
 */
public class UiProperty<T> extends Property<T> {
    private Property<T> property;

    private static final Handler UI_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * Creates a UIProperty for the given property.
     *
     * @param property the property to wrap.
     * @return the new property, whose getters and setters will dispatch to the UI thread.
     */
    public static <T> UiProperty<T> make(Property<T> property) {
        return new UiProperty<T>(property);
    }

    private UiProperty(Property<T> property) {
        this.property = property;
        if (property.getGetter() != null) {
            this.getter = new Function<T>() {
                @Override
                public T evaluate() {
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        return UiProperty.this.property.getValue();
                    }
                    final AtomicReference<T> ref = new AtomicReference<T>();
                    synchronized (ref) {
                        UI_THREAD_HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (ref) {
                                    ref.set(UiProperty.this.property.getValue());
                                    ref.notify();
                                }
                            }
                        });
                        try {
                            ref.wait();
                            return ref.get();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
        }
        if (property.getSetter() != null) {
            this.setter = new Action<T>() {
                @Override
                public void invoke(final T parameter) {
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        UiProperty.this.property.setValue(parameter);
                    } else {
                        final Object lock = new Object();
                        synchronized (lock) {
                            UI_THREAD_HANDLER.post(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (lock) {
                                        UiProperty.this.property.setValue(parameter);
                                        lock.notify();
                                    }
                                }
                            });
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            };
        }
    }

    @Override
    public Class<?> getType() {
        return this.property.getType();
    }
}
