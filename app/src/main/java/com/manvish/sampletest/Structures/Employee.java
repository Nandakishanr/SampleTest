package com.manvish.sampletest.Structures;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by kishan on 8/8/16.
 */
public class Employee implements Serializable {

    String empid;
    String aadharNo;
    String name;
    String deptid;
    String emp_catogory;
    String rfid;
    String admin_flag;
    byte[] photograph;
    String reg_finger_status;
    String ver_finger_status;
    String reg_rfid_status;
    String ver_rfid_status;
    String new_or_apprd_Flag;

    public String getAdmin_flag() {
        return admin_flag;
    }

    public void setAdmin_flag(String admin_flag) {
        this.admin_flag = admin_flag;
    }

    public String getEmp_catogory() {
        return emp_catogory;
    }

    public void setEmp_catogory(String emp_catogory) {
        this.emp_catogory = emp_catogory;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getNew_or_apprd_Flag() {
        return new_or_apprd_Flag;
    }

    public void setNew_or_apprd_Flag(String new_or_apprd_Flag) {
        this.new_or_apprd_Flag = new_or_apprd_Flag;
    }

    LinkedHashMap<String,String> apprdTemplateHashMap;

    public LinkedHashMap<String, String> getApprdTemplateHashMap() {
        return apprdTemplateHashMap;
    }

    public void setApprdTemplateHashMap(LinkedHashMap<String, String> apprdTemplateHashMap) {
        this.apprdTemplateHashMap = apprdTemplateHashMap;
    }

    public String getVer_rfid_status() {
        return ver_rfid_status;
    }

    public void setVer_rfid_status(String ver_rfid_status) {
        this.ver_rfid_status = ver_rfid_status;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public byte[] getPhotograph() {
        return photograph;
    }

    public void setPhotograph(byte[] photograph) {
        this.photograph = photograph;
    }

    public String getReg_finger_status() {
        return reg_finger_status;
    }

    public void setReg_finger_status(String reg_finger_status) {
        this.reg_finger_status = reg_finger_status;
    }

    public String getVer_finger_status() {
        return ver_finger_status;
    }

    public void setVer_finger_status(String ver_finger_status) {
        this.ver_finger_status = ver_finger_status;
    }

    public String getReg_rfid_status() {
        return reg_rfid_status;
    }

    public void setReg_rfid_status(String reg_rfid_status) {
        this.reg_rfid_status = reg_rfid_status;
    }
}
