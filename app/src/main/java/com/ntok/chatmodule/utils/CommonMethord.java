package com.ntok.chatmodule.utils;

/**
 * Created by Sonam on 08-05-2018.
 */

public class CommonMethord {

    public static String generateRoomID(String user1, String user2) {
        user1 = user1.replace("+", "");
        user2 = user2.replace("+", "");
        if (Long.parseLong(user1) > Long.parseLong(user2)) {
            return "room_" + user1 + "_" + user2;
        } else {
            return "room_" + user2 + "_" + user1;
        }
    }

}
