package com.bindroid.utils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows the Garbage Collector to be watched by creating a weak reference to a sentinel object and
 * waiting for its finalizer to run. Useful for verifying that objects are being garbage-collected.
 */
public final class GarbageCollectionListener {
  private static List<Action<Void>> listeners;
  static {
    GarbageCollectionListener.listeners = new LinkedList<Action<Void>>();
    new WeakReference<GarbageCollectionListener>(new GarbageCollectionListener());
  }

  /**
   * Adds a listener for garbage collections.
   * 
   * @param action
   *          the method to call whenever a garbage collection is detected.
   */
  public static synchronized void addListener(Action<Void> action) {
    synchronized (listeners) {
      GarbageCollectionListener.listeners.add(action);
    }
  }

  private static synchronized void notifyListeners() {
    synchronized (listeners) {
      for (Action<Void> l : new LinkedList<Action<Void>>(GarbageCollectionListener.listeners)) {
        try {
          l.invoke(null);
        } catch (Exception e) {
        }
      }
      new WeakReference<GarbageCollectionListener>(new GarbageCollectionListener());
    }
  }

  /**
   * Removes a listener for garbage collections.
   * 
   * @param action
   *          the action to remove.
   */
  public static synchronized void removeListener(Action<Void> action) {
    synchronized (listeners) {
      GarbageCollectionListener.listeners.remove(action);
    }
  }

  private GarbageCollectionListener() {
  }

  @Override
  protected void finalize() {
    GarbageCollectionListener.notifyListeners();
  }
}
