package com.manvish.sampletest.Structures;

import java.io.Serializable;

/**
 * Created by tapan on 7/11/14.
 */

public class FingerDetails  implements Serializable{
    String unitid;
    String empid;
    String Aadhar;
    String TemplateType;
    String date;
    String time;
    String latitude;
    String longitude;
    String altitude;
    String pinCode;
    byte[] fingerTemplatee;
    byte[] fingerImage;

    public byte[] getFingerImage() {
        return fingerImage;
    }

    public void setFingerImage(byte[] fingerImage) {
        this.fingerImage = fingerImage;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getAadhar() {
        return Aadhar;
    }

    public void setAadhar(String aadhar) {
        Aadhar = aadhar;
    }

    public String getTemplateType() {
        return TemplateType;
    }

    public void setTemplateType(String templateType) {
        TemplateType = templateType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public byte[] getFingerTemplatee() {
        return fingerTemplatee;
    }

    public void setFingerTemplatee(byte[] fingerTemplatee) {
        this.fingerTemplatee = fingerTemplatee;
    }
}
