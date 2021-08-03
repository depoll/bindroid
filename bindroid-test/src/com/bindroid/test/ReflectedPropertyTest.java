package com.bindroid.test;

import android.annotation.SuppressLint;

import com.bindroid.trackable.TrackableBoolean;
import com.bindroid.utils.ReflectedProperty;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectedPropertyTest extends TestCase {
    private String prop;
    private Map<String, List<Nestable>> weirdMap;
    private boolean enabled;
    private boolean enabled2;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public boolean isEnabled2() {
        return enabled2;
    }

    public void setIsEnabled2(boolean value) {
        enabled2 = value;
    }

    public void setProp(String value) {
        prop = value;
    }

    public String getProp() {
        return prop;
    }

    public String getReadOnlyProp() {
        return prop;
    }

    public Map<String, List<Nestable>> getWeirdMap() {
        return weirdMap;
    }

    public void setWriteOnlyProp(String value) {
        prop = value;
    }

    public void testReadWriteReflectedProperty() {
        setProp("Hello");
        ReflectedProperty property = new ReflectedProperty(this, "Prop");
        assertEquals("Hello", property.getValue());
        property.setValue("Goodbye");
        assertEquals("Goodbye", getProp());
        assertEquals("Goodbye", property.getValue());
    }

    public void testIsNamedReflectedProperty() {
        setEnabled(false);
        setIsEnabled2(false);
        ReflectedProperty prop = new ReflectedProperty(this, "IsEnabled");
        assertEquals(false, prop.getValue());
        prop.setValue(true);
        assertEquals(true, prop.getValue());
        ReflectedProperty prop2 = new ReflectedProperty(this, "IsEnabled2");
        assertEquals(false, prop2.getValue());
        prop2.setValue(true);
        assertEquals(true, prop2.getValue());
    }

    public void testReadOnlyReflectedProperty() {
        setProp("Hello");
        ReflectedProperty property = new ReflectedProperty(this, "ReadOnlyProp");
        assertEquals("Hello", property.getValue());
        try {
            property.setValue("Goodbye");
            fail();
        } catch (Exception e) {
        }
    }

    public void testWriteOnlyReflectedProperty() {
        setProp("Hello");
        ReflectedProperty property = new ReflectedProperty(this, "WriteOnlyProp");
        assertNull(property.getValue());
        property.setValue("Goodbye");
        assertEquals("Goodbye", getProp());
    }

    public void testReflectedListIndex() {
        List<String> list = new ArrayList<String>(Arrays.asList("Hello", "Goodbye"));
        ReflectedProperty property = new ReflectedProperty(list, "[0]");
        assertEquals("Hello", property.getValue());
        property.setValue("Bonjour");
        assertEquals("Bonjour", list.get(0));
        assertEquals("Bonjour", property.getValue());
        property = new ReflectedProperty(list, "[1]");
        assertEquals("Goodbye", property.getValue());
        property.setValue("Au Revoir");
        assertEquals("Au Revoir", list.get(1));
        assertEquals("Au Revoir", property.getValue());
    }

    public void testReflectedStringMapIndex() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("One", 1);
        map.put("Two", 2);
        ReflectedProperty property = new ReflectedProperty(map, "[One]");
        assertEquals(1, property.getValue());
        property.setValue(-1);
        assertEquals(-1, map.get("One").intValue());
        assertEquals(-1, property.getValue());
        property = new ReflectedProperty(map, "[Three]");
        assertNull(property.getValue());
        property.setValue(3);
        assertEquals(3, map.get("Three").intValue());
        assertEquals(3, property.getValue());
    }

    @SuppressLint("UseSparseArrays")
    public void testReflectedIntegerMapIndex() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 1);
        map.put(2, 2);
        ReflectedProperty property = new ReflectedProperty(map, "[1]");
        assertEquals(1, property.getValue());
        property.setValue(-1);
        assertEquals(-1, map.get(1).intValue());
        assertEquals(-1, property.getValue());
        property = new ReflectedProperty(map, "[3]");
        assertNull(property.getValue());
        property.setValue(3);
        assertEquals(3, map.get(3).intValue());
        assertEquals(3, property.getValue());
    }

    public void testNestedPaths() {
        weirdMap = new HashMap<String, List<Nestable>>();
        weirdMap.put("list", Arrays.asList((Nestable) new Nestable() {
            {
                Nestable child = new Nestable();
                child.setValue("Whoa");
                this.setChild(child);
            }
        }));
        ReflectedProperty property = new ReflectedProperty(this, "WeirdMap[list][0].Child.Value");
        assertEquals("Whoa", property.getValue());
        property.setValue("No way!");
        assertEquals("No way!", weirdMap.get("list").get(0).getChild().getValue());
        assertEquals("No way!", property.getValue());
    }
}
