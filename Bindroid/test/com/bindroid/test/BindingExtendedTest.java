package com.bindroid.test;

import com.bindroid.Binding;
import com.bindroid.BindingMode;
import com.bindroid.ValueConverter;
import com.bindroid.trackable.TrackableField;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.Property;
import com.bindroid.utils.ReflectedProperty;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Extended unit tests for Binding class.
 * These tests focus on edge cases and scenarios not covered by the existing tests.
 */
public class BindingExtendedTest {

    private TrackableField<Object> field1;
    private TrackableField<Object> field2;

    @Before
    public void setUp() {
        field1 = new TrackableField<Object>();
        field2 = new TrackableField<Object>();
    }

    // ==================== One-Way Binding Tests ====================

    @Test
    public void testOneWayBindingInitialSync() {
        field1.set("target");
        field2.set("source");
        
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY);
        
        assertEquals("source", field1.get());
        assertEquals("source", field2.get());
    }

    @Test
    public void testOneWayBindingSourceToTarget() {
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY);
        
        field2.set("new value");
        assertEquals("new value", field1.get());
    }

    @Test
    public void testOneWayBindingTargetChangeIgnored() {
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY);
        
        field2.set("source");
        assertEquals("source", field1.get());
        
        field1.set("target only");
        assertEquals("source", field2.get());
    }

    @Test
    public void testOneWayBindingWithNullValues() {
        field2.set("initial");
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY);
        
        assertEquals("initial", field1.get());
        
        field2.set(null);
        assertNull(field1.get());
    }

    // ==================== One-Way-To-Source Binding Tests ====================

    @Test
    public void testOneWayToSourceBindingInitialSync() {
        field1.set("target");
        field2.set("source");
        
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY_TO_SOURCE);
        
        assertEquals("target", field1.get());
        assertEquals("target", field2.get());
    }

    @Test
    public void testOneWayToSourceBindingTargetToSource() {
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY_TO_SOURCE);
        
        field1.set("new value");
        assertEquals("new value", field2.get());
    }

    @Test
    public void testOneWayToSourceBindingSourceChangeIgnored() {
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY_TO_SOURCE);
        
        field1.set("target");
        assertEquals("target", field2.get());
        
        field2.set("source only");
        assertEquals("target", field1.get());
    }

    // ==================== Two-Way Binding Tests ====================

    @Test
    public void testTwoWayBindingInitialSync() {
        field1.set("target");
        field2.set("source");
        
        new Binding(createProperty(field1), createProperty(field2), BindingMode.TWO_WAY);
        
        assertEquals("source", field1.get());
        assertEquals("source", field2.get());
    }

    @Test
    public void testTwoWayBindingSourceChanges() {
        new Binding(createProperty(field1), createProperty(field2), BindingMode.TWO_WAY);
        
        field2.set("source value");
        assertEquals("source value", field1.get());
        assertEquals("source value", field2.get());
    }

    @Test
    public void testTwoWayBindingTargetChanges() {
        new Binding(createProperty(field1), createProperty(field2), BindingMode.TWO_WAY);
        
        field1.set("target value");
        assertEquals("target value", field1.get());
        assertEquals("target value", field2.get());
    }

    @Test
    public void testTwoWayBindingAlternatingChanges() {
        new Binding(createProperty(field1), createProperty(field2), BindingMode.TWO_WAY);
        
        field1.set("from target 1");
        assertEquals("from target 1", field2.get());
        
        field2.set("from source 1");
        assertEquals("from source 1", field1.get());
        
        field1.set("from target 2");
        assertEquals("from target 2", field2.get());
    }

    // ==================== Value Converter Tests ====================

    @Test
    public void testOneWayBindingWithConverter() {
        ValueConverter uppercaseConverter = new ValueConverter() {
            @Override
            public Object convertToTarget(Object sourceValue, Class<?> targetType) {
                return sourceValue != null ? sourceValue.toString().toUpperCase() : null;
            }
        };
        
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY, uppercaseConverter);
        
        field2.set("hello");
        assertEquals("HELLO", field1.get());
    }

    @Test
    public void testOneWayToSourceBindingWithConverter() {
        ValueConverter lowercaseConverter = new ValueConverter() {
            @Override
            public Object convertToSource(Object targetValue, Class<?> sourceType) {
                return targetValue != null ? targetValue.toString().toLowerCase() : null;
            }
        };
        
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY_TO_SOURCE, lowercaseConverter);
        
        field1.set("HELLO");
        assertEquals("hello", field2.get());
    }

    @Test
    public void testTwoWayBindingWithSymmetricConverter() {
        ValueConverter multiplyConverter = new ValueConverter() {
            @Override
            public Object convertToTarget(Object sourceValue, Class<?> targetType) {
                if (sourceValue instanceof Integer) {
                    return ((Integer) sourceValue) * 2;
                }
                return sourceValue;
            }
            
            @Override
            public Object convertToSource(Object targetValue, Class<?> sourceType) {
                if (targetValue instanceof Integer) {
                    return ((Integer) targetValue) / 2;
                }
                return targetValue;
            }
        };
        
        new Binding(createProperty(field1), createProperty(field2), BindingMode.TWO_WAY, multiplyConverter);
        
        field2.set(10);
        assertEquals(20, field1.get());
        
        field1.set(100);
        assertEquals(50, field2.get());
    }

    @Test
    public void testConverterWithNullValue() {
        ValueConverter nullSafeConverter = new ValueConverter() {
            @Override
            public Object convertToTarget(Object sourceValue, Class<?> targetType) {
                return sourceValue != null ? sourceValue : "default";
            }
        };
        
        new Binding(createProperty(field1), createProperty(field2), BindingMode.ONE_WAY, nullSafeConverter);
        
        field2.set(null);
        assertEquals("default", field1.get());
    }

    // ==================== Default Converter Tests ====================

    @Test
    public void testDefaultConverterPassthrough() {
        ValueConverter defaultConverter = ValueConverter.getDefaultConverter();
        
        Object testValue = "test";
        assertEquals(testValue, defaultConverter.convertToTarget(testValue, String.class));
        assertEquals(testValue, defaultConverter.convertToSource(testValue, String.class));
    }

    @Test
    public void testDefaultConverterWithNull() {
        ValueConverter defaultConverter = ValueConverter.getDefaultConverter();
        
        assertNull(defaultConverter.convertToTarget(null, String.class));
        assertNull(defaultConverter.convertToSource(null, String.class));
    }

    // ==================== Property Tests ====================

    @Test
    public void testReadOnlyProperty() {
        final AtomicReference<String> value = new AtomicReference<String>("initial");
        
        Property<String> readOnlyProp = new Property<String>(
            new Function<String>() {
                @Override
                public String evaluate() {
                    return value.get();
                }
            },
            null, // No setter
            String.class
        );
        
        Property<Object> targetProp = createProperty(field1);
        
        // Binding from read-only source should work
        new Binding(targetProp, readOnlyProp, BindingMode.ONE_WAY);
        assertEquals("initial", field1.get());
    }

    @Test
    public void testWriteOnlyProperty() {
        final AtomicReference<String> value = new AtomicReference<String>();
        
        Property<String> writeOnlyProp = new Property<String>(
            null, // No getter
            new Action<String>() {
                @Override
                public void invoke(String parameter) {
                    value.set(parameter);
                }
            },
            String.class
        );
        
        Property<Object> sourceProp = createProperty(field2);
        
        // Binding to write-only target should work
        field2.set("test");
        new Binding(writeOnlyProp, sourceProp, BindingMode.ONE_WAY);
        assertEquals("test", value.get());
    }

    // ==================== Reflected Property Binding Tests ====================

    @Test
    public void testReflectedPropertyBinding() {
        Nestable n1 = new Nestable();
        Nestable n2 = new Nestable();
        n2.setValue("source value");
        
        new Binding(new ReflectedProperty(n1, "Value"), new ReflectedProperty(n2, "Value"), BindingMode.ONE_WAY);
        
        assertEquals("source value", n1.getValue());
        
        n2.setValue("updated");
        assertEquals("updated", n1.getValue());
    }

    @Test
    public void testNestedReflectedPropertyBinding() {
        Nestable parent = new Nestable();
        Nestable child = new Nestable();
        child.setValue("child value");
        parent.setChild(child);
        
        TrackableField<String> target = new TrackableField<String>();
        
        new Binding(
            createStringProperty(target),
            new ReflectedProperty(parent, "Child.Value"),
            BindingMode.ONE_WAY
        );
        
        assertEquals("child value", target.get());
        
        child.setValue("updated");
        assertEquals("updated", target.get());
    }

    @Test
    public void testNestedPropertyChildReplacement() {
        Nestable parent = new Nestable();
        Nestable child1 = new Nestable();
        child1.setValue("child1");
        parent.setChild(child1);
        
        TrackableField<String> target = new TrackableField<String>();
        
        new Binding(
            createStringProperty(target),
            new ReflectedProperty(parent, "Child.Value"),
            BindingMode.ONE_WAY
        );
        
        assertEquals("child1", target.get());
        
        // Replace child
        Nestable child2 = new Nestable();
        child2.setValue("child2");
        parent.setChild(child2);
        
        assertEquals("child2", target.get());
    }

    @Test
    public void testNestedPropertyWithNullChild() {
        Nestable parent = new Nestable();
        Nestable child = new Nestable();
        child.setValue("value");
        parent.setChild(child);
        
        TrackableField<String> target = new TrackableField<String>();
        
        new Binding(
            createStringProperty(target),
            new ReflectedProperty(parent, "Child.Value"),
            BindingMode.ONE_WAY
        );
        
        assertEquals("value", target.get());
        
        // Set child to null
        parent.setChild(null);
        assertNull(target.get());
        
        // Set child again
        Nestable newChild = new Nestable();
        newChild.setValue("new value");
        parent.setChild(newChild);
        assertEquals("new value", target.get());
    }

    // ==================== Multiple Binding Tests ====================

    @Test
    public void testMultipleBindingsToSameTarget() {
        TrackableField<String> source1 = new TrackableField<String>("source1");
        TrackableField<String> source2 = new TrackableField<String>("source2");
        TrackableField<String> target = new TrackableField<String>();
        
        // First binding
        new Binding(createStringProperty(target), createStringProperty(source1), BindingMode.ONE_WAY);
        assertEquals("source1", target.get());
        
        // Second binding overwrites first
        new Binding(createStringProperty(target), createStringProperty(source2), BindingMode.ONE_WAY);
        assertEquals("source2", target.get());
        
        // Both bindings may still be active
        source1.set("updated1");
        source2.set("updated2");
        // The last change wins
        assertEquals("updated2", target.get());
    }

    @Test
    public void testBindingChain() {
        TrackableField<String> field1 = new TrackableField<String>("initial");
        TrackableField<String> field2 = new TrackableField<String>();
        TrackableField<String> field3 = new TrackableField<String>();
        
        new Binding(createStringProperty(field2), createStringProperty(field1), BindingMode.ONE_WAY);
        new Binding(createStringProperty(field3), createStringProperty(field2), BindingMode.ONE_WAY);
        
        assertEquals("initial", field2.get());
        assertEquals("initial", field3.get());
        
        field1.set("updated");
        assertEquals("updated", field2.get());
        assertEquals("updated", field3.get());
    }

    // ==================== Type Conversion Tests ====================

    @Test
    public void testIntegerToStringBinding() {
        TrackableField<Integer> intField = new TrackableField<Integer>(42);
        TrackableField<String> stringField = new TrackableField<String>();
        
        ValueConverter intToStringConverter = new ValueConverter() {
            @Override
            public Object convertToTarget(Object sourceValue, Class<?> targetType) {
                return sourceValue != null ? String.valueOf(sourceValue) : null;
            }
        };
        
        new Binding(createStringProperty(stringField), createIntProperty(intField), BindingMode.ONE_WAY, intToStringConverter);
        
        assertEquals("42", stringField.get());
        
        intField.set(100);
        assertEquals("100", stringField.get());
    }

    @Test
    public void testBooleanToVisibilityBinding() {
        TrackableField<Boolean> boolField = new TrackableField<Boolean>(true);
        TrackableField<Integer> visibilityField = new TrackableField<Integer>();
        
        final int VISIBLE = 0;
        final int GONE = 8;
        
        ValueConverter boolToVisibilityConverter = new ValueConverter() {
            @Override
            public Object convertToTarget(Object sourceValue, Class<?> targetType) {
                if (sourceValue instanceof Boolean) {
                    return ((Boolean) sourceValue) ? VISIBLE : GONE;
                }
                return GONE;
            }
        };
        
        new Binding(createIntProperty(visibilityField), createBoolProperty(boolField), BindingMode.ONE_WAY, boolToVisibilityConverter);
        
        assertEquals(Integer.valueOf(VISIBLE), visibilityField.get());
        
        boolField.set(false);
        assertEquals(Integer.valueOf(GONE), visibilityField.get());
    }

    // ==================== Helper Methods ====================

    private Property<Object> createProperty(final TrackableField<Object> field) {
        return new Property<Object>(
            new Function<Object>() {
                @Override
                public Object evaluate() {
                    return field.get();
                }
            },
            new Action<Object>() {
                @Override
                public void invoke(Object parameter) {
                    field.set(parameter);
                }
            },
            Object.class
        );
    }

    private Property<String> createStringProperty(final TrackableField<String> field) {
        return new Property<String>(
            new Function<String>() {
                @Override
                public String evaluate() {
                    return field.get();
                }
            },
            new Action<String>() {
                @Override
                public void invoke(String parameter) {
                    field.set(parameter);
                }
            },
            String.class
        );
    }

    private Property<Integer> createIntProperty(final TrackableField<Integer> field) {
        return new Property<Integer>(
            new Function<Integer>() {
                @Override
                public Integer evaluate() {
                    return field.get();
                }
            },
            new Action<Integer>() {
                @Override
                public void invoke(Integer parameter) {
                    field.set(parameter);
                }
            },
            Integer.class
        );
    }

    private Property<Boolean> createBoolProperty(final TrackableField<Boolean> field) {
        return new Property<Boolean>(
            new Function<Boolean>() {
                @Override
                public Boolean evaluate() {
                    return field.get();
                }
            },
            new Action<Boolean>() {
                @Override
                public void invoke(Boolean parameter) {
                    field.set(parameter);
                }
            },
            Boolean.class
        );
    }
}
