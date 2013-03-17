package com.bindroid;

import java.lang.ref.WeakReference;

import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Property;
import com.bindroid.utils.WeakenedProperty;

/**
 * Allows two trackable properties to be bound together such that their values remain in sync. It
 * supports one-way and two-way bindings to any properties that use {@link Trackable}s.
 * ValueConverters allow conversions between the source and target properties to be applied in the
 * binding process.
 */
public class Binding {
  /**
   * A tracker for the Source property.
   */
  private class SourceTracker implements Tracker {
    /**
     * Keeps a strong reference to the property to prevent garbage collection of the Property (which
     * may be weakly held by the binding) as long as the tracked object is still alive. This
     * effectively reverses the direction of the reference on the Binding so that the tracked object
     * points to the Binding rather than the Binding pointing to the tracked object.
     */
    @SuppressWarnings("unused")
    private Property<?> property;

    public SourceTracker(Property<?> property) {
      this.property = property;
    }

    @Override
    public void update() {
      Binding.this.applySourceToTarget();
    }
  }

  /**
   * A tracker for the Target property.
   */
  private class TargetTracker implements Tracker {
    /**
     * Keeps a strong reference to the property to prevent garbage collection of the Property (which
     * may be weakly held by the binding) as long as the tracked object is still alive. This
     * effectively reverses the direction of the reference on the Binding so that the tracked object
     * points to the Binding rather than the Binding pointing to the tracked object.
     */
    @SuppressWarnings("unused")
    private Property<?> property;

    public TargetTracker(Property<?> property) {
      this.property = property;
    }

    @Override
    public void update() {
      Binding.this.applyTargetToSource();
    }
  }

  private Property<?> targetProperty;
  private Property<?> sourceProperty;
  private Tracker sourceTracker;
  private Tracker targetTracker;
  private ValueConverter converter;
  private BindingMode mode;
  private boolean isLoggingEnabled;

  private WeakReference<Binding> weakToMe;

  /**
   * Constructs a simple one-way binding between the given properties.
   * 
   * @param targetProperty
   *          The target property, whose value will be set to match the source property.
   * @param sourceProperty
   *          The source property, whose value will drive the binding.
   */
  public Binding(Property<?> targetProperty, Property<?> sourceProperty) {
    this(targetProperty, sourceProperty, BindingMode.ONE_WAY);
  }

  /**
   * Constructs a binding between the given properties.
   * 
   * @param targetProperty
   *          The target property.
   * @param sourceProperty
   *          The source property.
   * @param mode
   *          The BindingMode for the binding.
   */
  public Binding(Property<?> targetProperty, Property<?> sourceProperty, BindingMode mode) {
    this(targetProperty, sourceProperty, mode, ValueConverter.getDefaultConverter());
  }

  /**
   * Constructs a binding between the given properties.
   * 
   * @param targetProperty
   *          The target property.
   * @param sourceProperty
   *          The source property.
   * @param mode
   *          The BindingMode for the binding.
   * @param converter
   *          A ValueConverter to be applied whenever changes are detected.
   */
  public Binding(Property<?> targetProperty, Property<?> sourceProperty, BindingMode mode,
      ValueConverter converter) {
    this.weakToMe = new WeakReference<Binding>(this);
    this.mode = mode;

    this.sourceTracker = new SourceTracker(sourceProperty);
    this.targetTracker = new TargetTracker(targetProperty);

    // Weaken the property references to allow the source/target to be GC'd if all other references
    // are gone.
    if (mode == BindingMode.ONE_WAY) {
      sourceProperty = WeakenedProperty.weaken(sourceProperty);
    } else if (mode == BindingMode.ONE_WAY_TO_SOURCE) {
      targetProperty = WeakenedProperty.weaken(targetProperty);
    }

    this.targetProperty = targetProperty;
    this.sourceProperty = sourceProperty;
    this.converter = converter;
    this.isLoggingEnabled = false;
    this.initializeBinding();
  }

  @SuppressWarnings("unchecked")
  private void applySourceToTarget() {
    try {
      if (!(this.mode == BindingMode.TWO_WAY || this.mode == BindingMode.ONE_WAY)) {
        return;
      }
      if (this.sourceProperty.getGetter() == null || this.targetProperty.getSetter() == null) {
        return;
      }
      Object sourceValue = Trackable.track(this.sourceTracker, this.sourceProperty.getGetter());
      Object convertedValue = this.converter.convertToTarget(sourceValue,
          this.targetProperty.getType());
      ((Property<Object>) this.targetProperty).setValue(convertedValue);
    } catch (Exception e) {
      if (this.isLoggingEnabled) {
        System.err.println("Ignored exception in applySourceToTarget");
        System.err.println(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void applyTargetToSource() {
    try {
      if (!(this.mode == BindingMode.TWO_WAY || this.mode == BindingMode.ONE_WAY_TO_SOURCE)) {
        return;
      }
      if (this.targetProperty.getGetter() == null || this.sourceProperty.getSetter() == null) {
        return;
      }
      Object targetValue = Trackable.track(this.targetTracker, this.targetProperty.getGetter());
      Object convertedValue = this.converter.convertToSource(targetValue,
          this.sourceProperty.getType());
      ((Property<Object>) this.sourceProperty).setValue(convertedValue);
    } catch (Exception e) {
      if (this.isLoggingEnabled) {
        System.err.println("Ignored exception in applyTargetToSource");
        System.err.println(e);
      }
    }
  }

  /**
   * @return The BindingMode for this binding.
   */
  public BindingMode getMode() {
    return this.mode;
  }

  WeakReference<Binding> getWeakRef() {
    return this.weakToMe;
  }

  private void initializeBinding() {
    this.applySourceToTarget();
    this.applyTargetToSource();
  }

  /**
   * @return Whether logging is enabled.
   */
  public boolean isLoggingEnabled() {
    return this.isLoggingEnabled;
  }

  /**
   * Sets whether logging to the default error stream when errors occur in applying a binding.
   * 
   * @param log
   *          Whether logging is enabled.
   */
  public void setIsLoggingEnabled(boolean log) {
    this.isLoggingEnabled = log;
  }
}
