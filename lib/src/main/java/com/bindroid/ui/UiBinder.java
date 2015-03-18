package com.bindroid.ui;

import android.app.Activity;
import android.view.View;

import com.bindroid.Binding;
import com.bindroid.BindingMode;
import com.bindroid.ValueConverter;
import com.bindroid.utils.Property;
import com.bindroid.utils.ReflectedProperty;
import com.bindroid.utils.WeakReflectedProperty;

/**
 * Provides utility methods for bindings to UI, ensuring that the bindings hold weak references to
 * the UI hierarchy and that the UI is only accessed from the main thread.
 */
public final class UiBinder {

  /**
   * Binds a view within an {@link Activity} to the given property.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty) {
    return bind(activity, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.ONE_WAY, ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within an {@link Activity} to the given property.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty, BindingMode mode) {
    return bind(activity, targetId, targetProperty, sourceObject, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within an {@link Activity} to the given property.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @param mode
   *          the mode for the binding.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty, BindingMode mode, ValueConverter converter) {
    Binding b = new Binding(UiProperty.make(new WeakReflectedProperty(activity
        .findViewById(targetId), targetProperty)), new ReflectedProperty(sourceObject,
        sourceProperty), mode, converter);
    return b;
  }

  /**
   * Binds a view within an {@link Activity} to the given property.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      Object sourceObject, String sourceProperty, ValueConverter converter) {
    return bind(activity, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.ONE_WAY, converter);
  }

  /**
   * Binds a view within an {@link Activity} to the given property on the Activity itself.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the activity to bind.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty) {
    return bind(activity, targetId, targetProperty, sourceProperty, BindingMode.ONE_WAY,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within an {@link Activity} to the given property on the Activity itself.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the activity to bind.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty, BindingMode mode) {
    return bind(activity, targetId, targetProperty, activity, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within an {@link Activity} to the given property on the Activity itself.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the activity to bind.
   * @param mode
   *          the mode for the binding.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty, BindingMode mode, ValueConverter converter) {
    return bind(activity, targetId, targetProperty, activity, sourceProperty, mode, converter);
  }

  /**
   * Binds a view within an {@link Activity} to the given property on the Activity itself.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the activity to bind.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, int targetId, String targetProperty,
      String sourceProperty, ValueConverter converter) {
    return bind(activity, targetId, targetProperty, activity, sourceProperty,
        BindingMode.ONE_WAY, converter);
  }

  /**
   * Binds a view within an {@link Activity} to the given property on the Activity itself.
   * 
   * @param activity
   *          the parent Activity.
   * @param targetProperty
   *          the property to bind.
   * @param sourceProperty
   *          the property path on the activity to bind.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Activity activity, Property<?> targetProperty, String sourceProperty,
      BindingMode mode) {
    return bind(targetProperty, activity, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within an {@link Activity} to the given property on the Activity itself.
   *
   * @param targetProperty
   *          the property to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the activity to bind.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Property<?> targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode) {
    return bind(targetProperty, new ReflectedProperty(sourceObject, sourceProperty), mode,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within an {@link Activity} to the given property on the Activity itself.
   * 
   * @param targetProperty
   *          the property to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the activity to bind.
   * @param mode
   *          the mode for the binding.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Property<?> targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode, ValueConverter converter) {
    return bind(targetProperty, new ReflectedProperty(sourceObject, sourceProperty), mode,
        converter);
  }

  /**
   * Binds two arbitrary properties together, where the target will only be called from the main
   * thread.
   * 
   * @param targetProperty
   *          the target property being bound.
   * @param sourceProperty
   *          the source property being bound.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Property<?> targetProperty, Property<?> sourceProperty,
      BindingMode mode) {
    return bind(targetProperty, sourceProperty, mode, ValueConverter.getDefaultConverter());
  }

  /**
   * Binds two arbitrary properties together, where the target will only be called from the main
   * thread.
   * 
   * @param targetProperty
   *          the target property being bound.
   * @param sourceProperty
   *          the source property being bound.
   * @param mode
   *          the mode for the binding.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(Property<?> targetProperty, Property<?> sourceProperty,
      BindingMode mode, ValueConverter converter) {
    return new Binding(UiProperty.make(targetProperty), sourceProperty, mode, converter);
  }

  /**
   * Binds a view within a {@link View} to the given property.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty) {
    return bind(view, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.ONE_WAY, ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within a {@link View} to the given property.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode) {
    return bind(view, targetId, targetProperty, sourceObject, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within a {@link View} to the given property.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @param mode
   *          the mode for the binding.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty, BindingMode mode, ValueConverter converter) {
    return bind(new WeakReflectedProperty(view.findViewById(targetId), targetProperty),
        new ReflectedProperty(sourceObject, sourceProperty), mode, converter);
  }

  /**
   * Binds a view within a {@link View} to the given property.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceObject
   *          the source object for the binding.
   * @param sourceProperty
   *          the property path on the source object to bind.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, Object sourceObject,
      String sourceProperty, ValueConverter converter) {
    return bind(view, targetId, targetProperty, sourceObject, sourceProperty,
        BindingMode.ONE_WAY, converter);
  }

  /**
   * Binds a view within a {@link View} to the given property on the View itself.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the view to bind.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty) {
    return bind(view, targetId, targetProperty, view, sourceProperty, BindingMode.ONE_WAY,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within a {@link View} to the given property on the View itself.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the view to bind.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty,
      BindingMode mode) {
    return bind(view, targetId, targetProperty, view, sourceProperty, mode,
        ValueConverter.getDefaultConverter());
  }

  /**
   * Binds a view within a {@link View} to the given property on the View itself.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the view to bind.
   * @param mode
   *          the mode for the binding.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty,
      BindingMode mode, ValueConverter converter) {
    return bind(view, targetId, targetProperty, view, sourceProperty, mode, converter);
  }

  /**
   * Binds a view within a {@link View} to the given property on the View itself.
   * 
   * @param view
   *          the parent View.
   * @param targetId
   *          the resource ID of the target view.
   * @param targetProperty
   *          the property path on the target view to bind.
   * @param sourceProperty
   *          the property path on the view to bind.
   * @param converter
   *          the converter for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, int targetId, String targetProperty, String sourceProperty,
      ValueConverter converter) {
    return bind(view, targetId, targetProperty, view, sourceProperty, BindingMode.ONE_WAY,
        converter);
  }

  /**
   * Binds a view within a {@link View} to the given property on the View itself.
   * 
   * @param view
   *          the parent View.
   * @param targetProperty
   *          the property on the target view to bind.
   * @param sourceProperty
   *          the property path on the view to bind.
   * @param mode
   *          the mode for the binding.
   * @return the binding produced by this action.
   */
  public static Binding bind(View view, Property<?> targetProperty, String sourceProperty,
      BindingMode mode) {
    return bind(targetProperty, view, sourceProperty, mode, ValueConverter.getDefaultConverter());
  }

  private UiBinder() {
  }
}
