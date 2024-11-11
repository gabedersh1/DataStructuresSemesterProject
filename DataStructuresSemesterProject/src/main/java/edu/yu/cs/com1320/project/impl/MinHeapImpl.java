package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {


    public MinHeapImpl() {
        this.elements = (E[]) new Comparable[20];
    }

    @Override
    public void reHeapify(E element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        int x = this.getArrayIndex(element);
        this.downHeap(x);
        this.upHeap(x);
        }
    @Override
    protected int getArrayIndex(E element)  {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        int len = this.elements.length;
        int i = 1;
        while (i < len) {
            if (this.elements[i] != null && this.elements[i].equals(element)) {
                return i;
            } else {
                i++;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    protected void doubleArraySize() {
        E[] temp = (E[]) new Comparable[2 * this.elements.length];
        for (int i = 0; i < elements.length; i++) {
            temp[i] = elements[i];
        }
        this.elements = temp;
    }

}
