package com.ntok.chatmodule.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Sonam on 07-05-2018.
 */

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String phone_number;
    public String onlineStatus;
    public String about;
    public String notificationTokens;
    public String userImage;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String phone_number, String imageUploadInfo) {
        this.username = username;
        this.phone_number = phone_number;
        this.userImage = imageUploadInfo;
    }

}
// [END blog_user_class]