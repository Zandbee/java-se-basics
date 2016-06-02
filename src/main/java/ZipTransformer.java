import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.zip.*;

public class ZipTransformer {

    private static final Logger logger = Logger.getLogger(ZipTransformer.class.getName());

    private static final String PREFIX_101 = "101";
    private static final String PREFIX_202 = "202";
    private static final String PREFIX_301 = "301";
    private static final String PREFIX_321 = "321";
    private static final String PREFIX_401 = "401";
    private static final String PREFIX_802 = "802";

    private static final String PHONES_ZIP = "phones.txt";
    private static final String EMAILS_ZIP = "emails.txt";

    private static final String UTF_8 = "UTF-8";

    private Set<String> allEmails = new TreeSet<>(); // A TreeSet for unique emails in natural order
    private Set<String> allPhones = new TreeSet<>(); // A TreeSet for unique phone numbers in natural order
    private Set<String> allLines = new HashSet<>();

    private ZipInputStream in;
    private ZipOutputStream out;

    public ZipTransformer(ZipInputStream in, ZipOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void copyZipAndFilterPhonesEmails() throws IOException {
        copyZip(in, out);

        writeEmailFile();
        writePhoneFile();
    }

    private void copyZip(ZipInputStream in, ZipOutputStream out) throws IOException {
        ZipEntry entry;
        String entryName;

        while ((entry = in.getNextEntry()) != null) {
            entryName = entry.getName();
            logger.info("Processing " + entryName);
            out.putNextEntry(new ZipEntry(entryName));

            if (isTxt(entryName)) {
                transformAndWriteTextFile(in, out);
            }

            if (isZip(entryName)) {
                writeZip(in, out);
            }

            if (isGz(entryName)) {
                transformAndWriteGzip(in, out);
            }
        }
    }

    private void transformAndWriteTextFile(ZipInputStream in, ZipOutputStream out) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF_8));
        while ((line = reader.readLine()) != null) {
            transformAndWrite(line, out);
        }
    }

    private void writeZip(ZipInputStream in, ZipOutputStream out) throws IOException {
        ZipInputStream internalZip = new ZipInputStream(in);
        ZipOutputStream internalOut = new ZipOutputStream(out);

        copyZip(internalZip, internalOut);

        internalOut.finish();
        out.closeEntry();
    }

    private void transformAndWriteGzip(ZipInputStream in, ZipOutputStream out) throws IOException {
        GZIPInputStream internalGzip = new GZIPInputStream(in);
        GZIPOutputStream internalOut = new GZIPOutputStream(out);
        BufferedReader reader = new BufferedReader(new InputStreamReader(internalGzip, UTF_8));
        String line;

        while ((line = reader.readLine()) != null) {
            transformAndWrite(line, internalOut);
        }

        internalOut.finish();
    }

    private void transformAndWrite(String line, OutputStream out) throws IOException {
        /*
        I tried using the String class and its methods like substring(), indexOf(), split(regex), and others,
        but it is about 2,5 times longer (50 seconds instead of 20)
        */

        String[] splitted = line.split("@", 2);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, UTF_8));

        if (splitted.length > 1) {
            /*
            If '@' was found, the string contains a phone number and emails.
            splitted[0] contains a phone with delimiters and a local part of the first email
            in this string separated by space.
            splitted[1] contains a domain part of the first email and all the other emails
            in this string, each separated by delimiters.
             */
            String phoneAndFirstEmailLocalPart = splitted[0];
            String[] phonePartsAndFirstEmailLocalPart = phoneAndFirstEmailLocalPart.split(" ");
            int phonePartsAndFirstEmailLocalPartLength = phonePartsAndFirstEmailLocalPart.length;
            String[] phoneParts = new String[phonePartsAndFirstEmailLocalPartLength - 1];
            System.arraycopy(phonePartsAndFirstEmailLocalPart, 0, phoneParts, 0, phonePartsAndFirstEmailLocalPartLength - 1);

            String phone = extractAndFormatPhone(phoneParts);
            allPhones.add(phone);
            bw.write(phone);

            String emailString = phonePartsAndFirstEmailLocalPart[phonePartsAndFirstEmailLocalPartLength - 1] + "@" + splitted[1];
            bw.write(" " + emailString);

            if (allLines.add(emailString)) {
                addUniqueEmails(emailString);
            }
        } else {
            bw.write(line);
        }

        bw.newLine();
        bw.flush();
    }

    private void addUniqueEmails(String emailString) {
        StringBuilder sbEmail = new StringBuilder();
        for (int i = 0; i < emailString.length(); i++) {
            char c = emailString.charAt(i);
            if (c == ' ' || c == '\t' || c == ',' || c == ';') {
                if (sbEmail.length() > 0) {
                    addUniqueEmailIfOrg(sbEmail.toString());
                    sbEmail = new StringBuilder();
                }
            } else {
                sbEmail.append(c);
            }
        }
        if (sbEmail.length() > 0) {
            addUniqueEmailIfOrg(sbEmail.toString());
        }
    }

    private void addUniqueEmailIfOrg(String email) {
        if (email.endsWith(".org")) {
            allEmails.add(email);
        }
    }

    private String extractAndFormatPhone(String[] phoneParts) {
        StringBuilder sbPhone = new StringBuilder();
        StringBuilder sbPrefix = new StringBuilder();
        boolean isPrefix = false;

        for (String phonePart : phoneParts) {
            int phonePartLength = phonePart.length();
            for (int j = 0; j < phonePartLength; j++) {
                char c = phonePart.charAt(j);
                if (c == '(') {
                    isPrefix = true;
                } else if (c == ')') {
                    isPrefix = false;
                    String prefix = checkAndChangePhonePrefix(sbPrefix.toString());
                    sbPhone.append(" (").append(prefix).append(") ");
                } else if (c == '+' || Character.isDigit(c)) {
                    if (isPrefix) {
                        sbPrefix.append(c);
                    } else {
                        sbPhone.append(c);
                    }
                }
            }
        }

        return sbPhone.toString();
    }

    private static String checkAndChangePhonePrefix(String prefix) {
        switch (prefix) { // a. 101 -> 401; b. 202 -> 802; c. 301 -> 321.
            case PREFIX_101:
                prefix = PREFIX_401;
                break;
            case PREFIX_202:
                prefix = PREFIX_802;
                break;
            case PREFIX_301:
                prefix = PREFIX_321;
                break;
            default:
                break;
        }
        return prefix;
    }

    private void writePhoneFile() throws IOException {
        out.putNextEntry(new ZipEntry(PHONES_ZIP));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, UTF_8));
        for (String phoneLine : allPhones) {
            bw.write(phoneLine);
            bw.newLine();
        }
        bw.flush();
    }

    private void writeEmailFile() throws IOException {
        out.putNextEntry(new ZipEntry(EMAILS_ZIP));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, UTF_8));
        for (String emailLine : allEmails) {
            bw.write(emailLine);
            bw.newLine();
        }
        bw.flush();
    }

    private static boolean isTxt(String name) {
        return name.endsWith(".txt");
    }

    private static boolean isZip(String name) {
        return name.endsWith(".zip");
    }

    private static boolean isGz(String name) {
        return name.endsWith(".gz");
    }

}