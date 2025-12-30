package com.bindroid.test;

import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.TrackableCollection;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Action;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Extended unit tests for TrackableCollection.
 * These tests focus on comprehensive List interface coverage and edge cases.
 */
public class TrackableCollectionExtendedTest {

    private AtomicInteger updateCount;
    private TrackableCollection<String> collection;

    @Before
    public void setUp() {
        updateCount = new AtomicInteger(0);
        collection = new TrackableCollection<String>();
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testDefaultConstructor() {
        TrackableCollection<String> coll = new TrackableCollection<String>();
        assertTrue(coll.isEmpty());
        assertEquals(0, coll.size());
    }

    @Test
    public void testConstructorWithBackingStore() {
        List<String> backing = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        TrackableCollection<String> coll = new TrackableCollection<String>(backing);
        assertEquals(3, coll.size());
        assertEquals("a", coll.get(0));
        assertEquals("b", coll.get(1));
        assertEquals("c", coll.get(2));
    }

    @Test
    public void testCopyConstructor() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        TrackableCollection<String> copy = new TrackableCollection<String>(collection);
        
        assertEquals(collection.size(), copy.size());
        for (int i = 0; i < collection.size(); i++) {
            assertEquals(collection.get(i), copy.get(i));
        }
        
        // Verify independence
        copy.add("d");
        assertEquals(3, collection.size());
        assertEquals(4, copy.size());
    }

    // ==================== Add Operations Tests ====================

    @Test
    public void testAddSingleElement() {
        setupTracker();
        
        collection.add("first");
        assertEquals(1, collection.size());
        assertEquals("first", collection.get(0));
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testAddAtIndex() {
        collection.addAll(Arrays.asList("a", "c"));
        setupTracker();
        
        collection.add(1, "b");
        assertEquals(3, collection.size());
        assertEquals("b", collection.get(1));
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testAddAll() {
        setupTracker();
        
        boolean result = collection.addAll(Arrays.asList("a", "b", "c"));
        assertTrue(result);
        assertEquals(3, collection.size());
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testAddAllAtIndex() {
        collection.addAll(Arrays.asList("a", "d"));
        setupTracker();
        
        boolean result = collection.addAll(1, Arrays.asList("b", "c"));
        assertTrue(result);
        assertEquals(4, collection.size());
        assertEquals("a", collection.get(0));
        assertEquals("b", collection.get(1));
        assertEquals("c", collection.get(2));
        assertEquals("d", collection.get(3));
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testAddAllEmptyCollection() {
        setupTracker();
        
        boolean result = collection.addAll(Collections.<String>emptyList());
        assertFalse(result);
        assertEquals(0, updateCount.get());
    }

    // ==================== Remove Operations Tests ====================

    @Test
    public void testRemoveByIndex() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        setupTracker();
        
        String removed = collection.remove(1);
        assertEquals("b", removed);
        assertEquals(2, collection.size());
        assertEquals("a", collection.get(0));
        assertEquals("c", collection.get(1));
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testRemoveByObject() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        setupTracker();
        
        boolean removed = collection.remove("b");
        assertTrue(removed);
        assertEquals(2, collection.size());
        assertEquals(1, updateCount.get());
    }

    @Test
    public void testRemoveByObjectNotFound() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        setupTracker();
        
        boolean removed = collection.remove("x");
        assertFalse(removed);
        assertEquals(0, updateCount.get());
    }

    @Test
    public void testRemoveAll() {
        collection.addAll(Arrays.asList("a", "b", "c", "d"));
        setupTracker();
        
        boolean result = collection.removeAll(Arrays.asList("b", "d"));
        assertTrue(result);
        assertEquals(2, collection.size());
        assertTrue(collection.contains("a"));
        assertTrue(collection.contains("c"));
    }

    @Test
    public void testRetainAll() {
        collection.addAll(Arrays.asList("a", "b", "c", "d"));
        setupTracker();
        
        boolean result = collection.retainAll(Arrays.asList("b", "d"));
        assertTrue(result);
        assertEquals(2, collection.size());
        assertTrue(collection.contains("b"));
        assertTrue(collection.contains("d"));
    }

    @Test
    public void testClear() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        setupTracker();
        
        collection.clear();
        assertTrue(collection.isEmpty());
        assertEquals(0, collection.size());
        assertEquals(1, updateCount.get());
    }

    // ==================== Query Operations Tests ====================

    @Test
    public void testContains() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        
        assertTrue(collection.contains("a"));
        assertTrue(collection.contains("b"));
        assertTrue(collection.contains("c"));
        assertFalse(collection.contains("x"));
    }

