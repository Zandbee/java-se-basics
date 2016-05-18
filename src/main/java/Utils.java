import java.util.ArrayList;

/**
 * Created by vstrokova on 18.05.2016.
 */
public class Utils {
    public static byte[] getBytes(ArrayList<String> stringArray) {
        StringBuilder sb = new StringBuilder();
        for (String string : stringArray) {
            sb.append(string).append("\r\n");
        }

        return sb.toString().getBytes();
    }
}
