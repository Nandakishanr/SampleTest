package com.manvish.sampletest.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.manvish.sampletest.Activities.SensorInitializationClass;
import com.manvish.sampletest.Application.AadharTimeAttendanceApplication;
import com.manvish.sampletest.Application.ShellInterface;
import com.manvish.sampletest.Constants.DefinesClass;
import com.manvish.sampletest.Constants.ManvishPrefConstants;
import com.manvish.sampletest.R;
import com.morpho.morphosample.info.ProcessInfo;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.manvish.sampletest.Constants.DefinesClass.FILECREATED;
import static com.manvish.sampletest.Constants.DefinesClass.FILEORFOLDERALREADYEXISTS;
import static com.manvish.sampletest.Constants.DefinesClass.SERVICE_NAME;


/**
 * Created by kishan on 10/26/16.
 */

public class CommanUtils {

    private static ProgressDialog mProgressDialog;
    private static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    public static boolean sensor_recursion_flag = true;

    /**
     * Makes Activity as full screen.hides all navigation buttons and notification bars
     *
     * @param activity An Activity object
     */
    public static void MakeActivityFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);/*activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/

    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "-";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.d("kishan", "getCompleteAddressString: " +strReturnedAddress.toString());
            } else {
                Log.d("kishan", "getCompleteAddressString: No Address returned");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("kishan", "getCompleteAddressString: Cannot get Address "+e.toString());
        }
        return strAdd;
    }


    public static void setHideSoftKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * checks Wifi is enabled or not
     *
     * @param context An Activity context
     * @return returns state of wifi
     */
    public static boolean isWifiOn(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static int makeDirectory(String str) {


        System.out.println("mkdir1 " + str);
        File DirectoryName = new File(str);
        System.out.println("mkdir1 " + DirectoryName);
        if (!DirectoryName.exists()) {
            boolean b = DirectoryName.mkdir();
            System.out.println("mkdir " + b);
            if (b)
                return FILECREATED;
        }
        return FILEORFOLDERALREADYEXISTS;
    }

    public static void initializeSensor(Activity activity) {
        SensorInitializationClass initiateSensor;

        initiateSensor = new SensorInitializationClass(activity);
        AadharTimeAttendanceApplication.getInstance().setInitializationClass(initiateSensor);

        if (ProcessInfo.getInstance().getMorphoDevice() == null) {
            DefinesClass.morphoDevice = initiateSensor.initializeDevice();
            if(DefinesClass.morphoDevice != null)
                ProcessInfo.getInstance().setMorphoDevice(DefinesClass.morphoDevice);
            else
            {

                if(sensor_recursion_flag)
                {
                    SensorInitializationClass.disconnectotg();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SensorInitializationClass.connectotg();
                    sensor_recursion_flag = false;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    initializeSensor(activity);
                }
            }
        }

    }

    public static void showdwnldupldStatusDialog(String msg, int flag, Activity activity) {//0-completed,1-downloading,2-error occured

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View dwnlupldstatusdialogView = layoutInflater.inflate(R.layout.dwnlupldstatusdialog, null);

        AlertDialog.Builder dwnlupldstatusdialogAlertBuilder = new AlertDialog.Builder(activity);
        dwnlupldstatusdialogAlertBuilder.setView(dwnlupldstatusdialogView);

        LinearLayout mProgressDialog = (LinearLayout) dwnlupldstatusdialogView.findViewById(R.id.progressBarLayout);
        ProgressBar progressBar = (ProgressBar) dwnlupldstatusdialogView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mProgressDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView mProgressMsgTextView = (TextView) dwnlupldstatusdialogView.findViewById(R.id.progressBarMessage);
        mProgressMsgTextView.setText(msg);
        mProgressDialog.setVisibility(View.VISIBLE);

        int redColor = activity.getResources().getColor(R.color.red);//for error
        int greencolor = activity.getResources().getColor(R.color.green);//for succ
        int bluecolor = activity.getResources().getColor(R.color.blue);//for onprocess
        switch (flag) {
            case 0:
                mProgressMsgTextView.setTextColor(greencolor);
                progressBar.setVisibility(View.GONE);
                break;
            case 1:
                mProgressMsgTextView.setTextColor(bluecolor);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case 2:
                mProgressMsgTextView.setTextColor(redColor);
                progressBar.setVisibility(View.GONE);
                break;
        }


        final AlertDialog dwnlupldstatusdialog = dwnlupldstatusdialogAlertBuilder.create();
        WindowManager.LayoutParams wmlp = dwnlupldstatusdialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
//        wmlp.x = 75;   //x position
        wmlp.y = 75;   //y position
        dwnlupldstatusdialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.dialog_background));
        dwnlupldstatusdialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dwnlupldstatusdialog.dismiss();
            }
        }, 2000);
    }


    public static long byteToInt(byte[] bytes, int length) {
        int value = 0;

        for (int i = 0; i < bytes.length; i++)
            value = (value << 8) | bytes[i];
        return value;
    }

    public static void tapEffects(View view) {
        view.startAnimation(buttonClick);
    }

    public static void ShowCustomToast(Activity avt, String str, boolean flag) {
        LayoutInflater inflater = avt.getLayoutInflater();

        View customToastroot = inflater.inflate(R.layout.customtoast, null);

        ImageView CustomToastImageView = (ImageView) customToastroot.findViewById(R.id.CustomToastImageView);
        TextView CustomToastTextView = (TextView) customToastroot.findViewById(R.id.CustomToastTextView);
        TableRow CustomToasttablerow = (TableRow) customToastroot.findViewById(R.id.CustomToasttablerow);
        CustomToastTextView.setText(str);

        if (flag) {
//            CustomToasttablerow.setBackgroundColor(Color.parseColor("#0d980d"));
            CustomToastImageView.setImageResource(R.drawable.happsmilyy);
        } else {
//            CustomToasttablerow.setBackgroundColor(Color.parseColor("#e33324"));
            CustomToastImageView.setImageResource(R.drawable.sadsmilyy);
        }

        Toast customtoast = new Toast(avt);

        customtoast.setView(customToastroot);

//        customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER, 0, 0);
        customtoast.setDuration(Toast.LENGTH_SHORT);
        customtoast.show();
    }

    public static byte[] rotateByteArrayImage(byte[] originalImageByte, int angle) {

        // byte[] rotatedImage=null;

        Bitmap bmp = BitmapFactory.decodeByteArray(originalImageByte, 0, originalImageByte.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        bmp = Bitmap.createBitmap(bmp, 0, 0,
                bmp.getWidth(), bmp.getHeight(), matrix, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] flippedImageByteArray = stream.toByteArray();

        return flippedImageByteArray;
    }

    public static String getSerialNo() {
        String serialNo = "";

        serialNo = android.os.Build.SERIAL;

        return serialNo;
    }

    public static int checkSimCard(Context context) {

        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();

        Log.d("Gaju", "checkSimCard: " + simState);
//        }
        return simState;

    }

    public static String unzipAndGetFolderPath(String sourcePath) {

        String destinationPath = DefinesClass.APPLICATION_FOLDER + "Download/";
        try {
            ZipFile zipFile = new ZipFile(sourcePath);
            zipFile.extractAll(destinationPath);

            //After extracting all delete the zip file
            FileUtils.forceDelete(new File(sourcePath));
            return destinationPath;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("kishan", "unzipAndGetFolderPath: "+e.toString());
            return null;
        }
    }

    public static byte[] getByteFromFile(File AUPDHeaderFile) {

        try {
            return FileUtils.readFileToByteArray(AUPDHeaderFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("kishan", "getByteFromFile: " + e.toString());
            return null;
        }

    }

    public static Bitmap getRawToBmp(byte[] rawImage, int imageWidth, int imageHeight) {

        byte[] Src = null;
        byte[] Bits = null;
        Bitmap bm = null;
        Src = rawImage;
        Bits = new byte[Src.length * 4];
        int i;

        for (i = 0; i < Src.length; i++) {

            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = Src[i];
            // Invert the source bits
            Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
        }

        // Now put these nice RGBA pixels into a Bitmap object
        bm = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
        return bm;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return dateFormat.format(date);
    }

    public static String getCurrentUTCDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static String getCurrentTime() {

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        return timeFormat.format(date);
    }

    public static String getDateTime() {

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        return timeFormat.format(date);
    }

    public static String getCurrentUTCTime() {

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return timeFormat.format(date);
    }

    public static String getCurrentTimeForINOUT() {

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat homeDateFormat = new SimpleDateFormat("HH:mm");

        return homeDateFormat.format(date);
    }


    public static void showProgressBar(String msg, Activity activity) {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity, R.style.StyledDialog);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public static void dismissProgressBar() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public static void showProgressBar(String msg, Activity activity, ProgressDialog mProgressDialog) {

        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(activity, R.style.StyledDialog);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public static void dismissProgressBar(ProgressDialog mProgressDialog) {
        if (mProgressDialog != null) {//&& mProgressDialog.isShowing()) {
            System.out.println("kishan dismiss");
            mProgressDialog.dismiss();
        }
    }

    public static boolean getNetworkConnectionState(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo anInfo : info) {
            if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }

        }
        return false;
    }

    public static void disableScreenShot(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

    }

//    public static void onCreateMorphoDB(SensorInitializationClass initiateSensor) {
//        MorphoDatabase morphoDatabase = new MorphoDatabase();
//
////        ProcessInfo.getInstance().getMorphoDatabase().dbDelete(MorphoTypeDeletion.MORPHO_ERASE_BASE);
////        ProcessInfo.getInstance().setMorphoDatabase(null);
//
//        Integer index = new Integer(0);
//        MorphoField morphoFieldFirstName = new MorphoField();
//        morphoFieldFirstName.setName("First");
//        morphoFieldFirstName.setMaxSize(15);
//        morphoFieldFirstName.setFieldAttribute(FieldAttribute.MORPHO_PUBLIC_FIELD);
//        ProcessInfo.getInstance().getMorphoDatabase().putField(morphoFieldFirstName, index);
//        MorphoField morphoFieldLastName = new MorphoField();
//        morphoFieldLastName.setName("Last");
//        morphoFieldLastName.setMaxSize(15);
//        morphoFieldLastName.setFieldAttribute(FieldAttribute.MORPHO_PUBLIC_FIELD);
//        ProcessInfo.getInstance().getMorphoDatabase().putField(morphoFieldLastName, index);
//
//        int maxRecord = 500;
//        int maxNbFinger = 2;
//
//        boolean encryptDatabase = false;
//
//        int ret = ProcessInfo.getInstance().getMorphoDatabase().dbCreate(maxRecord, maxNbFinger, TemplateType.MORPHO_PK_COMP, 0, encryptDatabase);
//        Log.d("kishan", "onCreateMorphoDB: "+ret);
//        if (ret == ErrorCodes.MORPHO_OK) {
//            ProcessInfo.getInstance().setMorphoDatabase(ProcessInfo.getInstance().getMorphoDatabase());
//            ProcessInfo.getInstance().setBaseStatusOk(true);
//            initiateSensor.loadDatabaseItem(ProcessInfo.getInstance().getMorphoDatabase());
//        }
//    }


//    public static byte[] getByteFromFile(File AUPDHeaderFile) {
//
//        try {
//            return FileUtils.readFileToByteArray(AUPDHeaderFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

//    public static void identifiedCustomToast(Activity act, final Employee apprdEmployee) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 1;
//        options.inTempStorage = new byte[64 * 1024];
//
//        Bitmap bitmap = BitmapFactory.decodeByteArray(apprdEmployee.getPhotograph(), 0, apprdEmployee.getPhotograph().length, options);
//        Drawable d = new BitmapDrawable(act.getResources(), bitmap);
//
//        LayoutInflater inflater = act.getLayoutInflater();
//
//        View customToastroot = inflater.inflate(R.layout.identify_custom_toast, null);
//        ImageView identifyToastImageview = (ImageView) customToastroot.findViewById(R.id.identifyToastImageview);
//        TextView identifyToastNameTextview = (TextView) customToastroot.findViewById(R.id.identifyToastNameTextview);
//        TextView identifyToastEmpidTextview = (TextView) customToastroot.findViewById(R.id.identifyToastEmpidTextview);
//        TextView identifyToastMsgTextview = (TextView) customToastroot.findViewById(R.id.identifyToastMsgTextview);
//
//        identifyToastImageview.setImageBitmap(bitmap);
//        identifyToastEmpidTextview.setText("EMPID:" + apprdEmployee.getEmpid());
//        identifyToastMsgTextview.setText("Thank You For Accessing");
//        identifyToastNameTextview.setText("Name:" + apprdEmployee.getName());
//        Toast customtoast = new Toast(act);
//
//        customtoast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
//        customtoast.setDuration(Toast.LENGTH_SHORT);
//        customtoast.setView(customToastroot);
//        customtoast.show();
//
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                AadharTimeAttendanceApplication.speakOut("Thank You "+apprdEmployee.getName());//For Accessing");
//            }
//        },500);
//
//
//
//    }

//    public static void saveIdentifiedRecord(Employee apprdEmployee, IdentificationStruct identifiedUserDetails) {
//        String mainCompCode = ManvishPrefConstants.MAIN_COMP_CODE.read();
//        DefinesClass.dbHelperClass.saveIdentifiedEMPIDRecord(apprdEmployee,identifiedUserDetails,mainCompCode);
//
//        if(CommanUtils.isExternalSDCardPresent())
//        {
////            ArrayList<CapturedImageDetails> imageDetailsList = new ArrayList<>();
////            imageDetailsList.add(imageDetails);
//
////            CommanFileUtils.takeSdcardBackupofIMAGE(imageDetailsList,employee);
//            CommanFileUtils.takeSdcardBackupofASCL(apprdEmployee,identifiedUserDetails.getDate(),
//                    identifiedUserDetails.getTime(),identifiedUserDetails.getUtc_date(),identifiedUserDetails.getUtc_time());
//        }
//    }
//    public static String unzipAndGetFolderPath(String sourcePath) {
//
//        String destinationPath = DefinesClass.APPLICATION_FOLDER + "Download/";
//        try {
//            ZipFile zipFile = new ZipFile(sourcePath);
//            zipFile.extractAll(destinationPath);
//
//            //After extracting all delete the zip file
//            FileUtils.forceDelete(new File(sourcePath));
//            return destinationPath;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


    public static void addDwnlUpldStatus(ArrayList<String> stringArrayList) {
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < stringArrayList.size(); i++) {
            String statusText = stringArrayList.get(i);

            if (ManvishPrefConstants.STATUS_TEXTS.read() != null) {
                arrayList = ManvishPrefConstants.STATUS_TEXTS.read();

                if (arrayList.size() <= 4) {
                    arrayList.add(statusText);
                } else if (arrayList.size() > 4) {
                    arrayList.remove(0);

                    arrayList.add(statusText);
                }
            } else
                arrayList.add(statusText);

            //remove duplicates
            HashSet<String> hashSet = new HashSet<>();
            hashSet.addAll(arrayList);
            arrayList.clear();
            arrayList.addAll(hashSet);
            //remove duplicates

            ManvishPrefConstants.STATUS_TEXTS.write(arrayList);
        }

    }

