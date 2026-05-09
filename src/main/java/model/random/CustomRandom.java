package model.random;

import java.util.List;
import java.util.ListIterator;

public class CustomRandom {
  private int modulo;
  private int coefficient;
  private int increment;
  private int seed;

  public CustomRandom(int modulo, int coefficient, int increment, int seed) {
    this.modulo = modulo;
    this.coefficient = coefficient;
    this.increment = increment;
    this.seed = seed;
  }

  public int nextInt(int limit) {
    seed = (coefficient * seed + increment)%modulo;
    return seed%limit;
  }

  public double nextDouble() {
    seed = (coefficient * seed + increment)%modulo;
    return seed * 1.0 / modulo;
  }

  public void shuffle(List<?> list) {
    Object[] arr = list.toArray();
    int size = list.size();

    // Shuffle array
    for (int i=size; i>1; i--)
      swap(arr, i-1, nextInt(i));

    // Dump array back into list
    // instead of using a raw type here, it's possible to capture
    // the wildcard but it will require a call to a supplementary
    // private method
    ListIterator it = list.listIterator();
    for (Object e : arr) {
      it.next();
      it.set(e);
    }

  }

  private static void swap(Object[] arr, int i, int j) {
    Object tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

}
