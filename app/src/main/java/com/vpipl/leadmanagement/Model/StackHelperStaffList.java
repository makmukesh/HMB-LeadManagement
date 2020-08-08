package com.vpipl.leadmanagement.Model;

public class StackHelperStaffList {
    String code, name;

    public StackHelperStaffList() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStateName() {
        return name;
    }

    public void setStateName(String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return name;
    }}