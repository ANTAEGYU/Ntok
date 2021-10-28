package com.ntok.chatmodule.model;

import android.content.Context;

import com.ntok.chatmodule.backend.sqlite_database.MessageDB;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.sharedPreference.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap; 

/**
 * Created by Sonam on 08-05-2018.
 */

public class FirebaseDataSingleton {

    private static FirebaseDataSingleton firebaseDataSingleton;
    Context activity;
    private  User user;
    private ArrayList<FriendUser> friendList = new ArrayList<>();
    private ArrayList<GroupModel> groupModels = new ArrayList<>();
    private ArrayList<User> allUsers = new ArrayList<>();
    private HashMap<String, ArrayList<MessageModel>> messageListHashMap = new HashMap<>();

    private FirebaseDataSingleton() {

    }

    public static FirebaseDataSingleton getInstance(Context activity) {
        if (firebaseDataSingleton == null) {
            firebaseDataSingleton = new FirebaseDataSingleton();
            firebaseDataSingleton.activity = activity;
        }
        firebaseDataSingleton.activity = activity;
        return firebaseDataSingleton;
    }
    public  void deleteInstance(){
        firebaseDataSingleton = null;
        user = null;
        groupModels = null;
        friendList = null;
        messageListHashMap = new HashMap<>();
    }
    public ArrayList<GroupModel> getGroupModels() {
        return groupModels;
    }

    public void setGroupModels(ArrayList<GroupModel> groupModels) {
        this.groupModels = groupModels;
    }

    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
    }

    public HashMap<String, ArrayList<MessageModel>> getMessageListHashMap() {
        return messageListHashMap;
    }

    public void setMessageListHashMap(HashMap<String, ArrayList<MessageModel>> messageListHashMap) {
        this.messageListHashMap = messageListHashMap;
    }
    public void removeMessage(String roomId, String messageID){
        for(int i=0; i<messageListHashMap.get(roomId).size();i++){
            if(messageListHashMap.get(roomId).get(i).getId().equalsIgnoreCase(messageID)){
                MessageDB.getInstance(activity).deleteMessage(messageID);
                messageListHashMap.get(roomId).remove(i);

            }
        }
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        SharedPreference.getInstance(activity).setUser(user);
    }


    public ArrayList<FriendUser> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<FriendUser> friendList) {
        this.friendList = friendList;
    }

    public void addFriend(FriendUser friend) {
        int i = 0;
        for (; i < friendList.size(); i++) {
            if (friendList.get(i).userID.equalsIgnoreCase(friend.userID)) {
                break;
            }
        }
        if (i == friendList.size()) {
            friendList.add(0, friend);
            RealamDatabase.getInstance().saveSingelFriendInDatabase(friend);
        }
    }


    public void upDateFriend(FriendUser friend, User user) {
        int i = 0;
        if (friend == null) {
            for (; i < friendList.size(); i++) {
                if (friendList.get(i).userID.equalsIgnoreCase(user.phone_number)) {
                    friendList.get(i).username = user.username;
                    friendList.get(i).phone_number = user.phone_number;
                    friendList.get(i).userImage = user.userImage;
                    friendList.get(i).onlineStatus = user.onlineStatus;
                    RealamDatabase.getInstance().saveSingelFriendInDatabase(friendList.get(i));
                }
            }
        } else {
            for (; i < friendList.size(); i++) {
                if (friendList.get(i).userID.equalsIgnoreCase(friend.userID)) {
                    friendList.get(i).room_id = friend.room_id;
                    friendList.get(i).lastMessage = friend.lastMessage;
                    friendList.get(i).username = friendList.get(i).username;
                    friendList.get(i).phone_number = friendList.get(i).phone_number;
                    friendList.get(i).userImage = friendList.get(i).userImage;
                    friendList.get(i).onlineStatus = friendList.get(i).onlineStatus;
                    RealamDatabase.getInstance().saveSingelFriendInDatabase(friendList.get(i));
                }
            }
        }

    }

    public void removeFriend(FriendUser friend, User user) {
        int i = 0;
        if (friend == null) {
            for (; i < friendList.size(); i++) {
                if (friendList.get(i).phone_number.equalsIgnoreCase(user.phone_number)) {
                    friendList.remove(i);
                }
            }
        } else {
            for (; i < friendList.size(); i++) {
                if (friendList.get(i).phone_number.equalsIgnoreCase(friend.phone_number)) {
                    friendList.remove(i);
                }
            }
        }

    }

    public FriendUser getFriendFromUserID(String userID) {
        int i = 0;
        if (userID != null) {
            for (; i < friendList.size(); i++) {
                if (friendList.get(i).userID.equalsIgnoreCase(userID)) {
                    return friendList.get(i);
                }
            }
        }
        return null;

    }

    public void addGroup(GroupModel groupModel) {
        int i = 0;
        for (; i < groupModels.size(); i++) {
            if (groupModels.get(i).getId().equalsIgnoreCase(groupModel.getId())) {
                break;
            }
        }
        if (i == groupModels.size()) {
            groupModels.add(0, groupModel);
            RealamDatabase.getInstance().saveTheListOfGroupDatainsideTheDatabase(groupModel);
        }
    }
    public GroupModel getGroupFromRoomId(String groupRoom) {
        int i = 0;
        for (; i < groupModels.size(); i++) {
            if (groupModels.get(i).getRoom_id().equalsIgnoreCase(groupRoom)) {
                return groupModels.get(i);
            }
        }
       return null;
    }

    public void upDateGroup(GroupModel groupModel) {
        int i = 0;
        if (groupModel != null) {
            for (; i < groupModels.size(); i++) {
                if (groupModels.get(i).getId().equalsIgnoreCase(groupModel.getId())) {
                    groupModels.get(i).setGroud_name(groupModel.getGroup_name());
                    groupModels.get(i).setRoom_id(groupModel.getRoom_id());
                    groupModels.get(i).setFriendUsers(groupModel.getFriendUsers());
                    groupModels.get(i).setAdmin(groupModel.getAdmin());
                    RealamDatabase.getInstance().saveTheListOfGroupDatainsideTheDatabase(groupModel);
                }
            }
        }

    }

    public void removeGroup(GroupModel groupModel) {

        for (int i = 0; i < groupModels.size(); i++) {
            if (groupModels.get(i).getId().equalsIgnoreCase(groupModel.getId())) {
                groupModels.remove(i);
                RealamDatabase.getInstance().deleteTheGroupTheRealmDatabse(groupModel.getId());
            }
        }

    }

    public void checkDeleteGroup(ArrayList<String> groupList) {
        boolean isContains = false;
        for (int j = 0; j < groupModels.size(); j++) {
            isContains = false;
            for (int i = 0; i < groupList.size(); i++) {
                if (groupModels.get(j).getId().equals(groupList.get(i))) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                removeGroup(groupModels.get(j));
                j--;
            }
        }
    }
}
