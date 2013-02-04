package com.bindroid.utils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public final class GarbageCollectionListener {
  private static List<Action<Void>> listeners;
  static {
    GarbageCollectionListener.listeners = new LinkedList<Action<Void>>();
    new WeakReference<GarbageCollectionListener>(new GarbageCollectionListener());
  }

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
