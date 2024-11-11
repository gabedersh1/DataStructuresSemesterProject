import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DocumentPersistenceManagerTest {

    @Test
    void testserialization() throws IOException, URISyntaxException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
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

        String text = doc1.getDocumentTxt();
        pm.serialize(uri1, doc1);
        this.deleteallfromdisk(pm);
    }

    @Test
    void testdeserialization() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534/");
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

        Document one1 = new DocumentImpl(uri1, one, null);
        Document two2 = new DocumentImpl(uri2, two, null);
        Document three3 = new DocumentImpl(uri3, three, null);
        Document four4 = new DocumentImpl(uri4, four, null);
        Document five5 = new DocumentImpl(uri5, five, null);
        Document six6 = new DocumentImpl(uri6, six, null);

        pm.serialize(uri1, one1);
        pm.serialize(uri2, two2);
        pm.serialize(uri3, three3);
        pm.serialize(uri4, four4);
        pm.serialize(uri5, five5);
        pm.serialize(uri6, six6);

        Document s =   pm.deserialize(uri1);
        pm.deserialize(uri2);
        pm.deserialize(uri3);
        pm.deserialize(uri4);
        pm.deserialize(uri5);
        pm.deserialize(uri6);

    }

    @Test
    public void stagebTestSerializationContent()throws Exception {
        File baseDir = Files.createTempDirectory("stage6").toFile();
        //init values for doc1
        URI url1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        String txt1 = "This is the text of doc in plain text No fancy file format just plain old String Computer Headphones.";
        Document doc1 = new DocumentImpl(url1, txt1, null);
        doc1.setMetadataValue("k1", "v1") ;
//init values for doc2
       URI url2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        String txt2 = "Text for doc2 A plain old String";
        Document doc2 = new DocumentImpl(url2, txt2, null);
        doc2.setMetadataValue("k2", "v2") ;
//init values for doc3
        URI url3 = new URI( "http://cs.nyu.edu/datastructs/project/doc2");
        String txt3 = "Text for NYU doc2 A plain old String";
        Document doc3 = new DocumentImpl(url3 ,txt3, null);
        doc3.setMetadataValue("k3", "v3");

        DocumentPersistenceManager dpm = new DocumentPersistenceManager(baseDir);
        dpm.serialize(url1, doc1);
        String contents = TestUtils.getContents(baseDir, url1);
        if (contents == null) {
            fail("doc1 file not found in expected location on disk");
        }
        assertTrue(contents.lastIndexOf(txt1) >= 0, "doc1 text contents not found in serialized file");

        dpm.serialize(url2, doc2);
        String contents2 = TestUtils.getContents(baseDir, url2);
        if (contents2 == null) {
            fail("doc2 file not found in expected location on disk");
        }
        assertTrue(contents2.lastIndexOf(txt2) >= 0, "doc2 text contents not found in serialized file");

        dpm.serialize(url3, doc3);
        contents = TestUtils.getContents(baseDir, url3);
        if (contents == null) {
            fail("doc5 file not found in expected location on disk");
        }
        assertTrue(contents.lastIndexOf(txt3) >= 0, "doc3 text contents not found in serialized file");
    }

    @Test
    public void stagebTestDeserialization()throws Exception{
        File baseDir = Files.createTempDirectory("stage6").toFile();
        //init values for doc1
        URI url1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        String txt1 = "This is the text of doc in plain text No fancy file format just plain old String Computer Headphones.";
        Document doc1 = new DocumentImpl(url1, txt1, null);
        doc1.setMetadataValue("k1", "v1") ;
//init values for doc2
        URI url2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        String txt2 = "Text for doc2 A plain old String";
        Document doc2 = new DocumentImpl(url2, txt2, null);
        doc2.setMetadataValue("k2", "v2") ;
//init values for doc3
        URI url3 = new URI( "http://cs.nyu.edu/datastructs/project/doc2");
        String txt3 = "Text for NYU doc2 A plain old String";
        Document doc3 = new DocumentImpl(url3 ,txt3, null);
        doc3.setMetadataValue("k3", "v3");
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(baseDir);
        //serialize all 3 documents
        dpm.serialize(url1,doc1);
        dpm.serialize(url2,doc2);
        dpm.serialize(url3,doc3);
        TestUtils.equalButNotIdentical(doc1,dpm.deserialize(url1));

        TestUtils.equalButNotIdentical(doc2,dpm.deserialize(url2));

        TestUtils.equalButNotIdentical(doc3,dpm.deserialize(url3));
    }
        @Test
        void testdeserializationfromdirectory () throws URISyntaxException, IOException {
            File dir = new File("first/second/third/");
            DocumentPersistenceManager pm = new DocumentPersistenceManager(dir);
            URI uri1 = new URI("https://yu.instructure.com/courses/65568");
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

            Document one1 = new DocumentImpl(uri1, one, null);
            Document two2 = new DocumentImpl(uri2, two, null);
            Document three3 = new DocumentImpl(uri3, three, null);
            Document four4 = new DocumentImpl(uri4, four, null);
            Document five5 = new DocumentImpl(uri5, five, null);
            Document six6 = new DocumentImpl(uri6, six, null);

            pm.serialize(uri1, one1);
            pm.serialize(uri2, two2);
            pm.serialize(uri3, three3);
            pm.serialize(uri4, four4);
            pm.serialize(uri5, five5);
            pm.serialize(uri6, six6);

            pm.deserialize(uri4);
            pm.deserialize(uri2);
            pm.deserialize(uri3);
            pm.deserialize(uri1);
            pm.deserialize(uri5);
            pm.deserialize(uri6);
            deleteallfromdisk(pm);
        }

        @Test
        void testfiledeletion () throws URISyntaxException, IOException {
            DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
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

            Document one1 = new DocumentImpl(uri1, one, null);
            Document two2 = new DocumentImpl(uri2, two, null);
            Document three3 = new DocumentImpl(uri3, three, null);
            Document four4 = new DocumentImpl(uri4, four, null);
            Document five5 = new DocumentImpl(uri5, five, null);
            Document six6 = new DocumentImpl(uri6, six, null);

            pm.serialize(uri1, one1);
            pm.serialize(uri2, two2);
            pm.serialize(uri3, three3);
            pm.serialize(uri4, four4);
            pm.serialize(uri5, five5);
            pm.serialize(uri6, six6);

            deleteallfromdisk(pm);

        }

    @Test
    void testserializefromdirectory() throws URISyntaxException, IOException {
        File dir = new File("first/second/third/");
        DocumentPersistenceManager pm = new DocumentPersistenceManager(dir);
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

        Document one1 = new DocumentImpl(uri1, one, null);
        Document two2 = new DocumentImpl(uri2, two, null);
        Document three3 = new DocumentImpl(uri3, three, null);
        Document four4 = new DocumentImpl(uri4, four, null);
        Document five5 = new DocumentImpl(uri5, five, null);
        Document six6 = new DocumentImpl(uri6, six, null);

        pm.serialize(uri1, one1);
        pm.serialize(uri2, two2);
        pm.serialize(uri3, three3);
        pm.serialize(uri4, four4);
        pm.serialize(uri5, five5);
        pm.serialize(uri6, six6);

        deleteallfromdisk(pm);
    }


    @Test
    void testdeserializationasexpected() throws URISyntaxException, IOException {
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        String two = "Two boys went to the game. One of them ate a hot dog. the hot dog tasted good. They saw other boys.";
        DocumentImpl doc = new DocumentImpl(uri2, two, null);
        pm.serialize(uri2, doc);
        Document des = pm.deserialize(uri2);
        Assertions.assertEquals(two, des.getDocumentTxt());





    }


    void deleteallfromdisk(DocumentPersistenceManager pm) throws URISyntaxException, IOException {
        URI uri1 = new URI("http://yu.instructure.com/courses/65568");
        URI uri2 = new URI("http://piazza.com/class/lrfhl9wotpc6wh/post/534");
        URI uri3 = new URI("http://stackoverflow.com/questions/19307622/java-says-filenotfoundexception-but-file-exists");
        URI uri4 = new URI("http://en.wikipedia.org/wiki/Uniform_Resource_Identifier#:~:text=URI%3A%20%22http%3A%2F%2Fwww.,to%20select%20the%20requested%20document.");
        URI uri5 = new URI("http://www.torahweb.org/torah/special/2024/rleb_feeling.html");
        URI uri6 = new URI("http://www.sefaria.org/Tiferet_Yisrael.23.2?lang=bi&with=Notes%20by%20Rabbi%20Yehoshua%20Hartman&lang2=en");

        pm.delete(uri1);
        pm.delete(uri2);
        pm.delete(uri3);
        pm.delete(uri4);
        pm.delete(uri5);
        pm.delete(uri6);
    }

    private String uritopath(URI uri) {
        String uriasstring = uri.toString();
        String uristring = uriasstring.replace("http://", "");
        return uristring + ".json";
    }

}
