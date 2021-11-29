package com.example.gasitmobiledelvieryplatformapplication;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

public class UserAccounts {
    private String acc_fName;
    private String acc_lName;
    private String acc_address;
    private String acc_contactNum;
    private String acc_userName;
    private String acc_passWord;
    private String acc_confirmPass;
    private String acc_gender;
    private String acc_Type;
    private Integer acc_age;
    private Boolean acc_terms;

    public UserAccounts() {
    }

    public UserAccounts(String acc_fName, String acc_lName, String acc_address, String acc_contactNum, String acc_userName, String acc_passWord, String acc_confirmPass, String acc_gender, String acc_Type, Integer acc_age, Boolean acc_terms) {
        this.acc_fName = acc_fName;
        this.acc_lName = acc_lName;
        this.acc_address = acc_address;
        this.acc_contactNum = acc_contactNum;
        this.acc_userName = acc_userName;
        this.acc_passWord = acc_passWord;
        this.acc_confirmPass = acc_confirmPass;
        this.acc_gender = acc_gender;
        this.acc_Type = acc_Type;
        this.acc_age = acc_age;
        this.acc_terms = acc_terms;
    }

    public String getAcc_fName() {
        return acc_fName;
    }

    public void setAcc_fName(String acc_fName) {
        this.acc_fName = acc_fName;
    }

    public String getAcc_lName() {
        return acc_lName;
    }

    public void setAcc_lName(String acc_lName) {
        this.acc_lName = acc_lName;
    }

    public String getAcc_address() {
        return acc_address;
    }

    public void setAcc_address(String acc_address) {
        this.acc_address = acc_address;
    }

    public String getAcc_contactNum() {
        return acc_contactNum;
    }

    public void setAcc_contactNum(String acc_contactNum) {
        this.acc_contactNum = acc_contactNum;
    }

    public String getAcc_userName() {
        return acc_userName;
    }

    public void setAcc_userName(String acc_userName) {
        this.acc_userName = acc_userName;
    }

    public String getAcc_passWord() {
        return acc_passWord;
    }

    public void setAcc_passWord(String acc_passWord) {
        this.acc_passWord = acc_passWord;
    }

    public String getAcc_confirmPass() {
        return acc_confirmPass;
    }

    public void setAcc_confirmPass(String acc_confirmPass) {
        this.acc_confirmPass = acc_confirmPass;
    }

    public String getAcc_gender() {
        return acc_gender;
    }

    public void setAcc_gender(String acc_gender) {
        this.acc_gender = acc_gender;
    }

    public String getAcc_Type() {
        return acc_Type;
    }

    public void setAcc_Type(String acc_Type) {
        this.acc_Type = acc_Type;
    }

    public Integer getAcc_age() {
        return acc_age;
    }

    public void setAcc_age(Integer acc_age) {
        this.acc_age = acc_age;
    }

    public Boolean getAcc_terms() {
        return acc_terms;
    }

    public void setAcc_terms(Boolean acc_terms) {
        this.acc_terms = acc_terms;
    }
}
