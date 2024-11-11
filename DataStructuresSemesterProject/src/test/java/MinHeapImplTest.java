import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
public class MinHeapImplTest {

    @Test
    void testconstruction() throws URISyntaxException {
        MinHeap<Document> min = new MinHeapImpl<Document>();
        URI uri = new URI("hello");
        Document one = new DocumentImpl(uri, "hello", null);
        one.setLastUseTime(1);
        Document two =  new DocumentImpl(uri, "hello", null);
        two.setLastUseTime(2);
        Document three =  new DocumentImpl(uri, "hello", null);
        three.setLastUseTime(3);
        Document four =  new DocumentImpl(uri, "hello", null);
        four.setLastUseTime(4);
        Document five =  new DocumentImpl(uri, "hello", null);
        five.setLastUseTime(5);
        Document six =  new DocumentImpl(uri, "hello", null);
        six.setLastUseTime(6);
        Document seven =  new DocumentImpl(uri, "hello", null);
        seven.setLastUseTime(7);
        min.insert(one);
        min.insert(two);
        min.insert(three);
        min.insert(four);
        min.insert(five);
        min.insert(six);
        min.insert(seven);
        seven.setLastUseTime(0);
        min.reHeapify(seven);
        Assertions.assertEquals(seven, min.remove());
        Assertions.assertEquals(one, min.remove());
        Assertions.assertEquals(two, min.remove());
        six.setLastUseTime(0);
        min.reHeapify(six);
        Assertions.assertEquals(six, min.remove());
    }

    @Test
    void testinaction() throws URISyntaxException, IOException {

        DocumentStore store = new DocumentStoreImpl();
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

        store.put(isone, uri1, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri1,"date published", "January 7");
        store.put(istwo, uri2, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri2,"date published", "January 7");
        store.put(isthree, uri3, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri3,"date published", "January 7");
        store.setMetadata(uri1,"author", "Bob" );
        store.search("game");
        store.deleteAll("game");
        store.undo();
        return;

    }

    @Test
    void moretests() throws URISyntaxException {
        MinHeap<Document> min = new MinHeapImpl<Document>();
        URI uri = new URI("hello");
        Document one = new DocumentImpl(uri, "hello", null);
        one.setLastUseTime(1);
        Document two =new DocumentImpl(uri, "hello", null);
        two.setLastUseTime(2);
        Document three = new DocumentImpl(uri, "hello", null);
        three.setLastUseTime(3);
        Document four = new DocumentImpl(uri, "hello", null);
        four.setLastUseTime(4);
        Document five = new DocumentImpl(uri, "hello", null);
        five.setLastUseTime(5);
        Document six = new DocumentImpl(uri, "hello", null);
        six.setLastUseTime(6);
        Document seven = new DocumentImpl(uri, "hello", null);
        seven.setLastUseTime(7);
        min.insert(one);
        min.insert(two);
        min.insert(three);
        min.insert(four);
        min.insert(five);
        min.insert(six);
        min.insert(seven);
        Assertions.assertEquals(one, min.remove());
        two.setLastUseTime(0);
        min.reHeapify(two);
        Assertions.assertEquals(two, min.remove());
        five.setLastUseTime(25);
        min.reHeapify(five);
        Assertions.assertEquals(three, min.remove());
        four.setLastUseTime(76);
        min.reHeapify(four);
        Assertions.assertEquals(six, min.remove());
    }

    @Test
    void moremoretests () throws URISyntaxException {
        MinHeap<Document> min = new MinHeapImpl<Document>();
        URI uri = new URI("hello");
        Document one = new DocumentImpl(uri, "hello", null);
        one.setLastUseTime(1);
        Document two = new DocumentImpl(uri, "hello", null);
        two.setLastUseTime(2);
        min.insert(one);
        min.insert(two);
        Document three = new DocumentImpl(uri, "hello", null);
        min.insert(three);
        min.reHeapify(three);
        min.insert(three);
        Assertions.assertEquals(three, min.remove());
        Assertions.assertEquals(one, min.remove());

    }

    @Test
    void throwexceptionifnotinheap() throws URISyntaxException {
        MinHeap<Document> min = new MinHeapImpl<Document>();
        URI uri = new URI("hello");
        URI uri2 = new URI("bob");
        URI uri3 = new URI("meep");
        Document one = new DocumentImpl(uri, "hello", null);
        one.setLastUseTime(1);
        Document two = new DocumentImpl(uri2, "hello", null);
        two.setLastUseTime(2);
        min.insert(one);
        min.insert(two);
        Document three = new DocumentImpl(uri3, "hello", null);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            min.reHeapify(three);
        });
    }

    @Test
    void testdoublearraysize() throws URISyntaxException {
        MinHeap<Document> min = new MinHeapImpl<Document>();
        URI uri = new URI("hello");
        Document one = new DocumentImpl(uri, "hello", null);
        one.setLastUseTime(1);
        Document two = new DocumentImpl(uri, "hello", null);
        two.setLastUseTime(2);
        Document three = new DocumentImpl(uri, "hello", null);
        three.setLastUseTime(3);
        Document four = new DocumentImpl(uri, "hello", null);
        four.setLastUseTime(4);
        Document five = new DocumentImpl(uri, "hello", null);
        five.setLastUseTime(5);
        Document six = new DocumentImpl(uri, "hello", null);
        six.setLastUseTime(6);
        Document seven = new DocumentImpl(uri, "hello", null);
        seven.setLastUseTime(7);
        min.insert(one);
        min.insert(two);
        min.insert(three);
        min.insert(four);
        min.insert(five);
        min.insert(six);
        min.insert(seven);

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
}
