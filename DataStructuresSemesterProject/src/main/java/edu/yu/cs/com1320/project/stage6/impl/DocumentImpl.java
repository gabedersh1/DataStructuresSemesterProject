package edu.yu.cs.com1320.project.stage6.impl;


import edu.yu.cs.com1320.project.stage6.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document {
    HashMap<String, String> meta;
    URI uri;
    String text;
    byte[] binaryData;
    Map<String, Integer> hashMapwords;
    long lastusetime;

    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordcountMap) {
        if (uri == null || uri.toString().isEmpty() || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (txt == null || txt.isBlank() || txt.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.uri = uri;
        this.meta = new HashMap<>();
        this.text = txt;
        if (wordcountMap == null) {
            this.hashMapwords = new HashMap<>();
            createhashMap(this.getDocumentTxt());
        } else {
            this.hashMapwords = wordcountMap;
        }
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || uri.toString().isEmpty() || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (binaryData.length == 0) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
        this.meta = new HashMap<>();

    }

    @Override
    public String setMetadataValue(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (key.isBlank() || key.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String oldvalue = this.getMetadataValue(key);
        this.meta.put(key, value);
        return oldvalue;
    }


    @Override
    public String getMetadataValue(String key) {
        if (key == null || key.isEmpty() || key.isBlank()) {
            throw new IllegalArgumentException();
        }
        return this.getMetadata().get(key);
    }

    @Override
    public HashMap<String, String> getMetadata() {
        if (meta == null) {
            return null;
        }
        return (HashMap<String, String>) meta.clone();
    }


    @Override
    public void setMetadata(HashMap<String, String> metadata) {
        this.meta = metadata;
    }

    @Override
    public String getDocumentTxt() {
        return this.text;
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public int wordCount(String word) {
        if (hashMapwords.get(word) == null) {
            return 0;
        }
        return hashMapwords.get(word);
    }

    @Override
    public Set<String> getWords() {
        if (this.hashMapwords == null) {
            return null;
        }
        return hashMapwords.keySet();
    }

    @Override
    public long getLastUseTime() {
        return this.lastusetime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.lastusetime = timeInNanoseconds;
    }

    @Override
    public HashMap<String, Integer> getWordMap() {
        if (hashMapwords != null) {
            return new HashMap<>(hashMapwords);
        }
        return null;
    }

    @Override
    public void setWordMap(HashMap<String, Integer> wordMap) {

    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return Math.abs(result);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DocumentImpl)) {
            return false;
        }
        return this.hashCode() == object.hashCode();
    }

    private String strippunctuation(String text) {
        String strip = "";
        for (Character c : text.toCharArray()) {
            if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
                strip += c;
            }
        }
        return strip;
    }

    private void createhashMap(String text1) {
        String text = strippunctuation(text1);
        for (String word : text.split(" ")) {
            if (hashMapwords.containsKey(word)) {
                hashMapwords.put(word, (hashMapwords.get(word) + 1));
            } else {
                hashMapwords.put(word, 1);
            }
        }
    }


    @Override
    public int compareTo(Document o) {
        if (this.lastusetime == o.getLastUseTime()) {
            return 0;
        } else if (this.lastusetime > o.getLastUseTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}


