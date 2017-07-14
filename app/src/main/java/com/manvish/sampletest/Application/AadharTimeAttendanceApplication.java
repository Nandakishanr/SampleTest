package com.manvish.sampletest.Application;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Window;

import com.manvish.sampletest.Activities.SensorInitializationClass;
import com.manvish.sampletest.Constants.ManvishPrefConstants;
import com.manvish.sampletest.Structures.Employee;
import com.manvish.sampletest.Utils.CommanUtils;

import java.util.LinkedHashMap;
import java.util.Locale;


public class AadharTimeAttendanceApplication extends Application  {

    public static int mLowLevel = 0;
    static TextToSpeech tts;
    private static AadharTimeAttendanceApplication thisInstance;
//    public MorphoDevice morphoDevice = null;
//    public MorphoDatabase morphoDatabase = new MorphoDatabase();
    private static Context context;
    Handler handler;
    Runnable runable;


//    SensorInitializationClass initializationClass = null;
//
//    public SensorInitializationClass getInitializationClass() {
//        return initializationClass;
//    }
//
//    public void setInitializationClass(SensorInitializationClass initializationClass) {
//        this.initializationClass = initializationClass;
//    }
    Employee employee;
    String dbName;
    LinkedHashMap<String, Employee> employee_List;
    private Window window;

    public static synchronized AadharTimeAttendanceApplication getInstance() {
        return thisInstance;
    }

    public static Context getAppContext() {
        return AadharTimeAttendanceApplication.context;
    }

    public static void speakOut(String input) {
        // String text = txtText.getText().toString();
        if (ManvishPrefConstants.IS_AUDIO_ENABLED.read()) {
            System.out.println("kishan I am speaking your text==");
            tts.speak(input, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    SensorInitializationClass initializationClass = null;

    public SensorInitializationClass getInitializationClass() {
        return initializationClass;
    }

    public void setInitializationClass(SensorInitializationClass initializationClass) {
        this.initializationClass = initializationClass;
    }


    public static void playClick() {

        if (ManvishPrefConstants.IS_BEEP_ENABLED.read()) {
            AudioManager am = (AudioManager) getAppContext().getSystemService(AUDIO_SERVICE);
            am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 2.0f);
        }
    }

    public String getDbName() {

        if (dbName == null) {
            dbName = ManvishPrefConstants.DB_NAME.read();
        }
        return dbName;
    }

    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }

    public LinkedHashMap<String, Employee> getEmployee_List()
    {
        return employee_List;
    }

    public void setEmployee_List(LinkedHashMap<String, Employee> employee_List) {
        this.employee_List = employee_List;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

//    public MorphoDevice getMorphoDevice() {
//        return morphoDevice;
//    }
//
//    public void setMorphoDevice(final MorphoDevice morphoDevice) {
//        this.morphoDevice = morphoDevice;
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();


//        Fabric.with(this, new Crashlytics());

        AadharTimeAttendanceApplication.context = getApplicationContext();
        thisInstance = this;
        ManvishPrefConstants.SERIAL_NO.write(CommanUtils.getSerialNo());
        ManvishPrefConstants.IS_FIRSTTIME_LUNCH.write(true);
        enableUnknownSourceInstallation();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void enableUnknownSourceInstallation() {

        try {
            Settings.Global.putInt(getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Window getWindow() {
        return window;
    }
}
