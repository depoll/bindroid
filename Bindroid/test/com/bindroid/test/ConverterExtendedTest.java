package com.bindroid.test;

import com.bindroid.ValueConverter;
import com.bindroid.converters.BoolConverter;
import com.bindroid.converters.ToStringConverter;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Extended unit tests for converters.
 */
public class ConverterExtendedTest {

    // ==================== ValueConverter Tests ====================

    @Test
    public void testDefaultConverterIsSingleton() {
        ValueConverter c1 = ValueConverter.getDefaultConverter();
        ValueConverter c2 = ValueConverter.getDefaultConverter();
        assertSame(c1, c2);
    }

    @Test
    public void testDefaultConverterPassthrough() {
        ValueConverter converter = ValueConverter.getDefaultConverter();
        
        String str = "test";
        assertEquals(str, converter.convertToTarget(str, String.class));
        assertEquals(str, converter.convertToSource(str, String.class));
        
        Integer num = 42;
        assertEquals(num, converter.convertToTarget(num, Integer.class));
        assertEquals(num, converter.convertToSource(num, Integer.class));
    }

    @Test
    public void testDefaultConverterWithNull() {
        ValueConverter converter = ValueConverter.getDefaultConverter();
        
        assertNull(converter.convertToTarget(null, Object.class));
        assertNull(converter.convertToSource(null, Object.class));
    }

    // ==================== BoolConverter Tests ====================

    @Test
    public void testBoolConverterNullIsFalse() {
        ValueConverter converter = BoolConverter.get();
        assertEquals(Boolean.FALSE, converter.convertToTarget(null, Boolean.class));
    }

    @Test
    public void testBoolConverterZeroIsFalse() {
        ValueConverter converter = BoolConverter.get();
        assertEquals(Boolean.FALSE, converter.convertToTarget(0, Boolean.class));
        assertEquals(Boolean.FALSE, converter.convertToTarget(Integer.valueOf(0), Boolean.class));
    }

    @Test
    public void testBoolConverterNonZeroIsTrue() {
        ValueConverter converter = BoolConverter.get();
        assertEquals(Boolean.TRUE, converter.convertToTarget(1, Boolean.class));
        assertEquals(Boolean.TRUE, converter.convertToTarget(-1, Boolean.class));
        assertEquals(Boolean.TRUE, converter.convertToTarget(100, Boolean.class));
    }

    @Test
    public void testBoolConverterFalseIsFalse() {
        ValueConverter converter = BoolConverter.get();
        assertEquals(Boolean.FALSE, converter.convertToTarget(false, Boolean.class));
        assertEquals(Boolean.FALSE, converter.convertToTarget(Boolean.FALSE, Boolean.class));
    }

    @Test
    public void testBoolConverterTrueIsTrue() {
        ValueConverter converter = BoolConverter.get();
        assertEquals(Boolean.TRUE, converter.convertToTarget(true, Boolean.class));
        assertEquals(Boolean.TRUE, converter.convertToTarget(Boolean.TRUE, Boolean.class));
    }

    @Test
    public void testBoolConverterNonNullObjectIsTrue() {
        ValueConverter converter = BoolConverter.get();
        assertEquals(Boolean.TRUE, converter.convertToTarget("any string", Boolean.class));
        assertEquals(Boolean.TRUE, converter.convertToTarget(new Object(), Boolean.class));
    }

    @Test
    public void testBoolConverterInverted() {
        ValueConverter converter = BoolConverter.get(true);
        
        assertEquals(Boolean.TRUE, converter.convertToTarget(null, Boolean.class));
        assertEquals(Boolean.TRUE, converter.convertToTarget(0, Boolean.class));
        assertEquals(Boolean.TRUE, converter.convertToTarget(false, Boolean.class));
        assertEquals(Boolean.FALSE, converter.convertToTarget(1, Boolean.class));
        assertEquals(Boolean.FALSE, converter.convertToTarget(true, Boolean.class));
        assertEquals(Boolean.FALSE, converter.convertToTarget("string", Boolean.class));
    }

    @Test
    public void testBoolConverterZeroLengthListIsFalse() {
        ValueConverter converter = BoolConverter.get(false, true);
        
        List<String> emptyList = new ArrayList<String>();
        assertEquals(Boolean.FALSE, converter.convertToTarget(emptyList, Boolean.class));
        
        List<String> nonEmptyList = Arrays.asList("item");
        assertEquals(Boolean.TRUE, converter.convertToTarget(nonEmptyList, Boolean.class));
    }

    @Test
    public void testBoolConverterInvertedZeroLengthListIsFalse() {
        ValueConverter converter = BoolConverter.get(true, true);
        
        List<String> emptyList = new ArrayList<String>();
        assertEquals(Boolean.TRUE, converter.convertToTarget(emptyList, Boolean.class));
        
        List<String> nonEmptyList = Arrays.asList("item");
        assertEquals(Boolean.FALSE, converter.convertToTarget(nonEmptyList, Boolean.class));
    }