    @Test
    public void testContainsAll() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        
        assertTrue(collection.containsAll(Arrays.asList("a", "b")));
        assertFalse(collection.containsAll(Arrays.asList("a", "x")));
    }

    @Test
    public void testIndexOf() {
        collection.addAll(Arrays.asList("a", "b", "c", "b"));
        
        assertEquals(0, collection.indexOf("a"));
        assertEquals(1, collection.indexOf("b"));
        assertEquals(-1, collection.indexOf("x"));
    }

    @Test
    public void testLastIndexOf() {
        collection.addAll(Arrays.asList("a", "b", "c", "b"));
        
        assertEquals(0, collection.lastIndexOf("a"));
        assertEquals(3, collection.lastIndexOf("b"));
        assertEquals(-1, collection.lastIndexOf("x"));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(collection.isEmpty());
        collection.add("a");
        assertFalse(collection.isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, collection.size());
        collection.add("a");
        assertEquals(1, collection.size());
        collection.addAll(Arrays.asList("b", "c"));
        assertEquals(3, collection.size());
    }

    // ==================== Set Operation Tests ====================

    @Test
    public void testSet() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        setupTracker();
        
        String oldValue = collection.set(1, "x");
        assertEquals("b", oldValue);
        assertEquals("x", collection.get(1));
        assertEquals(1, updateCount.get());
    }

    // ==================== SubList Tests ====================

    @Test
    public void testSubList() {
        collection.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        
        List<String> subList = collection.subList(1, 4);
        assertEquals(3, subList.size());
        assertEquals("b", subList.get(0));
        assertEquals("c", subList.get(1));
        assertEquals("d", subList.get(2));
    }

    // ==================== Iterator Tests ====================

    @Test
    public void testIterator() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        
        Iterator<String> iter = collection.iterator();
        assertTrue(iter.hasNext());
        assertEquals("a", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testIteratorRemove() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        setupTracker();
        
        Iterator<String> iter = collection.iterator();
        iter.next();
        iter.next();
        iter.remove();
        
        assertEquals(2, collection.size());
        assertEquals("a", collection.get(0));
        assertEquals("c", collection.get(1));
    }

    @Test
    public void testListIterator() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        
        ListIterator<String> iter = collection.listIterator();
        
        // Forward
        assertTrue(iter.hasNext());
        assertFalse(iter.hasPrevious());
        assertEquals(0, iter.nextIndex());
        assertEquals("a", iter.next());
        
        // Backward
        assertTrue(iter.hasPrevious());
        assertEquals(0, iter.previousIndex());
        assertEquals("a", iter.previous());
    }

    @Test
    public void testListIteratorAtIndex() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        
        ListIterator<String> iter = collection.listIterator(1);
        assertEquals("b", iter.next());
        assertEquals("c", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testListIteratorSet() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        setupTracker();
        
        ListIterator<String> iter = collection.listIterator();
        iter.next();
        iter.set("x");
        
        assertEquals("x", collection.get(0));
    }

    @Test
    public void testListIteratorAdd() {
        collection.addAll(Arrays.asList("a", "c"));
        setupTracker();
        
        ListIterator<String> iter = collection.listIterator(1);
        iter.add("b");
        
        assertEquals(3, collection.size());
        assertEquals("a", collection.get(0));
        assertEquals("b", collection.get(1));
        assertEquals("c", collection.get(2));
    }

    // ==================== Tracking Control Tests ====================

    @Test
    public void testSetTrackingDisabled() {
        setupTracker();
        
        collection.setTracking(false);
        assertFalse(collection.isTracking());
        
        collection.add("a");
        collection.add("b");
        assertEquals(0, updateCount.get()); // No updates when tracking disabled
        
        collection.setTracking(true);
        assertTrue(collection.isTracking());
        
        collection.add("c");
        assertEquals(1, updateCount.get()); // Update occurs when tracking enabled
    }

    @Test
    public void testReplaceBackingStore() {
        collection.addAll(Arrays.asList("a", "b"));
        setupTracker();
        
        List<String> newBacking = new ArrayList<String>(Arrays.asList("x", "y", "z"));
        collection.replaceBackingStore(newBacking);
        
        assertEquals(3, collection.size());
        assertEquals("x", collection.get(0));
        assertEquals("y", collection.get(1));
        assertEquals("z", collection.get(2));
        assertEquals(1, updateCount.get());
    }

    // ==================== toArray Tests ====================

    @Test
    public void testToArray() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        
        Object[] array = collection.toArray();
        assertEquals(3, array.length);
        assertEquals("a", array[0]);
        assertEquals("b", array[1]);
        assertEquals("c", array[2]);
    }

    @Test
    public void testToArrayTyped() {
        collection.addAll(Arrays.asList("a", "b", "c"));
        
        String[] array = collection.toArray(new String[0]);
        assertEquals(3, array.length);
        assertEquals("a", array[0]);
        assertEquals("b", array[1]);
        assertEquals("c", array[2]);
    }

    // ==================== Observation Tests ====================

    @Test
    public void testContainsObservesChanges() {
        final AtomicReference<Boolean> containsA = new AtomicReference<Boolean>();
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        containsA.set(collection.contains("a"));
                    }
                });
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                containsA.set(collection.contains("a"));
            }
        });
        
        assertFalse(containsA.get());
        
        collection.add("a");
        assertTrue(containsA.get());
        
        collection.remove("a");
        assertFalse(containsA.get());
    }

    @Test
    public void testSizeObservesChanges() {
        final AtomicReference<Integer> size = new AtomicReference<Integer>();
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        size.set(collection.size());
                    }
                });
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                size.set(collection.size());
            }
        });
        
        assertEquals(Integer.valueOf(0), size.get());
        
        collection.add("a");
        assertEquals(Integer.valueOf(1), size.get());
        
        collection.addAll(Arrays.asList("b", "c"));
        assertEquals(Integer.valueOf(3), size.get());
        
        collection.clear();
        assertEquals(Integer.valueOf(0), size.get());
    }

    @Test
    public void testGetObservesChanges() {
        collection.add("initial");
        final AtomicReference<String> firstElement = new AtomicReference<String>();
        
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                Trackable.track(this, new Action<Void>() {
                    @Override
                    public void invoke(Void o) {
                        if (!collection.isEmpty()) {
                            firstElement.set(collection.get(0));
                        }
                    }
                });
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                firstElement.set(collection.get(0));
            }
        });
        
        assertEquals("initial", firstElement.get());
        
        collection.set(0, "changed");
        assertEquals("changed", firstElement.get());
        
        collection.add(0, "new first");
        assertEquals("new first", firstElement.get());
    }

    // ==================== ID Tracking Tests ====================

    @Test
    public void testGetId() {
        collection.add("a");
        collection.add("b");
        collection.add("c");
        
        long id0 = collection.getId(0);
        long id1 = collection.getId(1);
        long id2 = collection.getId(2);
        
        // IDs should be unique
        assertNotEquals(id0, id1);
        assertNotEquals(id1, id2);
        assertNotEquals(id0, id2);
        
        // IDs should be consistent
        assertEquals(id0, collection.getId(0));
        assertEquals(id1, collection.getId(1));
        assertEquals(id2, collection.getId(2));
    }

    @Test
    public void testIdsPersistAfterRemoval() {
        collection.add("a");
        collection.add("b");
        collection.add("c");
        
        long idB = collection.getId(1);
        long idC = collection.getId(2);
        
        collection.remove(0);
        
        // "b" is now at index 0, "c" at index 1
        assertEquals(idB, collection.getId(0));
        assertEquals(idC, collection.getId(1));
    }

    // ==================== Helper Methods ====================

    private void setupTracker() {
        Tracker tracker = new Tracker() {
            @Override
            public void update() {
                updateCount.incrementAndGet();
            }
        };
        
        Trackable.track(tracker, new Action<Void>() {
            @Override
            public void invoke(Void o) {
                collection.size(); // Track the collection
            }
        });
    }
}
