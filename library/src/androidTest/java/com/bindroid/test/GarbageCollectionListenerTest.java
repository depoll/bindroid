package com.bindroid.test;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import com.bindroid.utils.Action;
import com.bindroid.utils.GarbageCollectionListener;

public class GarbageCollectionListenerTest extends TestCase {
  public void testNotification() throws Exception {
    final Object lock = new Object();
    final AtomicBoolean bool = new AtomicBoolean(false);
    synchronized (lock) {
      GarbageCollectionListener.addListener(new Action<Void>() {
        @Override
        public void invoke(Void parameter) {
          synchronized (lock) {
            bool.set(true);
            lock.notify();
            GarbageCollectionListener.removeListener(this);
          }
        }
      });
      Runtime.getRuntime().gc();
      lock.wait(1000);
      assertTrue(bool.get());
    }
  }
}
