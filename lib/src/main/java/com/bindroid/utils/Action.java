package com.bindroid.utils;

/**
 * An object representing a void function that takes a single parameter.
 * 
 * @param <T>
 *          the type of the parameter.
 */
public interface Action<T> {
  /**
   * The method to invoke.
   * 
   * @param parameter
   *          the parameter to the action.
   */
  void invoke(T parameter);
}
