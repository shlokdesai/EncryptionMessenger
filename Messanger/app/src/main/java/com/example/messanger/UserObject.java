package com.example.messanger;

import java.util.ArrayList;

public class UserObject {

    private String email;
    private String Uid;
    private int timesClicked;
    private boolean isSelected;
    private String Name;

    public UserObject(String email, String key/*, String name*/){
        this.email = email;
        this.Uid = key;
        this.timesClicked = 0;
        //this.Name = name;
    }

    public String getEmail() {
        String sendEmail = email.substring(email.indexOf("email=")+6, email.length()-1);
        return sendEmail;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public void setTimesClicked(int timesClicked1){
        timesClicked = timesClicked1;
    }
    public String getName() {
        return Name;
    }

    public int getTimesClicked(){return timesClicked;}
    public boolean getSelected() {
        return isSelected;
    }
    public String getUid() {
        return Uid;
    }
}
