package com.bindroid.test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class GCTestUtils {
  /**
   * Returns a runnable that will assert that all of the objects in toWatch can be collected.
   */
  public static <T> Runnable watchPointers(Collection<T> toWatch) {
    final ReferenceQueue<T> q = new ReferenceQueue<T>();
    final List<WeakReference<T>> weakRefs = new ArrayList<WeakReference<T>>();
    for (T o : toWatch) {
      weakRefs.add(new WeakReference<T>(o, q));
    }
    return new Runnable() {
      @Override
      public void run() {
        int iterations = 0;
        int count = 0;
        while (count < weakRefs.size() && iterations < 100) {
          Runtime.getRuntime().gc();
          try {
            if (q.remove(100) != null) {
              count++;
            } else {
              iterations++;
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
        if (count < weakRefs.size()) {
          throw new RuntimeException("Not all references were collected.");
        }
      }
    };
  }
}
