import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by vstrokova on 12.05.2016.
 */
public class Hell {

    public static void main(String args[]) throws Exception {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream("inputs.zip")));

        FileOutputStream dest = new FileOutputStream("inputsv2.zip");
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        ZipTransformer zipTransformer = new ZipTransformer();
        zipTransformer.copyZip(zis, out);

        out.close();
    }

    /*private static void copyZip(ZipInputStream zipInputStream, ZipOutputStream out) throws Exception {
        ZipEntry zipEntry;
        int count, pos;
        String delimiters = "[ \t,;]+";
        String phonesZip = "phones.txt";
        String emailsZip = "emails.txt";
        ArrayList<String> allEmails = new ArrayList<>(); // is initialization ok? TODO: rename?
        ArrayList<String> allPhones = new ArrayList<>(); // is initialization ok? TODO: rename?
        allPhones = new ArrayList<>();
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = zipEntry.getName();
            System.out.println(entryName);

            if (entryName.endsWith(".zip")) {
                System.out.println("Add zip to zip: " + entryName);
                ZipInputStream zis = new ZipInputStream(zipInputStream);
                out.putNextEntry(new ZipEntry(entryName));
                *//*while ((count = zipInputStream.read()) != -1) {
                    System.out.println("Writing zip");
                    out.write(count);
                }*//*

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(out);
                //byteOutputStream.toByteArray()

                copyZip(zis, zos);

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
            } *//*else if (zipEntry.isDirectory()) {
                System.out.println("Add dir to zip: " + entryName);
                System.out.println();
                out.putNextEntry(new ZipEntry(entryName));
            }*//*
        }

        out.putNextEntry(new ZipEntry(phonesZip));
        out.write(Utils.getBytes(allPhones));

        out.putNextEntry(new ZipEntry(emailsZip));
        out.write(Utils.getBytes(allEmails));

        System.out.println("COPIED");
    }*/
}
