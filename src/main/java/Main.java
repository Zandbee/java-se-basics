import java.io.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.lang.System.*;

public class Main {

    private static final String IN_ZIP = "inputs.zip";
    private static final String OUT_ZIP = "inputsv2.zip";

    private static String inZip, outZip;

    public static void main(String args[]) throws Exception{
        double start, finish;

        setZipFiles(args);

        out.println("Copying...");
        start = System.currentTimeMillis();

        transformZip();

        finish = System.currentTimeMillis();
        out.println("TOTAL TIME: " + (finish - start) / 1000);
    }

    private static void transformZip() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(inZip)));
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outZip)))) {
            ZipTransformer zipTransformer = new ZipTransformer(zis, out);

            zipTransformer.copyZipAndFilterPhonesEmails();
        } catch (FileNotFoundException ex) {
            err.println("Invalid input zip name - no such file.");
        }
    }

    private static void setZipFiles(String args[]) {
        int argsLength;

        argsLength = args.length;
        switch (argsLength) {
            case 1:
                inZip = IN_ZIP;
                outZip = args[0];
                break;
            case 2:
                inZip = args[0];
                outZip = args[1];
                break;
            default:
                inZip = IN_ZIP;
                outZip = OUT_ZIP;
                break;
        }

        out.println("Input file: " + inZip);
        out.println("Output file: " + outZip);
    }
}
