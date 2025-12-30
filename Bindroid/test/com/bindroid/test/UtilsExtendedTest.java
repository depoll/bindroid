package com.bindroid.test;

import com.bindroid.utils.Action;
import com.bindroid.utils.EqualityComparer;
import com.bindroid.utils.Function;
import com.bindroid.utils.ObjectUtilities;
import com.bindroid.utils.Pair;
import com.bindroid.utils.Property;
import com.bindroid.utils.ReflectedProperty;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Extended unit tests for utility classes in the utils package.
 */
public class UtilsExtendedTest {

    // ==================== ObjectUtilities Tests ====================

    @Test
    public void testEqualsWithBothNull() {
        assertTrue(ObjectUtilities.equals(null, null));
    }

    @Test
    public void testEqualsWithFirstNull() {
        assertFalse(ObjectUtilities.equals(null, "value"));
    }

    @Test
    public void testEqualsWithSecondNull() {
        assertFalse(ObjectUtilities.equals("value", null));
    }

    @Test
    public void testEqualsWithSameObject() {
        String obj = "test";
        assertTrue(ObjectUtilities.equals(obj, obj));
    }

    @Test
    public void testEqualsWithEqualObjects() {
        assertTrue(ObjectUtilities.equals("test", "test"));
        assertTrue(ObjectUtilities.equals(Integer.valueOf(42), Integer.valueOf(42)));
    }

    @Test
    public void testEqualsWithDifferentObjects() {
        assertFalse(ObjectUtilities.equals("test1", "test2"));
        assertFalse(ObjectUtilities.equals(Integer.valueOf(42), Integer.valueOf(43)));
    }

    @Test
    public void testEqualsWithExceptionThrowingEquals() {
        Object badObject = new Object() {
            @Override
            public boolean equals(Object obj) {
                throw new RuntimeException("Bad equals");
            }
        };
        
        // Should return false instead of throwing
        assertFalse(ObjectUtilities.equals(badObject, "anything"));
    }

    @Test
    public void testGetDefaultComparer() {
        EqualityComparer<Object> comparer = ObjectUtilities.getDefaultComparer();
        assertNotNull(comparer);
        
        assertTrue(comparer.equals(null, null));
        assertTrue(comparer.equals("test", "test"));
        assertFalse(comparer.equals("test", "other"));
        assertFalse(comparer.equals(null, "test"));
    }

    // ==================== Pair Tests ====================

    @Test
    public void testPairCreation() {
        Pair<String, Integer> pair = new Pair<String, Integer>("key", 42);
        assertEquals("key", pair.first);
        assertEquals(Integer.valueOf(42), pair.second);
    }

    @Test
    public void testPairWithNullValues() {
        Pair<String, String> pair = new Pair<String, String>(null, null);
        assertNull(pair.first);
        assertNull(pair.second);
    }

    @Test
    public void testPairEquality() {
        Pair<String, Integer> pair1 = new Pair<String, Integer>("key", 42);
        Pair<String, Integer> pair2 = new Pair<String, Integer>("key", 42);
        Pair<String, Integer> pair3 = new Pair<String, Integer>("other", 42);
        
        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
    }

    @Test
    public void testPairHashCode() {
        Pair<String, Integer> pair1 = new Pair<String, Integer>("key", 42);
        Pair<String, Integer> pair2 = new Pair<String, Integer>("key", 42);
        
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }

    // ==================== Property Tests ====================

    @Test
    public void testPropertyWithGetterAndSetter() {
        final AtomicReference<String> value = new AtomicReference<String>("initial");
        
        Property<String> prop = new Property<String>(
            new Function<String>() {
                @Override
                public String evaluate() {
                    return value.get();
                }
            },
            new Action<String>() {
                @Override
                public void invoke(String parameter) {
                    value.set(parameter);
                }
            },
            String.class
        );
        
        assertEquals("initial", prop.getValue());
        prop.setValue("updated");
        assertEquals("updated", prop.getValue());
        assertEquals(String.class, prop.getType());
    }

    @Test
    public void testPropertyGetterOnly() {
        Property<String> prop = new Property<String>(
            new Function<String>() {
                @Override
                public String evaluate() {
                    return "constant";
                }
            },
            null
        );
        
        assertEquals("constant", prop.getValue());
        assertNull(prop.getSetter());
    }

    @Test
    public void testPropertySetterOnly() {
        final AtomicReference<String> value = new AtomicReference<String>();
        
        Property<String> prop = new Property<String>(
            null,
            new Action<String>() {
                @Override
                public void invoke(String parameter) {
                    value.set(parameter);
                }
            }
        );
        
        assertNull(prop.getGetter());
        prop.setValue("test");
        assertEquals("test", value.get());
    }

    @Test
    public void testPropertyDefaultType() {
        Property<String> prop = new Property<String>(
            new Function<String>() {
                @Override
                public String evaluate() {
                    return "test";
                }
            },
            null
        );
        
        assertEquals(Object.class, prop.getType());
    }

    // ==================== ReflectedProperty Extended Tests ====================

    @Test
    public void testReflectedPropertySimpleGet() {
        TestBean bean = new TestBean();
        bean.setName("test");
        
        ReflectedProperty prop = new ReflectedProperty(bean, "Name");
        assertEquals("test", prop.getValue());
    }

    @Test
    public void testReflectedPropertySimpleSet() {
        TestBean bean = new TestBean();
        
        ReflectedProperty prop = new ReflectedProperty(bean, "Name");
        prop.setValue("updated");
        assertEquals("updated", bean.getName());
    }

