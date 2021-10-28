package com.ntok.chatmodule.model;

import java.util.ArrayList;

/**
 * Created by Sonam on 18-05-2018.
 */

public class GroupModel {

    ArrayList<FriendUserDetail> friendUsers = new ArrayList<>();
    private String id;
    private String group_name;
    private String admin;
    private String room_id;
    private String lastMessage;
    private String groupImage;


    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public ArrayList<FriendUserDetail> getFriendUsers() {
        return friendUsers;
    }

    public void setFriendUsers(ArrayList<FriendUserDetail> friendUsers) {
        this.friendUsers = friendUsers;
    }

    public GroupModel(String id) {
        this.id = id;
    }

    public GroupModel() {
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setGroud_name(String group_name) {
        this.group_name = group_name;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return "GroupModel{" +
                "friendUsers=" + friendUsers +
                ", id='" + id + '\'' +
                ", group_name='" + group_name + '\'' +
                ", admin='" + admin + '\'' +
                ", room_id='" + room_id + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", groupImage='" + groupImage + '\'' +
                '}';
    }
}
