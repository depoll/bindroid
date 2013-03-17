package com.bindroid.utils;

/**
 * Provides utilities for comparing objects.
 */
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

  /**
   * Safely compares two objects for equality, even if the objects are null.
   * 
   * @param obj1
   *          an object to check for equality.
   * @param obj2
   *          an object to check for equality.
   * @return whether the objects are equal.
   */
  public static boolean equals(Object obj1, Object obj2) {
    try {
      return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Gets the default equality comparer.
   * 
   * @return the default equality comparer.
   */
  @SuppressWarnings("unchecked")
  public static <T> EqualityComparer<T> getDefaultComparer() {
    return (EqualityComparer<T>) ObjectUtilities.defaultComparer;
  }

  private ObjectUtilities() {
  }
}
