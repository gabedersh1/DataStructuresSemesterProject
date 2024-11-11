
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.*;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import java.nio.file.Files;


import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;


import javax.print.Doc;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


import java.net.URI;
import java.net.URISyntaxException;

public class DocumentStoreImplTest {
    DocumentStoreImpl store;
    URI uri;
    InputStream stream;

    @BeforeEach
    void beforeEach() throws FileNotFoundException, URISyntaxException {
        store = new DocumentStoreImpl();
        uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);
        stream = new ByteArrayInputStream(arr);

        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
    }



    @Test
    void testput() throws IOException {
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);
        stream = new ByteArrayInputStream(arr);

        //test format null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            store.put(stream, uri, null);
        });

        //test uri null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            store.put(stream, null, DocumentStore.DocumentFormat.BINARY);
        });

        //test uri blank
        URI blank_uri = null;
        try {
            new URI("");
        } catch (URISyntaxException e) {
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            store.put(stream, blank_uri, DocumentStore.DocumentFormat.BINARY);
        });

        // if there is no previous doc at the given URI, return 0
        Assertions.assertEquals(0, store.put(stream, uri, DocumentStore.DocumentFormat.BINARY));
    }

    @Test
    void testputreturnhashofpreviousdoc() throws IOException, URISyntaxException {
        // If there is a previous doc, return the hashCode of the previous doc.
        URI testuri = new URI("hello");
        store.put(stream, testuri, DocumentStore.DocumentFormat.BINARY);
        int prevhash = store.get(testuri).hashCode();

        Random newbytes = new Random();
        byte[] arr = new byte[6];
        newbytes.nextBytes(arr);
        InputStream newstream = new ByteArrayInputStream(arr);

        Assertions.assertEquals(prevhash, store.put(newstream, testuri, DocumentStore.DocumentFormat.BINARY));

    }

    // If InputStream is null....
    @Test
    void testputstreamnull() throws IOException, URISyntaxException {
        URI testuri = new URI("hello");
        store.put(stream, testuri, DocumentStore.DocumentFormat.BINARY);
        int prevhash = store.get(testuri).hashCode();
        //...this is a delete, and thus return the hashCode of the deleted doc
        Assertions.assertEquals(prevhash, store.put(null, testuri, DocumentStore.DocumentFormat.BINARY));

    }

    @Test
    void testputstreamnulltwo() throws URISyntaxException, IOException {
        //...return 0 if there is no doc to delete
        URI testuri = new URI("hello");
        Assertions.assertEquals(0, store.put(null, testuri, DocumentStore.DocumentFormat.BINARY));
    }

    @Test
    void testDeleteDocReturnValue() throws IOException, URISyntaxException {
        URI testuri = new URI("hello");
        store.put(stream, testuri, DocumentStore.DocumentFormat.BINARY);
        store.put(null, testuri, DocumentStore.DocumentFormat.BINARY);
        Assertions.assertEquals(false, store.delete(testuri));
    }

    // Stage 4 tests


    @Test
    void testsearch() throws URISyntaxException, IOException {

        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");


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

        List<Document> testlist = new ArrayList<>();
        testlist.add(doc1);
        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Assertions.assertEquals(testlist, this.store.search("park"));

        List<Document> testlist2 = new ArrayList<>();
        testlist2.add(doc2);
        testlist2.add(doc3);
        Assertions.assertEquals(testlist2, this.store.search("boys"));

        List<Document> testlist3 = new ArrayList<>();
        testlist3.add(doc3);
        testlist3.add(doc2);
        testlist3.add(doc1);
        Assertions.assertEquals(testlist3, this.store.search("the"));
    }

    @Test
    void testsearchbyPrefix() throws URISyntaxException, IOException {

        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        List<Document> testlist1 = new ArrayList<>();
        testlist1.add(doc1);
        testlist1.add(doc2);
        Assertions.assertEquals(testlist1, this.store.searchByPrefix("we"));

    }

    @Test
    void testdeleteall() throws IOException, URISyntaxException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Set<URI> test = new HashSet<>();
        test.add(doc1.getKey());
        test.add(doc2.getKey());
        test.add(doc3.getKey());

        Assertions.assertEquals(test, this.store.deleteAll("the"));
        Assertions.assertNull(this.store.get(uri1));
    }

    @Test
    void testdeleteallwithPrefix() throws IOException, URISyntaxException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Set<URI> test = new HashSet<>();
        test.add(doc1.getKey());
        test.add(doc2.getKey());
        test.add(doc3.getKey());
        Assertions.assertEquals(test, this.store.deleteAllWithPrefix("the"));
        Assertions.assertEquals(null, this.store.get(uri1));

    }

    @Test
    void testsearchbymetadata() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
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
        doc1.setMetadataValue("author", "Bob");
        doc1.setMetadataValue("date published", "January 7");


        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri1, "date published", "January 7");
        this.store.setMetadata(uri1, "author", "Bob");
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri2, "date published", "January 7");

        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Map<String, String> metamap = new HashMap<>();
        metamap.put("author", "Bob");
        metamap.put("date published", "January 7");
        List<Document> testlist1 = new ArrayList<>();
        testlist1.add(doc1);
        Assertions.assertEquals(testlist1, this.store.searchByMetadata(metamap));

    }

    @Test
    void testsearchbykeywordandMetadata() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC. game";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        doc1.setMetadataValue("author", "Bob");
        doc1.setMetadataValue("date published", "January 7");


        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri1, "date published", "January 7");
        this.store.setMetadata(uri1, "author", "Bob");
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri2, "date published", "January 7");
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        List<Document> testlist1 = new ArrayList<>();
        testlist1.add(doc2);
        Assertions.assertEquals(testlist1, this.store.searchByKeywordAndMetadata("game", metamap));
    }

    @Test
    void searchByPrefixAndMetadata() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC. game";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        doc1.setMetadataValue("author", "Bob");
        doc1.setMetadataValue("date published", "January 7");


        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri1, "date published", "January 7");
        this.store.setMetadata(uri1, "author", "Bob");
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri2, "date published", "January 7");
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        List<Document> testlist1 = new ArrayList<>();
        testlist1.add(doc1);
        Assertions.assertEquals(testlist1, this.store.searchByPrefixAndMetadata("p", metamap));
    }

    @Test
    void deleteAllWithMetadata() throws IOException, URISyntaxException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri1, "date published", "January 7");
        this.store.setMetadata(uri1, "author", "Bob");
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri2, "date published", "January 7");
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Set<URI> test = new HashSet<>();
        test.add(doc1.getKey());
        test.add(doc2.getKey());
        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        Assertions.assertEquals(test, this.store.deleteAllWithMetadata(metamap));
        Assertions.assertEquals(null, this.store.get(uri1));
        Assertions.assertEquals(null, this.store.get(uri2));
    }

    @Test
    void deleteAllWithKeywordandMetadata() throws IOException, URISyntaxException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri1, "date published", "January 7");
        this.store.setMetadata(uri1, "author", "Bob");
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri2, "date published", "January 7");
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

        Set<URI> test = new HashSet<>();
        test.add(doc2.getKey());
        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        Assertions.assertEquals(test, this.store.deleteAllWithKeywordAndMetadata("game", metamap));
        Assertions.assertNotNull(this.store.get(uri1));
        Assertions.assertNull(this.store.get(uri2));
    }

    @Test
    void deleteAllWithPrefixandMetadata() throws IOException, URISyntaxException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri1, "date published", "January 7");
        this.store.setMetadata(uri1, "author", "Bob");
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri2, "date published", "January 7");
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri3, "date published", "January 7");

        Set<URI> test = new HashSet<>();
        test.add(doc2.getKey());
        test.add(doc3.getKey());
        Map<String, String> metamap = new HashMap<>();
        metamap.put("date published", "January 7");
        Assertions.assertEquals(test, this.store.deleteAllWithPrefixAndMetadata("g", metamap));
        Assertions.assertNotNull(this.store.get(uri1));
        Assertions.assertNull(this.store.get(uri2));
    }

    //stage5 -- max storage tests

    @Test
    void setmaxdoccount() throws URISyntaxException, IOException {
        this.store.setMaxDocumentCount(2);
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym. They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.search("park");
        //place a third doc. this should remove the least used doc, doc2, from the entire docstore.
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        File disk = new File(uritopath(uri2));
        Assertions.assertTrue(disk.length() > 0);
        this.store.search("dog");
        Assertions.assertEquals(0, disk.length());
        this.store.setMaxDocumentCount(3);
        InputStream isthreeb = new ByteArrayInputStream(three.getBytes());
        this.store.put(isthreeb, uri3, DocumentStore.DocumentFormat.TXT);
        this.store.searchByPrefix("tur");
        this.store.setMaxDocumentCount(1);
        Assertions.assertTrue(disk.length() > 0);
        File disk2 = new File(uritopath(uri1));
        Assertions.assertTrue(disk.length() > 0);
        Assertions.assertTrue(disk2.length() > 0 );
    }

    @Test
    void testmaxdocbytes() throws IOException, URISyntaxException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        Path disk3 = Paths.get(uritopath(uri3));
        Assertions.assertFalse(Files.exists(disk3));

        //basic test
        String one = "One boy went to the park";
        int doc1bytes = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int doc2bytes = two.getBytes().length;
        String three = "Three boys played basketball in the gym. It was hot in the gym. They turned on the AC.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        this.store.setMaxDocumentBytes(doc1bytes + doc2bytes);
        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.search("park");
        //place a third doc. this should remove the least used doc, doc2, from the entire docstore.

        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        Path disk = Paths.get(uritopath(uri2));
        Assertions.assertTrue(Files.exists(disk));
        this.store.setMaxDocumentCount(3);


        this.store.searchByPrefix("tur");
        Assertions.assertFalse(Files.exists(disk3));

        this.store.setMaxDocumentCount(1);
        Assertions.assertTrue(Files.exists(disk) && Files.size(disk) > 0);
        Path disk2 = Paths.get(uritopath(uri1));
        Assertions.assertTrue(Files.exists(disk2) && Files.size(disk2) > 0);
        Assertions.assertTrue(!Files.exists(disk3));
    }

    @Test
    void testexceptionthrownifdocbiggerthanmaxfootprint() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        //basic test
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "hfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuhfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuncrncr";
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

        this.store.setMaxDocumentBytes(a + b);
        this.store.put(isone, uri1, DocumentStore.DocumentFormat.BINARY);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        });

    }

    @Test
    void testleastrecentlyuseddocumentkickedout() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
        //basic test
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "hfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuhfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuncrncr";
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.BINARY);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.store.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        this.store.put(isfive, uri5, DocumentStore.DocumentFormat.TXT);
        this.store.put(issix, uri6, DocumentStore.DocumentFormat.TXT);
        this.store.search("jf;d");
