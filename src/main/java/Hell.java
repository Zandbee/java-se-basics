import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by vstrokova on 12.05.2016.
 */
public class Hell {

    public static void main(String args[]) throws Exception {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream("inputs.zip")));

        copyZip(zis);
    }

    private static void copyZip(ZipInputStream zipInputStream) throws Exception {
        FileOutputStream dest = new FileOutputStream("inputsv2.zip");
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        while (zipInputStream.available() > 0) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null) break;

            String entryName = zipEntry.getName();
            System.out.println(entryName);

            if (entryName.endsWith(".zip")) {
                ZipInputStream zis = new ZipInputStream(zipInputStream);
                copyZip(zis);
            } else if (entryName.endsWith(".txt")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\r\n");
                }

                ZipEntry newZipEntry = new ZipEntry(entryName);
                out.putNextEntry(newZipEntry);
                out.write(sb.toString().getBytes("UTF-8"));
            }
        }

        out.close();
    }
}
