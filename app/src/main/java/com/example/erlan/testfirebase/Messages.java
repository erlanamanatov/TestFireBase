package com.example.erlan.testfirebase;

import android.os.Message;

/**
 * Created by erlan on 19.01.2017.
 */

public class Messages {
    private String name;
    private String text;
    private int User_Id;


    public Messages(){

    }
    public Messages(String name, String text, int user_Id) {
        this.name = name;
        this.text = text;
        User_Id = user_Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(int user_Id) {
        User_Id = user_Id;
    }
}
