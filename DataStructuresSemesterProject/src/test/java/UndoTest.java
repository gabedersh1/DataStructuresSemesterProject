import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.*;

public class UndoTest {
    @BeforeEach
    void beforeEach() {
    }

    DocumentStore docstore = new DocumentStoreImpl();

    @Test
    void stam() throws IOException {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
        docstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        DocumentImpl newdoc = new DocumentImpl(uri, arr);

        //Assertions.assertEquals(arr, (docstore.get(uri).getDocumentBinaryData()));
        docstore.undo();
        Assertions.assertNull(docstore.get(uri));
    }

    @Test
    void testtwodocsundoURIput() throws IOException {
        //"put" two docs to the stack. undo the first put using the undo(URI) method. make sure the only doc remaining is the second one.
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }

        URI uri2 = null;
        try {
            uri2 = new URI("byebye");
        } catch (URISyntaxException e) {
        }
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);

        Random bytes2 = new Random();
        byte[] arr2 = new byte[7];
        bytes2.nextBytes(arr2);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(arr2);
        docstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        docstore.put(stream2, uri2, DocumentStore.DocumentFormat.BINARY);
        docstore.undo(uri);
        Assertions.assertNull(docstore.get(uri));

    }

    @Test
    void testputwtodocsregundo() throws IOException {
        //"put" two docs to the stack. undo both puts with the "plain" undo method. check after each undo to make sure they're gone.
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }

        URI uri2 = null;
        try {
            uri2 = new URI("byebye");
        } catch (URISyntaxException e) {
        }
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);

        Random bytes2 = new Random();
        byte[] arr2 = new byte[7];
        bytes2.nextBytes(arr2);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(arr2);
        docstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        docstore.put(stream2, uri2, DocumentStore.DocumentFormat.BINARY);
        docstore.undo();
        Assertions.assertNull(docstore.get(uri2));
        docstore.undo();
        Assertions.assertNull(docstore.get(uri));
    }

    @Test
    void testundosurietmetadata() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        URI uri2 = null;
        try {
            uri2 = new URI("byebye");
        } catch (URISyntaxException e) {
        }
        //put first document in. this will be the one that we try to change.
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
        docstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        this.docstore.setMetadata(uri, "Key", "First Value");
        //put a second document in.
        Random bytes2 = new Random();
        byte[] arr2 = new byte[7];
        bytes2.nextBytes(arr2);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(arr2);
        docstore.put(stream2, uri2, DocumentStore.DocumentFormat.BINARY);
        //
        Assertions.assertEquals("First Value", this.docstore.getMetadata(uri, "Key"));
        this.docstore.setMetadata(uri, "Key", "Second Value");
        Map<String, String> metamap = new HashMap<>();
        metamap.put("Key", "Second Value");
        Map<String, String> metamap2 = new HashMap<>();
        metamap2.put("Key", "First Value");
        List<Document> list = new ArrayList<>();
        list.add(this.docstore.get(uri));
        Assertions.assertEquals(list, this.docstore.searchByMetadata(metamap));
        Assertions.assertEquals("Second Value", this.docstore.getMetadata(uri, "Key"));
        this.docstore.undo(uri);
        Assertions.assertEquals("First Value", this.docstore.getMetadata(uri, "Key"));
        Assertions.assertFalse(this.docstore.searchByMetadata(metamap).contains(this.docstore.get(uri)));
        Assertions.assertEquals(list, this.docstore.searchByMetadata(metamap2));

    }

    @Test
    void testundostamsetmetadata() throws IOException {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        URI uri2 = null;
        try {
            uri2 = new URI("byebye");
        } catch (URISyntaxException e) {
        }
        //put first document in.
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
        docstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        this.docstore.setMetadata(uri, "Key", "First Value");
        //put a second document in. this will be the one we change
        Random bytes2 = new Random();
        byte[] arr2 = new byte[7];
        bytes2.nextBytes(arr2);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(arr2);
        docstore.put(stream2, uri2, DocumentStore.DocumentFormat.BINARY);
        Assertions.assertEquals("First Value", this.docstore.getMetadata(uri, "Key"));
        this.docstore.setMetadata(uri2, "Key2", "First Value2");
        Assertions.assertEquals("First Value2", this.docstore.getMetadata(uri2, "Key2"));
        this.docstore.setMetadata(uri2, "Key2", "Second Value2");
        Assertions.assertEquals("Second Value2", this.docstore.getMetadata(uri2, "Key2"));
        this.docstore.undo(uri2);
        Assertions.assertEquals("First Value2", docstore.getMetadata(uri2, "Key2"));

    }

    @Test
    void testundodeletebasic() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        Path p = Paths.get(uritopath(uri));
        Assertions.assertFalse(Files.exists(p));
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
        docstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        this.docstore.put(null, uri, DocumentStore.DocumentFormat.BINARY);
        Assertions.assertEquals(null, docstore.get(uri));
        Assertions.assertFalse(Files.exists(p));
        URI uri2 = null;
        try {
            uri2 = new URI("byebye");
        } catch (URISyntaxException e) {
        }
        Random bytes2 = new Random();
        byte[] arr2 = new byte[7];
        bytes2.nextBytes(arr2);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(arr2);
        docstore.put(stream2, uri2, DocumentStore.DocumentFormat.BINARY);
        this.docstore.undo(uri);
        Assertions.assertNotNull(docstore.get(uri));

        URI uri1 = new URI("One");
        URI uri3 = new URI("Two");
        URI uri4 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri1, "date published", "January 7");
        this.docstore.setMetadata(uri1, "author", "Bob");
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri2, "date published", "January 7");
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri3, "date published", "January 7");
        this.docstore.undo(uri1);
        this.docstore.undo(uri1);
        this.docstore.put(null, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(null, uri, DocumentStore.DocumentFormat.TXT);
        this.docstore.undo(uri2);
    }

    @Test
    void undoagain() throws IOException, URISyntaxException {
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri = this.docstore.get(uri1).getKey();
        this.docstore.put(null, uri, DocumentStore.DocumentFormat.TXT);
        this.docstore.undo(uri);
        Assertions.assertNotNull((this.docstore.get(uri)));
        Assertions.assertEquals(uri, this.docstore.get(uri).getKey());
    }

    @Test
    void undoset() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.deleteAll("the");
        Assertions.assertNull(this.docstore.get(uri1));
        this.docstore.undo();
        Assertions.assertNotNull(this.docstore.get(uri1));
    }

    @Test
    void undoset2() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.deleteAllWithPrefix("b");
        Assertions.assertNull(this.docstore.get(uri1));
        this.docstore.undo();
        Assertions.assertNotNull(this.docstore.get(uri1));
    }

    @Test
    void undoset3() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri1, "date published", "January 7");
        this.docstore.setMetadata(uri1, "author", "Bob");
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri2, "date published", "January 7");
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Set<URI> test = new HashSet<>();
        test.add(doc1.getKey());
        test.add(doc2.getKey());
        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        Assertions.assertEquals(test, this.docstore.deleteAllWithMetadata(metamap));
        Assertions.assertEquals(null, this.docstore.get(uri1));
        Assertions.assertEquals(null, this.docstore.get(uri2));
        this.docstore.undo();
        Assertions.assertNotNull(this.docstore.get(uri1));
    }

    @Test
    void undoset4() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri1, "date published", "January 7");
        this.docstore.setMetadata(uri1, "author", "Bob");
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri2, "date published", "January 7");
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri3, "date published", "January 7");

        Set<URI> test = new HashSet<>();
        test.add(doc2.getKey());
        test.add(doc3.getKey());
        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        Assertions.assertEquals(test, this.docstore.deleteAllWithPrefixAndMetadata("g", metamap));
        Assertions.assertNotNull(this.docstore.get(uri1));
        Assertions.assertNull(this.docstore.get(uri2));
        this.docstore.undo();
        Assertions.assertNotNull(this.docstore.get(uri2));
        Assertions.assertNotNull(this.docstore.get(uri1));
    }

    @Test
    void undoset5() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri1, "date published", "January 7");
        this.docstore.setMetadata(uri1, "author", "Bob");
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.setMetadata(uri2, "date published", "January 7");
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Set<URI> test = new HashSet<>();
        test.add(doc2.getKey());
        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        Assertions.assertEquals(test, this.docstore.deleteAllWithKeywordAndMetadata("game", metamap));
        Assertions.assertNotNull(this.docstore.get(uri1));
        Assertions.assertNull(this.docstore.get(uri2));
        this.docstore.undo();
        Assertions.assertNotNull(this.docstore.get(uri2));

    }

    @Test
    void testdeleteundosamenanotime() throws URISyntaxException, IOException {
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Document first = this.docstore.get(uri1);
        Document second = this.docstore.get(uri2);
        Document third = this.docstore.get(uri3);
        Map<String, String> metamap = new HashMap<>();
        metamap.put("Key1", "Value1");
        docstore.setMetadata(uri1, "Key1", "Value1");
        docstore.setMetadata(uri2, "Key1", "Value1");
        docstore.setMetadata(uri3, "Key1", "Value1");
        this.docstore.deleteAllWithKeywordAndMetadata("the", metamap);
        this.docstore.undo();
        Assertions.assertTrue(first.getLastUseTime() == second.getLastUseTime());
        Assertions.assertTrue(second.getLastUseTime() == third.getLastUseTime());

    }

    @Test
    void testdeleteundosamenanotimewherenotalldocsareequal() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Document a = this.docstore.get(uri1);
        Document b = this.docstore.get(uri2);
        Document c = this.docstore.get(uri3);
        Map<String, String> metamap = new HashMap<>();
        metamap.put("Key1", "Value1");
        docstore.setMetadata(uri1, "Key1", "Value1");
        docstore.setMetadata(uri2, "Key1", "Value1");
        this.docstore.deleteAllWithPrefix("went");
        this.docstore.undo();
        Assertions.assertTrue(a.getLastUseTime() == b.getLastUseTime());
        Assertions.assertTrue(b.getLastUseTime() != c.getLastUseTime());
    }

    @Test
    void testsametimeonsearch() throws URISyntaxException, IOException {
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);


        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        Document a = this.docstore.get(uri1);
        Document b = this.docstore.get(uri2);
        Document c = this.docstore.get(uri3);
        Map<String, String> metamap = new HashMap<>();
        metamap.put("Key1", "Value1");
        docstore.setMetadata(uri1, "Key1", "Value1");
        docstore.setMetadata(uri2, "Key1", "Value1");
        docstore.setMetadata(uri3, "Key1", "Value1");

        this.docstore.search("the");
        Assertions.assertTrue(a.getLastUseTime() == b.getLastUseTime());
        Assertions.assertTrue(b.getLastUseTime() == c.getLastUseTime());

        this.docstore.search("went");
        Assertions.assertTrue(a.getLastUseTime() == b.getLastUseTime());
        Assertions.assertTrue(b.getLastUseTime() != c.getLastUseTime());
        this.docstore.searchByMetadata(metamap);
        Assertions.assertTrue(a.getLastUseTime() == b.getLastUseTime());
        Assertions.assertTrue(b.getLastUseTime() == c.getLastUseTime());
        this.docstore.setMetadata(uri3, "Key1", null);
        Assertions.assertTrue(a.getLastUseTime() == b.getLastUseTime());
        Assertions.assertTrue(b.getLastUseTime() != c.getLastUseTime());
    }

    @Test
    void testunodcommandsetoverlimit() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        //if command set to undo is greater than available space, the current documents should be unaffected

        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        URI uri6 = new URI("Yevamos/PerekRishon");
        //basic test
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        int c = three.getBytes().length;
        String four = "is kol ha'oleh a fundamental din?";
        int d = four.getBytes().length;
        String five = "or maybe it's something more technical";
        int e = five.getBytes().length;
        String six = "could it be that it tells us something about the relationship between yibum and chalitzah?";
        int f = six.getBytes().length;
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());

        //set max doc bytes to storage needed for one and two
        this.docstore.setMaxDocumentBytes(a + b + c + d + e + f);
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isfive, uri5, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(issix, uri6, DocumentStore.DocumentFormat.TXT);
        //deleteAll, returning a CommandSet
        this.docstore.deleteAll("the");
        //confirm the deleteAll worked
        Path doc1 = Paths.get(uritopath(uri1));
        Path doc2 = Paths.get(uritopath(uri2));
        Path doc3 = Paths.get(uritopath(uri3));
        Path doc4 = Paths.get(uritopath(uri4));
        Path doc5 = Paths.get(uritopath(uri5));
        Path doc6 = Paths.get(uritopath(uri6));

        Assertions.assertFalse(Files.exists(doc1) || Files.exists(doc2) || Files.exists(doc3) || Files.exists(doc4) || Files.exists(doc5) || Files.exists(doc6));

        //attempt an undo
        this.docstore.undo();
        //lower the max bytes
        this.docstore.setMaxDocumentBytes((a + b + c + d + e + f) / 2);
        //confirm that the docstore shrunk
        return;
    }

    @Test
    void testtoobigremovedfromcommandstack() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        URI uri6 = new URI("Yevamos/PerekRishon");
        //basic test
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        int c = three.getBytes().length;
        String four = "is kol ha'oleh a fundamental din?";
        int d = four.getBytes().length;
        String five = "or maybe it's something more technical";
        int e = five.getBytes().length;
        String six = "could it be that it tells us something about the relationship between yibum and chalitzah?";
        int f = six.getBytes().length;
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());

        //set max doc bytes to storage needed for one and two
        this.docstore.setMaxDocumentCount(3);
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        Path p = Paths.get(uritopath(uri1));
        Assertions.assertTrue(!Files.exists(p));

        //this will put the store over the limit, and should kick out the first document
        this.docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        Assertions.assertTrue(Files.exists(p));

        //this should delete uri1 from disk
        Assertions.assertTrue(Files.exists(p) && Files.size(p) > 5);
        this.docstore.undo(uri1);
        Assertions.assertFalse(Files.exists(p) && Files.size(p) > 5);

        this.docstore.put(isfive, uri5, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(issix, uri6, DocumentStore.DocumentFormat.TXT);
    }

    @Test
    void testtoobigremovedfromcommandstackwhenpartofcommandset() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);

        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        URI uri6 = new URI("Yevamos/PerekRishon");
        //basic test
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        int c = three.getBytes().length;
        String four = "is kol ha'oleh a fundamental din?";
        int d = four.getBytes().length;
        String five = "or maybe it's something more technical";
        int e = five.getBytes().length;
        String six = "could it be that it tells us something about the relationship between yibum and chalitzah?";
        int f = six.getBytes().length;
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());

        //set max doc bytes to storage needed for first four docs
        this.docstore.setMaxDocumentBytes(a + b + c + d);
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        this.docstore.deleteAll("boys");
        this.docstore.get(uri1);
        this.docstore.get(uri4);
        this.docstore.setMaxDocumentBytes(a + d);
        //this will put the store over the limit, and should kick out the first doc
        Path firstdoc = Paths.get(uritopath(uri1));
        Assertions.assertFalse(Files.exists(firstdoc));
        this.docstore.put(isfive, uri5, DocumentStore.DocumentFormat.TXT);
        Assertions.assertTrue(Files.exists(firstdoc));
        Path thirddoc = Paths.get(uritopath(uri3));
        Assertions.assertFalse(Files.exists(thirddoc));
        Assertions.assertThrows(IllegalStateException.class, () -> {
            this.docstore.undo(uri6);
        });
    }

    @Test
    void testundodeleteallgreaterthanmaxbytes() throws IOException, URISyntaxException {
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        URI uri6 = new URI("Yevamos/PerekRishon");
        //basic test
        String one = "One boy went to the park hm. At the park he played baseball. He hit a home run and won the game";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys. hm";
        int b = two.getBytes().length;
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC. hm";
        int c = three.getBytes().length;
        String four = "is kol ha'oleh a fundamental din? hm";
        int d = four.getBytes().length;
        String five = "or maybe it's something more technical hm";
        int e = five.getBytes().length;
        String six = "could it be that it tells us something about the relationship between yibum and chalitzah? hm";
        int f = six.getBytes().length;
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());

        //set max doc bytes to storage needed for first four docs
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        this.docstore.deleteAll("hm");
        this.docstore.setMaxDocumentBytes(a + b + c);
        this.docstore.undo();
        Path d4 = Paths.get(uritopath(uri4));
        Assertions.assertTrue(Files.exists(d4));
        Path d2 = Paths.get(uritopath(uri2));
        Assertions.assertFalse(Files.exists(d2));
        Path d1 = Paths.get(uritopath(uri1));
        Assertions.assertFalse(Files.exists(d1));
        Path d3 = Paths.get(uritopath(uri3));
        Assertions.assertFalse(Files.exists(d3));
    }

    @Test
    void testundodeleteallgreaterthanmaxdoccount() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        URI uri6 = new URI("Yevamos/PerekRishon");
        //basic test
        String one = "One boy went to the park hm";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys. hm";
        int b = two.getBytes().length;
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC. hm";
        int c = three.getBytes().length;
        String four = "is kol ha'oleh a fundamental din? hm";
        int d = four.getBytes().length;
        String five = "or maybe it's something more technical hm";
        int e = five.getBytes().length;
        String six = "could it be that it tells us something about the relationship between yibum and chalitzah? hm";
        int f = six.getBytes().length;
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());

        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        this.docstore.deleteAll("hm");
        Path p4 = Paths.get(uritopath(uri4));
        Assertions.assertFalse(Files.exists(p4));
        this.docstore.setMaxDocumentCount(3);
        this.docstore.undo();

        Path p = Paths.get(uritopath(uri1));
        Assertions.assertFalse(Files.exists(p));
        Path p2 = Paths.get(uritopath(uri2));
        Assertions.assertFalse(Files.exists(p2));
        Path p3 = Paths.get(uritopath(uri3));
        Assertions.assertFalse(Files.exists(p3));
        Assertions.assertTrue(Files.exists(p4));


    }

    @Test
    void testundodelete() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        URI uri6 = new URI("Yevamos/PerekRishon");
        //basic test
        String one = "One boy went to the park hm";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys. hm";
        int b = two.getBytes().length;
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC. hm";
        int c = three.getBytes().length;
        String four = "is kol ha'oleh a fundamental din? hm";
        int d = four.getBytes().length;
        String five = "or maybe it's something more technical hm";
        int e = five.getBytes().length;
        String six = "could it be that it tells us something about the relationship between yibum and chalitzah? hm";
        int f = six.getBytes().length;
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());

        //set max doc bytes to storage needed for first four docs
        this.docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.docstore.put(null, uri3, DocumentStore.DocumentFormat.TXT);
        Assertions.assertNull(this.docstore.get(uri3));
        this.docstore.undo();
        this.docstore.get(uri3);

    }
    private String uritopath(URI uri) {
        String uriasstring = uri.toString();
        String uristring = uriasstring.replace("http://", "");
        return uristring + ".json";
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
        URI uri10 = new URI("One");
        URI uri11 = new URI("Two");
        URI uri12 = new URI("byebye");
        URI uri13 = new URI("Four");
        URI uri14 = new URI("ESPN.com");
        URI uri15 = new URI("Six");
        URI uri16 = new URI("timesofisrael.com");
        URI uri17 = new URI("Yevamos/PerekRishon");




        pm.delete(uri1);
        pm.delete(uri2);
        pm.delete(uri3);
        pm.delete(uri4);
        pm.delete(uri5);
        pm.delete(uri6);
        pm.delete(uri7);
        pm.delete(uri8);
        pm.delete(uri9);
        pm.delete(uri10);
        pm.delete(uri11);
        pm.delete(uri12);
        pm.delete(uri13);
        pm.delete(uri14);
        pm.delete(uri15);
        pm.delete(uri16);
        pm.delete(uri17);
    }
}

