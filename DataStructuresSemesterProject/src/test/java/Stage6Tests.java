import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Stage6Tests {

    @BeforeAll
    static void BeforeAll() {
        File direc = new File(System.getProperty("user.dir"));
    }
    @Test
    void stage6PushToDiskViaMaxBytesBringBackInViaMetadataSearch () throws URISyntaxException, IOException {
        DocumentStore store = new DocumentStoreImpl();
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        String one = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int c = three.getBytes().length;

        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());

        store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        //set metadata of uri1
        store.setMetadata(uri1, "Author", "Dan Brown");
        store.setMetadata(uri1, "Date Published", "April 10, 1998");

        store.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        store.put(isthree, uri3, DocumentStore.DocumentFormat.BINARY);



        store.setMaxDocumentBytes(2 * a);

        // first file is kicked out
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri1, null))));

        Map<String, String>  keyvalue = new HashMap<>();
        keyvalue.put("Author", "Dan Brown");
        keyvalue.put("Date Published", "April 10, 1998");
        store.searchByMetadata(keyvalue);

        //bring first one back in

        Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri1, null))));
        Document e = store.get(uri1);
        deleteallfromdisk(pm);

        return;
    }

    @Test
    void stage6PushToDiskViaMaxDocCountBringBackInViaMetadataSearch() throws URISyntaxException, IOException {
        DocumentStore store = new DocumentStoreImpl();
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        String one = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int c = three.getBytes().length;

        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());

        store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        //set metadata of uri1
        store.setMetadata(uri1, "Author", "Dan Brown");
        store.setMetadata(uri1, "Date Published", "April 10, 1998");

        store.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        store.put(isthree, uri3, DocumentStore.DocumentFormat.BINARY);



        store.setMaxDocumentCount(2);

        // first file is kicked out
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri1, null))));

        Map<String, String>  keyvalue = new HashMap<>();
        keyvalue.put("Author", "Dan Brown");
        keyvalue.put("Date Published", "April 10, 1998");
        store.searchByMetadata(keyvalue);

        //bring first one back in

        Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri1,null))));
        Document e = store.get(uri1);
        return;
    }

    @Test
    void stage6PushToDiskViaMaxDocCountViaUndoDelete() throws URISyntaxException, IOException {
        File file = new File("/hello/darkness/my/old/friend");
        DocumentStore store = new DocumentStoreImpl(file);
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        String one = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int c = three.getBytes().length;

        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        store.setMaxDocumentCount(2);
        store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        store.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        store.delete(uri2);
        store.put(isthree, uri3, DocumentStore.DocumentFormat.BINARY);
        Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri3, null))));
        store.undo(uri2);
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri3, null))));
        Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri2, null))));

        deleteallfromdisk(pm);
    }

    @Test
    void stage6PushToDiskViaMaxDocCountBringBackInViaDeleteAndSearch() throws URISyntaxException, IOException {

        File file = new File("/hello/darkness/my/old/friend");
        DocumentStore store = new DocumentStoreImpl(file);
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        String one = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys. blah";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys. ";
        int c = three.getBytes().length;

        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        store.setMaxDocumentCount(2);
        store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        store.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        store.put(isthree, uri3, DocumentStore.DocumentFormat.BINARY);
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri1, file))));
        store.delete(uri2);
        store.search("blah");
        Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri1, file))));

    }

    void deleteallfromdisk(DocumentPersistenceManager pm) throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
        URI uri7 = new URI("Five");
        URI uri8 = new URI("Six");
        URI uri9 = new URI("Three");

        pm.delete(uri1);
        pm.delete(uri2);
        pm.delete(uri3);
        pm.delete(uri4);
        pm.delete(uri5);
        pm.delete(uri6);
        pm.delete(uri7);
        pm.delete(uri8);
        pm.delete(uri9);
    }

    private String uritopath(URI uri, File direc)  {
        File dir = null;
        if (direc == null) {
            dir = new File(System.getProperty("user.dir"));
        } else {
            dir = direc;
        }
        String filepath = uri.toString() + ".json";
        String path = filepath.replace("http://", "");
        Path g = Paths.get(dir.getPath(),path);
        return g.toString();
    }
}
