package com.bindroid.converters;

import java.util.List;

import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;

import com.bindroid.ValueConverter;
import com.bindroid.trackable.TrackableCollection;
import com.bindroid.ui.BoundCollectionAdapter;

/**
 * A {@link ValueConverter} that converts a {@link List} or {@link TrackableCollection} into an
 * {@link Adapter} that can be used for {@link ListView}s and other UI widgets.
 */
public class AdapterConverter extends ValueConverter {
  private Class<? extends View> viewType;
  private Class<? extends View> dropDownViewType;
  private boolean recycleViews;
  private boolean cacheViews;

  /**
   * Constructs an AdapterConverter that generates the given views for each object in the bound
   * list.
   * 
   * @param viewType
   *          The type of view to construct for each object in the list.
   */
  public AdapterConverter(Class<? extends View> viewType) {
    this(viewType, true);
  }

  /**
   * Constructs an AdapterConverter that generates the given views for each object in the bound
   * list.
   * 
   * @param viewType
   *          The type of view to construct for each object in the list.
   * @param recycleViews
   *          Whether views should be recycled by the adapter.
   */
  public AdapterConverter(Class<? extends View> viewType, boolean recycleViews) {
    this(viewType, recycleViews, false);
  }

  /**
   * Constructs an AdapterConverter that generates the given views for each object in the bound
   * list.
   * 
   * @param viewType
   *          The type of view to construct for each object in the list.
   * @param recycleViews
   *          Whether views should be recycled by the adapter.
   * @param cacheViews
   *          Whether views should be cached by the adapter.
   */
  public AdapterConverter(Class<? extends View> viewType, boolean recycleViews, boolean cacheViews) {
    this(viewType, recycleViews, cacheViews, viewType);
  }

  /**
   * Constructs an AdapterConverter that generates the given views for each object in the bound
   * list.
   * 
   * @param viewType
   *          The type of view to construct for each object in the list.
   * @param recycleViews
   *          Whether views should be recycled by the adapter.
   * @param cacheViews
   *          Whether views should be cached by the adapter.
   * @param dropDownViewType
   *          The type of view to construct for drop-downs.
   */
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
    TrackableCollection<Object> source;
    if (sourceValue instanceof TrackableCollection) {
      source = (TrackableCollection<Object>) sourceValue;
    } else {
      source = new TrackableCollection<Object>((List<Object>) sourceValue);
    }
    return new BoundCollectionAdapter<Object>(source, this.getViewType(), this.recycleViews,
        this.cacheViews, this.getDropDownViewType());
  }

  private Class<? extends View> getDropDownViewType() {
    return this.dropDownViewType;
  }

  private Class<? extends View> getViewType() {
    return this.viewType;
  }

  private void setDropDownViewType(Class<? extends View> value) {
    this.dropDownViewType = value;
  }

  private void setViewType(Class<? extends View> value) {
    this.viewType = value;
  }
}
