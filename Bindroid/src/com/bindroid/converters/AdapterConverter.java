package com.bindroid.converters;

import java.util.List;

import android.view.View;

import com.bindroid.ObservableCollection;
import com.bindroid.ValueConverter;
import com.bindroid.ui.BoundCollectionAdapter;

public class AdapterConverter extends ValueConverter {
  private Class<? extends View> viewType;
  private Class<? extends View> dropDownViewType;
  private boolean recycleViews;
  private boolean cacheViews;

  public AdapterConverter(Class<? extends View> viewType) {
    this(viewType, true);
  }

  public AdapterConverter(Class<? extends View> viewType, boolean recycleViews) {
    this(viewType, recycleViews, false);
  }

  public AdapterConverter(Class<? extends View> viewType, boolean recycleViews, boolean cacheViews) {
    this(viewType, recycleViews, cacheViews, viewType);
  }

  public AdapterConverter(Class<? extends View> viewType, boolean recycleViews, boolean cacheViews,
      Class<? extends View> dropDownViewType) {
    this.setViewType(viewType);
    this.setDropDownViewType(dropDownViewType);
    this.recycleViews = recycleViews;
    this.cacheViews = cacheViews;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object convertToTarget(Object sourceValue, Class<?> targetType) {
    ObservableCollection<Object> source;
    if (sourceValue instanceof ObservableCollection) {
      source = (ObservableCollection<Object>) sourceValue;
    } else {
      source = new ObservableCollection<Object>((List<Object>) sourceValue);
    }
    return new BoundCollectionAdapter<Object>(source, this.getViewType(), this.recycleViews,
        this.cacheViews, this.getDropDownViewType());
  }

  public Class<? extends View> getDropDownViewType() {
    return this.dropDownViewType;
  }

  public Class<? extends View> getViewType() {
    return this.viewType;
  }

  public void setDropDownViewType(Class<? extends View> value) {
    this.dropDownViewType = value;
  }

  public void setViewType(Class<? extends View> value) {
    this.viewType = value;
  }
}
