
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage6.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DocumentImplTest {


    @Test
    void testDocumentConstructor() throws URISyntaxException {
        URI uri = new URI("hello");
        URI blank_uri = new URI("");
        String x = null;
        //null text
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl d = new DocumentImpl(uri, x, null);
        });
        //null uri
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl d = new DocumentImpl(null, "hello", null);
        });
        //blank text
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl d = new DocumentImpl(uri, "", null);
        });
        //blank uri
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl d = new DocumentImpl(blank_uri, "hello", null);
        });
        //test that it can contruct ******************


    }

    @Test
    void testsetMetadataValue() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {

        }
        DocumentImpl d = new DocumentImpl(uri, "test", null);

        //ensure it returns null if no old value
        Assertions.assertEquals(null, d.setMetadataValue("key", "value"));

        //ensure it returns the old value
        Assertions.assertEquals("value", d.setMetadataValue("key", "newvalue"));

        //test throwing IAE if key is null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            d.setMetadataValue(null, "value");
        });
        //test throwing IAE if key is blank
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            d.setMetadataValue("", "value");
        });

    }

    @Test
    void testgetMetadataValue() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        DocumentImpl d = new DocumentImpl(uri, "test", null);

        // throw IAE if key is blank
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            d.getMetadataValue("");
        });

        //throw IAE if key is null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            d.getMetadataValue(null);
        });

        //return corresponding value
        d.setMetadataValue("key", "newvalue");
        Assertions.assertEquals("newvalue", d.getMetadataValue("key"));

        //return null if no such key
        Assertions.assertEquals(null, d.getMetadataValue("fakekey"));
    }

    @Test
    void testgetDocumentTxt() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        DocumentImpl d = new DocumentImpl(uri, "test", null);
        Assertions.assertEquals("test", d.getDocumentTxt());
    }

    @Test
    void testgetDocumentBinaryData() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        byte[] byteArray = new byte[1];
        byteArray[0] = (byte) 0;
        DocumentImpl d = new DocumentImpl(uri, byteArray);
        Assertions.assertEquals(byteArray[0], d.getDocumentBinaryData()[0]);
    }

    @Test
    void testgetKey() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        DocumentImpl d = new DocumentImpl(uri, "text", null);
        Assertions.assertEquals(uri, d.getKey());
    }

    @Test
    void teststrippunctuation() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        DocumentImpl d = new DocumentImpl(uri, "Hel-l'o my name! is Gabriel.", null);
        Assertions.assertEquals("Hel-l'o my name! is Gabriel.", d.getDocumentTxt());
    }

    @Test
    void testhashmap() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        DocumentImpl d = new DocumentImpl(uri, "Hel-l'o my name! is Gabriel. Hel-lo Hello--- my name na!..me name is Ga++/briel", null);
        //Assertions.assertEquals(3, d.hashMap.get("Hello"));
    }

    @Test
    void testwordcount() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        DocumentImpl d = new DocumentImpl(uri, "Hel-l'o my name! is Gabriel. Hel-lo Hello--- my name na!..me name is Ga++/briel", null);
        Assertions.assertEquals(3, d.wordCount("Hello"));
    }

    @Test
    void testgetwords() {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        DocumentImpl d = new DocumentImpl(uri, "Hello. Oh yeah. Oh yeah.", null);
        Set s = new HashSet<>();
        s.add("Hello");
        s.add("Oh");
        s.add("yeah");
        Assertions.assertEquals(s, d.getWords());
    }

    @Test
    void teststrip() throws URISyntaxException {
        URI uri = new URI("hello");
        Document d = new DocumentImpl(uri, "he%^$llo", null);
        Assertions.assertEquals("he%^$llo", d.getDocumentTxt());
    }
}


