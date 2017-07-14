package com.manvish.sampletest.Constants;

import java.util.ArrayList;

public class ManvishPrefConstants {

    // sample to follow to declare shared preference constants ==



    public static final  ManvishPreference<String> LATITUDE=new ManvishPreference<String>
            ("latitude",null,ManvishPreference.stringHandler);

    public static final  ManvishPreference<String> LONGITUDE=new ManvishPreference<String>
            ("longitude",null,ManvishPreference.stringHandler);

    public static final  ManvishPreference<String> ALTITUDE=new ManvishPreference<String>
            ("ALTITUDE",null,ManvishPreference.stringHandler);

    public static final  ManvishPreference<String> LOCADDRESS=new ManvishPreference<String>
            ("LOCADDRESS","-",ManvishPreference.stringHandler);

    public static final ManvishPreference<String> MAC_ADDRESS = new ManvishPreference<String>(
            "macaddress", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SERVER_IP = new ManvishPreference<String>(
            "serverip", "192.168.1.95", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SERVER_PORT = new ManvishPreference<String>(
            "serverport", "8081", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> REGVERFLAG = new ManvishPreference<String>(
            "regverflag", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> IN_OUT = new ManvishPreference<String>(
            "in_out", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> MAIN_COMP_CODE = new ManvishPreference<String>(
            "main_comp_code", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> IN_STTIME = new ManvishPreference<String>(
            "in_sttime", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> IN_ENDTIME = new ManvishPreference<String>(
            "in_endtime", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> OUT_STTIME = new ManvishPreference<String>(
            "out_sttime", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> OUT_ENDTIME = new ManvishPreference<String>(
            "out_endtime", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> COMPY_LOGO = new ManvishPreference<String>(
            "comp_logo", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> COMPY_NAME = new ManvishPreference<String>(
            "compy_name", "Manvish eTech Pvt Ltd", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> COMPY_ADDRS = new ManvishPreference<String>(
            "compy_addrs", "3rd block Jayanagar,Bangalore", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> THREAD_SCHEDULING = new ManvishPreference<String>(
            "threadscheduling", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SERIAL_NO = new ManvishPreference<String>(
            "serialno", "12345", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> AUPD_HTTP = new ManvishPreference<String>(
            "aupd_http", "http://", ManvishPreference.stringHandler);

    public static final ManvishPreference<Boolean> LOG_IN_FLAG = new ManvishPreference<Boolean>(
            "loginflag", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<Boolean> IS_GPRS_ENABLE = new ManvishPreference<Boolean>(
            "isgprsenabled", false, ManvishPreference.booleanHandler);


    public static final ManvishPreference<String> FINGERCONFIG = new ManvishPreference<String>(
            "fingerConfig", "", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> EXTRAIMAGES = new ManvishPreference<String>(
            "extraimages", "", ManvishPreference.stringHandler);

    // determine ,it's PRETEST ,POSTTEST,PRECOUNCIL,POSTCOUNCIL
    public static final ManvishPreference<String> SELECTED_EVENT_STAGE = new ManvishPreference<String>(
            "event_stage", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_STATS = new ManvishPreference<String>(
            "stats", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_REG_OR_VER = new ManvishPreference<String>(
            "regorver", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_EVENT_CODE = new ManvishPreference<String>(
            "event_code", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_DATE = new ManvishPreference<String>(
            "selecteddate", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_VENUE = new ManvishPreference<String>(
            "selectedvenue", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_BATCH = new ManvishPreference<String>(
            "selectedbatch", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_CLASS = new ManvishPreference<String>(
            "selectedclass", null, ManvishPreference.stringHandler);


    public static final ManvishPreference<Boolean> IS_TEST = new ManvishPreference<Boolean>(
            "is_test", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<String> COUNSILING_CODE = new ManvishPreference<String>(
            "counsiling_code", "", ManvishPreference.stringHandler);


    public static final ManvishPreference<String> FINGERPRINTBASE64STRING = new ManvishPreference<String>(
            "fingerprintbase64string", "", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_SESSION_CODE = new ManvishPreference<String>(
            "selectedsessioncode", "", ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SELECTED_SESSION_DATE = new ManvishPreference<String>(
            "selectedsessiondate", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> TOTAL_STUDENT = new ManvishPreference<String>(
            "totalStudent", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> TOTAL_STUDENT_REG_VER_SUCC = new ManvishPreference<String>(
            "totalStudentregversucc", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> TOTAL_STUDENT_REG_VER_FAIL = new ManvishPreference<String>(
            "totalStudentregverfail", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> DB_NAME = new ManvishPreference<String>(
            "db_name", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> PROJECT_PASSWORD = new ManvishPreference<String>(
            "project_password", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<Boolean> IDEN_ENABLE = new ManvishPreference<Boolean>(
            "iden_enable", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<Boolean> IS_LOCKED = new ManvishPreference<Boolean>(
            "is_locked", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<Boolean> IS_FIRSTTIME_LUNCH= new ManvishPreference<Boolean>(
            "first_time_lunch", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<String> IS_CAMERA_WORKING = new ManvishPreference<String>(
            "is_camera_working", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> IS_SENSOR_WORKING = new ManvishPreference<String>(
            "is_sensor_working", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> IS_DATE_WORKING = new ManvishPreference<String>(
            "is_date_working", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> IS_TIME_WORKING = new ManvishPreference<String>(
            "is_time_working", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> BATTERY_PERCENTAGE = new ManvishPreference<String>(
            "battery_percentage", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<Boolean> IS_IMAGECAPTURE_ENABLED= new ManvishPreference<Boolean>(
            "is_imagecapture_enabled", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<Boolean> IS_AUDIO_ENABLED= new ManvishPreference<Boolean>(
            "is_audio_enabled", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<Boolean> IS_BEEP_ENABLED= new ManvishPreference<Boolean>(
            "is_beep_enabled", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<Boolean> IS_GPSBUTTON_ENABLED= new ManvishPreference<Boolean>(
            "is_gpsbutton_enabled", false, ManvishPreference.booleanHandler);

    public static final ManvishPreference<ArrayList<String>> STATUS_TEXTS = new ManvishPreference<ArrayList<String>>(
            "status_texts", null, ManvishPreference.HashMapHandler);

    public static final ManvishPreference<String> TCONTROL_HASHMAPSTRING = new ManvishPreference<String>(
            "terminal_control", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> TTABLE_HASHMAPSTRING = new ManvishPreference<String>(
            "terminal_table", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> TMAPPS_HASHMAPSTRING = new ManvishPreference<String>(
            "m_apps", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> TCDETAILS_HASHMAPSTRING = new ManvishPreference<String>(
            "c_details", null, ManvishPreference.stringHandler);

    public static final ManvishPreference<String> SHOWTOUCH = new ManvishPreference<String>(
            "regverfrid_flag", "off", ManvishPreference.stringHandler);



}
