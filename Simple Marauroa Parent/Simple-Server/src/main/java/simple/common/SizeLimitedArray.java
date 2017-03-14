package simple.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 * @param <T> Type of array.
 */
public class SizeLimitedArray<T> implements java.util.List<T> {

    private static final int DEFAULT_SIZE_LIMIT = 10;
    private ArrayList<T> myList;
    private int maxSize;

    public SizeLimitedArray() {
        this(DEFAULT_SIZE_LIMIT);
    }

    public SizeLimitedArray(int size) {
        myList = new ArrayList<>(size);
        maxSize = size;
    }

    @Override
    public boolean add(T objectToAdd) {
        if (myList.size() > maxSize) {
            throw new IllegalStateException("The array is full");
        }
        if (myList.size() == maxSize) {
            myList.remove(0);
        }
        return myList.add(objectToAdd);
    }

    @Override
    public boolean addAll(Collection collectionToAdd) {
        if (myList.size() + collectionToAdd.size() > maxSize) {
            throw new IllegalStateException("The array is full");
        }
        return myList.addAll(collectionToAdd);
    }

    @Override
    public int size() {
        return myList.size();
    }

    @Override
    public boolean isEmpty() {
        return myList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return myList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return myList.iterator();
    }

    @Override
    public Object[] toArray() {
        return myList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return myList.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return myList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return myList.containsAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return myList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return myList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return myList.retainAll(c);
    }

    @Override
    public void clear() {
        myList.clear();
    }

    @Override
    public T get(int index) {
        return myList.get(index);
    }

    @Override
    public T set(int index, T element) {
        return myList.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        if (myList.size() == maxSize) {
            myList.remove(0);
        }
        myList.add(index, element);
    }

    @Override
    public T remove(int index) {
        return myList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return myList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return myList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return myList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return myList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return myList.subList(fromIndex, toIndex);
    }
}
