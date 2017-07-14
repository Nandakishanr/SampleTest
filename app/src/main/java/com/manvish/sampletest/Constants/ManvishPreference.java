package com.manvish.sampletest.Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.manvish.sampletest.Application.AadharTimeAttendanceApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressLint("NewApi")
public class ManvishPreference<T> {

    private static Map<String, ManvishPreference<? extends Object>> sNameMap = new HashMap<String, ManvishPreference<? extends Object>>();



    private static interface SettingHandler<T> {
        public static final String PREF_FILE_NAME = DefinesClass.SHARED_PREFERENCE_FILE_NAME;

        public T read(ManvishPreference<T> s);

        public void write(ManvishPreference<T> s, T value);

        public T valueOf(String value);
    }

    public static final SettingHandler<Integer> integerHandler = new SettingHandler<Integer>() {
        @Override
        public synchronized Integer read(ManvishPreference<Integer> s) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            final int value = sharedPref.getInt(s.name, s.defaultValue);
            return Integer.valueOf(value);
        }

        @Override
        public synchronized void write(ManvishPreference<Integer> s,
                                       Integer value) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();
            editor.putInt(s.name, value.intValue());
            editor.apply();
        }

        @Override
        public Integer valueOf(String value) {
            return Integer.valueOf(value);
        }
    };

    /*public static final SettingHandler<ArrayList<String>> HashMapHandler = new SettingHandler<ArrayList<String>>() {

        @Override
        public synchronized ArrayList<String> read(ManvishPreference<ArrayList<String>> s) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication.getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);

            ArrayList<String> linkedHashMap = new ArrayList<String>();
            Set<String> set = sharedPref.getStringSet("1", null);
            ArrayList<String> sample=new ArrayList<String>(set);
            return sample;
        }

        @Override
        public void write(ManvishPreference<ArrayList<String>> s, ArrayList<String> value) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication.getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();

            Set<String> set = new HashSet<String>();
            set.addAll(value);
            editor.putStringSet("1",set);
            editor.apply();
        }

        @Override
        public ArrayList<String> valueOf(String value) {
            ArrayList<String> linkedHashMap = new ArrayList<>();
            linkedHashMap.add("1");
            return linkedHashMap;
        }


    };*/

    public static final SettingHandler<ArrayList<String>> HashMapHandler = new SettingHandler<ArrayList<String>>() {

        @Override
        public synchronized ArrayList<String> read(ManvishPreference<ArrayList<String>> s) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);

            ArrayList<String> linkedHashMap = new ArrayList<String>();
            linkedHashMap.clear();
            int size = sharedPref.getInt("Status_size", 0);

            for(int i=0;i<size;i++)
            {
                linkedHashMap.add(sharedPref.getString("Status_" + i, null));
            }

            String string = " ";
            System.out.println("string 1 "+linkedHashMap);
            return linkedHashMap;
        }

        @Override
        public void write(ManvishPreference<ArrayList<String>> s, ArrayList<String> value) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();
            editor.putInt("Status_size", value.size());

            for(int i=0;i<value.size();i++)
            {
                editor.remove("Status_" + i);
                editor.putString("Status_" + i, value.get(i));
                System.out.println("string "+i+" "+ value.get(i));
            }

            editor.apply();
        }

        @Override
        public ArrayList<String> valueOf(String value) {
            ArrayList<String> linkedHashMap = new ArrayList<String>();
            linkedHashMap.add(1,"1");
            return linkedHashMap;
        }


    };


    public static final SettingHandler<Boolean> booleanHandler = new SettingHandler<Boolean>() {
        @Override
        public synchronized Boolean read(ManvishPreference<Boolean> s) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            final boolean value = sharedPref.getBoolean(s.name, s.defaultValue);
            return Boolean.valueOf(value);
        }

        @Override
        public synchronized void write(ManvishPreference<Boolean> s,
                                       Boolean value) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();
            editor.putBoolean(s.name, value.booleanValue());
            editor.apply();
        }

        @Override
        public Boolean valueOf(String value) {
            return Boolean.valueOf(value);
        }
    };

    public static final SettingHandler<Long> longHandler = new SettingHandler<Long>() {
        @Override
        public synchronized Long read(ManvishPreference<Long> s) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            final long value = sharedPref.getLong(s.name, s.defaultValue);
            return Long.valueOf(value);
        }

        @Override
        public synchronized void write(ManvishPreference<Long> s,
                                       Long value) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();
            editor.putLong(s.name, value.longValue());
            editor.apply();
        }

        @Override
        public Long valueOf(String value) {
            return Long.valueOf(value);
        }
    };

    public static final SettingHandler<Float> floatHandler = new SettingHandler<Float>() {
        @Override
        public synchronized Float read(ManvishPreference<Float> s) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            final float value = sharedPref.getFloat(s.name, s.defaultValue);
            return Float.valueOf(value);
        }

        @Override
        public synchronized void write(ManvishPreference<Float> s,
                                       Float value) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();
            editor.putFloat(s.name, value.floatValue());
            editor.apply();
        }

        @Override
        public Float valueOf(String value) {
            return Float.valueOf(value);
        }
    };

    public static final SettingHandler<String> stringHandler = new SettingHandler<String>() {
        @Override
        public synchronized String read(ManvishPreference<String> s) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            return sharedPref.getString(s.name, s.defaultValue);
        }

        @Override
        public synchronized void write(ManvishPreference<String> s,
                                       String value) {
            SharedPreferences sharedPref = AadharTimeAttendanceApplication
                    .getAppContext().getSharedPreferences(PREF_FILE_NAME,
                            Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();
            editor.putString(s.name, value);
            editor.apply();
        }

        @Override
        public String valueOf(String value) {
            return value;
        }
    };

    public String name = null;
    public T defaultValue = null;
    public SettingHandler<T> handler = null;

    public ManvishPreference(String name, T defaultValue,
                             SettingHandler<T> handler) {
        if (name == null)
            return;
        this.name = name;
        this.defaultValue = defaultValue;
        this.handler = handler;
        sNameMap.put(name.toLowerCase(), this);
    }

    public T read() {
        return handler.read(this);
    }

    public void write(T value) {
        handler.write(this, value);
    }

    public T valueOf(String value) {
        return handler.valueOf(value);
    }

    public static ManvishPreference<? extends Object> forName(String name) {
        if (name == null)
            return null;
        return sNameMap.get(name.toLowerCase());
    }


}
