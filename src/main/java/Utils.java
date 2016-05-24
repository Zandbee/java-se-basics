import java.util.Set;

public class Utils {
    public static byte[] getBytes(Set<String> stringSet) {
        StringBuilder sb = new StringBuilder();
        for (String string : stringSet) {
            sb.append(string).append("\r\n");
        }

        return sb.toString().getBytes();
    }
}
