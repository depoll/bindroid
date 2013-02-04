package com.bindroid;

public enum BindingMode {
  /**
   * Makes a one-way binding where the target's properties are set whenever the source's values
   * change. The binding weakly references the source.
   */
  OneWay,
  /**
   * Makes a one-way binding where the source's properties are set whenever the target's values
   * change. The binding weakly references the target.
   */
  OneWayToSource,
  /**
   * Makes a two-way binding that keeps the source and target properties in sync. The binding
   * strongly references both the source and target.
   */
  TwoWay
}