//    public static void showdwnldupldStatusDialog(String msg, int flag, Activity activity) {//0-completed,1-downloading,2-error occured
//
//        LayoutInflater layoutInflater = LayoutInflater.from(activity);
//        View dwnlupldstatusdialogView = layoutInflater.inflate(R.layout.dwnlupldstatusdialog, null);
//
//        AlertDialog.Builder dwnlupldstatusdialogAlertBuilder = new AlertDialog.Builder(activity);
//        dwnlupldstatusdialogAlertBuilder.setView(dwnlupldstatusdialogView);
//
//        LinearLayout mProgressDialog = (LinearLayout) dwnlupldstatusdialogView.findViewById(R.id.progressBarLayout);
//        ProgressBar progressBar = (ProgressBar) dwnlupldstatusdialogView.findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);
//        mProgressDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        TextView mProgressMsgTextView = (TextView) dwnlupldstatusdialogView.findViewById(R.id.progressBarMessage);
//        mProgressMsgTextView.setText(msg);
//        mProgressDialog.setVisibility(View.VISIBLE);
//
//        int redColor = activity.getResources().getColor(R.color.red);//for error
//        int greencolor = activity.getResources().getColor(R.color.green);//for succ
//        int bluecolor = activity.getResources().getColor(R.color.blue);//for onprocess
//        switch (flag) {
//            case 0:
//                mProgressMsgTextView.setTextColor(greencolor);
//                progressBar.setVisibility(View.GONE);
//                break;
//            case 1:
//                mProgressMsgTextView.setTextColor(bluecolor);
//                progressBar.setVisibility(View.VISIBLE);
//                break;
//            case 2:
//                mProgressMsgTextView.setTextColor(redColor);
//                progressBar.setVisibility(View.GONE);
//                break;
//        }
//
//
//        final AlertDialog dwnlupldstatusdialog = dwnlupldstatusdialogAlertBuilder.create();
//        WindowManager.LayoutParams wmlp = dwnlupldstatusdialog.getWindow().getAttributes();
//
//        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
////        wmlp.x = 75;   //x position
//        wmlp.y = 75;   //y position
//        dwnlupldstatusdialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.dialog_background));
//        dwnlupldstatusdialog.show();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dwnlupldstatusdialog.dismiss();
//            }
//        }, 2000);
//    }


    public static boolean isExternalSDCardPresent() {

        boolean isSDCardPresent = false;

        try {
            File testfile = new File("/mnt/sdcard2");
            Log.e("kishan sdcard length", Integer.toString(testfile.listFiles().length) + "");
            isSDCardPresent = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSDCardPresent = false;
        }

        System.out.println("kishan isSDCardPresent " + isSDCardPresent);
        return isSDCardPresent;

    }


    public static void CreateURL() {
        if (ManvishPrefConstants.SERVER_IP.read() == null) {

            ManvishPrefConstants.SERVER_IP.write(DefinesClass.AUPD_BASE_URL);
            ManvishPrefConstants.SERVER_PORT.write(DefinesClass.AUPD_BASE_PORT);
            DefinesClass.BASE_URL = ManvishPrefConstants.AUPD_HTTP.read() + DefinesClass.AUPD_BASE_URL + ":" + DefinesClass.AUPD_BASE_PORT + "/" + SERVICE_NAME + "/";

        } else {
            DefinesClass.BASE_URL = ManvishPrefConstants.SERVER_IP.read();
            String port = ManvishPrefConstants.SERVER_PORT.read();
            DefinesClass.BASE_URL = ManvishPrefConstants.AUPD_HTTP.read() + DefinesClass.BASE_URL + ":" + port + "/" + SERVICE_NAME + "/";
        }

        Log.e("base", DefinesClass.BASE_URL);

        //All urls are hard coded for now
        DefinesClass.AUPD_URL = DefinesClass.BASE_URL + "AUPD";
        DefinesClass.AUPD_URL_TIME = DefinesClass.BASE_URL + "TIME";
        DefinesClass.MUPD_URL = DefinesClass.BASE_URL + "MUPD";
        DefinesClass.AUPD_URL_ACKNOWLEDGEMENT = DefinesClass.BASE_URL + "SUCC";
        DefinesClass.MUPD_URL_ACKNOWLEDGEMENT = DefinesClass.BASE_URL + "FSUCC";
        DefinesClass.RDATA_URL = DefinesClass.BASE_URL + "RDATA";
        DefinesClass.AAUTH_URL = DefinesClass.BASE_URL + "AAUTH";
        DefinesClass.ACSL_URL = DefinesClass.BASE_URL + "ACSL";
        DefinesClass.IMAGE_URL = DefinesClass.BASE_URL + "FIMAGE";
    }