    @Test
    public void testReflectedPropertyWithListIndex() {
        List<String> list = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        
        ReflectedProperty prop = new ReflectedProperty(list, "[1]");
        assertEquals("b", prop.getValue());
        
        prop.setValue("updated");
        assertEquals("updated", list.get(1));
    }

    @Test
    public void testReflectedPropertyWithMapStringKey() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one", 1);
        map.put("two", 2);
        
        ReflectedProperty prop = new ReflectedProperty(map, "[one]");
        assertEquals(1, prop.getValue());
        
        prop.setValue(10);
        assertEquals(Integer.valueOf(10), map.get("one"));
    }

    @Test
    public void testReflectedPropertyWithMapIntegerKey() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "one");
        map.put(2, "two");
        
        ReflectedProperty prop = new ReflectedProperty(map, "[1]");
        assertEquals("one", prop.getValue());
        
        prop.setValue("updated");
        assertEquals("updated", map.get(1));
    }

    @Test
    public void testReflectedPropertyNestedPath() {
        TestBean parent = new TestBean();
        TestBean child = new TestBean();
        child.setName("child name");
        parent.setChild(child);
        
        ReflectedProperty prop = new ReflectedProperty(parent, "Child.Name");
        assertEquals("child name", prop.getValue());
        
        prop.setValue("updated name");
        assertEquals("updated name", child.getName());
    }

    @Test
    public void testReflectedPropertyDeeplyNestedPath() {
        TestBean root = new TestBean();
        TestBean level1 = new TestBean();
        TestBean level2 = new TestBean();
        level2.setName("deep value");
        level1.setChild(level2);
        root.setChild(level1);
        
        ReflectedProperty prop = new ReflectedProperty(root, "Child.Child.Name");
        assertEquals("deep value", prop.getValue());
    }

    @Test
    public void testReflectedPropertyMixedPath() {
        TestBean bean = new TestBean();
        bean.setItems(new ArrayList<String>(Arrays.asList("a", "b", "c")));
        
        ReflectedProperty prop = new ReflectedProperty(bean, "Items[1]");
        assertEquals("b", prop.getValue());
    }

    @Test
    public void testReflectedPropertyWithMapAndNestedObject() {
        TestBean bean = new TestBean();
        Map<String, TestBean> map = new HashMap<String, TestBean>();
        TestBean nested = new TestBean();
        nested.setName("nested name");
        map.put("key", nested);
        bean.setMap(map);
        
        ReflectedProperty prop = new ReflectedProperty(bean, "Map[key].Name");
        assertEquals("nested name", prop.getValue());
    }

    @Test
    public void testReflectedPropertyBooleanIsGetter() {
        TestBean bean = new TestBean();
        bean.setEnabled(true);
        
        ReflectedProperty prop = new ReflectedProperty(bean, "IsEnabled");
        assertEquals(true, prop.getValue());
        
        prop.setValue(false);
        assertEquals(false, bean.isEnabled());
    }

    @Test
    public void testReflectedPropertyNullValue() {
        TestBean bean = new TestBean();
        bean.setName(null);
        
        ReflectedProperty prop = new ReflectedProperty(bean, "Name");
        assertNull(prop.getValue());
        
        prop.setValue("not null");
        assertEquals("not null", prop.getValue());
    }

    @Test
    public void testReflectedPropertyType() {
        TestBean bean = new TestBean();
        
        ReflectedProperty nameProp = new ReflectedProperty(bean, "Name");
        assertEquals(String.class, nameProp.getType());
        
        ReflectedProperty enabledProp = new ReflectedProperty(bean, "IsEnabled");
        assertEquals(boolean.class, enabledProp.getType());
    }

    // ==================== Function Tests ====================

    @Test
    public void testFunctionEvaluate() {
        Function<Integer> func = new Function<Integer>() {
            @Override
            public Integer evaluate() {
                return 42;
            }
        };
        
        assertEquals(Integer.valueOf(42), func.evaluate());
    }

    @Test
    public void testFunctionWithCapture() {
        final AtomicReference<String> captured = new AtomicReference<String>("captured");
        
        Function<String> func = new Function<String>() {
            @Override
            public String evaluate() {
                return captured.get();
            }
        };
        
        assertEquals("captured", func.evaluate());
        captured.set("updated");
        assertEquals("updated", func.evaluate());
    }

    // ==================== Action Tests ====================

    @Test
    public void testActionInvoke() {
        final AtomicReference<String> result = new AtomicReference<String>();
        
        Action<String> action = new Action<String>() {
            @Override
            public void invoke(String parameter) {
                result.set(parameter);
            }
        };
        
        action.invoke("test");
        assertEquals("test", result.get());
    }

    @Test
    public void testActionWithNullParameter() {
        final AtomicReference<String> result = new AtomicReference<String>("initial");
        
        Action<String> action = new Action<String>() {
            @Override
            public void invoke(String parameter) {
                result.set(parameter);
            }
        };
        
        action.invoke(null);
        assertNull(result.get());
    }

    // ==================== Helper Classes ====================

    public static class TestBean {
        private String name;
        private TestBean child;
        private List<String> items;
        private Map<String, TestBean> map;
        private boolean enabled;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TestBean getChild() {
            return child;
        }

        public void setChild(TestBean child) {
            this.child = child;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }

        public Map<String, TestBean> getMap() {
            return map;
        }

        public void setMap(Map<String, TestBean> map) {
            this.map = map;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
