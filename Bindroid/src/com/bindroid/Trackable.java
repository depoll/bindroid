package com.bindroid;

import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import com.bindroid.utils.Action;
import com.bindroid.utils.Function;

/**
 * Provides an object to which {@link Tracker} can subscribe for notifications as well as methods
 * that allow a Tracker to evaluate an {@link Action} or {@link Function} while subscribing to
 * notifications for any trackers used during that evaluation.
 * 
 * Most uses of raw Trackables will be {@link TrackableField}s and {@link ObservableCollection}s.
 * Raw Trackables are primarily useful when manually wrapping the behavior of an object that uses
 * the Listener pattern for its notifications, calling {@link #track()} in the getter for the
 * property and {@link #updateTrackers()} when the Listener notifies of a change to its value.
 * 
 * Trackable instances are meant to be as lightweight as possible in order to minimize their
 * overhead when used in large numbers of objects.
 */
public class Trackable {
  private static ThreadLocal<Stack<Tracker>> trackersInFrame = new ThreadLocal<Stack<Tracker>>() {
    @Override
    protected synchronized Stack<Tracker> initialValue() {
      return new Stack<Tracker>();
    }
  };

  /**
   * Causes a {@link Tracker} to track any Trackables on which {@link #track()} was called while
   * executing the given {@link Action}. {@link Tracker#update()} will be called at most once for
   * all Trackables tracked while executing the action.
   * 
   * @param tracker
   *          The tracker to subscribe.
   * @param action
   *          The action to run.
   */
  public static void track(Tracker tracker, Action<Void> action) {
    Trackable.trackersInFrame.get().push(wrapTracker(tracker));
    try {
      action.invoke(null);
    } finally {
      Trackable.trackersInFrame.get().pop();
    }
  }

  /**
   * Causes a {@link Tracker} to track any Trackables on which {@link #track()} was called while
   * evaluating the given {@link Function}. The result of the function is returned.
   * {@link Tracker#update()} will be called at most once for all Trackables tracked while
   * evaluating the function.
   * 
   * @param tracker
   *          The tracker to subscribe.
   * @param function
   *          The function to evaluate.
   * @return The result of the function.
   */
  public static <T> T track(Tracker tracker, Function<T> function) {
    Trackable.trackersInFrame.get().push(wrapTracker(tracker));
    try {
      return function.evaluate();
    } finally {
      Trackable.trackersInFrame.get().pop();
    }
  }

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

  private LinkedList<Tracker> trackers;

  /**
   * Constructs a new Trackable.
   */
  public Trackable() {
    this.trackers = new LinkedList<Tracker>();
  }

  private LinkedList<Tracker> getTrackers() {
    return this.trackers;
  }

  /**
   * Captures any {@link Tracker}s that are configured to {@link #track(Tracker, Action)} this
   * evaluation. These trackers will be notified the next time {@link #updateTrackers()} is called.
   */
  public void track() {
    if (Trackable.trackersInFrame.get().size() > 0) {
      this.getTrackers().addAll(Trackable.trackersInFrame.get());
    }
  }

  /**
   * Notifies any {@link Tracker}s watching this Trackable.
   */
  public void updateTrackers() {
    LinkedList<Tracker> trackers = this.getTrackers();
    this.trackers = new LinkedList<Tracker>();
    for (Tracker t : trackers) {
      t.update();
    }
  }
}