//
//    public static void superToast(Activity activity, String msg, boolean flag) {
//        SuperActivityToast superActivityToast = new SuperActivityToast(activity);
//        superActivityToast.create(activity, new Style(), Style.TYPE_STANDARD);
//        superActivityToast.setGravity(Gravity.CENTER);
//
//        if (flag)
//            superActivityToast.setIconResource(Style.ICONPOSITION_TOP, R.drawable.happsmilyy);
//        else
//            superActivityToast.setIconResource(Style.ICONPOSITION_TOP, R.drawable.sadsmilyy);
//
//        superActivityToast.setTextSize(Style.TEXTSIZE_VERY_LARGE);
//        superActivityToast.setText(msg);
//        superActivityToast.setDuration(Style.DURATION_VERY_SHORT);
//        superActivityToast.setFrame(Style.FRAME_KITKAT);
//        superActivityToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN));
//        superActivityToast.setAnimations(Style.ANIMATIONS_FLY);
//        superActivityToast.show();
//    }


    public static String availMacAddress(Context context) {
        String address = "";
        WifiManager wifiManager1 = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            if (wifiManager1.isWifiEnabled()) {
                // WIFI ALREADY ENABLED. GRAB THE MAC ADDRESS HERE
                WifiInfo info = wifiManager1.getConnectionInfo();
                address = info.getMacAddress();
                ManvishPrefConstants.MAC_ADDRESS.write(address);
                //  Toast.makeText(context, "WifiNetwork is already avilable  grab the macaddress", Toast.LENGTH_SHORT).show();
            } else {
                // ENABLE THE WIFI FIRST
                wifiManager1.setWifiEnabled(true);

                // WIFI IS NOW ENABLED. GRAB THE MAC ADDRESS HERE
                WifiInfo info = wifiManager1.getConnectionInfo();
                address = info.getMacAddress();
                ManvishPrefConstants.MAC_ADDRESS.write(address);
                //return  address;
                // Toast.makeText(context, "WifiNetwork is now avilable  grab the macaddress", Toast.LENGTH_SHORT).show();
                wifiManager1.setWifiEnabled(false);
            }
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }


//    public static void initializeSensor() {
//        SensorInitializationClass initiateSensor;
//
//        initiateSensor = new SensorInitializationClass();
//        AadharTimeAttendanceApplication.getInstance().setInitializationClass(initiateSensor);
//
//        if (ProcessInfo.getInstance().getMorphoDevice() == null) {
//            DefinesClass.morphoDevice = initiateSensor.initializeDevice();
//            ProcessInfo.getInstance().setMorphoDevice(DefinesClass.morphoDevice);
//        }
//
//        if (ProcessInfo.getInstance().getMorphoDevice() != null && ProcessInfo.getInstance().getMorphoDatabase() == null) {
//            MorphoDatabase morphoDatabase = new MorphoDatabase();
//            int ret = ProcessInfo.getInstance().getMorphoDevice().getDatabase(0, morphoDatabase);
//            System.out.println("kishan retunr from getDatabase " + ret);
//            if (ret == ErrorCodes.MORPHO_OK) {
//                ProcessInfo.getInstance().setMorphoDatabase(morphoDatabase);
//
//            } else if (ret == ErrorCodes.MORPHOERR_BASE_NOT_FOUND)
//                CommanUtils.onCreateMorphoDB(initiateSensor);
//        }
//    }

    public static boolean getGPSState(Context act) {

        LocationManager locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public static void turnGPSOn() {

        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(new String[]{"su",
                    "pm grant com.your_app_packagename android.permission.WRITE_SECURE_SETTINGS",
                    "settings put secure location_providers_allowed +gps,network,wifi"});
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Log.d("kishan", "turnGPSOn: " + e.toString());
        } finally {
            ManvishPrefConstants.LATITUDE.write(null);
            ManvishPrefConstants.LONGITUDE.write(null);
        }
    }


    public static void turnGPSOff() {

        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(new String[]{"su",
                    "pm grant com.your_app_packagename android.permission.WRITE_SECURE_SETTINGS",
                    "settings put secure location_providers_allowed ' '"});
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Log.d("kishan", "turnGPSOn: " + e.toString());
        }
    }

    public static void getGPSLatLong(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        find_Location(context);

    }


    public static void find_Location(Context context) {
        Log.d("kishan Find Location", "in find_location");
        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) context.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        Log.d("kishan", "find_Location: providers "+providers);
        for (String provider : providers) {
            Log.d("kishan", "find_Location: provider got is "+provider);

            if(DefinesClass.gpsFlag)
                return;

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationListener myLL = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Log.d("kishan", "onLocationChanged: ");

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };


            locationManager.requestLocationUpdates(provider, 5000, 0,myLL);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0,myLL);

            Location location = locationManager.getLastKnownLocation(provider);
            Log.d("kishan", "find_Location: b4 checking");
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double Altitude = location.getAltitude();
                Log.d("kishan", "find_Location: "+latitude+" "+longitude+" "+Altitude);
                if(latitude > 0.0 && longitude > 0.0)
                {
                    String lat = new DecimalFormat("##.####").format(latitude);
                    String lon = new DecimalFormat("##.####").format(longitude);
                    String alti = new DecimalFormat("##.####").format(Altitude);
                    ManvishPrefConstants.LATITUDE.write(lat);
                    ManvishPrefConstants.LONGITUDE.write(lon);
                    ManvishPrefConstants.ALTITUDE.write(alti);
                    ManvishPrefConstants.LOCADDRESS.write(getCompleteAddressString(context,latitude,longitude));
                    Log.d("kishan", "find_Location: address got is "+ManvishPrefConstants.LOCADDRESS.read());

//                    Toast.makeText(context, "find_Location: "+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
                    if (locationManager != null) {

                        locationManager.removeUpdates(myLL);
                    }
                    break;
                }
            }
            try {
                Thread.sleep(1000*30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getPendriveStorageDirectory(Context context, boolean flag) {//if flag is true display toast msg else dont
        File storageDir = null;
        File primaryDir = new File(DefinesClass.USB_BACKUP_PATH_PRIMARY);
        boolean isPrimary = false;
        try {
            try {

                android.util.Log.e("size", primaryDir.listFiles().length + "");
                isPrimary = true;
                storageDir = primaryDir;
            } catch (Exception e) {

                isPrimary = false;
                storageDir = null;
            }

            if (!isPrimary) {

                File secondryDir = new File(DefinesClass.USB_BACKUP_PATH_SECONDRY);
                try {
                    android.util.Log.e("size", secondryDir.listFiles().length + "");
                    storageDir = secondryDir;
                } catch (Exception e) {
                    storageDir = null;
                    if (flag)
//                        CommanUtils.ShowCustomToast((Activity) context,"Pendrive not detected",false);
                        Toast.makeText(context, "Pendrive not detected", Toast.LENGTH_SHORT).show();
                }
            }


        } catch (Exception e) {
            android.util.Log.e("error", e.toString());
            storageDir = null;
            if (flag)
//                CommanUtils.ShowCustomToast((Activity) context,"Pendrive not detected",false);
                Toast.makeText(context, "Pendrive not detected", Toast.LENGTH_SHORT).show();
        }
        System.out.println("kishan " + storageDir);
        return storageDir;
    }

    public static void doYouWantToContinueDialog(final Activity activity, String str) {
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.do_you_want_to_msg_dialog);
        dialog.setCancelable(true);

        Button btnOk = (Button) dialog.findViewById(R.id.wantToContinueDialogEnterButton);
        Button btnCancel = (Button) dialog.findViewById(R.id.wantToContinueDialogCancelButton);
        TextView textview = (TextView) dialog.findViewById(R.id.wantToContinueDialogTextView);
        textview.setText(str);//"Downloaded File,Do You Want To Update Firmware?");
        btnOk.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         dialog.dismiss();
                                         CommanUtils.updateFirmWare(activity);
                                     }
                                 }

        );
        btnCancel.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             dialog.dismiss();
                                         }
                                     }

        );

        dialog.show();
    }

    public static void updateFirmWare(final Activity activity) {

        try {

            File storageDir = null;
            //File storageDir = new File("/storage/usbotg/");
            File primaryDir = new File(DefinesClass.USB_BACKUP_PATH_PRIMARY);
            boolean isPrimary = false;
            try {

                Log.e("size", primaryDir.listFiles().length + "");
                isPrimary = true;
                storageDir = primaryDir;

            } catch (Exception e) {

                isPrimary = false;
            }

            if (!isPrimary) {

                File secondryDir = new File(DefinesClass.USB_BACKUP_PATH_SECONDRY);
                try {
                    Log.e("size", secondryDir.listFiles().length + "");

                    storageDir = secondryDir;
                } catch (Exception e) {
                    Toast.makeText(activity, "Pendrive not detected", Toast.LENGTH_SHORT).show();

                }
            } else {
                updateFirmWareFromPath("/storage/usbotg/miFaun/miFaun.man", activity);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void updateFirmWareFromPath(final String path, final Activity activity) {
        AsyncTask<Void, Void, Integer> updateTask = new AsyncTask<Void, Void, Integer>() {
            ProgressDialog dialog = new ProgressDialog(activity);

            @Override
            protected void onPreExecute() {
                // what to do before background task

                dialog.setMessage("Updating firmware please wait..");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                Integer returnValue = 0;
                // do your background operation here
                if (ShellInterface.isSuAvailable()) {
//
                    if (new File(path).exists()) {
//                                boolean isencrypted = false;
//                                try{
                                /*isencrypted = CryptoUtils.decrypt("mifaunhsivnam005",new File("/storage/usbotg/miFaun/miFaun.man"),
                                        new File("/storage/sdcard0/miFaun.man"));*/

//                                if(isencrypted)
                        ShellInterface.runCommand("sh " + path);
                                /*else
                                {
                                    returnValue = 1;
                                    return returnValue;
                                }*/
                    } else {
                        returnValue = 2;
                        return returnValue;
                    }
                }

                return returnValue;
            }

            @Override
            protected void onPostExecute(Integer result) {
                // what to do when background task is completed
                dialog.dismiss();

                if (result == 1)
                    CommanUtils.ShowCustomToast(activity, "Firmware file not proper", false);
                else if (result == 2)
                    CommanUtils.ShowCustomToast(activity, "Firmware file does not exists", false);
            }

            ;

            @Override
            protected void onCancelled() {
                dialog.dismiss();
                super.onCancelled();
            }
        };
        updateTask.execute((Void[]) null);

    }


//


}
