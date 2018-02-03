package com.bindroid.utils;

/**
 * An object representing a function that takes no parameters and has a return value.
 *
 * @param <T> the return type of the method.
 */
public interface Function<T> {
    /**
     * The function to evaluate.
     *
     * @return the result of the evaluation.
     */
    T evaluate();
}
