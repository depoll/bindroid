package com.bindroid.trackable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * A {@link List} implementation that implements Trackable on all of its methods, notifying
 * {@link Tracker}s whenever a change to the list occurs.
 *
 * @param <T> The type of object in the List.
 */
public class TrackableCollection<T> extends Trackable implements List<T> {
    private List<T> backingStore;
    private List<Long> ids;
    private long curId;
    private Stack<Long> returnedIds;
    private boolean shouldTrack = true;

    /**
     * Constructs a new, empty, {@link ArrayList}-backed ObservableCollection.
     */
    public TrackableCollection() {
        this(new ArrayList<T>());
    }

    /**
     * Constructs a new ObservableCollection backed by the given {@link List} implementation.
     *
     * @param backingStore The list implementation for the ObservableCollection.
     */
    public TrackableCollection(List<T> backingStore) {
        replaceBackingStore(backingStore);
    }

    /**
     * A utility function for cloning an ObservableCollection. Object identifiers will remain the same
     * in the cloned collection.
     *
     * @param toClone The ObservableCollection to clone.
     */
    public TrackableCollection(TrackableCollection<T> toClone) {
        this.backingStore = new ArrayList<T>(toClone.backingStore);
        this.ids = new ArrayList<Long>(toClone.ids);
        this.returnedIds = new Stack<Long>();
        this.curId = toClone.curId;
        this.shouldTrack = toClone.shouldTrack;
    }

    /**
     * Allows disabling of tracking so that multiple operations can proceed atomically without
     * notifying trackers.
     *
     * @param shouldTrack Whether to track
     */
    public void setTracking(boolean shouldTrack) {
        this.shouldTrack = shouldTrack;
    }

    public boolean isTracking() {
        return shouldTrack;
    }

    /**
     * Replaces the backing store, allowing the array to be replaced atomically without updating
     * trackers in between changes.
     *
     * @param backingStore The new backing store.
     */
    public void replaceBackingStore(List<T> backingStore) {
        this.backingStore = backingStore;
        this.ids = new ArrayList<Long>();
        this.returnedIds = new Stack<Long>();
        for (int x = 0; x < backingStore.size(); x++) {
            this.ids.add(this.getNewId());
        }
        this.updateTrackers();
    }

    @Override
    public void updateTrackers() {
        if (shouldTrack) {
            super.updateTrackers();
        }
    }

    @Override
    public void add(int location, T object) {
        this.backingStore.add(location, object);
        this.ids.add(location, this.getNewId());
        this.updateTrackers();
    }

    @Override
    public boolean add(T object) {
        boolean result = this.backingStore.add(object);
        this.ids.add(this.getNewId());
        if (result) {
            this.updateTrackers();
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> arg0) {
        boolean result = this.backingStore.addAll(arg0);
        for (@SuppressWarnings("unused")
                Object item : arg0) {
            this.ids.add(this.getNewId());
        }
        if (result) {
            this.updateTrackers();
        }
        return result;
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends T> arg1) {
        boolean result = this.backingStore.addAll(arg0, arg1);
        for (@SuppressWarnings("unused")
                Object item : arg1) {
            this.ids.add(this.getNewId());
        }
        if (result) {
            this.updateTrackers();
        }
        return result;
    }

    @Override
    public void clear() {
        this.backingStore.clear();
        this.ids.clear();
        this.returnedIds.clear();
        this.curId = 0;
        this.updateTrackers();
    }

    @Override
    public boolean contains(Object object) {
        this.track();
        return this.backingStore.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        this.track();
        return this.backingStore.containsAll(arg0);
    }

    @Override
    public T get(int location) {
        this.track();
        return this.backingStore.get(location);
    }

    /**
     * Gets a list-unique identifier associated with the object at the given index. This is useful for
     * UI to ensure that UI can be reused when the collection changes.
     *
     * @param index The index for which an identifier should be retrieved.
     * @return The list-unique identifier for the object at the given index.
     */
    public long getId(int index) {
        return this.ids.get(index);
    }

    private long getNewId() {
        if (this.returnedIds.isEmpty()) {
            return this.curId++;
        }
        return this.returnedIds.pop();
    }

    @Override
    public int indexOf(Object object) {
        this.track();
        return this.backingStore.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        this.track();
        return this.backingStore.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        this.track();
        return this.backingStore.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int location) {
        this.track();
        return new ListIterator<T>() {
            private int curIndex = location - 1;

            @Override
            public boolean hasNext() {
                return curIndex < size() - 1;
            }

            @Override
            public T next() {
                if (curIndex >= size() - 1) {
                    throw new NoSuchElementException();
                }
                return get(++curIndex);
            }

            @Override
            public boolean hasPrevious() {
                return curIndex > 0;
            }

            @Override
            public T previous() {
                if (curIndex <= 0) {
                    throw new NoSuchElementException();
                }
                return get(--curIndex);
            }

            @Override
            public int nextIndex() {
                return curIndex + 1;
            }

            @Override
            public int previousIndex() {
                return Math.max(curIndex - 1, -1);
            }

            @Override
            public void remove() {
                TrackableCollection.this.remove(curIndex);
            }

            @Override
            public void set(T t) {
                TrackableCollection.this.set(curIndex, t);
            }

            @Override
            public void add(T t) {
                TrackableCollection.this.add(++curIndex, t);
            }
        };
    }

    @Override
    public T remove(int location) {
        T result = this.backingStore.remove(location);
        this.returnId(this.ids.remove(location));
        this.updateTrackers();
        return result;
    }

    @Override
    public boolean remove(Object object) {
        int index = this.backingStore.indexOf(object);
        boolean result = index >= 0;
        if (result) {
            this.remove(index);
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        HashSet<?> items = new HashSet<Object>(arg0);
        ArrayList<Long> toRemove = new ArrayList<Long>();
        for (int x = 0; x < this.backingStore.size(); x++) {
            if (items.contains(this.backingStore.get(x))) {
                toRemove.add(this.ids.get(x));
                this.returnId(this.ids.get(x));
            }
        }
        boolean result = this.backingStore.removeAll(arg0);
        this.ids.removeAll(toRemove);
        if (result) {
            this.updateTrackers();
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        HashSet<?> items = new HashSet<Object>(arg0);
        ArrayList<Long> toRemove = new ArrayList<Long>();
        for (int x = 0; x < this.backingStore.size(); x++) {
            if (!items.contains(this.backingStore.get(x))) {
                toRemove.add(this.ids.get(x));
                this.returnId(this.ids.get(x));
            }
        }
        boolean result = this.backingStore.retainAll(arg0);
        this.ids.removeAll(toRemove);
        if (result) {
            this.updateTrackers();
        }
        return result;
    }

    private void returnId(long id) {
        this.returnedIds.push(id);
    }

    @Override
    public T set(int location, T object) {
        returnId(ids.get(location));
        ids.set(location, getNewId());
        T result = this.backingStore.set(location, object);
        this.updateTrackers();
        return result;
    }

    @Override
    public int size() {
        this.track();
        return this.backingStore.size();
    }

    @Override
    public List<T> subList(int start, int end) {
        this.track();
        return new TrackableCollection<T>(this.backingStore.subList(start, end));
    }

    @Override
    public Object[] toArray() {
        this.track();
        return this.backingStore.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        this.track();
        return this.backingStore.toArray(array);
    }

}
