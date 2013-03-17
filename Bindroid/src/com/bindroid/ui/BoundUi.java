package com.bindroid.ui;

import android.view.View;

/**
 * Provides a standard interface for a {@link View} to be bound to some model or data source.
 * 
 * @param <T>
 *          the type of the dataSource.
 */
public interface BoundUi<T> {
  void bind(T dataSource);
}
