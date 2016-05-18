import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by vstrokova on 18.05.2016.
 */
public class ZipTransformer {

    private String delimiters = "[ \t,;]+";
    private String phonesZip = "phones.txt";
    private String emailsZip = "emails.txt";
    private ArrayList<String> allEmails; // TODO: rename?
    private ArrayList<String> allPhones; // TODO: rename?

    public ZipTransformer() {
        allEmails = new ArrayList<>();
        allPhones = new ArrayList<>();
    }

    public void copyZip(ZipInputStream zipInputStream, ZipOutputStream out) throws Exception {
        ZipEntry zipEntry;
        int count, pos;

        allPhones = new ArrayList<>();
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = zipEntry.getName();
            System.out.println(entryName);

            if (entryName.endsWith(".zip")) {
                System.out.println("Add zip to zip: " + entryName);
                ZipInputStream zis = new ZipInputStream(zipInputStream);
                out.putNextEntry(new ZipEntry(entryName));
                while ((count = zipInputStream.read()) != -1) {
                    System.out.println("Writing zip");
                    out.write(count);
                }

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(out);
                //byteOutputStream.toByteArray()

                //copyZip(zis, out);

            } else if (entryName.endsWith(".txt")) {
                System.out.println("Add txt to zip: " + entryName);

                BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                StringBuilder sb = new StringBuilder();

                String line, phone, phonePrefix, mails; // TODO: rename 'mails'
                String[] emails;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("@")) {
                        pos = line.lastIndexOf(" ", line.indexOf("@")); // phone num ends here and emails begin

                        phone = line.substring(0, pos);
                        System.out.println("PHONE: " + phone);

                        mails = line.substring(pos);
                        emails = mails.trim().split(delimiters);
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

                ZipEntry newZipEntry = new ZipEntry(entryName);
                out.putNextEntry(newZipEntry);
                out.write(sb.toString().getBytes("UTF-8"));
            } /*else if (zipEntry.isDirectory()) {
                System.out.println("Add dir to zip: " + entryName);
                System.out.println();
                out.putNextEntry(new ZipEntry(entryName));
            }*/
        }

        out.putNextEntry(new ZipEntry(phonesZip));
        out.write(Utils.getBytes(allPhones));

        out.putNextEntry(new ZipEntry(emailsZip));
        out.write(Utils.getBytes(allEmails));

        System.out.println("COPIED");
    }
}
