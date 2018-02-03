package com.bindroid.test;

import android.view.View;

import com.bindroid.Binding;
import com.bindroid.BindingMode;
import com.bindroid.converters.AdapterConverter;
import com.bindroid.converters.BoolConverter;
import com.bindroid.converters.ToStringConverter;
import com.bindroid.trackable.TrackableCollection;
import com.bindroid.trackable.TrackableField;
import com.bindroid.ui.BoundCollectionAdapter;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.Property;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tests the various converters.
 */
public class ConverterTest extends TestCase {

    public void testBoolConverter() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Object.class), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get());

        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set("Anything");
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(false);
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(1);
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(0);
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Boolean.TRUE, obj1.get());
    }

    public void testInvertedBoolConverter() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Object.class), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get(true));

        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set("Anything");
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(false);
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(1);
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(0);
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Boolean.FALSE, obj1.get());
    }

    public void testBoolConverterWithEmptyList() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Object.class), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get(false, true));

        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set("Anything");
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(false);
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(1);
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(0);
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Boolean.FALSE, obj1.get());
    }

    public void testInvertedBoolConverterWithEmptyList() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Object.class), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get(true, true));

        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set("Anything");
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(false);
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(1);
        assertEquals(Boolean.FALSE, obj1.get());
        obj2.set(0);
        assertEquals(Boolean.TRUE, obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Boolean.TRUE, obj1.get());
    }

    public void testVisibilityBoolConverter() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Integer.TYPE), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get());

        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set("Anything");
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(false);
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(1);
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(0);
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
    }

    public void testVisibilityInvertedBoolConverter() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Integer.TYPE), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get(true));

        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set("Anything");
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(false);
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(1);
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(0);
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
    }

    public void testVisibilityBoolConverterWithEmptyList() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Integer.TYPE), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get(false, true));

        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set("Anything");
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(false);
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(1);
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(0);
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
    }

    public void testVisibilityInvertedBoolConverterWithEmptyList() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Integer.TYPE), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, BoolConverter.get(true, true));

        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set("Anything");
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(false);
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(1);
        assertEquals(Integer.valueOf(View.GONE), obj1.get());
        obj2.set(0);
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals(Integer.valueOf(View.VISIBLE), obj1.get());
    }

    public void testToStringConverter() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Object.class), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, new ToStringConverter());

        assertEquals("null", obj1.get());
        obj2.set("Anything");
        assertEquals("Anything", obj1.get());
        obj2.set(false);
        assertEquals("false", obj1.get());
        obj2.set(1);
        assertEquals("1", obj1.get());
        obj2.set(0);
        assertEquals("0", obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals("[]", obj1.get());
    }

    public void testToStringFormatConverter() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Object.class), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, new ToStringConverter("Whoa there: %s"));

        assertEquals("Whoa there: null", obj1.get());
        obj2.set("Anything");
        assertEquals("Whoa there: Anything", obj1.get());
        obj2.set(false);
        assertEquals("Whoa there: false", obj1.get());
        obj2.set(1);
        assertEquals("Whoa there: 1", obj1.get());
        obj2.set(0);
        assertEquals("Whoa there: 0", obj1.get());
        obj2.set(new ArrayList<Object>());
        assertEquals("Whoa there: []", obj1.get());
    }

    public void testAdapterConverter() {
        final TrackableField<Object> obj1 = new TrackableField<Object>();
        final TrackableField<Object> obj2 = new TrackableField<Object>();

        new Binding(new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj1.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj1.set(parameter);
            }
        }, Object.class), new Property<Object>(new Function<Object>() {
            @Override
            public Object evaluate() {
                return obj2.get();
            }
        }, new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                obj2.set(parameter);
            }
        }, Object.class), BindingMode.ONE_WAY, new AdapterConverter(View.class));

        assertNull(obj1.get());
        obj2.set(Arrays.asList(1, 2, 3));
        assertTrue(obj1.get() instanceof BoundCollectionAdapter);
        obj2.set(new TrackableCollection<Integer>());
        assertEquals(((BoundCollectionAdapter<?>) obj1.get()).getData(), obj2.get());
    }
}
