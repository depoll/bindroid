package com.bindroid.utils;

/**
 * Represents a pair of objects that can be used as a compound key for a {@link HashMap} or
 * {@link HashSet}.
 * 
 * @param <T1>
 *          the type of the left value.
 * @param <T2>
 *          the type of the right value.
 */
public class Pair<T1, T2> {
  private T1 left;
  private T2 right;

  /**
   * Creates a pair.
   * 
   * @param left
   *          the left value of the pair.
   * @param right
   *          the right value of the pair.
   */
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

  /**
   * @return the left value.
   */
  public T1 getLeft() {
    return this.left;
  }

  /**
   * @return the right value.
   */
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
