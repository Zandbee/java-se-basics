import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static final String IN_ZIP = "inputs.zip";
    private static final String OUT_ZIP = "inputsv2.zip";

    private static String inZip, outZip;

    public static void main(String args[]) {
        setZipFiles(args);

        logger.info("Started copying at " + new Date());
        double start = System.currentTimeMillis();

        transformZip();

        logger.info("Finished copying at " + new Date());
        double finish = System.currentTimeMillis();
        logger.info("TOTAL TIME: " + ((finish - start) / 1000) + " seconds");
    }

    private static void transformZip() {
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(inZip)));
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outZip)))) {
            ZipTransformer zipTransformer = new ZipTransformer(zis, out);

            zipTransformer.copyZipAndFilterPhonesEmails();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Invalid input zip name. No such file: " + inZip, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error processing files: " + ex.getMessage(), ex);
        }
    }

    private static void setZipFiles(String args[]) {
        int argsLength;

        argsLength = args.length;
        switch (argsLength) {
            case 1:
                inZip = args[0];
                outZip = OUT_ZIP;
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

        logger.info("Input file: " + inZip);
        logger.info("Output file: " + outZip);
    }
}
