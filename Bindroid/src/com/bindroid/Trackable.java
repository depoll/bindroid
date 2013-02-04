package com.bindroid;

import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import com.bindroid.utils.Action;
import com.bindroid.utils.Function;

public class Trackable {
  private static ThreadLocal<Stack<Tracker>> trackersInFrame = new ThreadLocal<Stack<Tracker>>() {
    @Override
    protected synchronized Stack<Tracker> initialValue() {
      return new Stack<Tracker>();
    }
  };

  /**
   * Wraps the tracker to ensure that references to it are released after the first change
   * notification is raised.
   */
  private static Tracker wrapTracker(Tracker tracker) {
    final AtomicReference<Tracker> sourceTracker = new AtomicReference<Tracker>(tracker);
    return new Tracker() {
      @Override
      public void update() {
        Tracker source = sourceTracker.getAndSet(null);
        if (source != null) {
          source.update();
        }
      }
    };
  }

  public static void track(Tracker tracker, Action<Void> action) {
    Trackable.trackersInFrame.get().push(wrapTracker(tracker));
    try {
      action.invoke(null);
    } finally {
      Trackable.trackersInFrame.get().pop();
    }
  }

  public static <T> T track(Tracker tracker, Function<T> action) {
    Trackable.trackersInFrame.get().push(wrapTracker(tracker));
    try {
      return action.evaluate();
    } finally {
      Trackable.trackersInFrame.get().pop();
    }
  }

  private LinkedList<Tracker> trackers;

  public Trackable() {
    this.trackers = new LinkedList<Tracker>();
  }

  private LinkedList<Tracker> getTrackers() {
    return this.trackers;
  }

  public void track() {
    if (Trackable.trackersInFrame.get().size() > 0) {
      this.getTrackers().addAll(Trackable.trackersInFrame.get());
    }
  }

  public void updateTrackers() {
    LinkedList<Tracker> trackers = this.getTrackers();
    this.trackers = new LinkedList<Tracker>();
    for (Tracker t : trackers) {
      t.update();
    }
  }
}
