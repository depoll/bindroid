package com.bindroid;

import java.lang.ref.WeakReference;

import com.bindroid.utils.Property;
import com.bindroid.utils.WeakenedProperty;

public class Binding {
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

    public void update() {
      applySourceToTarget();
    }
  }

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

    public void update() {
      applyTargetToSource();
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

  public Binding(Property<?> targetProperty, Property<?> sourceProperty) {
    this(targetProperty, sourceProperty, BindingMode.OneWay);
  }

  public Binding(Property<?> targetProperty, Property<?> sourceProperty, BindingMode mode) {
    this(targetProperty, sourceProperty, mode, ValueConverter.getDefaultConverter());
  }

  public Binding(Property<?> targetProperty, Property<?> sourceProperty, BindingMode mode,
      ValueConverter converter) {
    this.weakToMe = new WeakReference<Binding>(this);
    this.mode = mode;

    this.sourceTracker = new SourceTracker(sourceProperty);
    this.targetTracker = new TargetTracker(targetProperty);

    // Weaken the property references to allow the source/target to be GC'd if all other references
    // are gone.
    if (mode == BindingMode.OneWay) {
      sourceProperty = WeakenedProperty.weaken(sourceProperty);
    } else if (mode == BindingMode.OneWayToSource) {
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
      if (!(this.mode == BindingMode.TwoWay || this.mode == BindingMode.OneWay)) {
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
      if (!(this.mode == BindingMode.TwoWay || this.mode == BindingMode.OneWayToSource)) {
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

  public ValueConverter getConverter() {
    return this.converter;
  }

  public boolean getIsLoggingEnabled() {
    return this.isLoggingEnabled;
  }

  public BindingMode getMode() {
    return this.mode;
  }

  public Property<?> getSourceProperty() {
    return this.sourceProperty;
  }

  public Property<?> getTargetProperty() {
    return this.targetProperty;
  }

  public WeakReference<Binding> getWeakRef() {
    return this.weakToMe;
  }

  private void initializeBinding() {
    this.applySourceToTarget();
    this.applyTargetToSource();
  }

  public void setIsLoggingEnabled(boolean log) {
    this.isLoggingEnabled = log;
  }
}
