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
    private static final String NOT_NUMBERS = "[^0-9]";
    private TreeSet<String> allEmails; // TODO: rename?
    private TreeSet<String> allPhones; // TODO: rename?

    public ZipTransformer() {

    }

    public byte[] readEntry(ZipInputStream zipInputStream, String entryName) throws Exception {
        ZipEntry entry;
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        if (allPhones == null) {
            allPhones = new TreeSet<>();
        }
        if (allEmails == null) {
            allEmails = new TreeSet<>();
        }

            System.out.println("ENTRY IN READER: " + entryName);

            if (entryName.endsWith(".zip")) {
                ZipInputStream internalZis = new ZipInputStream(zipInputStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream internalZos = new ZipOutputStream(baos);

                while ((entry = internalZis.getNextEntry()) != null) {
                    internalZos.putNextEntry(new ZipEntry(entry.getName()));
                    internalZos.write(readEntry(internalZis, entry.getName()));
                }
                internalZos.close();
                data.write(baos.toByteArray());
            }

            if (entryName.endsWith("txt")) {
                data.write(readTxt(zipInputStream));
            }

        return data.toByteArray();
    }

    private byte[] readTxt(ZipInputStream zipInputStream) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
        StringBuilder sb = new StringBuilder();
        int pos, phonePrefixStart, phonePrefixEnd;

        String line, phone, phonePrefix, mails; // TODO: rename 'mails'
        String[] emails;
        while ((line = reader.readLine()) != null) {
            if (line.contains("@")) {
                pos = line.lastIndexOf(" ", line.indexOf("@")); // phone num ends here and emails begin

                phone = line.substring(0, pos);
                System.out.println("PHONE: " + phone);

                mails = line.substring(pos);
                emails = mails.trim().split(DELIMITERS);
                /*System.out.println("EMAILS:");
                for (String email : emails) {
                    System.out.println(email);
                }*/

                phonePrefixStart = phone.indexOf("(");
                phonePrefixEnd = phone.indexOf(")");
                phonePrefix = phone.substring(phonePrefixStart, phonePrefixEnd + 1);
                //System.out.println("PHONE PREFIX: " + phonePrefix);
                switch (phonePrefix) { // a. 101 -> 401; b. 202 -> 802; c. 301 -> 321.
                    case "(101)":
                        phone = phone.replace(phonePrefix, "(401)");
                        //System.out.println("PHONE changed: " + phone);
                        break;
                    case "(202)":
                        phone = phone.replace(phonePrefix, "(802)");
                        //System.out.println("PHONE changed: " + phone);
                        break;
                    case "(301)":
                        phone = phone.replace(phonePrefix, "(321)");
                        //System.out.println("PHONE changed: " + phone);
                        break;
                    default:
                        break;
                }
                phone = phone.substring(0, phonePrefixEnd + 2)
                        .concat(phone.substring(phonePrefixEnd + 2).replaceAll(NOT_NUMBERS, ""));

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

    public TreeSet<String> getAllPhones() {
        return allPhones;
    }

    public TreeSet<String> getAllEmails() {
        return allEmails;
    }
}
