import edu.yu.cs.com1320.project.impl.BTreeImpl;
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


public class BTreeImplTest {

    BTreeImpl<Integer, String> st;

    @Test

    void testget() {
        st = new BTreeImpl<>();
        st.put(1, "one");
        st.put(2, "two");
        st.put(3, "three");
        st.put(4, "four");
        st.put(5, "five");
        st.put(6, "six");
        st.put(7, "seven");
        st.put(8, "eight");
        st.put(9, "nine");
        st.put(10, "ten");
        st.put(11, "eleven");
        st.put(12, "twelve");
        st.put(13, "thirteen");
        st.put(14, "fourteen");
        st.put(15, "fifteen");
        st.put(16, "sixteen");
        st.put(17, "seventeen");
        st.put(18, "eighteen");
        st.put(19, "nineteen");
        st.put(20, "twenty");
        st.put(21, "twenty one");
        st.put(22, "twenty two");
        st.put(23, "twenty three");
        st.put(24, "twenty four");
        st.put(25, "twenty five");
        st.put(26, "twenty six");
        Assertions.assertEquals("four", st.get(4));
        Assertions.assertEquals("twenty five", st.get(25));
        Assertions.assertEquals("seventeen", st.get(17));
        //replace, return old value
        //Assertions.assertEquals("seventeen", st.put(17, "Seventeen"));
        //delete, return old value
        //Assertions.assertEquals("thirteen", st.put(13, null));


    }


}
