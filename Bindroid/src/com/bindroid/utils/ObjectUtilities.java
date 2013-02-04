package com.bindroid.utils;

public class ObjectUtilities {
  private static EqualityComparer<Object> defaultComparer;

  static {
    ObjectUtilities.defaultComparer = new EqualityComparer<Object>() {
      @Override
      public boolean equals(Object obj1, Object obj2) {
        return ObjectUtilities.equals(obj1, obj2);
      }
    };
  }

  public static boolean equals(Object obj1, Object obj2) {
    try {
      return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    } catch (Exception e) {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> EqualityComparer<T> getDefaultComparer() {
    return (EqualityComparer<T>) ObjectUtilities.defaultComparer;
  }

  private ObjectUtilities() {
  }
}
