package com.ntok.chatmodule.model;

/**
 * Created by balwinder on 30/3/18.
 */

public class ContactEmail {
    public String address;
    public String type;

    public ContactEmail(String address, String type) {
        this.address = address;
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
