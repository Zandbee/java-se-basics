import java.util.Set;

/**
 * Created by vstrokova on 18.05.2016.
 */
public class Utils {
    public static byte[] getBytes(Set<String> stringSet) {
        StringBuilder sb = new StringBuilder();
        for (String string : stringSet) {
            sb.append(string).append("\r\n");
        }

        return sb.toString().getBytes();
    }
}
