package com.ntok.chatmodule.datatbase.RealmModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ListOfGroupRealmModel extends RealmObject {

    @PrimaryKey
    private String id;
    private String group_name;
    private String admin;
    private String room_id;
    private String lastMessage;
    private String groupImage;
    private RealmList<FriendUserDetailRealm>  friendUsers = new RealmList<FriendUserDetailRealm>();


    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public RealmList<FriendUserDetailRealm> getFriendUsers() {
        return friendUsers;
    }

    public void setFriendUsers(RealmList<FriendUserDetailRealm> friendUsers) {
        this.friendUsers = friendUsers;
    }
}
