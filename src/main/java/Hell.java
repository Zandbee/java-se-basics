import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by vstrokova on 12.05.2016.
 */
public class Hell {

    private static final String PHONES_ZIP = "phones.txt";
    private static final String EMAILS_ZIP = "emails.txt";
    private static final String IN_ZIP = "inputs.zip";
    private static final String OUT_ZIP = "inputsv2.zip";

    public static void main(String args[]) throws Exception {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(IN_ZIP)));

        FileOutputStream dest = new FileOutputStream(OUT_ZIP);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        ZipTransformer zipTransformer = new ZipTransformer();

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            System.out.println("ENTRY: " + entry.getName());

            out.putNextEntry(new ZipEntry(entry.getName()));
            out.write(zipTransformer.readEntry(zis, entry.getName()));

            if (entry.getName().endsWith(".zip")) {
                out.closeEntry();
            }

            System.out.println("PUT: " + entry.getName());
        }

        out.putNextEntry(new ZipEntry(PHONES_ZIP)); // TODO: if emails != null ?
        out.write(Utils.getBytes(zipTransformer.getAllPhones()));

        out.putNextEntry(new ZipEntry(EMAILS_ZIP)); // TODO: if emails != null ?
        out.write(Utils.getBytes(zipTransformer.getAllEmails()));

        out.close();
    }
}
