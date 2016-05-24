import java.io.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Hell {

    private static final String IN_ZIP = "inputs.zip";
    private static final String OUT_ZIP = "inputsv2.zip";

    public static void main(String args[]) throws Exception {
        double start, finish;

        start = System.currentTimeMillis();

        // TODO: vvvv
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(IN_ZIP)));

        FileOutputStream dest = new FileOutputStream(OUT_ZIP);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        ZipTransformer zipTransformer = new ZipTransformer();

        zipTransformer.copyZip(zis, out);

        zipTransformer.writeEmailFile(out);
        zipTransformer.writePhoneFile(out);

        out.close();

        finish = System.currentTimeMillis();
        System.out.println("TOTAL TIME: " + (finish - start) / 1000);
    }
}
