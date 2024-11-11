package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    JsonSerializer<Document> ser;
    JsonDeserializer<Document> des;
    File directory;

    public DocumentPersistenceManager(File baseDir) {
        if (baseDir == null) {
            this.directory = new File(System.getProperty("user.dir"));
        } else {
            this.directory = baseDir;
            this.directory.mkdirs();
        }
    }

    @Override
    public void serialize(URI key, Document val) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        ser = (document, type, jsonSerializationContext) -> {
            JsonObject doc = new JsonObject();
            if (val.getDocumentTxt() != null) {
                doc.add("contents", new JsonPrimitive(val.getDocumentTxt()));
            } else if (val.getDocumentBinaryData() != null){
                String base64Encoded = DatatypeConverter.printBase64Binary(val.getDocumentBinaryData());
                doc.add("contents", new JsonPrimitive(base64Encoded));
            }
            JsonObject map = new JsonObject();
            if (val.getMetadata() != null) {
                for (Map.Entry<String, String> entry : val.getMetadata().entrySet()) {
                    map.addProperty(entry.getKey(), entry.getValue());
                }
            }
            doc.add("metadata", map);
            doc.add("URI", new JsonPrimitive(val.getKey().toString()));
            JsonObject wordcountmap = new JsonObject();
            if (val.getWordMap() != null) {
                for (String k : val.getWordMap().keySet()) {
                    Integer value = val.getWordMap().get(k);
                    wordcountmap.add(k, new JsonPrimitive(value));
                }
            }
            doc.add("wordmap", wordcountmap);
            return doc;
        };
        extracted(key, val, builder);
    }

    private void extracted(URI key, Document val, GsonBuilder builder) throws IOException {
        builder.registerTypeAdapter(Document.class, ser);
        builder.registerTypeAdapter(DocumentImpl.class, ser);
        Gson gson = builder.create();
        String path = uritopath(key);
        File newfile;
        newfile = new File(path);
        File parentdirec = newfile.getParentFile();
        if (parentdirec != null && !parentdirec.exists()) {
            parentdirec.mkdirs();
        }
        if (!Files.exists(Paths.get(uritopath(key)))){
            newfile.createNewFile();
        }
        try (FileWriter write = new FileWriter(newfile)) {
            gson.toJson(val, write);
        } catch (IOException e) {
            throw new IOException();

        }
    }


    @Override
    public Document deserialize(URI uri) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Document deserialized = null;
        des = (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject offdisk = jsonElement.getAsJsonObject();
            Gson gson = new Gson();
            Type metamap = new TypeToken<HashMap<String, String>>() {
            }.getType();
            HashMap<String, String> metadata;
            JsonElement metajsonelement = offdisk.get("metadata");
            metadata = gson.fromJson(metajsonelement, metamap);
            Type map = new TypeToken<Map<String, Integer>>() {
            }.getType();
            Map<String, Integer> wordcountmap = null;
            Document d = null;
            try {
                JsonElement wordmap = offdisk.get("wordmap");
                wordcountmap = gson.fromJson(wordmap, map);
            } catch (Exception e) {
                String contents = offdisk.get("contents").getAsString();
                byte[] base64Decoded = DatatypeConverter.parseBase64Binary(contents);
                d = new DocumentImpl(uri, base64Decoded);
            }
            String contents = offdisk.get("contents").getAsString();
            d = new DocumentImpl(uri, contents, wordcountmap);
            d.setMetadata(metadata);
            return d;


        };
        builder.registerTypeAdapter(Document.class, des);
        builder.registerTypeAdapter(DocumentImpl.class, des);
        Gson gson = builder.create();

        String filepath = null;

        filepath = uritopath(uri);

        try (FileReader read = new FileReader(filepath)) {
            Document document = gson.fromJson(read, Document.class);
            this.delete(uri);
            return document;
        } catch (IOException e) {
            throw new IOException();
        }
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        File file = new File(uritopath(uri));
        return deleterecursively(file);

    }

    private boolean deleterecursively(File file) throws IOException {
        if (Files.isRegularFile(file.toPath())) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Files.isDirectory(file.toPath())) {
            try {
                Files.delete(file.toPath());
            } catch (DirectoryNotEmptyException e) {
                return false;
            } catch (FileSystemException e) {
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

    private String uritopath(URI uri)  {
        String filepath = uri.toString() + ".json";
        String path = filepath.replace("http://", "");
        Path g = Paths.get(this.directory.getPath(),path);
        return g.toString();
    }
}
