package com.ntok.chatmodule.datatbase.RealmModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaveContactModel extends RealmObject{

    private String name;
    @PrimaryKey
    private String phone_number;
    private String email;
    private boolean isOurContact;
    private String photoUri;
    private String birthday;

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

    public boolean isOurContact() {
        return isOurContact;
    }

    public void setOurContact(boolean ourContact) {
        isOurContact = ourContact;
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
}