    @Test
    public void testBoolConverterGetIsCached() {
        ValueConverter normal1 = BoolConverter.get();
        ValueConverter normal2 = BoolConverter.get();
        assertSame(normal1, normal2);
        
        ValueConverter inverted1 = BoolConverter.get(true);
        ValueConverter inverted2 = BoolConverter.get(true);
        assertSame(inverted1, inverted2);
        
        ValueConverter zeroList1 = BoolConverter.get(false, true);
        ValueConverter zeroList2 = BoolConverter.get(false, true);
        assertSame(zeroList1, zeroList2);
    }

    @Test
    public void testBoolConverterWithEmptyString() {
        // Empty string is still a non-null object
        ValueConverter converter = BoolConverter.get();
        assertEquals(Boolean.TRUE, converter.convertToTarget("", Boolean.class));
    }

    // ==================== ToStringConverter Tests ====================

    @Test
    public void testToStringConverterDefault() {
        ToStringConverter converter = new ToStringConverter();
        
        assertEquals("42", converter.convertToTarget(42, String.class));
        assertEquals("true", converter.convertToTarget(true, String.class));
        assertEquals("hello", converter.convertToTarget("hello", String.class));
    }

    @Test
    public void testToStringConverterWithNull() {
        ToStringConverter converter = new ToStringConverter();
        assertEquals("null", converter.convertToTarget(null, String.class));
    }

    @Test
    public void testToStringConverterWithFormat() {
        ToStringConverter converter = new ToStringConverter("Value: %s");
        
        assertEquals("Value: 42", converter.convertToTarget(42, String.class));
        assertEquals("Value: hello", converter.convertToTarget("hello", String.class));
    }

    @Test
    public void testToStringConverterWithNumberFormat() {
        ToStringConverter converter = new ToStringConverter("Number: %d");
        assertEquals("Number: 42", converter.convertToTarget(42, String.class));
    }

    @Test
    public void testToStringConverterWithFloatFormat() {
        ToStringConverter converter = new ToStringConverter("Price: $%.2f");
        assertEquals("Price: $19.99", converter.convertToTarget(19.99, String.class));
    }

    @Test
    public void testToStringConverterWithPaddedFormat() {
        ToStringConverter converter = new ToStringConverter("ID: %05d");
        assertEquals("ID: 00042", converter.convertToTarget(42, String.class));
    }

    @Test
    public void testToStringConverterWithCustomObject() {
        ToStringConverter converter = new ToStringConverter();
        
        Object obj = new Object() {
            @Override
            public String toString() {
                return "custom toString";
            }
        };
        
        assertEquals("custom toString", converter.convertToTarget(obj, String.class));
    }

    // ==================== Custom Converter Tests ====================

    @Test
    public void testCustomBidirectionalConverter() {
        ValueConverter converter = new ValueConverter() {
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
        
        assertEquals(20, converter.convertToTarget(10, Integer.class));
        assertEquals(10, converter.convertToSource(20, Integer.class));
    }

    @Test
    public void testCustomNullSafeConverter() {
        ValueConverter converter = new ValueConverter() {
            @Override
            public Object convertToTarget(Object sourceValue, Class<?> targetType) {
                if (sourceValue == null) {
                    return "N/A";
                }
                return sourceValue.toString();
            }
        };
        
        assertEquals("N/A", converter.convertToTarget(null, String.class));
        assertEquals("test", converter.convertToTarget("test", String.class));
    }

    @Test
    public void testCustomTypeCheckingConverter() {
        ValueConverter converter = new ValueConverter() {
            @Override
            public Object convertToTarget(Object sourceValue, Class<?> targetType) {
                if (targetType == String.class) {
                    return String.valueOf(sourceValue);
                } else if (targetType == Integer.class) {
                    if (sourceValue instanceof String) {
                        return Integer.parseInt((String) sourceValue);
                    }
                    return sourceValue;
                }
                return sourceValue;
            }
        };
        
        assertEquals("42", converter.convertToTarget(42, String.class));
        assertEquals(42, converter.convertToTarget("42", Integer.class));
    }

    // ==================== Edge Case Tests ====================

    @Test
    public void testBoolConverterWithCollections() {
        ValueConverter converter = BoolConverter.get(false, true);
        
        // Empty collections
        assertEquals(Boolean.FALSE, converter.convertToTarget(new ArrayList<Object>(), Boolean.class));
        assertEquals(Boolean.FALSE, converter.convertToTarget(Collections.emptyList(), Boolean.class));
        
        // Non-empty collections
        assertEquals(Boolean.TRUE, converter.convertToTarget(Arrays.asList(1, 2, 3), Boolean.class));
        assertEquals(Boolean.TRUE, converter.convertToTarget(Collections.singletonList("item"), Boolean.class));
    }

    @Test
    public void testToStringConverterChaining() {
        ToStringConverter upper = new ToStringConverter("%S"); // %S for uppercase
        
        assertEquals("HELLO", upper.convertToTarget("hello", String.class));
    }
}
