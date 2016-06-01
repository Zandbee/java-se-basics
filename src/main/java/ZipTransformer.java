import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.*;

public class ZipTransformer {

    private static final String PREFIX_101 = "101";
    private static final String PREFIX_202 = "202";
    private static final String PREFIX_301 = "301";
    private static final String PREFIX_321 = "321";
    private static final String PREFIX_401 = "401";
    private static final String PREFIX_802 = "802";

    private static final String PHONES_ZIP = "phones.txt";
    private static final String EMAILS_ZIP = "emails.txt";

    private static final String UTF_8 = "UTF-8";

    private Set<String> allEmails = new TreeSet<>();
    private Set<String> allPhones = new TreeSet<>();
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

    private void copyZip(ZipInputStream in, ZipOutputStream out) throws IOException{
        ZipEntry entry;
        String entryName;

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

    private void readTextFile(ZipInputStream in, ZipOutputStream out) throws IOException{
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF_8));
        while ((line = reader.readLine()) != null) {
            readLine(line, out);
        }
    }

    private void readZip(ZipInputStream in, ZipOutputStream out) throws IOException{
        ZipInputStream internalZip = new ZipInputStream(in);
        ZipOutputStream internalOut = new ZipOutputStream(out);

        copyZip(internalZip, internalOut);

        internalOut.finish();
        out.closeEntry();
    }

    private void readGzip(ZipInputStream in, ZipOutputStream out) throws IOException{
        GZIPInputStream internalGzip = new GZIPInputStream(in);
        GZIPOutputStream internalOut = new GZIPOutputStream(out);
        BufferedReader reader = new BufferedReader(new InputStreamReader(internalGzip, UTF_8));
        String line;

        while ((line = reader.readLine()) != null) {
            readLine(line, internalOut);
        }

        internalOut.finish();
    }

    private void readLine(String line, OutputStream out) throws IOException{
        String[] splitted = line.split("@", 2);
        if (splitted.length > 1) {
            String phoneAndKusochekEmail = splitted[0];
            PhoneFormatResult phoneFormatResult = extractAndFormatPhone(phoneAndKusochekEmail);
            String phone = phoneFormatResult.phone;
            allPhones.add(phone);
            out.write(Utils.toBytes(phone));

            String emailString = phoneFormatResult.kusochekEmail + "@" + splitted[1];
            out.write(Utils.toBytes(" "));
            out.write(Utils.toBytes(emailString));

            if (allLines.add(emailString)) {
                addUniqueEmails(emailString);
            }
        } else {
            out.write(Utils.toBytes(line));
        }

        out.write(Utils.toBytes("\r\n"));
    }

    private void addUniqueEmails(String emailString) {
        StringBuilder sbEmail = new StringBuilder();
        for (int i = 0; i < emailString.length(); i++) {
            char c = emailString.charAt(i);
            if (c == ' ' || c == '\t' || c == ',' || c == ';') {
                if (sbEmail.length() > 0) {
                    allEmails.add(sbEmail.toString());
                    sbEmail = new StringBuilder();
                }
            } else {
                sbEmail.append(c);
            }
        }
        if (sbEmail.length() > 0) {
            allEmails.add(sbEmail.toString());
        }
    }

    private PhoneFormatResult extractAndFormatPhone(String phoneAndKusochekEmail) {
        String[] phoneParts = phoneAndKusochekEmail.split(" ");

        StringBuilder sbPhone = new StringBuilder();
        StringBuilder sbPrefix = new StringBuilder();
        boolean isPrefix = false;
        for (int i = 0; i < phoneParts.length - 1; i++) {
            String phonePart = phoneParts[i];
            int phonePartLength = phonePart.length();
            for (int j = 0; j < phonePartLength; j++) {
                char c = phonePart.charAt(j);
                if (c == '(') {
                    isPrefix = true;
                } else if (c == ')') {
                    isPrefix = false;
                    String prefix = sbPrefix.toString();
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
                    sbPhone.append(" (").append(prefix).append(") ");
                } else if (c == '+' ||
                        c == '0' ||
                        c == '1' ||
                        c == '2' ||
                        c == '3' ||
                        c == '4' ||
                        c == '5' ||
                        c == '6' ||
                        c == '7' ||
                        c == '8' ||
                        c == '9') {
                    if (isPrefix) {
                        sbPrefix.append(c);
                    } else {
                        sbPhone.append(c);
                    }
                }

            }
        }

        return new PhoneFormatResult(sbPhone.toString(), phoneParts[phoneParts.length - 1]);
    }

    private void writePhoneFile() throws IOException {
        out.putNextEntry(new ZipEntry(PHONES_ZIP));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, UTF_8));
        for (String phoneLine : allPhones){
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

    private boolean isTxt(String name) {
        return name.endsWith(".txt");
    }

    private boolean isZip(String name) {
        return name.endsWith(".zip");
    }

    private boolean isGz(String name) {
        return name.endsWith(".gz");
    }

    private static class PhoneFormatResult {
        private String phone;
        private String kusochekEmail;

        PhoneFormatResult(String phone, String kusochekEmail) {
            this.phone = phone;
            this.kusochekEmail = kusochekEmail;
        }
    }
}