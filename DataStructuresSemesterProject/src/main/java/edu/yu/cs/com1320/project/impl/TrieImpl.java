package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

    private static final int alphabetSize = 256;
    private Node<Value> root; // root of trie

    private class Node<Value> {
        Set<Value> val = new HashSet<>();
        TrieImpl<Value>.Node<Value>[] links = new Node[alphabetSize];
    }

    public TrieImpl() {

        this.root = new Node<>();
    }

    @Override
    public void put(String key, Value val) {
        this.root = put(this.root, key, val, 0);
    }

    private Node<Value> put(Node<Value> x, String key, Value val, int d) {
        //create a new node
        if (x == null) {
            x = new Node<>();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length()) {
            x.val.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    @Override
    public List<Value> getSorted(String key, Comparator<Value> comparator) {
        List<Value> list = new ArrayList<>(this.get(key));
        list.sort(comparator);
        return list;
    }

    @Override
    public Set<Value> get(String key) {
        Node x = this.get(this.root, key, 0);
        if (x == null) {
            return Collections.emptySet();
        }
        return (Set<Value>) x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            return x;
        }
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        List<Value> returnlist = new ArrayList<>();
        this.root = getAllWithPrefixSortedprivate(this.root, prefix, 0, returnlist);
        Set s = new HashSet<>(returnlist);
        List<Value> finalreturn = new ArrayList<>(s);
        finalreturn.sort(comparator);
        return finalreturn;

    }


    private Node<Value> getAllWithPrefixSortedprivate(Node<Value> x, String key, int d, List<Value> list) {
        if (x == null) {
            x = new Node<>();
        }

        if (d == key.length()) {
            postordertraversalgetsorted(x, list);
        }

        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.getAllWithPrefixSortedprivate(x.links[c], key, d + 1, list);
        }
        return x;
    }

    private void postordertraversalgetsorted(Node<Value> x, List<Value> lst) {
        if (x.val != null) {
            for (Value v : x.val) {
                lst.add(v);
            }
        }
        for (Node<Value> z : getChildren(x)) {
            if (z.val != null) {
                for (Value v : z.val) {
                    lst.add(v);
                }
            }
            for (int y = 0; y < alphabetSize; y++) {
                if (z.links[y] != null && z.links[y].val != null) {
                    for (Value v : z.links[y].val) {
                        lst.add(v);
                    }
                }
            }
            postordertraversalgetsorted(z, lst);
        }

    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        Set<Value> f = new HashSet<>();
        this.root = deleteAllWithPrefixprivate(this.root, prefix, 0, f);
        return f;
    }

    private Node<Value> deleteAllWithPrefixprivate(Node<Value> x, String key, int d, Set<Value> set) {
        if (x == null) {
            return null;
        }
        //we're at the node to del - delete the subtree
        if (d == key.length()) {
            postordertraversaldelete(x, set);
        }

        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.deleteAllWithPrefixprivate(x.links[c], key, d + 1, set);
        }
        if (x.val != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c <TrieImpl.alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    private void postordertraversaldelete(Node<Value> x, Set<Value> set) {
        if (x.val != null) {
            for (Value v : x.val) {
                set.add(v);
            }
            x.val = null;
        }
        for (Node<Value> z : getChildren(x)) {
            postordertraversaldelete(z, set);
        }
        for (int y = 0; y < alphabetSize; y++) {
            if (x.links[y] != null && x.links[y].val != null) {
                for (Value v : x.links[y].val) {
                    set.add(v);
                }
                x.links[y].val = null;
            }
        }
    }

    private List<Node<Value>> getChildren(Node<Value> x) {
        List<Node<Value>> list = new ArrayList<>();
        for (int f = 0; f < alphabetSize; f++) {
            if (x.links[f] != null) {
                list.add(x.links[f]);
            }
        }
        return list;
    }

    @Override
    public Set<Value> deleteAll(String key) {
        Set<Value> ret = new HashSet<>();
        this.root = deleteAll(this.root, key, 0, ret);
        return ret;
    }

    private Node<Value> deleteAll(Node x, String key, int d, Set<Value> set) {
        if (x == null) {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()) {
            set.addAll( x.val);
            x.val = null;
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1, set);
        }
        if (x.val != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c <TrieImpl.alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }


    @Override
    public Value delete(String key, Value val) {
        Node<Value> s = delete(this.root, key, val, 0);
        if (s == null) {
            return null;
        } else {
            this.root = s;
            return val;
        }
    }

    private Node<Value> delete(Node<Value> x, String key, Value val, int d) {
        if (x == null) {
            return null;
        }
        //we're at the node to del - remove the specific value
        if (d == key.length()) {
            x.val.remove(val);
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.delete(x.links[c], key, val, d + 1);
        }
        if (x.val != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c <TrieImpl.alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
}

