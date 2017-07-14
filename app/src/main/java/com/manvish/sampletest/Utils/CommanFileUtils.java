package com.manvish.sampletest.Utils;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

import com.manvish.sampletest.Application.AadharTimeAttendanceApplication;
import com.manvish.sampletest.CommonViews.ManvishAlertDialog;
import com.manvish.sampletest.Constants.ManvishPrefConstants;
import com.manvish.sampletest.Structures.RFIDRegData;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by kishan on 2/10/17.
 */

public class CommanFileUtils {

    static StringWriter writer;
    static BufferedWriter bufferedWriter;

    public static void sortByNumber(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.lastIndexOf('_')+1;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format
                    // then default to 0
                }
                return i;
            }
        });

        for(File f : files) {
            Log.d("kishan", "sortByNumber: "+f.getName());
        }
    }



    public static boolean zip(String[] _files, String zipFileName) {
        try {

            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(dest);
            byte data[] = new byte[4096];

            for (int i = 0; i < _files.length; i++) {
                System.out.println("kishan Adding: " + _files[i]);
                System.out.println("kishan Adding: " + _files[i].substring(_files[i].lastIndexOf("/") + 1));
                File file = new File(_files[i]);
                System.out.println("kishan Adding: " + file.getCanonicalPath());

                FileInputStream fi = new FileInputStream(file.getCanonicalPath());
//                origin = new BufferedInputStream(fi, 4096);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = fi.read(data)) > 0) {
                    out.write(data, 0, count);
                }

//                origin.close();
                out.closeEntry();
                fi.close();
            }

            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("kishan "+e.toString());
            return false;
        }
        return true;
    }

    public static boolean generateRegRfidDataXml(ArrayList<RFIDRegData> regDataArrayList, String regxmlFilename) {

        try{
            XmlSerializer xmlSerializer = Xml.newSerializer();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(regxmlFilename)));
            xmlSerializer.setOutput(bufferedWriter);

            xmlSerializer.startDocument("UTF-8", true);

            xmlSerializer.startTag("", "root");

            xmlSerializer.startTag("", "EnrData");

            for(RFIDRegData rfidRegData:regDataArrayList)
            {
                xmlSerializer.startTag("", "employee");

                xmlSerializer.startTag("", "empid");
                xmlSerializer.text(rfidRegData.getEmpid());
                xmlSerializer.endTag("", "empid");

                xmlSerializer.startTag("", "Date");
                xmlSerializer.text(rfidRegData.getDate());
                xmlSerializer.endTag("", "Date");

                xmlSerializer.startTag("", "Time");
                xmlSerializer.text(rfidRegData.getTime());
                xmlSerializer.endTag("", "Time");

                xmlSerializer.startTag("", "Longitude");
                xmlSerializer.text(rfidRegData.getLongitude());
                xmlSerializer.endTag("", "Longitude");

                xmlSerializer.startTag("", "Latitude");
                xmlSerializer.text(rfidRegData.getLatitude());
                xmlSerializer.endTag("", "Latitude");

                xmlSerializer.startTag("", "Altitude");
                xmlSerializer.text(rfidRegData.getAltitide());
                xmlSerializer.endTag("", "Altitude");

                xmlSerializer.startTag("", "RFID");
                xmlSerializer.text(rfidRegData.getRfid());
                xmlSerializer.endTag("", "RFID");

                xmlSerializer.endTag("", "employee");
            }


            xmlSerializer.endTag("", "EnrData");

            xmlSerializer.endTag("", "root");

            xmlSerializer.endDocument();
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        return true;
    }
    public static boolean copyFileToPendrive(File sourceFile, File storageDir, Activity avt) {

        if (sourceFile.exists()) {


            String offLineZipFilePath = storageDir.getAbsolutePath() + "/miFaunBackup";//File will be created in the miFaunBackup  folder
            File file = new File(offLineZipFilePath);

            boolean rootFolder = false;

            if (!file.exists()) {
                rootFolder = file.mkdir();//created the Directory will be in the rootfolder
            } else {
                rootFolder = true;
            }

            if (rootFolder) {

                File nFile = new File(offLineZipFilePath + "/Backup");
                boolean childFolder = false;

                if (!nFile.exists()) {
                    childFolder = nFile.mkdir();//Offline zip folder will be in the childfolder
                } else {
                    childFolder = true;
                }

                if (childFolder) {
                    //added to prevent overriding of backup files
                    SimpleDateFormat s = new SimpleDateFormat("ddMMyyyy_hhmmss");
                    String timeStamp = s.format(new Date());
                    //added to prevent overriding of backup files

                    String str;
                    if(sourceFile.getAbsolutePath().contains("ACSL"))
                        str = "_ACSL.zip";
                    else
                        str = "_REG.zip";
                    offLineZipFilePath = offLineZipFilePath + "/" + ManvishPrefConstants.SERIAL_NO.read()+"_"+timeStamp+str;;
                    final File destinationFile = new File(offLineZipFilePath);

                    try {
                        FileUtils.moveFile(sourceFile, destinationFile);
                        new ManvishAlertDialog(avt, "Back Up status",
                                "Back up Success").showAlertDialog();

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                AadharTimeAttendanceApplication.speakOut("Back up Success");
                            }
                        },300);

                        forceDeleteFile(sourceFile);

                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        new ManvishAlertDialog(avt, "Back Up status",
                                "Back up failure").showAlertDialog();
                        forceDeleteFile(sourceFile);

                        return false;
                    }
                } else {
                    new ManvishAlertDialog(avt, "Back Up status",
                            "Back up failure").showAlertDialog();
                    forceDeleteFile(sourceFile);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            AadharTimeAttendanceApplication.speakOut("Back up failure");
                        }
                    },300);
                    return false;
                }

            } else {
                new ManvishAlertDialog(avt, "Back Up status",
                        "Back up failure").showAlertDialog();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        AadharTimeAttendanceApplication.speakOut("Back up failure");
                    }
                },300);
                return false;

            }
        }
        return false;
    }

    private static void forceDeleteFile(File sourceFile) {
        try{
            FileUtils.forceDelete(sourceFile);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static boolean deleteImageData(String empid) {
        try{
            File sdCardImageFolder = new File("/mnt/sdcard2/TimeAttendanceSDBackUp/" + ManvishPrefConstants.SERIAL_NO.read() + "/IMAGES/");

            if (!sdCardImageFolder.exists())
                return false;

            File[] sdCardImageFiles = sdCardImageFolder.listFiles();

            for(File file : sdCardImageFiles)
            {
                if(file.getName().contains(empid + "_IMAGE" + ".xml"))
                    file.delete();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }




}
