package com.ntok.chatmodule.datatbase.RealmModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PhoneContactRealm extends RealmObject {

    private String name;
    @PrimaryKey
    private String phone_number;
    private String photoUri;

    public PhoneContactRealm() {
    }

    public PhoneContactRealm(String name, String phone_number, String photoUri) {
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


    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

}
