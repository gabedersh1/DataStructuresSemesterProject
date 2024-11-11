package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {
    private BTreeImpl<URI, Document> docstore;
    private StackImpl<Undoable> commandstack;
    TrieImpl<btreelookerupper> trieimpl;
    TrieImpl<btreelookerupper> triemetadata;
    MinHeapImpl<btreelookerupper> minHeap;
    int maxdoccount;
    int maxdocbytes;
    int numdocsinmemory;
    HashSet<btreelookerupper> alldocsinmemory;
    Map<URI, Map<String, String>> copyofmetadata;
    File direc;

    public DocumentStoreImpl() {
        this.docstore = new BTreeImpl<URI, Document>();
        this.commandstack = new StackImpl<>();
        this.trieimpl = new TrieImpl<>();
        this.triemetadata = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.alldocsinmemory = new HashSet<>();
        docstore.setPersistenceManager(new DocumentPersistenceManager(null));
        this.copyofmetadata = new HashMap<>();
        this.direc = new File(System.getProperty("user.dir"));
    }

    public DocumentStoreImpl(File baseDir){
        this.docstore = new BTreeImpl<URI, Document>();
        this.commandstack = new StackImpl<>();
        this.trieimpl = new TrieImpl<>();
        this.triemetadata = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.alldocsinmemory = new HashSet<>();
        docstore.setPersistenceManager(new DocumentPersistenceManager(baseDir));
        this.copyofmetadata = new HashMap<>();
        this.direc = baseDir;
    }

    private class btreelookerupper implements Comparable<btreelookerupper> {
        URI id;
        BTreeImpl<URI, Document> tree;
        Set <String> wordlist = new HashSet<>();
        Map <String, String> metadatacopy = new HashMap<>();
        String doctxt;
        byte[] bindata;

        private btreelookerupper(URI uri, BTreeImpl treetree) {
            this.id = uri;
            this.tree = treetree;
            for (btreelookerupper b : alldocsinmemory) {
                if (b.getid() == uri) {
                    if (b.getdoctext() != null) {
                        this.doctxt = b.getdoctext();
                    }
                    if (b.getBindata() != null) {
                        this.bindata = b.getBindata();
                    }

                }
            }

        }

        public void setwordlist(Set<String> list) {
            this.wordlist = list;
        }

        private Set<String> getwordlist() {
            return this.wordlist;
        }

        public void setdocumenttext(String txt) {
            this.doctxt = txt;
        }

        private String getdoctext() {
            return this.doctxt;
        }

        public void setbinarydata(byte[] binaryData) {
            this.bindata = binaryData;
        }
        private byte[] getBindata() {
            return this.bindata;
        }

        public void setMetadatacopy (Map <String, String> metamap) {
            this.metadatacopy =  metamap;
        }

        public Map<String, String> getMetadatacopy() {
            return this.metadatacopy;
        }

        private URI getid() {
            return this.id;
        }


        public Document getdoc() {
            boolean ondisk = Files.exists(Paths.get(uritopath(this.getid())));
            if (ondisk) {
               tree.get(id);
               tree.get(id).setLastUseTime(System.nanoTime());
               minHeap.insert(new btreelookerupper(this.getid(), this.tree));
               minHeap.reHeapify(new btreelookerupper(this.getid(), this.tree));
               numdocsinmemory++;
               alldocsinmemory.add(new btreelookerupper(this.getid(), this.tree));
               checkstoragelimitsonput(this, false);

            }
            return tree.get(id);
        }

        @Override
        public int compareTo(btreelookerupper o) {
            if (this.getdoc().getLastUseTime() == o.getdoc().getLastUseTime()) {
                return 0;
            } else if (this.getdoc().getLastUseTime() > o.getdoc().getLastUseTime()) {
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        public boolean equals (Object o) {
            if (!(o instanceof btreelookerupper)) {
                return false;
            }
            return Objects.equals(this.getid().toString(), ((btreelookerupper) o).getid().toString());
        }

        @Override
        public int hashCode() {
            return this.getid().hashCode();
        }
    }

    @Override
    public String setMetadata(URI uri, String key, String value) throws IOException{
        if (key == null || key.isBlank() || key.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (uri == null || uri.hashCode() == 0 || uri.toString().isEmpty() || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }

        String prev = this.get(uri).getMetadataValue(key);

        GenericCommand<URI> setmetadatacommand = new GenericCommand<URI>(uri, (URI undo) -> {
            try {
                this.get(undo).setMetadataValue(key, prev);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.triemetadata.delete(key + value, new btreelookerupper(uri, this.docstore));
        } );
        commandstack.push(setmetadatacommand);
        this.triemetadata.put(key + value, new btreelookerupper(uri, this.docstore));
        this.get(uri).setLastUseTime(System.nanoTime());
        minHeap.reHeapify(new btreelookerupper(uri, this.docstore));
        String s = this.get(uri).setMetadataValue(key, value);
        this.copyofmetadata.put(uri, this.get(uri).getMetadata());
        return s;
    }

    @Override
    public String getMetadata(URI uri, String key) throws IOException {
        if (key == null || key.isBlank() || key.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (uri == null || uri.hashCode() == 0 || uri.toString().isEmpty() || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (this.get(uri) == null) {
            return null;
        }
        String string = this.get(uri).getMetadataValue(key);
        this.get(uri).setLastUseTime(System.nanoTime());
        minHeap.reHeapify(new btreelookerupper(uri, this.docstore));
        return string;

    }

    @Override
    public int put(InputStream input, URI url, DocumentFormat format) throws IOException {
        if (format == null || url == null || url.hashCode() == 0 || url.toString().isEmpty() || url.toString().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (input == null) {
            return this.deletecommand(url);
        }

        byte[] bytes;
        try { bytes = input.readAllBytes();
        } catch (Exception e) { throw new IOException();}
        input.close();
        DocumentImpl newdoc = null;
        boolean overwrite = false;
        for (btreelookerupper b : alldocsinmemory ) {
            if (b.getid() == url) {
                overwrite = true;
            }
        }
            if (Files.exists(Paths.get(uritopath(url)))) {
                overwrite = true;
            }
        HashSet<btreelookerupper> currentstate = (HashSet<btreelookerupper>) alldocsinmemory.clone();
        Document v = null;
        boolean wasondisk = Files.exists(Paths.get(uritopath(url)));
        if (wasondisk) {
            v = this.get(url);
        }
        if (overwrite) {
            this.delete(url);
        }
        if (format == DocumentFormat.BINARY) {
            newdoc = new DocumentImpl(url, bytes);
            newdoc.setLastUseTime(System.nanoTime());
            if (!overwrite) {
                docstore.put(url, newdoc);
            } else {
                v = docstore.put(url, newdoc);
            }
            numdocsinmemory++;
            btreelookerupper copy = new btreelookerupper(newdoc.getKey(), this.docstore);
            copy.setbinarydata(newdoc.getDocumentBinaryData());
            placeinmetatrie(copy);
            minHeap.insert(copy);
            alldocsinmemory.add(copy);
            checkstoragelimitsonput(copy, false);
        }
        if (format == DocumentFormat.TXT) {
            String str = new String(bytes);
            newdoc = new DocumentImpl(url, str, null);
            newdoc.setLastUseTime(System.nanoTime());
            docstore.put(url, newdoc);
            numdocsinmemory++;
            btreelookerupper copy = new btreelookerupper(newdoc.getKey(), this.docstore);
            copy.setwordlist(newdoc.getWords());
            copy.setdocumenttext(newdoc.getDocumentTxt());
            for (String word: newdoc.getWords()) {
                trieimpl.put(word, copy);
            }
            placeinmetatrie(copy);
            minHeap.insert(copy);
            alldocsinmemory.add(copy);
            checkstoragelimitsonput(copy, false);
        }

        Set<btreelookerupper> movedtodiskonthisoperation = new HashSet<>();
        for (btreelookerupper b : currentstate) {
            if (!alldocsinmemory.contains(b)){
                movedtodiskonthisoperation.add(b);
            }
        }

        DocumentImpl doc = (DocumentImpl) docstore.get(url);
        putaddtostack(url, format, v, doc, overwrite, wasondisk, movedtodiskonthisoperation);
        return dochashcode(v);
    }

    private void putaddtostack(URI url, DocumentFormat format, Document olddoc, Document newdoc, boolean isoverwrite, boolean wasthisonndisk, Set<btreelookerupper> movedtodisk) {
        if (isoverwrite) {
            GenericCommand<URI> overwritecommand = new GenericCommand<>(url, (URI undo) -> {
                docstore.put(undo, olddoc);
                if (wasthisonndisk) {
                    try {
                        docstore.moveToDisk(undo);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (String key: olddoc.getMetadata().keySet()) {
                    triemetadata.put(key + olddoc.getMetadataValue(key), new btreelookerupper(olddoc.getKey(), this.docstore));
                }
                if (format == DocumentFormat.TXT) {
                    for (String word: olddoc.getWords()) {
                        trieimpl.put(word, new btreelookerupper(olddoc.getKey(), this.docstore));}
                }
                for (String key: newdoc.getMetadata().keySet()) {
                    triemetadata.delete(key + newdoc.getMetadataValue(key), new btreelookerupper(newdoc.getKey(), this.docstore));
                }
                for (btreelookerupper b : movedtodisk) {
                    b.getdoc();
                }
                if (format == DocumentFormat.TXT) {
                    for (String word: newdoc.getWords()) {
                        trieimpl.delete(word, new btreelookerupper(newdoc.getKey(), this.docstore));}}});
            this.commandstack.push(overwritecommand);
            return;}
        GenericCommand<URI> putcommand = new GenericCommand<>(url, (URI undo) -> {
            Path p = Paths.get(uritopath(undo));
            btreelookerupper nd = new btreelookerupper(newdoc.getKey(), this.docstore);
            deletespecificdocfromheap(nd);
            numdocsinmemory--;
            docstore.put(undo, null);
            for (String key: newdoc.getMetadata().keySet()) {
                triemetadata.delete(key + newdoc.getMetadataValue(key), nd);
            }
            if (format == DocumentFormat.TXT) {
                for (String word: newdoc.getWords()) {
                    trieimpl.delete(word, nd);}
            }
            for (btreelookerupper b : movedtodisk) {
                b.getdoc();
            }
        });
        this.commandstack.push(putcommand);
    }

    private void checkstoragelimitsonput(btreelookerupper d, boolean undo) {
        if (maxdoccount != 0) {
            if (numdocsinmemory > maxdoccount) {
                while (numdocsinmemory > maxdoccount) {
                    moveleastusedtodisk();
                }}
        }
        if (maxdocbytes != 0) {
            if (d.getBindata() != null) {
                int i = d.getBindata().length;
                if (i > maxdocbytes) {
                    if (!undo) {
                        throw new IllegalArgumentException();
                    } else {
                        return;
                    }
                }
                if (getbytesused() > maxdocbytes) {
                    while (getbytesused() > maxdocbytes) {
                        moveleastusedtodisk();
                    }
                }
            }
            if (d.getdoctext() != null) {
                int i = (d.getdoctext()).getBytes().length;
                if (i > maxdocbytes) {
                    if (!undo) {
                        throw new IllegalArgumentException();
                    } else {
                        return;
                    }
                }
                if (getbytesused() > maxdocbytes) {
                    while (getbytesused() > maxdocbytes) {
                        moveleastusedtodisk();
                    }}}}
    }

    private void placeinmetatrie(btreelookerupper copy) {
        if (copy.getMetadatacopy() == null) {
            return;
        }
        for (String key: copy.getMetadatacopy().keySet()) {
            triemetadata.put(key + copy.getdoc().getMetadataValue(key), copy );
        }
    }

    private int deletecommand(URI url) {
        boolean onfile = Files.exists(Paths.get(uritopath(url)));
        Set<btreelookerupper> inmemory = new HashSet<>(alldocsinmemory);
        int i;
        Document old;
        if (this.docstore.get(url) != null) {
            i = this.docstore.get(url).hashCode();
            old = (Document) this.docstore.get(url);
        } else {
            old = null;
            i = 0;
        }
        DocumentImpl doc = (DocumentImpl) this.docstore.get(url);
        if (doc == null) {
            return i;
        }
        btreelookerupper fff = new btreelookerupper(url, this.docstore);
        deletespecificdocfromheap(fff);
        docstore.put(url, null);
        for (String key : doc.getMetadata().keySet()) {
            triemetadata.delete(key + doc.getMetadataValue(key), fff);
        }

        if (doc.getDocumentTxt() != null) {
            for (String word : doc.getWords()) {
                trieimpl.delete(word, fff);
            }
        }

        GenericCommand<URI> deletecommand = new GenericCommand<>(url, (URI undo) -> {
            for (btreelookerupper b : this.alldocsinmemory) {
                if (!inmemory.contains(b)) {
                    try {
                        this.docstore.moveToDisk(b.getid());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            this.docstore.put(url, old);
            checkstoragelimitsonput(fff, true);
            if (onfile) {
                try {
                    this.docstore.moveToDisk(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for (String key: doc.getMetadata().keySet()) {
                triemetadata.put(key + doc.getMetadataValue(key), fff);}
            if (doc.getWords() != null) {
                for (String word : doc.getWords()) {
                    trieimpl.put(word, fff);}
            }
            doc.setLastUseTime(System.nanoTime());
            minHeap.insert(fff);
            minHeap.reHeapify(fff);
            alldocsinmemory.add(fff);
            numdocsinmemory++;
        });
        commandstack.push(deletecommand);
        return i;
    }

    private int dochashcode(Document doc) {
        if (doc == null) {
            return 0;
        } else {
            return doc.hashCode();
        }
    }

    @Override
    public Document get(URI url) throws IOException {
        Path urlfile = Paths.get(uritopath(url));
        try {
            if (Files.exists(urlfile) && Files.size(urlfile) > 7) {
                //a call to get will move the document from disk to memory
                //so make sure there is room in memory
                Document back = docstore.get(url);
                numdocsinmemory++;
                alldocsinmemory.add(new btreelookerupper(url, this.docstore));
                //add it to minheap
                this.minHeap.insert(new btreelookerupper(url, this.docstore));
                this.docstore.get(url).setLastUseTime(System.nanoTime());
                this.minHeap.reHeapify(new btreelookerupper(url, this.docstore));
                //make sure there is memory
                btreelookerupper x = new btreelookerupper(url, this.docstore);
                x.setdocumenttext(back.getDocumentTxt());
                x.setbinarydata(back.getDocumentBinaryData());
                checkstoragelimitsonput(x, false);

            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        if (docstore.get(url) == null) {
            return null;
        }
        Document d = docstore.get(url);
        d.setLastUseTime(System.nanoTime());
        minHeap.reHeapify(new btreelookerupper(url, this.docstore));
        return d;
    }

    @Override
    public boolean delete(URI url) {
        if (Files.exists(Paths.get(uritopath(url)))) {
            File file = new File(uritopath(url));
            try {
                return deleterecursively(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else if (docstore.get(url) != null){
            this.deletecommand(url);
            numdocsinmemory--;
            alldocsinmemory.remove(new btreelookerupper(url, this.docstore));
            return true;
        }
        return false;
    }

    private boolean deleterecursively(File file) throws IOException {
        if (Files.isRegularFile(file.toPath())) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                throw new IOException(e);
            }
        } else if (Files.isDirectory(file.toPath())) {
            try {
                Files.delete(file.toPath());
            } catch (DirectoryNotEmptyException e) {
                return false;
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        if (file.getParentFile() != null) {
            deleterecursively(file.getParentFile());
        }
        return true;
    }


    @Override
    public void undo() throws IllegalStateException {
        if (commandstack.size() == 0) {
            throw new IllegalStateException("There are no commands to be undone");
        }
        CommandSet<URI> times;
        if (commandstack.peek() instanceof CommandSet<?>) {
            List<URI> list = new ArrayList<>();
            for (GenericCommand<URI> uri :(CommandSet<URI>) commandstack.peek()){
                list.add(uri.getTarget());
            }
            (commandstack.pop()).undo();
            long time = System.nanoTime();
            for (URI uri : list) {
                if (currenturilist().contains(uri)) {
                    try {
                        this.get(uri).setLastUseTime(time);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return;
        }
        (commandstack.pop()).undo();

    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        int b = commandstack.size();
        if (commandstack.size() == 0) {
            throw new IllegalStateException("There are no commands to be undone");
        }
        StackImpl<Undoable> tempstack = new StackImpl<>();
        int a = commandstack.size();
        for (int i = 0; i < a; i++) {
            if (commandstack.peek() instanceof CommandSet<?>) {
                if ( ((CommandSet<URI>) commandstack.peek()).containsTarget(url)) {
                    ((CommandSet<URI>) commandstack.pop()).undo(url);
                    return;
                }
            }
            if (commandstack.peek() instanceof GenericCommand<?> && ((GenericCommand<?>) commandstack.peek()).getTarget() == url) {
                commandstack.pop().undo();
                return;
            } else {
                tempstack.push(commandstack.pop());
            }
        }
        int c = tempstack.size();
        for (int z = 0; z < c; z++) {
            commandstack.push(tempstack.pop());
        }
        if (commandstack.size() == b) {
            throw new IllegalStateException();
        }
    }

    @Override
    public List<Document> search(String keyword) throws IOException {
        btreelookeruppercomparator comparator = new btreelookeruppercomparator(keyword, this);
        List<Document> list = new ArrayList<>();
        long nano = System.nanoTime();
        for (btreelookerupper b : trieimpl.getSorted(keyword,comparator)) {
            b.getdoc().setLastUseTime(nano);
            minHeap.reHeapify(b);
            list.add(b.getdoc());

        }
        return list;
    }

    private List<btreelookerupper> searchbutdontupdatenanotime(String keyword) {
        btreelookeruppercomparator comparator = new btreelookeruppercomparator(keyword, this);
        List<btreelookerupper> list = new ArrayList<>();
        for (btreelookerupper b : trieimpl.getSorted(keyword,comparator)) {
            list.add(b);
        }
        return list;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) throws IOException {
        btreelookeruppercomparator comparator = new btreelookeruppercomparator(keywordPrefix, this);
        List<Document> list = new ArrayList<>();
        for (btreelookerupper b: trieimpl.getAllWithPrefixSorted(keywordPrefix, comparator)) {
            list.add(b.getdoc());
        }
        long nano = System.nanoTime();
        for (Document d : list) {
            d.setLastUseTime(nano);
            minHeap.reHeapify(new btreelookerupper(d.getKey(), this.docstore));
        }
        return list;
    }

    private List<btreelookerupper> searchbyprefixbutdontupdatenanotime(String keywordPrefix) {
        btreelookeruppercomparator comparator = new btreelookeruppercomparator(keywordPrefix, this);
        List<btreelookerupper> list = new ArrayList<>();
        for (btreelookerupper btlu :trieimpl.getAllWithPrefixSorted(keywordPrefix, comparator)) {
            list.add(btlu);
        }
        return list;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        CommandSet<URI> t = new CommandSet<>();
        List<Document> set = null;
        try {
            set = this.search(keyword);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Document d : set) {
            boolean onfile = Files.exists(Paths.get(uritopath(d.getKey())));
            btreelookerupper ford = new btreelookerupper(d.getKey(), this.docstore);
            //delete from heap
            for (String key: d.getMetadata().keySet()) {
                triemetadata.delete(key + d.getMetadataValue(key), ford );
            }
            trieimpl.delete(keyword, ford);
            this.delete(d.getKey());

            GenericCommand<URI> com = new GenericCommand<>(d.getKey(), (URI url) -> {
                for (String key: d.getMetadata().keySet()) {
                    this.triemetadata.put(key + d.getMetadataValue(key), ford);
                }
                trieimpl.put(keyword, ford);
                docstore.put(d.getKey(), d);
                if (onfile) {
                    try {
                        this.docstore.moveToDisk(url);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                d.setLastUseTime(System.nanoTime());
                numdocsinmemory++;
                alldocsinmemory.add(ford);
                minHeap.insert(ford);
                minHeap.reHeapify(ford);
                checkstoragelimitsonput(ford, true);

            });
            t.addCommand(com);
        }
        Set<URI> uriset = new HashSet<>();
        for (Document d : set) {
            uriset.add(d.getKey());
        }
        commandstack.push(t);
        return uriset;
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        CommandSet<URI> t = new CommandSet<>();
        List<Document> set = null;
        try {
            set = this.searchByPrefix(keywordPrefix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Document d : set) {
            boolean onfile = Files.exists(Paths.get(uritopath(d.getKey())));
            btreelookerupper ford = new btreelookerupper(d.getKey(), this.docstore);
            //delete from heap
            this.delete(d.getKey());
            for (String key: d.getMetadata().keySet()) {
                triemetadata.delete(key + d.getMetadataValue(key), ford);}
            for (String word: d.getWords()) {
                if(word.startsWith(keywordPrefix)) {
                    trieimpl.delete(word, ford);}}

            GenericCommand<URI> com = new GenericCommand<>(d.getKey(), (URI url) -> {
                    for (String key: d.getMetadata().keySet()) {
                        this.triemetadata.put(key + d.getMetadataValue(key), ford);
                    }
                    for (String word: d.getWords()) {
                        if(word.startsWith(keywordPrefix)) {
                            trieimpl.put(word, ford);}}
                    docstore.put(d.getKey(), d);
                    if (onfile) {
                        try {
                            docstore.moveToDisk(url);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    d.setLastUseTime(System.nanoTime());
                    numdocsinmemory++;
                    alldocsinmemory.add(ford);
                    minHeap.insert(ford);
                    minHeap.reHeapify(ford);
                    checkstoragelimitsonput(ford, true);

            });
                t.addCommand(com);
        }
        Set<URI> uriset = new HashSet<>();
        for (Document d : set) {
            uriset.add(d.getKey());
        }
        for (URI uri : uriset) {
            this.delete(uri);
        }
        commandstack.push(t);
        return uriset;
    }

    @Override
    public List<Document> searchByMetadata(Map<String,String> keysValues) throws IOException {
        ArrayList<btreelookerupper> meta = new ArrayList<>();
        // add all the docs with at least one correct key-value pairs to a list

        for (Map.Entry entry: keysValues.entrySet()) {
            for (btreelookerupper d : (triemetadata.get((String) entry.getKey() + (String) entry.getValue()))){
                meta.add(d);
            }
        }
        // determine which of the docs in the list appeared for all key-value pairings; ie,
        // which docs appear as many times as the amount of key-value pairings there are
        Set<Document> list = new HashSet<>();
        for (btreelookerupper b : meta) {
            if (Collections.frequency(meta, b) == keysValues.size()) {
                list.add(b.getdoc());
            }
        }
        Set r = new HashSet<>(list);
        List<Document> returnlist = new ArrayList<>(r);
        long nano = System.nanoTime();
        for (Document d : returnlist) {
            d.setLastUseTime(nano);
            minHeap.reHeapify(new btreelookerupper(d.getKey(), this.docstore));
        }
        return returnlist;
    }

    private List<btreelookerupper> searchByMetadatabuutdontupdatenanotime(Map<String,String> keysValues) {
        ArrayList<btreelookerupper> meta = new ArrayList<>();
        for (Map.Entry entry: keysValues.entrySet()) {
            for (btreelookerupper d : (triemetadata.get((String) entry.getKey() + (String) entry.getValue()))){
                meta.add(d);
            }
        }
        List<btreelookerupper> list = new ArrayList<>();
        for (btreelookerupper b : meta) {
            if (Collections.frequency(meta, b) == keysValues.size()) {
                list.add(b);
            }
        }
        Set r = new HashSet<>(list);
        List<btreelookerupper> returnlist = new ArrayList<>(r);
        return returnlist;
    }

    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String,String> keysValues) throws IOException {
        Set<btreelookerupper> s = new HashSet<>();
        for (btreelookerupper d : this.searchbutdontupdatenanotime(keyword)) {
            if (this.searchByMetadatabuutdontupdatenanotime(keysValues).contains(d)) {
                s.add(d);
            }
        }
        List<Document> returnlist = new ArrayList<>();
        for (btreelookerupper b : s) {
            returnlist.add(b.getdoc());
        }
        long nano = System.nanoTime();
        for (Document d : returnlist) {
            d.setLastUseTime(nano);
            minHeap.reHeapify(new btreelookerupper(d.getKey(), this.docstore));
        }
        return returnlist;
    }

    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String,String> keysValues) throws IOException {
        Set<btreelookerupper> s = new HashSet<>();
        for (btreelookerupper d : this.searchbyprefixbutdontupdatenanotime(keywordPrefix)) {
            if (this.searchByMetadatabuutdontupdatenanotime(keysValues).contains(d)) {
                s.add(d);
            }
        }
        List<Document> returnlist = new ArrayList<>();
        for (btreelookerupper b: s) {
            returnlist.add(b.getdoc());
        }
        long nano = System.nanoTime();
        for (Document d : returnlist) {
            d.setLastUseTime(nano);
            minHeap.reHeapify(new btreelookerupper(d.getKey(), this.docstore));
        }
        return returnlist;
    }

    @Override
    public Set<URI> deleteAllWithMetadata(Map<String,String> keysValues) throws IOException {
        CommandSet<URI> set = new CommandSet<>();
        ArrayList<btreelookerupper> deleteset = new ArrayList<>(this.searchByMetadatabuutdontupdatenanotime(keysValues));
        Set<URI> returnset = new HashSet<>();
        for (btreelookerupper d : deleteset) {
            //store for undo
            Document doc = d.getdoc();
            //delete from heap
            returnset.add(d.getid());
            //delete from tree
            this.delete(d.getid());
            //delete in orig trie
            for (String word : d.getwordlist()) {
                trieimpl.delete(word, d);
            }
            //delete in metadata trie
            for (String key: copyofmetadata.get(d.getid()).keySet()) {
                triemetadata.delete(key + copyofmetadata.get(d.getid()), d);
            }

            GenericCommand<URI> mand = new GenericCommand<>(d.getid(), (URI url) -> {
                for (String word : d.getwordlist()) {
                    this.trieimpl.put(word, d);
                }
                for (String key: copyofmetadata.get(d.getid()).keySet()) {
                this.triemetadata.put(key + copyofmetadata.get(d.getid()), d);
            }   this.docstore.put(d.getid(), doc);
                d.getdoc().setLastUseTime((System.nanoTime()));
                numdocsinmemory++;
                alldocsinmemory.add(d);
                minHeap.insert(d);
                minHeap.reHeapify(d);
                checkstoragelimitsonput(d, true);
            });
            set.addCommand(mand);
        }
        commandstack.push(set);
        return returnset;
    }

    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword,Map<String,String> keysValues) throws IOException {
        CommandSet<URI> set = new CommandSet<>();
        ArrayList<Document> deleteset = new ArrayList<>(this.searchByKeywordAndMetadata(keyword, keysValues));
        Set<URI> returnset = new HashSet<>();
        for (Document d : deleteset) {
            btreelookerupper b = new btreelookerupper(d.getKey(), this.docstore);
            //delete from heap
            returnset.add(d.getKey());
            this.delete(d.getKey());
            //delete in orig trie
            for (String word : d.getWords()) {
                trieimpl.delete(word, b);
            }
            //delete in metadata trie
            for (String key : d.getMetadata().keySet()) {
                triemetadata.delete(key + d.getMetadataValue(key), b);
            }

            GenericCommand<URI> mand = new GenericCommand<>(d.getKey(), (URI url) -> {
                for (String word : d.getWords()) {
                    this.trieimpl.put(word, b);
                }
                for (String key : d.getMetadata().keySet()) {
                    this.triemetadata.put(key + d.getMetadataValue(key), b);
                } this.docstore.put(d.getKey(), d);
                d.setLastUseTime(System.nanoTime());
                numdocsinmemory++;
                alldocsinmemory.add(b);
                minHeap.insert(b);
                minHeap.reHeapify(b);
                checkstoragelimitsonput(b, true);
            });
            set.addCommand(mand);
            }
        commandstack.push(set);
        return returnset;
    }

    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix,Map<String,String> keysValues) throws IOException {
        CommandSet<URI> set = new CommandSet<>();
        ArrayList<Document> deleteset = new ArrayList<>(this.searchByPrefixAndMetadata(keywordPrefix, keysValues));
        Set<URI> returnset = new HashSet<>();
        for (Document d : deleteset) {
            btreelookerupper b = new btreelookerupper(d.getKey(), this.docstore);
            //delete from heap
            returnset.add(d.getKey());
            //delete in orig trie
            for (String word : d.getWords()) {
                if(word.startsWith(keywordPrefix)) {
                    trieimpl.delete(word, b);
                }
            }
            //delete in metadata trie
            for (String key : d.getMetadata().keySet()) {
                triemetadata.delete(key + d.getMetadataValue(key), b);
            }
            this.delete(d.getKey());

            GenericCommand<URI> mand = new GenericCommand<>(d.getKey(), (URI url) -> {
                for (String word : d.getWords()) {
                    if (word.startsWith(keywordPrefix));
                    this.trieimpl.put(word, b);
                }
                d.setLastUseTime(System.nanoTime());
                docstore.put(d.getKey(), d);
                numdocsinmemory++;
                alldocsinmemory.add(b);
                minHeap.insert(b);
                minHeap.reHeapify(b);
                checkstoragelimitsonput(b, true);
                for (String key : d.getMetadata().keySet()) {
                    this.triemetadata.put(key + d.getMetadataValue(key), b);
                }});
            set.addCommand(mand);
            }
        commandstack.push(set);
        return returnset;
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        if (limit < 1){
            throw new IllegalArgumentException();
        }
        if (limit < numdocsinmemory) {
            while (this.numdocsinmemory > limit) {
                moveleastusedtodisk();
            }
        }
        this.maxdoccount = limit;
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException();
        }
        if (limit < maxdocbytes || limit < getbytesused()) {
            while (getbytesused() > limit){
                moveleastusedtodisk();
            }
        }
        this.maxdocbytes = limit;
    }
    private int getbytesused(){
        int i = 0;
        for (btreelookerupper b : this.alldocsinmemory) {
            Document d = b.getdoc();
            if (d == null) {
                continue;
            }
            if (d.getDocumentTxt() != null) {
                String str = d.getDocumentTxt();
                byte[] arr = str.getBytes();
                i += arr.length;
            }
            if (d.getDocumentBinaryData() != null) {
                i += d.getDocumentBinaryData().length;
            }
        }
        return i;
    }
    private void moveleastusedtodisk(){
        if (minHeap.peek() != null) {
            btreelookerupper d = minHeap.remove();
            URI url = d.getid();
            //btree
            try {
                this.docstore.moveToDisk(url);
                numdocsinmemory--;
                alldocsinmemory.remove(d);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void deletecommandfromcommandstack(URI url) {
        StackImpl<Undoable> tempstack = new StackImpl<>();
        int a = commandstack.size();
        for (int i = 0; i < a; i++) {
            if (commandstack.peek() instanceof CommandSet<?>) {
                if (((CommandSet<URI>) commandstack.peek()).containsTarget(url)) {
                    Iterator<GenericCommand<URI>> itr = ((CommandSet<URI>) commandstack.peek()).iterator();
                    while (itr.hasNext()) {
                        URI uri = itr.next().getTarget();
                        if (uri == url) {
                            itr.remove();
                        }
                    }
                }
            }
            if (commandstack.peek() instanceof GenericCommand<?> && ((GenericCommand<?>) commandstack.peek()).getTarget() == url) {
                commandstack.pop();
            } else {
                tempstack.push(commandstack.pop());
            }
        }
        int g = tempstack.size();
        for (int z = 0; z < g; z++) {
            commandstack.push(tempstack.pop());
        }
    }

    private void deletespecificdocfromheap(btreelookerupper d) {

        d.getdoc().setLastUseTime(Long.MIN_VALUE);
        minHeap.reHeapify(d);
        minHeap.remove();
    }
    private int numbertimesprefix(String prefix, btreelookerupper d) {
        int count = 0;
        for (String word : d.getwordlist()) {
            if (word.startsWith(prefix)) {
                String string = d.getdoctext();
                String temp[] = string.split(" ");
                for (int i = 0; i < temp.length; i++) {
                    if (word.equals(temp[i]))
                        count++;
                }
            }
        }
        return count;
    }

    private class btreelookeruppercomparator implements Comparator <btreelookerupper> {
        String word;
        DocumentStoreImpl docstore;
        private btreelookeruppercomparator(String keyword, DocumentStoreImpl outer) {
            this.word = keyword;
            this.docstore = outer;
        }
        @Override
        public int compare(btreelookerupper d, btreelookerupper e) {
            if ((!d.getwordlist().contains(word)) && (!e.getwordlist().contains(word))) {
                return (-1) * (Integer.compare(docstore.numbertimesprefix(word, d), docstore.numbertimesprefix(word, e)));
            }
            return (-1) * (Integer.compare(d.getdoc().wordCount(word), e.getdoc().wordCount(word)));
        }
    }
    private String uritopath(URI uri)  {
        String filepath = uri.toString() + ".json";
        String path = filepath.replace("http://", "");
        Path g = Paths.get(this.direc.getPath(),path);
        return g.toString();
    }

    private List<URI> currenturilist(){
        List<URI> returnlist = new ArrayList<>();
        for (btreelookerupper x : alldocsinmemory){
            returnlist.add(x.getid());
        }
        return returnlist;
    }



}

