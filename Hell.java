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
        extractFolder("inputs.zip");
    }

    private static void extract(String zipName) throws Exception{
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream
                (new FileInputStream(zipName)));
        ZipEntry entry;

        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            System.out.println("reading: " + entryName);
            if (entryName.endsWith(".txt")) {
                System.out.println("DATA");
                int c;
                while ((c = zis.read()) != -1) {
                    System.out.print((char) c);
                }
                System.out.println();
            } else if (entryName.endsWith(".zip")) {
                extract(entryName);
            }
        }
    }

    private static void extractFolder(String zipFile) throws ZipException, IOException
    {
        System.out.println(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 4);

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements())
        {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory())
            {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }

            if (currentEntry.endsWith(".zip"))
            {
                // found a zip file, try to open
                extractFolder(destFile.getAbsolutePath());

            }
        }
    }

}
