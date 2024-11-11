import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.BTreeImpl;

import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.stage6.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Stage4FixesTest {


    @Test
    void testsetgetmetadata() throws URISyntaxException {
        URI uri1 = new URI("www.espn.com");
        Document d = new DocumentImpl(uri1, "hello darkness my old friend", null);
        d.setMetadataValue("key1", "value1");
        d.setMetadataValue("key2", "value2");
        Assertions.assertEquals("value1", d.getMetadataValue("key1"));

    }

    @Test
    void stage4CaseSensitiveWordCount() throws URISyntaxException {
        URI uri1 = new URI("www.espn.com");
        Document document = new DocumentImpl(uri1, "hello Hello HEElo helLO", null);
        Assertions.assertEquals(1, document.wordCount("hello"));
        Document document2 = new DocumentImpl(uri1, "hello", null);
        Assertions.assertEquals(1, document2.wordCount("hello"));

    }

    @Test
    void stage4WordCount() throws URISyntaxException {

        URI uri1 = new URI("www.espn.com");
        Document document = new DocumentImpl(uri1, "hello Hello HEElo helLO", null);
        Assertions.assertEquals(1, document.wordCount("hello"));
    }

    @Test
    void testGetAllMetadata() throws URISyntaxException {
        URI uri1 = new URI("www.espn.com");
        Document d = new DocumentImpl(uri1, "hello darkness my old friend", null);
        d.setMetadataValue("key1", "value1");
        d.setMetadataValue("key2", "value2");
        BTreeImpl z = new BTreeImpl();
        z.put("key1", "value1");
        z.put("key2", "value2");
    }

    @Test
    void stage4searchexists() throws IOException {
        DocumentStore x = new DocumentStoreImpl();
        x.search("hello");
    }

    @Test
    void stage4SearchBinaryByPrefix() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        DocumentStore docstore = new DocumentStoreImpl();
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("Four");
        URI uri5 = new URI("Five");
        URI uri6 = new URI("Six");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        String four = "Four boys went to the park to play baseball. one of them hit a home run";
        String five = "Five boys played football. one was official quarterback";
        String six = "Six boys played soccer. the score was four to one";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        docstore.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        docstore.put(isfive, uri5, DocumentStore.DocumentFormat.BINARY);
        docstore.put(issix, uri6, DocumentStore.DocumentFormat.BINARY);

        Assertions.assertEquals(3, docstore.deleteAllWithPrefix("boy").size());
    }

    @Test
    void testundodeletebyuri() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        DocumentStore docstore = new DocumentStoreImpl();
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("Four");
        URI uri5 = new URI("Five");
        URI uri6 = new URI("Six");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        String four = "Four boys went to the park to play baseball. one of them hit a home run";
        String five = "Five boys played football. one was official quarterback";
        String six = "Six boys played soccer. the score was four to one";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        docstore.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        docstore.put(isfive, uri5, DocumentStore.DocumentFormat.BINARY);
        docstore.put(issix, uri6, DocumentStore.DocumentFormat.BINARY);

        Assertions.assertNotNull(docstore.get(uri2));
        docstore.undo(uri2);
        Assertions.assertNull(docstore.get(uri2));

    }
    @Test
    void testundodeletebyuri2() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        DocumentStore docstore = new DocumentStoreImpl();
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("Four");
        URI uri5 = new URI("Five");
        URI uri6 = new URI("Six");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        String four = "Four boys went to the park to play baseball. one of them hit a home run";
        String five = "Five boys played football. one was official quarterback";
        String six = "Six boys played soccer. the score was four to one";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        docstore.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        docstore.put(null, uri2, DocumentStore.DocumentFormat.BINARY);
        docstore.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        docstore.put(isfour, uri4, DocumentStore.DocumentFormat.TXT);
        docstore.put(isfive, uri5, DocumentStore.DocumentFormat.BINARY);
        docstore.put(issix, uri6, DocumentStore.DocumentFormat.BINARY);

        Assertions.assertNull(docstore.get(uri2));
        docstore.undo(uri2);
        Assertions.assertNotNull((docstore.get(uri2)));
}
    @Test
    void undoMetadataTwoDocuments() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        deleteallfromdisk(pm);
        DocumentStore docstore = new DocumentStoreImpl();
        URI uri1 = new URI("One");
        URI uri2 = new URI("Two");
        URI uri3 = new URI("Three");
        URI uri4 = new URI("Four");
        URI uri5 = new URI("Five");
        URI uri6 = new URI("Six");
        //basic test
        String one = "One boy went to the park";
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        String three = "Three boys played basketball in the gym. It was hot in the gym They turned on the AC.";
        String four = "Four boys went to the park to play baseball. one of them hit a home run";
        String five = "Five boys played football. one was official quarterback";
        String six = "Six boys played soccer. the score was four to one";
        InputStream isone = new ByteArrayInputStream(one.getBytes());
        InputStream istwo = new ByteArrayInputStream(two.getBytes());
        InputStream isthree = new ByteArrayInputStream(three.getBytes());
        InputStream isfour = new ByteArrayInputStream(four.getBytes());
        InputStream isfive = new ByteArrayInputStream(five.getBytes());
        InputStream issix = new ByteArrayInputStream(six.getBytes());
        Document doc1 = new DocumentImpl(uri1, one, null);
        Document doc2 = new DocumentImpl(uri2, two, null);
        Document doc3 = new DocumentImpl(uri3, three, null);

        docstore.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        docstore.setMetadata(uri1, "author", "bob");
        docstore.put(istwo, uri2, DocumentStore.DocumentFormat.BINARY);
        docstore.setMetadata(uri2, "publisher", "penguin" );
        docstore.undo();
        docstore.undo();
        Assertions.assertEquals(null, docstore.getMetadata(uri2, "publisher"));
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





