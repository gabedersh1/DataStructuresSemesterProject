package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    T [] arraystack;
    int top = -1;

    public StackImpl() {
        this.arraystack = (T[]) new Object[100];
    }
    @Override
    public void push(T element) {
        if (top < arraystack.length-1) {
            top++;
            arraystack[top] = (T) element;
        }
    }

    @Override
    public T pop() {
        if (top == -1) {
            return null;
        }
        T item = arraystack[top];
        arraystack[top] = null;
        top--;
        return item;

    }

    @Override
    public T peek() {
        if (top == -1) {
            return null;
        }
        T peek = arraystack[top];
        return peek;
    }

    @Override
    public int size() {
        int size = 0;
        for (int y = 0; y < arraystack.length; y++) {
            if (arraystack[y] != null) {
                size++;
            }
        }
        return size;
    }
}
