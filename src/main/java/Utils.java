import java.io.UnsupportedEncodingException;

public class Utils {

    private static final String UTF_8 = "UTF-8";

    public static byte[] toBytes(String str) {
        try {
            return str.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
