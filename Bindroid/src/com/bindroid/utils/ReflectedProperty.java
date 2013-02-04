package com.bindroid.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  private static class PropertyPathPart extends PathPart {
    private String propertyName;

    public PropertyPathPart(String propertyName) {
      this.propertyName = propertyName;
    }

    @Override
    public Class<?> getType(Object root) throws Exception {
      String getterName = "get" + this.propertyName;
      Pair<Class<?>, String> id = new Pair<Class<?>, String>(root.getClass(), getterName);
      Method getter;
      if (ReflectedProperty.knownGetters.containsKey(id)) {
        getter = ReflectedProperty.knownGetters.get(id);
      } else {
        getter = root.getClass().getMethod(getterName);
        ReflectedProperty.knownGetters.put(id, getter);
      }
      return getter.getReturnType();
    }

    @Override
    public Object getValue(Object root) throws Exception {
      String getterName = "get" + this.propertyName;
      Pair<Class<?>, String> id = new Pair<Class<?>, String>(root.getClass(), getterName);
      Method getter;
      if (ReflectedProperty.knownGetters.containsKey(id)) {
        getter = ReflectedProperty.knownGetters.get(id);
      } else {
        getter = root.getClass().getMethod(getterName);
        ReflectedProperty.knownGetters.put(id, getter);
      }
      return getter.invoke(root);
    }

    @Override
    public void setValue(Object root, Object value) throws Exception {
      Method setter = null;
      String setterName = "set" + this.propertyName;
      Pair<Class<?>, String> setterId = new Pair<Class<?>, String>(root.getClass(), setterName);
      if (ReflectedProperty.knownSetters.containsKey(setterId)) {
        setter = ReflectedProperty.knownSetters.get(setterId);
      } else {
        try {
          String getterName = "get" + this.propertyName;
          Pair<Class<?>, String> getterId = new Pair<Class<?>, String>(root.getClass(), getterName);
          Method getter;
          if (ReflectedProperty.knownGetters.containsKey(getterId)) {
            getter = ReflectedProperty.knownGetters.get(getterId);
          } else {
            getter = root.getClass().getMethod(getterName);
            ReflectedProperty.knownGetters.put(getterId, getter);
          }
          setter = ReflectedProperty.getMethod(root.getClass(), setterName, 1,
              getter.getReturnType());
        } catch (Exception e) {
        }
        if (setter == null) {
          setter = ReflectedProperty.getMethod(root.getClass(), setterName, 1);
        }
        ReflectedProperty.knownSetters.put(setterId, setter);
      }
      setter.invoke(root, value);
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

  private static Method
      getMethod(Class<?> type, String name, int parameterCount, Class<?>... hints)
          throws NoSuchMethodException, SecurityException {
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
    throw new NoSuchMethodException("Cannot find a method called " + name + " on type " + type
        + " with " + parameterCount + " parameters.");
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

  public ReflectedProperty(Object source, String path) {
    this.source = source;
    this.parts = ReflectedProperty.getPathParts(path);
    this.getter = new Function<Object>() {
      @Override
      public Object evaluate() {
        Object current = getSource();
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
        Object current = getSource();
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

  @Override
  public Class<?> getType() {
    Object current = getSource();
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

  protected Object getSource() {
    return source;
  }
}
