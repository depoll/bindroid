package com.bindroid;

/**
 * Specifies the direction of a binding.
 */
public enum BindingMode {
    /**
     * Makes a one-way binding where the target's properties are set whenever the source's values
     * change. The binding weakly references the source.
     */
    ONE_WAY,
    /**
     * Makes a one-way binding where the source's properties are set whenever the target's values
     * change. The binding weakly references the target.
     */
    ONE_WAY_TO_SOURCE,
    /**
     * Makes a two-way binding that keeps the source and target properties in sync. The binding
     * strongly references both the source and target.
     */
    TWO_WAY
}
