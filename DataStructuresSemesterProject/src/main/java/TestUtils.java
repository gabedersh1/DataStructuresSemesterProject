import edu.yu.cs.com1320.project.stage6.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class TestUtils {

    public static void deleteTree(File base) {
        try {
            File[] files = base.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteTree(file);
                }
                else {
                    file.delete();
                }
            }
            base.delete();
        }
        catch (Exception e) {
        }
    }

    public static File urlToFile(File baseDir, URI url) {
        String auth = url.getAuthority();
        String path = url.getPath().replaceAll("//", File.separator) + ".json";
        File f = new File(baseDir, auth + File.separator + path);
        System.err.println("TestUtils.urlToFile file path: " + f.getAbsolutePath());
        return f;
    }

    public static String getContents(File baseDir, URI url) throws IOException {
        File file = urlToFile(baseDir, url);
        System.err.println("TestUtils.getContents file path: " + file.getAbsolutePath());

        if (!file.exists()) {
            return null;
        }
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    public static boolean equalButNotIdentical(Document first, Document second) throws IOException {
        if(first == null){
            throw new NullPointerException("first document was null");
        }
        if(second == null){
            throw new NullPointerException("second document was null");
        }
        if(System.identityHashCode(first) == System.identityHashCode(second)){
            return false;
        }
        if(!first.getKey().equals(second.getKey())){
            return false;
        }
        if(!first.getDocumentTxt().equals(second.getDocumentTxt())){
            return false;
        }
        return true;
    }
}