import java.io.UnsupportedEncodingException;
import java.util.Set;

public class Utils {

    public static final String UTF_8 = "UTF-8";

    public static byte[] toBytes(Set<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str).append("\r\n");
        }

        try {
            return sb.toString().getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte[] toBytes(String str) {
        try {
            return str.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
