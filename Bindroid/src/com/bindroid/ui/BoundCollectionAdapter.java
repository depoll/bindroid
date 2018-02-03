package com.bindroid.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.TrackableCollection;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Action;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides a {@link ListAdapter} or {@link SpinnerAdapter} to wrap a {@link TrackableCollection},
 * listening for changes to the collection and notifying any UI using the adapter of those changes.
 * <p>
 * If the {@link View} type that this adapter creates implements {@link BoundUi}, each item the
 * adapter creates will be bound to its corresponding data value.
 *
 * @param <T> the type of object in the collection.
 */
public class BoundCollectionAdapter<T> implements ListAdapter, SpinnerAdapter {
    private TrackableCollection<T> data;
    private TrackableCollection<T> presentedData;
    private Class<? extends View> viewType;
    private Class<? extends View> dropDownViewType;
    private Constructor<? extends View> viewConstructor;
    private Constructor<? extends View> dropDownViewConstructor;
    private Tracker tracker;
    private final List<DataSetObserver> observers;
    private boolean recycleViews;
    private Map<T, View> cachedViews;
    private Action<Void> trackAction = new Action<Void>() {
        @Override
        public void invoke(Void parameter) {
            BoundCollectionAdapter.this.data.track();
        }
    };

    /**
     * Constructs a BonudCollectionAdapter for a {@link TrackableCollection} using the given viewType.
     *
     * @param data     the data being wrapped.
     * @param viewType the type of {@link View} to create for each element of the collection.
     */
    public BoundCollectionAdapter(TrackableCollection<T> data, Class<? extends View> viewType) {
        this(data, viewType, true, false);
    }

    /**
     * Constructs a BonudCollectionAdapter for a {@link TrackableCollection} using the given viewType.
     *
     * @param data         the data being wrapped.
     * @param viewType     the type of {@link View} to create for each element of the collection.
     * @param recycleViews whether to recycle views.
     * @param cacheViews   whether to cache views.
     */
    public BoundCollectionAdapter(TrackableCollection<T> data, Class<? extends View> viewType,
                                  boolean recycleViews, boolean cacheViews) {
        this(data, viewType, recycleViews, cacheViews, viewType);
    }

    /**
     * Constructs a BonudCollectionAdapter for a {@link TrackableCollection} using the given viewType.
     *
     * @param data             the data being wrapped.
     * @param viewType         the type of {@link View} to create for each element of the collection.
     * @param recycleViews     whether to recycle views.
     * @param cacheViews       whether to cache views.
     * @param dropDownViewType the type of {@link View} to create for dropdowns.
     */
    public BoundCollectionAdapter(TrackableCollection<T> data, Class<? extends View> viewType,
                                  boolean recycleViews, boolean cacheViews, Class<? extends View> dropDownViewType) {
        if (cacheViews) {
            this.cachedViews = new HashMap<T, View>();
        }
        this.observers = new LinkedList<DataSetObserver>();
        this.data = data;
        this.presentedData = new TrackableCollection<T>(data);
        this.viewType = viewType;
        this.dropDownViewType = dropDownViewType;
        try {
            this.viewConstructor = this.viewType.getConstructor(Context.class);
            this.dropDownViewConstructor = this.dropDownViewType.getConstructor(Context.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.recycleViews = recycleViews;
        this.tracker = new Tracker() {
            @Override
            public void update() {
                synchronized (BoundCollectionAdapter.this.observers) {
                    BoundCollectionAdapter.this.notifyCollectionChanged();
                    Trackable.track(BoundCollectionAdapter.this.tracker,
                            BoundCollectionAdapter.this.trackAction);
                }
            }
        };
        Trackable.track(BoundCollectionAdapter.this.tracker, this.trackAction);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public int getCount() {
        return this.presentedData.size();
    }

    /**
     * @return the underlying {@link TrackableCollection}.
     */
    public TrackableCollection<T> getData() {
        return this.data;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent, this.dropDownViewConstructor);
    }

    @Override
    public Object getItem(int position) {
        return this.presentedData.get(position);
    }

    @Override
    public long getItemId(int position) {
        try {
            return this.presentedData.getId(position);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent, this.viewConstructor);
    }

    @SuppressWarnings("unchecked")
    private View getView(int position, View convertView, ViewGroup parent,
                         Constructor<? extends View> viewConstructor) {
        synchronized (this.observers) {
            T dataItem = this.presentedData.get(position);
            if (this.cachedViews != null && this.cachedViews.containsKey(dataItem)) {
                return this.cachedViews.get(dataItem);
            }
            View result = convertView;
            if (!this.recycleViews || result == null || this.cachedViews != null) {
                try {
                    result = viewConstructor.newInstance(parent.getContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (result instanceof BoundUi) {
                ((BoundUi<T>) result).bind(dataItem);
            }
            if (this.cachedViews != null) {
                this.cachedViews.put(dataItem, result);
            }
            return result;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.presentedData.isEmpty();
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private void notifyCollectionChanged() {
        // Dispatch notifications to the main thread.
        final List<DataSetObserver> observers = new ArrayList<DataSetObserver>(this.observers);
        Runnable toRun = new Runnable() {
            @Override
            public void run() {
                BoundCollectionAdapter.this.presentedData = new TrackableCollection<T>(
                        BoundCollectionAdapter.this.data);

                if (BoundCollectionAdapter.this.cachedViews != null) {
                    Map<T, View> newCache = new HashMap<T, View>();
                    for (T item : BoundCollectionAdapter.this.presentedData) {
                        if (BoundCollectionAdapter.this.cachedViews.containsKey(item)) {
                            newCache.put(item, BoundCollectionAdapter.this.cachedViews.get(item));
                        }
                    }
                    BoundCollectionAdapter.this.cachedViews = newCache;
                }

                for (DataSetObserver obs : observers) {
                    obs.onChanged();
                }
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            toRun.run();
        } else {
            new Handler(Looper.getMainLooper()).post(toRun);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.observers.remove(observer);
    }

}
