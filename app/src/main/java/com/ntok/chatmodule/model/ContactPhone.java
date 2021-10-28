package com.ntok.chatmodule.model;

/**
 * Created by balwinder on 30/3/18.
 */

public class ContactPhone {
    public String number;
    public String type;

    public ContactPhone(String number, String type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}