//        this.store.setMaxDocumentBytes(a);
        return;
    }

    @Test
    void undooverwritebyuri() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        Assertions.assertEquals(one, this.store.get(uri1).getDocumentTxt());
        this.store.put(istwo, uri1, DocumentStore.DocumentFormat.TXT);
        Assertions.assertEquals(two, this.store.get(uri1).getDocumentTxt());
        this.store.undo(uri1);
        Assertions.assertEquals(one, this.store.get(uri1).getDocumentTxt());
    }

    @Test
    void undooverwitefromurideeltedfromcommandstacktomakespace() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "B/c it's kind of like morning comes faster. Buy from of your favorite breakfast items and get another for just a buck, like your favorite sandwhich...the jeep celebration event is going...";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        Assertions.assertEquals(one, this.store.get(uri1).getDocumentTxt());
        this.store.put(istwo, uri1, DocumentStore.DocumentFormat.TXT);
        Assertions.assertEquals(two, this.store.get(uri1).getDocumentTxt());
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.store.setMaxDocumentBytes(three.getBytes().length);
        Assertions.assertThrows(IllegalStateException.class, () -> {
            this.store.undo(uri2);
        });
    }

    //STAGE 6 ADDITIONS

    @Test
    void testfirstrequirementsecondsection() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "hfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuhfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuncrncr";
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

        //add two docs, set max to 1 doc, kicks one out of memory.
        // Tests:
        // first doc did not have a file before, does have a file after being kicked out
        Path p = Paths.get(uritopath(uri1));
        Assertions.assertFalse(Files.exists(p));
        this.store.setMaxDocumentCount(1);
        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT );
        this.store.setMetadata(uri1, "Author", "Bob John");
        this.store.setMetadata(uri1, "Publisher", "Penguin");
        Document z = this.store.get(uri1);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        Assertions.assertTrue(Files.exists(p));
        this.store.undo();
        Assertions.assertFalse(Files.exists(p));


        //if btree.get is called on uri1:
        Document y = this.store.get(uri1);
        // bring it back into memory, ie, delete its file on disk
        Assertions.assertFalse(Files.exists(p));
        Assertions.assertEquals(y, z);
        //and put the other doc into memory
        Path doc2 = Paths.get(uritopath(uri2));
        Assertions.assertTrue(Files.exists(doc2));

    }

    @Test
    void bringbackoverbyteslimit() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        URI uri4 = new URI("ESPN.com");
        URI uri5 = new URI("timesofisrael.com");
        URI uri6 = new URI("Yevamos/PerekRishon");
        String one = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int c = three.getBytes().length;
        String four = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
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

        //add three docs, set max to 2 docs, kicks one out of memory.
        // Tests:
        // first doc did not have a file before, does have a file after being kicked out
        Path p = Paths.get(uritopath(uri1));
        Assertions.assertFalse(Files.exists(p));
        this.store.put(isone, uri1, DocumentStore.DocumentFormat.TXT );
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.store.setMaxDocumentCount(2);
        Assertions.assertTrue(Files.exists(p));
        //restore max document count to 3
        this.store.setMaxDocumentCount(3);
        //set max doc bytes to total of last two docs, so when first doc gets brought back to memory it should kick out the first one put
        this.store.setMaxDocumentBytes(b + c);
        //if btree.get is called on uri1:
        this.store.get(uri1);
        // bring it back into memory, ie, delete its file on disk
        Assertions.assertFalse(Files.exists(p));
        //and put the other two docs into memory
        Path doc2 = Paths.get(uritopath(uri2));
        Assertions.assertTrue(Files.exists(doc2));
        Path doc3 = Paths.get(uritopath(uri3));
        Assertions.assertFalse(Files.exists(doc3));
    }

    @Test
    void searchbringbacktomemory() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        //basic test
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "hfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuhfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuncrncr";
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
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);
        store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        this.store.setMetadata(uri1, "date published", "January 7");
        this.store.setMetadata(uri1, "author", "Bob");
        store.setMaxDocumentCount(1);
        //this should kick the first document out
        store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);

        doc1.setMetadataValue("author", "Bob");
        doc1.setMetadataValue("date published", "January 7");


        this.store.setMetadata(uri2, "date published", "January 7");


        Map<String, String> metamap = new HashMap<>();
        metamap.put("author", "Bob");
        metamap.put("date published", "January 7");
        List<Document> testlist1 = new ArrayList<>();
        testlist1.add(doc1);
        Assertions.assertEquals(testlist1, this.store.searchByMetadata(metamap));
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri2))));
        //this should bring the first doc back, and kick the second one out

        pm.delete(uri1);
    }

    @Test
    void testputsentotdiskundo() throws URISyntaxException, IOException {
            DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
            URI uri1 = new URI("http://yu.instructure.com/courses/65568");
            URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
            URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
            URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
            URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
            URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
            store.delete(uri1);
            store.delete(uri2);
            store.delete(uri3);
            //basic test
            String one = "One boy went to the park";
            int a = one.getBytes().length;
            String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
            int b = two.getBytes().length;
            String three = "hfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuhfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuncrncr";
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

            this.store.put(isone, uri1, DocumentStore.DocumentFormat.BINARY);
            this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
            this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);

            Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri1))));
            this.store.setMaxDocumentCount(2);
            Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri1))));
            this.store.put(issix, uri1, DocumentStore.DocumentFormat.TXT);
            Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri1))));
            this.store.undo();
            Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri1))));

    }

    @Test
    void putfivesetlimitothtree() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        //basic test
        String one = "One boy went to the park";
        int a = one.getBytes().length;
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        int b = two.getBytes().length;
        String three = "hfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuhfhfhfhk  queropruqhp urqpq uhrpu rphqu rphqu rphq urqph rquhp pruhq rpuh rpuh rpuhrpqupruqpururu  ur ru r  r r qr uh qh j;fjd;lajadfjladd jf;d; jioq fkjnasjfh;asifqerfiji;jasdbifjb;sidbfsbf;dsfbsiab;isfdbaifbsfdb;  ufui;sfiubibie;br;ifb fif;;afbfbfibaifbaiafbfrb;  ;purenpucnrcpunuprcnurcnuu rpupr rpupnrcprunpnurc pru upr urpurpncnrpuncrncr";
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

        this.store.put(isone, uri1, DocumentStore.DocumentFormat.BINARY);
        this.store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        this.store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        this.store.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        this.store.put(isfive, uri5, DocumentStore.DocumentFormat.TXT);
        this.store.put(issix, uri6, DocumentStore.DocumentFormat.BINARY);
        Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri1))));
        Assertions.assertFalse(Files.exists(Paths.get(uritopath(uri2))));
        this.store.setMaxDocumentCount(3);
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri1))));
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri2))));
        Assertions.assertTrue(Files.exists(Paths.get(uritopath(uri3))));
        this.store.undo();

    }

    private String uritopath(URI uri) {
        String uriasstring = uri.toString();
        String uristring = uriasstring.replace("http://", "");
        return uristring + ".json";
    }


    @AfterEach
    void deletefromdisk() throws URISyntaxException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");
        store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        File uri1file = new File(uritopath(uri1));
        File uri2file = new File(uritopath(uri2));
        File uri3file = new File(uritopath(uri3));
        File uri4file = new File(uritopath(uri4));
        uri1file.delete();
        uri2file.delete();
        uri3file.delete();
        store.delete(uri4);
        uri4file.delete();
    }
}








