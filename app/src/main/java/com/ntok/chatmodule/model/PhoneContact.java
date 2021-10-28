package com.ntok.chatmodule.model;

/**
 * Created by Sonam on 09-05-2018.
 */

public class PhoneContact {

    private String name;
    private String phone_number;
    private String email;
    private boolean isOurContact;
    private String photoUri;
    private String birthday;

    public PhoneContact() {
    }

    public PhoneContact(String name, String phone_number, String photoUri) {
        this.name = name;
        this.phone_number = phone_number;
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsOurContact() {
        return isOurContact;
    }

    public void setIsOurContact(boolean isOurContact) {
        this.isOurContact = isOurContact;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "PhoneContact{" +
                "name='" + name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", isOurContact=" + isOurContact +
                ", photoUri='" + photoUri + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
