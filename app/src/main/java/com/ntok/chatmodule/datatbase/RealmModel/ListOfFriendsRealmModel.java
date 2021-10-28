package com.ntok.chatmodule.datatbase.RealmModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ListOfFriendsRealmModel extends RealmObject {

    private String username;
    private String phone_number;
    private String onlineStatus;
    private String about;
    private String notificationTokens;
    private String userImage;
    @PrimaryKey
    private String userID;
    private String lastMessage;
    private String room_id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getNotificationTokens() {
        return notificationTokens;
    }

    public void setNotificationTokens(String notificationTokens) {
        this.notificationTokens = notificationTokens;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }
}
