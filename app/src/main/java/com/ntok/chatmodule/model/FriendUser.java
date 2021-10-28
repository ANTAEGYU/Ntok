package com.ntok.chatmodule.model;

/**
 * Created by Sonam on 08-05-2018.
 */

public class FriendUser extends User {

    public String userID;
    public String lastMessage;
    public String room_id;

    @Override
    public String toString() {
        return "FriendUser{" +
                "userID='" + userID + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", room_id='" + room_id + '\'' +
                ", username='" + username + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", onlineStatus='" + onlineStatus + '\'' +
                ", about='" + about + '\'' +
                ", notificationTokens='" + notificationTokens + '\'' +
                ", userImage='" + userImage + '\'' +
                '}';
    }
}
