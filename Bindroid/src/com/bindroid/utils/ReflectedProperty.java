package com.bindroid.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a property by reflecting down a property path. ReflectedProperty assumes that a pair of
 * methods named "get{Name}" and "set{Name}" represent a property called "{Name}".
 * <p>
 * Reflected properties also support complex property paths that include list or map indexes or
 * nested properties. For example a property path of "Foo.Bar[baz].Bat[1]" is equivalent to calling
 * <code>getFoo().getBar().get("baz").getBat().get(1)</code>.
 */
public class ReflectedProperty extends Property<Object> {
    private static class IndexerPathPart extends PathPart {
        private String indexString;
        private Integer index;

        public IndexerPathPart(String index) {
            this.indexString = index;
            try {
                this.index = Integer.parseInt(index.trim());
            } catch (Exception e) {
            }
        }

        @Override
        public Class<?> getType(Object root) throws Exception {
            return Object.class;
        }

        @Override
        public Object getValue(Object root) throws Exception {
            if (this.index != null) {
                if (root instanceof List) {
                    return ((List<?>) root).get(this.index);
                }
                try {
                    if (root instanceof Map && ((Map<?, ?>) root).containsKey(this.index)) {
                        return ((Map<?, ?>) root).get(this.index);
                    }
                } catch (Exception e) {
                }
            }
            if (root instanceof Map && ((Map<?, ?>) root).containsKey(this.indexString)) {
                return ((Map<?, ?>) root).get(this.indexString);
            }
            throw new Exception();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setValue(Object root, Object value) throws Exception {
            if (this.index != null) {
                if (root instanceof List) {
                    ((List<Object>) root).set(this.index, value);
                    return;
                }
                try {
                    if (root instanceof Map && !((Map<?, ?>) root).containsKey(this.indexString)) {
                        ((Map<Integer, Object>) root).put(this.index, value);
                        return;
                    }
                } catch (Exception e) {
                }
            }
            if (root instanceof Map) {
                ((Map<String, Object>) root).put(this.indexString, value);
                return;
            }
            throw new Exception();
        }
    }

    private static abstract class PathPart {
        public abstract Class<?> getType(Object root) throws Exception;

        public abstract Object getValue(Object root) throws Exception;

        public abstract void setValue(Object root, Object value) throws Exception;
    }

