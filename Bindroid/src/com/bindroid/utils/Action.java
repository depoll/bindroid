package com.bindroid.utils;

public interface Action<T> {
  void invoke(T parameter);
}
