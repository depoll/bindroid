package com.bindroid.utils;

public class Pair<T1, T2> {
  private T1 left;
  private T2 right;

  public Pair(T1 left, T2 right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Pair)) {
      return false;
    }
    Pair<?, ?> otherPair = (Pair<?, ?>) other;
    return ObjectUtilities.equals(this.getLeft(), otherPair.getLeft())
        && ObjectUtilities.equals(this.getRight(), otherPair.getRight());
  }

  public T1 getLeft() {
    return this.left;
  }

  public T2 getRight() {
    return this.right;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    if (this.getLeft() != null) {
      hash ^= this.getLeft().hashCode();
    }
    if (this.getRight() != null) {
      hash ^= this.getRight().hashCode();
    }
    return hash;
  }
}
