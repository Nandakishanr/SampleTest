package com.manvish.sampletest.Structures;


public class packet_header {

    String cmd;
    String stat;
    String unitid;
    String dept_code;
    String compy_code;
    String main_compcode;
    String date;
    String time;
    int size;

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

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public String getDept_code() {
        return dept_code;
    }

    public void setDept_code(String dept_code) {
        this.dept_code = dept_code;
    }

    public String getCompy_code() {
        return compy_code;
    }

    public void setCompy_code(String compy_code) {
        this.compy_code = compy_code;
    }

    public String getMain_compcode() {
        return main_compcode;
    }

    public void setMain_compcode(String main_compcode) {
        this.main_compcode = main_compcode;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
