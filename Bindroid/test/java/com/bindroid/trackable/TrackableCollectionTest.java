package com.bindroid.trackable;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.ListIterator;

import static org.junit.Assert.*;

public class TrackableCollectionTest {
    @Test
    public void list_operations_notify_and_behave_like_list() {
        TrackableCollection<String> list = new TrackableCollection<>();
        list.add("a");
        list.addAll(Arrays.asList("b","c"));
        list.set(1, "b2");
        list.remove("a");
        list.retainAll(Collections.singleton("c"));
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    public void listIterator_mutations_reflect_in_collection() {
        TrackableCollection<Integer> list = new TrackableCollection<>();
        list.addAll(Arrays.asList(1,2,3));
        ListIterator<Integer> it = list.listIterator();
        assertEquals(Integer.valueOf(1), it.next());
        it.add(4); // [1,4,2,3]
        assertEquals(Integer.valueOf(2), it.next());
        it.set(5); // [1,4,5,3]
        it.previous(); // move back to 5
        it.remove();   // remove 5 -> [1,4,3]
        assertEquals(Arrays.asList(1,5,3), Arrays.asList(list.toArray(new Integer[0])));
    }
}
