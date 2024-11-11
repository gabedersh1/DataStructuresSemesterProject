import edu.yu.cs.com1320.project.impl.TrieImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TrieImplTest {
    TrieImpl trie;

    @BeforeEach
    void beforeEach() {
        this.trie = new TrieImpl<>();
    }


    @Test
    void testput() {
        trie.put("hello", "byebye");
    }

    @Test
    void testgetallwithPrefixSorted() {
        DocumentComparator doccomp = new DocumentComparator();
        List s = new ArrayList();
        s.add(33);
        trie.put("Hello darkness my old friend", 33);
        trie.put("I've come to talk to you again. I've.", 47);
        trie.put("Ivan. Because a vision softly creeping", 23);
        Assertions.assertEquals(s, trie.getAllWithPrefixSorted("H", doccomp));

        List b = new ArrayList<>();
        b.add(47);
        b.add(23);
        Assertions.assertEquals(b, trie.getAllWithPrefixSorted("I", doccomp));

    }

    @Test
    void testget() {
        Set s = new HashSet();
        s.add(23);
        s.add(47);
        trie.put("key", 23);
        trie.put("key", 47);
        trie.put("ke", 23);
        Assertions.assertEquals(s, trie.get("key"));
        Assertions.assertEquals(Collections.emptySet(), trie.get("Key"));
    }

    @Test
    void testdeleteallwithprefix() {
        Set s = new HashSet();
        s.add(23);
        s.add(47);
        trie.put("key", 23);
        trie.put("key", 47);
        trie.put("k", 22);
        Assertions.assertEquals(s, trie.deleteAllWithPrefix("key"));
    }

    @Test
    void testdeletall() {
        trie.put("key", 23);
        trie.put("key", 47);
        trie.put("keys", 55);
        trie.deleteAll("key");
        Set s = new HashSet();
        s.add(55);
        Assertions.assertEquals(null, trie.get("key"));
        Assertions.assertEquals(s, trie.get("keys"));
    }

    @Test
    void testdelete() {
        Set s = new HashSet();
        s.add(23);
        s.add(47);
        trie.put("key", 23);
        trie.put("key", 47);
        trie.put("keys", 23);
        Assertions.assertEquals(s, trie.get("key"));
        s.remove(23);
        Assertions.assertEquals(23, trie.delete("key", 23));
        Assertions.assertEquals(s, trie.get("key"));
    }

    private class DocumentComparator implements Comparator<Integer> {
        String word;

        private DocumentComparator() {
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return (-1) * (o1.compareTo(o2));
        }
    }
}
