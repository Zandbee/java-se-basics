import java.io.*;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.zip.*;

public class ZipTransformer {

    private static final String PREFIX_101 = "101";
    private static final String PREFIX_202 = "202";
    private static final String PREFIX_301 = "301";
    private static final String PREFIX_321 = "321";
    private static final String PREFIX_401 = "401";
    private static final String PREFIX_802 = "802";

    private static final String DELIMITERS = "[ \t,;]+";
    private static final String NOT_NUMBERS = "[^0-9]";
    private static final String PHONES_ZIP = "phones.txt";
    private static final String EMAILS_ZIP = "emails.txt";
    private TreeSet<String> allEmails;
    private TreeSet<String> allPhones;

    public ZipTransformer() { }

    public void copyZip(ZipInputStream in, ZipOutputStream out) throws Exception {
        ZipEntry entry;
        String entryName;

        if (allPhones == null) {
            allPhones = new TreeSet<>();
        }
        if (allEmails == null) {
            allEmails = new TreeSet<>();
        }

        while ((entry = in.getNextEntry()) != null) {
            entryName = entry.getName();
            out.putNextEntry(new ZipEntry(entryName));

            if (isTxt(entryName)) {
                readTextFile(in, out);
            }

            if (isZip(entryName)) {
                readZip(in, out);
            }

            if (isGz(entryName)) {
                readGzip(in, out);
            }
        }

    }

    // if txt, read by string and write to out by string
    private void readTextFile(ZipInputStream in, ZipOutputStream out) throws Exception {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while ((line = reader.readLine()) != null) {
            out.write(readLine(line));
        }
    }

    // if zip, copyZip
    private void readZip(ZipInputStream in, ZipOutputStream out) throws Exception {
        ZipInputStream internalZip = new ZipInputStream(in);
        ZipOutputStream internalOut = new ZipOutputStream(out);

        copyZip(internalZip, internalOut);

        internalOut.finish();
        out.closeEntry();
    }

    // if gz, read by string and write to out by string
    private void readGzip(ZipInputStream in, ZipOutputStream out) throws Exception {
        GZIPInputStream internalGzip = new GZIPInputStream(in);
        GZIPOutputStream internalOut = new GZIPOutputStream(out);
        BufferedReader reader = new BufferedReader(new InputStreamReader(internalGzip, "UTF-8"));
        String line;

        while ((line = reader.readLine()) != null) {
            internalOut.write(readLine(line));
        }

        internalOut.finish();
    }

    private byte[] readLine(String line) throws Exception {
        int phoneEnd, phonePrefixStart, phonePrefixEnd;
        String phone, phonePrefix, emailString;
        String[] emails;
        StringBuilder sb = new StringBuilder();

        System.out.println("LINE: " + line);

        if (line.contains("@")) {
            phoneEnd = line.lastIndexOf(" ", line.indexOf("@")); // phone num ends here and emails begin

            phone = line.substring(0, phoneEnd); // TODO: trim?
            System.out.println("PHONE: " + phone);

            emailString = line.substring(phoneEnd);
            emails = emailString.trim().split(DELIMITERS); // TODO: trim?

            phonePrefixStart = phone.indexOf("(");
            phonePrefixEnd = phone.indexOf(")");
            phonePrefix = phone.substring(phonePrefixStart, phonePrefixEnd + 1);
            switch (phonePrefix) { // a. 101 -> 401; b. 202 -> 802; c. 301 -> 321.
                case PREFIX_101:
                    phone = phone.replace(phonePrefix, PREFIX_401); // TODO: extract String const?
                    break;
                case PREFIX_202:
                    phone = phone.replace(phonePrefix, PREFIX_802);
                    break;
                case PREFIX_301:
                    phone = phone.replace(phonePrefix, PREFIX_321);
                    break;
                default:
                    break;
            }
            phone = phone.substring(0, phonePrefixEnd + 2)
                    .concat(phone.substring(phonePrefixEnd + 2).replaceAll(NOT_NUMBERS, "")); // TODO: is it ok?

            line = phone.concat(emailString);

            allPhones.add(phone);
            allEmails.addAll(Arrays.asList(emails));
        }

        sb.append(line).append("\r\n"); // TODO: vvvv

        byte[] txtBytes = new byte[2048];
        try {
            txtBytes = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return txtBytes;
    }

    public void writePhoneFile(ZipOutputStream out) throws Exception {
        out.putNextEntry(new ZipEntry(PHONES_ZIP));
        out.write(Utils.getBytes(allPhones)); // TODO: if phones == null ?
    }

    public void writeEmailFile(ZipOutputStream out) throws Exception {
        out.putNextEntry(new ZipEntry(EMAILS_ZIP));
        out.write(Utils.getBytes(allEmails)); // TODO: if emails == null ?
    }

    private boolean isTxt(String name) {
        return name.endsWith(".txt");
    }

    private boolean isZip(String name) {
        return name.endsWith(".zip");
    }

    private boolean isGz(String name) {
        return name.endsWith(".gz");
    }
}
