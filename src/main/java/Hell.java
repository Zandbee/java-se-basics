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

        FileOutputStream dest = new FileOutputStream("inputsv2.zip");
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        copyZip(zis, out);

        out.close();
    }

    private static void copyZip(ZipInputStream zipInputStream, ZipOutputStream out) throws Exception {
        ZipEntry zipEntry;
        StringBuilder sbPhones, sbEmails;
        sbPhones = sbEmails = new StringBuilder();
        int count, pos;
        char separators[] = {'\t', ' ', ',', ';'};
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = zipEntry.getName();
            System.out.println(entryName);

            if (entryName.endsWith(".zip")) {
                System.out.println("Add zip to zip: " + entryName);
                ZipInputStream zis = new ZipInputStream(zipInputStream);
                out.putNextEntry(new ZipEntry(entryName));
                /*while ((count = zis.read()) != -1) {
                    System.out.println("Writing zip");
                    out.write(count);
                }*/

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(byteOutputStream);
                //byteOutputStream.toByteArray()

                copyZip(zis, zos);

            } else if (entryName.endsWith(".txt")) {
                System.out.println("Add txt to zip: " + entryName);

                BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                StringBuilder sbTxt = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("@")) {
                        pos = line.indexOf("@");
                        System.out.println("PHONE: " + line.substring(0, line.lastIndexOf(" ", pos)).trim().replace("-", ""));
                    }

                    sbTxt.append(line).append("\r\n");
                }

                ZipEntry newZipEntry = new ZipEntry(entryName);
                out.putNextEntry(newZipEntry);
                out.write(sbTxt.toString().getBytes("UTF-8"));
            } /*else if (zipEntry.isDirectory()) {
                System.out.println("Add dir to zip: " + entryName);
                System.out.println();
                out.putNextEntry(new ZipEntry(entryName));
            }*/
        }
        System.out.println("COPIED");
    }
}
