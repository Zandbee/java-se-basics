import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by vstrokova on 12.05.2016.
 */
public class Hell {

    public static void main(String args[]) throws Exception {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream("inputs.zip")));

        extract(zis);
    }

    private static void extract(ZipInputStream zipInputStream) throws Exception {
        while (zipInputStream.available() > 0) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null) break;

            String entryName = zipEntry.getName();
            System.out.println(entryName);

            if (entryName.endsWith(".zip")) {
                ZipInputStream zis = new ZipInputStream(zipInputStream);
                extract(zis);
            } else if (entryName.endsWith(".txt")) {
                int c;
                while ((c = zipInputStream.read()) != -1) {
                    System.out.print((char) c);
                }
            }
        }
    }
}
