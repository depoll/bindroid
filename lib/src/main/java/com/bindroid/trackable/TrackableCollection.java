package com.bindroid.trackable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * A {@link List} implementation that implements Trackable on all of its methods, notifying
 * {@link Tracker}s whenever a change to the list occurs.
 * 
 * @param <T>
 *          The type of object in the List.
 */
public class TrackableCollection<T> extends Trackable implements List<T> {
  private List<T> backingStore;
  private List<Long> ids;
  private long curId;
  private Stack<Long> returnedIds;
  private Trackable trackable = this;

  /**
   * Constructs a new, empty, {@link ArrayList}-backed ObservableCollection.
   */
  public TrackableCollection() {
    this(new ArrayList<T>());
  }

  /**
   * Constructs a new ObservableCollection backed by the given {@link List} implementation.
   * 
   * @param backingStore
   *          The list implementation for the ObservableCollection.
   */
  public TrackableCollection(List<T> backingStore) {
    this.backingStore = backingStore;
    this.ids = new ArrayList<Long>();
    this.returnedIds = new Stack<Long>();
    for (int x = 0; x < backingStore.size(); x++) {
      this.ids.add(this.getNewId());
    }
  }

  /**
   * A utility function for cloning an ObservableCollection. Object identifiers will remain the same
   * in the cloned collection.
   * 
   * @param toClone
   *          The ObservableCollection to clone.
   */
  public TrackableCollection(TrackableCollection<T> toClone) {
    this.backingStore = new ArrayList<T>(toClone.backingStore);
    this.ids = new ArrayList<Long>(toClone.ids);
    this.returnedIds = new Stack<Long>();
  }

  @Override
  public void add(int location, T object) {
    this.backingStore.add(location, object);
    this.ids.add(location, this.getNewId());
    this.trackable.updateTrackers();
  }

  @Override
  public boolean add(T object) {
    boolean result = this.backingStore.add(object);
    this.ids.add(this.getNewId());
    if (result) {
      this.trackable.updateTrackers();
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
      this.trackable.updateTrackers();
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
      this.trackable.updateTrackers();
    }
    return result;
  }

  @Override
  public void clear() {
    this.backingStore.clear();
    this.ids.clear();
    this.returnedIds.clear();
    this.curId = 0;
    this.trackable.updateTrackers();
  }

  @Override
  public boolean contains(Object object) {
    this.trackable.track();
    return this.backingStore.contains(object);
  }

  @Override
  public boolean containsAll(Collection<?> arg0) {
    this.trackable.track();
    return this.backingStore.containsAll(arg0);
  }

  @Override
  public T get(int location) {
    this.trackable.track();
    return this.backingStore.get(location);
  }

  /**
   * Gets a list-unique identifier associated with the object at the given index. This is useful for
   * UI to ensure that UI can be reused when the collection changes.
   * 
   * @param index
   *          The index for which an identifier should be retrieved.
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
    this.trackable.track();
    return this.backingStore.indexOf(object);
  }

  @Override
  public boolean isEmpty() {
    this.trackable.track();
    return this.backingStore.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    this.trackable.track();
    return this.backingStore.iterator();
  }

  @Override
  public int lastIndexOf(Object object) {
    this.trackable.track();
    return this.backingStore.lastIndexOf(object);
  }

  @Override
  public ListIterator<T> listIterator() {
    this.trackable.track();
    return this.backingStore.listIterator();
  }

  @Override
  public ListIterator<T> listIterator(int location) {
    this.trackable.track();
    return this.backingStore.listIterator(location);
  }

  @Override
  public T remove(int location) {
    T result = this.backingStore.remove(location);
    this.returnId(this.ids.remove(location));
    this.trackable.updateTrackers();
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
      this.trackable.updateTrackers();
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
      this.trackable.updateTrackers();
    }
    return result;
  }

  private void returnId(long id) {
    this.returnedIds.push(id);
  }

  @Override
  public T set(int location, T object) {
    T result = this.backingStore.set(location, object);
    this.trackable.updateTrackers();
    return result;
  }

  @Override
  public int size() {
    this.trackable.track();
    return this.backingStore.size();
  }

  @Override
  public List<T> subList(int start, int end) {
    this.trackable.track();
    return new TrackableCollection<T>(this.backingStore.subList(start, end));
  }

  @Override
  public Object[] toArray() {
    this.trackable.track();
    return this.backingStore.toArray();
  }

  @Override
  public <T1> T1[] toArray(T1[] array) {
    this.trackable.track();
    return this.backingStore.toArray(array);
  }

}
