package com.bindroid.ui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import com.bindroid.ObservableCollection;
import com.bindroid.Trackable;
import com.bindroid.Tracker;
import com.bindroid.utils.Action;

public class BoundCollectionAdapter<T> implements ListAdapter, SpinnerAdapter {
  private ObservableCollection<T> data;
  private ObservableCollection<T> presentedData;
  private Class<? extends View> viewType;
  private Class<? extends View> dropDownViewType;
  private Constructor<? extends View> viewConstructor;
  private Constructor<? extends View> dropDownViewConstructor;
  private Tracker tracker;
  private List<DataSetObserver> observers;
  private boolean recycleViews;
  private Map<T, View> cachedViews;
  private Action<Void> trackAction = new Action<Void>() {
    @Override
    public void invoke(Void parameter) {
      BoundCollectionAdapter.this.data.track();
    }
  };

  public BoundCollectionAdapter(ObservableCollection<T> data, Class<? extends View> viewType) {
    this(data, viewType, true, false);
  }

  public BoundCollectionAdapter(ObservableCollection<T> data, Class<? extends View> viewType,
      boolean recycleViews, boolean cacheViews) {
    this(data, viewType, recycleViews, cacheViews, viewType);
  }

  public BoundCollectionAdapter(ObservableCollection<T> data, Class<? extends View> viewType,
      boolean recycleViews, boolean cacheViews, Class<? extends View> dropDownViewType) {
    if (cacheViews) {
      this.cachedViews = new HashMap<T, View>();
    }
    this.observers = new LinkedList<DataSetObserver>();
    this.data = data;
    this.presentedData = new ObservableCollection<T>(data);
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

  public ObservableCollection<T> getData() {
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
        BoundCollectionAdapter.this.presentedData = new ObservableCollection<T>(BoundCollectionAdapter.this.data);

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
