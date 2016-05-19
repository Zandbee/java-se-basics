import java.io.*;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by vstrokova on 18.05.2016.
 */
public class ZipTransformer {

    private static final String DELIMITERS = "[ \t,;]+";
    private static final String PHONES_ZIP = "phones.txt";
    private static final String EMAILS_ZIP = "emails.txt";
    private TreeSet<String> allEmails; // TODO: rename?
    private TreeSet<String> allPhones; // TODO: rename?

    public ZipTransformer() {

    }

    public void copyZip(ZipInputStream zipInputStream, ZipOutputStream out) throws Exception {
        ZipEntry zipEntry;
        int count;

        if (allPhones == null) {
            allPhones = new TreeSet<>();
        }
        if (allEmails == null) {
            allEmails = new TreeSet<>();
        }

        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = zipEntry.getName();
            System.out.println(entryName);

            if (entryName.endsWith(".zip")) {
                System.out.println("Add zip to zip: " + entryName);


                /*while ((count = zipInputStream.read()) != -1) {
                    System.out.println("Writing zip");
                    internalOut.write(count);
                }*/

            } else if (entryName.endsWith(".txt")) {
                System.out.println("Add txt to zip: " + entryName);

                ZipEntry newZipEntry = new ZipEntry(entryName);
                out.putNextEntry(newZipEntry);
                out.write(readTxt(zipInputStream));
            }
        }


        /*out.putNextEntry(new ZipEntry(PHONES_ZIP));
        out.write(Utils.getBytes(allPhones));

        out.putNextEntry(new ZipEntry(EMAILS_ZIP));
        out.write(Utils.getBytes(allEmails));*/

        System.out.println("COPIED");
    }

    private byte[] readTxt(ZipInputStream zipInputStream) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
        StringBuilder sb = new StringBuilder();
        int pos;

        String line, phone, phonePrefix, mails; // TODO: rename 'mails'
        String[] emails;
        while ((line = reader.readLine()) != null) {
            if (line.contains("@")) {
                pos = line.lastIndexOf(" ", line.indexOf("@")); // phone num ends here and emails begin

                phone = line.substring(0, pos);
                System.out.println("PHONE: " + phone);

                mails = line.substring(pos);
                emails = mails.trim().split(DELIMITERS);
                System.out.println("EMAILS:");
                for (String email : emails) {
                    System.out.println(email);
                }

                phonePrefix = phone.substring(phone.indexOf("("), phone.indexOf(")") + 1);
                System.out.println("PHONE PREFIX: " + phonePrefix);
                switch (phonePrefix) { // a. 101 -> 401; b. 202 -> 802; c. 301 -> 321.
                    case "(101)":
                        phone = phone.replace(phonePrefix, "(401)");
                        System.out.println("PHONE changed: " + phone);
                        break;
                    case "(202)":
                        phone = phone.replace(phonePrefix, "(802)");
                        System.out.println("PHONE changed: " + phone);
                        break;
                    case "(301)":
                        phone = phone.replace(phonePrefix, "(321)");
                        System.out.println("PHONE changed: " + phone);
                        break;
                    default:
                        break;
                }

                line = phone.concat(mails);

                allPhones.add(phone);
                allEmails.addAll(Arrays.asList(emails));
            }
            sb.append(line).append("\r\n");
        }

        byte[] txtBytes = new byte[2048];
        try {
            txtBytes = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return txtBytes;
    }
}
