import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class StackImplTest {

    StackImpl<Integer> teststack;
    DocumentStoreImpl docstore = new DocumentStoreImpl();

    @BeforeEach
    void beforeeach() throws IOException {
        URI uri = null;
        try {
            uri = new URI("hello");
        } catch (URISyntaxException e) {
        }
        Random bytes = new Random();
        byte[] arr = new byte[7];
        bytes.nextBytes(arr);
        ByteArrayInputStream stream = new ByteArrayInputStream(arr);
    }

    @Test
    void teststack(){
        this.teststack = new StackImpl<>();
        //ensure that pushing one object works
        Integer a = 5;
        teststack.push(a);
        Assertions.assertEquals(5, teststack.peek());
        //ensure pushing a second object works
        Integer b = 6;
        teststack.push(6);
        Assertions.assertEquals(6, teststack.peek());

        //try popping
        Integer c = (Integer) teststack.pop();
        Assertions.assertEquals(6, c);
        Assertions.assertEquals(5, teststack.peek());

        //test size

        Assertions.assertEquals(1, teststack.size());

    }

    @Test
    void testaddput() throws IOException {
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
    }

    @Test
    void testsize(){
        this.teststack = new StackImpl<>();
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 4;
        Integer e = 5;
        teststack.push(a);
        teststack.push(b);
        teststack.push(c);
        Assertions.assertEquals(3, teststack.size());
        teststack.pop();
        Assertions.assertEquals(2, teststack.size());
        teststack.push(d);
        Assertions.assertEquals(3, teststack.size());



    }

}