    private static Method getMethodOrNull(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static Method getGetter(Class<?> clazz, String propertyName) {
        String getterName = "get" + capitalize(propertyName);
        Pair<Class<?>, String> id = new Pair<Class<?>, String>(clazz, getterName);
        Method getter = null;
        if (knownGetters.containsKey(id)) {
            getter = knownGetters.get(id);
        } else {
            getter = getMethodOrNull(clazz, getterName);
            if (getter == null && propertyName.startsWith("Is")) {
                // Try the "Is" form"
                getter = getMethodOrNull(clazz, propertyName.replaceFirst("Is", "is"));
            }
            knownGetters.put(id, getter);
        }
        return getter;
    }

    private static Method getSetter(Class<?> clazz, String propertyName) {
        Method setter = null;
        String setterName = "set" + capitalize(propertyName);
        Pair<Class<?>, String> setterId = new Pair<Class<?>, String>(clazz, setterName);
        if (ReflectedProperty.knownSetters.containsKey(setterId)) {
            setter = ReflectedProperty.knownSetters.get(setterId);
        } else {
            Method getter = getGetter(clazz, propertyName);
            if (getter != null) {
                setter = getMethodOrNull(clazz, setterName, 1, getter.getReturnType());
                if (setter == null && propertyName.startsWith("Is")) {
                    // Try the setter without the "Is" prefix
                    String noIsPropertyName = propertyName.replaceFirst("Is", "set");
                    setter = getMethodOrNull(clazz, noIsPropertyName, 1, getter.getReturnType());
                }
            }
            if (setter == null) {
                setter = getMethodOrNull(clazz, setterName, 1);
            }
            if (setter == null && propertyName.startsWith("Is")) {
                // Try the setter without the "Is" prefix
                String noIsPropertyName = propertyName.replaceFirst("Is", "set");
                setter = getMethodOrNull(clazz, noIsPropertyName, 1);
            }
            knownSetters.put(setterId, setter);
        }
        return setter;
    }

    private static class PropertyPathPart extends PathPart {
        private String propertyName;

        public PropertyPathPart(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public Class<?> getType(Object root) throws Exception {
            return getGetter(root.getClass(), propertyName).getReturnType();
        }

        @Override
        public Object getValue(Object root) throws Exception {
            return getGetter(root.getClass(), propertyName).invoke(root);
        }

        @Override
        public void setValue(Object root, Object value) throws Exception {
            getSetter(root.getClass(), propertyName).invoke(root, value);
        }
    }

    private static Map<Pair<Class<?>, String>, Method> knownGetters;

    private static Map<Pair<Class<?>, String>, Method> knownSetters;

    static {
        ReflectedProperty.knownGetters = new HashMap<Pair<Class<?>, String>, Method>();
        ReflectedProperty.knownSetters = new HashMap<Pair<Class<?>, String>, Method>();
        ReflectedProperty.knownPaths = new HashMap<String, PathPart[]>();
    }

    private static final Pattern pathPartPattern = Pattern
            .compile("(?:(?:^|\\.)([a-zA-Z0-9_]+))|(?:\\[([^]]+?)\\])");

    private static Map<String, PathPart[]> knownPaths;

    private static Method getMethodOrNull(Class<?> type, String name, int parameterCount, Class<?>... hints) {
        try {
            for (Method m : type.getMethods()) {
                if (m.getName().equals(name) && m.getParameterTypes().length == parameterCount) {
                    boolean matches = true;
                    for (int x = 0; x < hints.length; x++) {
                        if (hints[x] != null && !m.getParameterTypes()[x].equals(hints[x])) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        return m;
                    }
                }
            }
        } catch (SecurityException e) {
        }
        return null;
    }

    private static PathPart[] getPathParts(String path) {
        if (ReflectedProperty.knownPaths.containsKey(path)) {
            return ReflectedProperty.knownPaths.get(path);
        }
        Matcher m = ReflectedProperty.pathPartPattern.matcher(path);
        List<PathPart> parts = new ArrayList<PathPart>();
        while (m.find()) {
            String propertyPart = m.group(1);
            String indexerPart = m.group(2);
            if (indexerPart != null) {
                parts.add(new IndexerPathPart(indexerPart));
            } else {
                parts.add(new PropertyPathPart(propertyPart));
            }
            if (m.end() == path.length()) {
                PathPart[] realParts = parts.toArray(new PathPart[parts.size()]);
                ReflectedProperty.knownPaths.put(path, realParts);
                return realParts;
            }
        }
        throw new IllegalArgumentException("Invalid property path");
    }

    private Object source;
    private PathPart[] parts;

    /**
     * Constructs a reflected property using the path provided, starting from the source.
     *
     * @param source the starting point for the property.
     * @param path   the property path that this property will represent for the source.
     */
    public ReflectedProperty(Object source, String path) {
        this.source = source;
        this.parts = ReflectedProperty.getPathParts(path);
        this.getter = new Function<Object>() {
            @Override
            public Object evaluate() {
                Object current = ReflectedProperty.this.getSource();
                for (int x = 0; x < ReflectedProperty.this.parts.length; x++) {
                    PathPart curPart = ReflectedProperty.this.parts[x];
                    try {
                        current = curPart.getValue(current);
                    } catch (Exception e) {
                        return null;
                    }
                }
                return current;
            }
        };
        this.setter = new Action<Object>() {
            @Override
            public void invoke(Object parameter) {
                Object current = ReflectedProperty.this.getSource();
                for (int x = 0; x < ReflectedProperty.this.parts.length - 1; x++) {
                    PathPart curPart = ReflectedProperty.this.parts[x];
                    try {
                        current = curPart.getValue(current);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                PathPart lastPart = ReflectedProperty.this.parts[ReflectedProperty.this.parts.length - 1];
                try {
                    lastPart.setValue(current, parameter);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    protected Object getSource() {
        return this.source;
    }

    @Override
    public Class<?> getType() {
        Object current = this.getSource();
        for (int x = 0; x < this.parts.length - 1; x++) {
            PathPart curPart = this.parts[x];
            try {
                current = curPart.getValue(current);
            } catch (Exception e) {
                return Object.class;
            }
        }
        try {
            PathPart lastPart = this.parts[this.parts.length - 1];
            return lastPart.getType(current);
        } catch (Exception e) {
            return Object.class;
        }
    }
}
