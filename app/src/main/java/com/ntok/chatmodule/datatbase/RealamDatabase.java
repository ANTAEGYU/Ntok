package com.ntok.chatmodule.datatbase;

import com.ntok.chatmodule.datatbase.RealmModel.FriendUserDetailRealm;
import com.ntok.chatmodule.datatbase.RealmModel.ListOfFriendsRealmModel;
import com.ntok.chatmodule.datatbase.RealmModel.ListOfGroupRealmModel;
import com.ntok.chatmodule.datatbase.RealmModel.SaveContactModel;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.FriendUserDetail;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.model.PhoneContact;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

    public class RealamDatabase {

    private static RealamDatabase realamDatabase;


    /**
     * Get the instance of the Realm Database to access
     * method from anywhere
     *
     * @return
     */

        public static RealamDatabase getInstance() {
            if (realamDatabase == null) {
                realamDatabase = new RealamDatabase();
            }
            return realamDatabase;
        }


    /**
     * save The contacts to realm database
     *
     * @param phoneContactNoDuplicate
     */

    public void saveContactsToRealmDatabase(ArrayList<PhoneContact> phoneContactNoDuplicate) {
        Realm realm = Realm.getDefaultInstance();
        List<SaveContactModel> realmData = new ArrayList<SaveContactModel>();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();

            realm.beginTransaction();
            for (int i = 0; i < phoneContactNoDuplicate.size(); i++) {
                SaveContactModel saveContactModel = new SaveContactModel();
                saveContactModel.setEmail(phoneContactNoDuplicate.get(i).getEmail());
                saveContactModel.setBirthday(phoneContactNoDuplicate.get(i).getBirthday());
                saveContactModel.setName(phoneContactNoDuplicate.get(i).getName());
                saveContactModel.setOurContact(phoneContactNoDuplicate.get(i).getIsOurContact());
                saveContactModel.setPhotoUri(phoneContactNoDuplicate.get(i).getPhotoUri());
                saveContactModel.setPhone_number(phoneContactNoDuplicate.get(i).getPhone_number());
                realmData.add(saveContactModel);
            }

            realm.copyToRealmOrUpdate(realmData);
            // Save the data here

            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
    }


    /**
     * Get all the Contacts from the Realm database
     */

    public ArrayList<PhoneContact> getAllContact() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<PhoneContact> phoneContactsList = new ArrayList<>();
        RealmResults<SaveContactModel> results = realm.where(SaveContactModel.class).sort("name", Sort.ASCENDING).findAll();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();

            realm.beginTransaction();

            for (int i = 0; i < results.size(); i++) {
                PhoneContact phoneContact = new PhoneContact();
                phoneContact.setBirthday(results.get(i).getBirthday());
                phoneContact.setEmail(results.get(i).getEmail());
                phoneContact.setPhone_number(results.get(i).getPhone_number());
                phoneContact.setPhotoUri(results.get(i).getPhotoUri());
                phoneContact.setIsOurContact(results.get(i).isOurContact());
                phoneContact.setName(results.get(i).getName());
                phoneContactsList.add(phoneContact);
            }

            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }

        return phoneContactsList;
    }





    /**
     * This method used to store the number
     * of friends inside the realm database
     *
     * @param friendList
     */

    public void saveTheListOfFriendsInsideDatabase(ArrayList<FriendUser> friendList) {
        Realm realm = Realm.getDefaultInstance();
        List<ListOfFriendsRealmModel> realmData = new ArrayList<ListOfFriendsRealmModel>();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();

            realm.beginTransaction();
            for (int i = 0; i < friendList.size(); i++) {
                ListOfFriendsRealmModel listOfFriendsRealmModel = new ListOfFriendsRealmModel();
                listOfFriendsRealmModel.setAbout(friendList.get(i).about);
                listOfFriendsRealmModel.setLastMessage(friendList.get(i).lastMessage);
                listOfFriendsRealmModel.setNotificationTokens(friendList.get(i).notificationTokens);
                listOfFriendsRealmModel.setOnlineStatus(friendList.get(i).onlineStatus);
                listOfFriendsRealmModel.setPhone_number(friendList.get(i).phone_number);
                listOfFriendsRealmModel.setUserID(friendList.get(i).userID);
                listOfFriendsRealmModel.setRoom_id(friendList.get(i).room_id);
                listOfFriendsRealmModel.setUsername(friendList.get(i).username);
                listOfFriendsRealmModel.setUserImage(friendList.get(i).userImage);
                realmData.add(listOfFriendsRealmModel);
            }

            realm.copyToRealmOrUpdate(realmData);
            // Save the data here

            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
    }


    /**
     * This method is used to get the
     * number of friend inside the database
     *
     * @return
     */

    public ArrayList<FriendUser> getAllFriends() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<FriendUser> phoneContactsList = new ArrayList<FriendUser>();
        RealmResults<ListOfFriendsRealmModel> results = realm.where(ListOfFriendsRealmModel.class).sort("lastMessage", Sort.ASCENDING).findAll();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();

            realm.beginTransaction();

            for (int i = 0; i < results.size(); i++) {
                FriendUser friendUser = new FriendUser();
                friendUser.about = results.get(i).getAbout();
                friendUser.lastMessage = results.get(i).getLastMessage();
                friendUser.notificationTokens = results.get(i).getNotificationTokens();
                friendUser.onlineStatus = results.get(i).getOnlineStatus();
                friendUser.phone_number = results.get(i).getPhone_number();
                friendUser.room_id = results.get(i).getRoom_id();
                friendUser.userImage = results.get(i).getUserImage();
                friendUser.username = results.get(i).getUsername();
                friendUser.userID = results.get(i).getUserID();
                phoneContactsList.add(friendUser);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }

        return phoneContactsList;
    }


    /**
    * Get the last message From realm Friend database
    * */
    public String getLastMessageFromFriendDatabase(String phonenumber) {
        String lastmessage = null;
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();

            ListOfFriendsRealmModel listOfFriendsRealmModel = realm.where(ListOfFriendsRealmModel.class).equalTo("phone_number",phonenumber).findFirst();
            lastmessage = listOfFriendsRealmModel.getLastMessage();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
        return lastmessage;
    }

    /**
    * Save a single friend in realm database
    * */

    public void saveSingelFriendInDatabase(FriendUser friend) {

        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            ListOfFriendsRealmModel listOfFriendsRealmModel = new ListOfFriendsRealmModel();
            listOfFriendsRealmModel.setAbout(friend.about);
            listOfFriendsRealmModel.setLastMessage(friend.lastMessage);
            listOfFriendsRealmModel.setNotificationTokens(friend.notificationTokens);
            listOfFriendsRealmModel.setOnlineStatus(friend.onlineStatus);
            listOfFriendsRealmModel.setPhone_number(friend.phone_number);
            listOfFriendsRealmModel.setUserID(friend.userID);
            listOfFriendsRealmModel.setRoom_id(friend.room_id);
            listOfFriendsRealmModel.setUsername(friend.username);
            listOfFriendsRealmModel.setUserImage(friend.userImage);

            realm.copyToRealmOrUpdate(listOfFriendsRealmModel);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }

    }

    /**
    * Svae the group of data inside the realm database
    * */

    public void saveTheListOfGroupDatainsideTheDatabase(GroupModel groupModel) {
        Realm realm = Realm.getDefaultInstance();
        RealmList<FriendUserDetailRealm> friendUsers = new RealmList<FriendUserDetailRealm>();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            ListOfGroupRealmModel listOfGroupRealmModel = new ListOfGroupRealmModel();
            listOfGroupRealmModel.setRoom_id(groupModel.getRoom_id());
            listOfGroupRealmModel.setAdmin(groupModel.getAdmin());
            for(FriendUserDetail friend : groupModel.getFriendUsers() ) {
                FriendUserDetailRealm friendUserDetailRealm = new FriendUserDetailRealm();
                friendUserDetailRealm.setFriendImage(friend.getFriendImage());
                friendUserDetailRealm.setFriendName(friend.getFriendName());
                friendUserDetailRealm.setFriendPhoneNumber(friend.getFriendPhoneNumber());
                friendUsers.add(friendUserDetailRealm);
            }
            listOfGroupRealmModel.setFriendUsers(friendUsers);
            listOfGroupRealmModel.setGroup_name(groupModel.getGroup_name());
            listOfGroupRealmModel.setId(groupModel.getId());
            listOfGroupRealmModel.setLastMessage(groupModel.getLastMessage());
            listOfGroupRealmModel.setGroupImage(groupModel.getGroupImage());

            realm.copyToRealmOrUpdate(listOfGroupRealmModel);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
    }


    public void clearOldData(){
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            RealmResults<ListOfGroupRealmModel> rows = realm.where(ListOfGroupRealmModel.class).findAll();
            rows.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
    }


    /**
    * Delete The Group
    * */
    public void deleteTheGroupTheRealmDatabse(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            RealmResults<ListOfGroupRealmModel> rows = realm.where(ListOfGroupRealmModel.class).equalTo("id",groupId).findAll();
            rows.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
    }


    /**
     * This method is used to get the
     * number of Group From the database
     *
     * @return
     */

    public ArrayList<GroupModel> getAllListOfGroupFormDB() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<GroupModel> groupList = new ArrayList<GroupModel>();
        RealmResults<ListOfGroupRealmModel> results = realm.where(ListOfGroupRealmModel.class).sort("lastMessage", Sort.ASCENDING).findAll();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();

            realm.beginTransaction();

            for (int i = 0; i < results.size(); i++) {
                ArrayList<FriendUserDetail> friendsInsideGroup = new ArrayList<FriendUserDetail>();
                GroupModel groupModel = new GroupModel();
                groupModel.setAdmin(results.get(i).getAdmin());
                groupModel.setId(results.get(i).getId());
                groupModel.setRoom_id(results.get(i).getRoom_id());
                groupModel.setGroud_name(results.get(i).getGroup_name());
                groupModel.setLastMessage(results.get(i).getLastMessage());
                groupModel.setGroupImage(results.get(i).getGroupImage());
                for(FriendUserDetailRealm friend :results.get(i).getFriendUsers()){
                    FriendUserDetail friendUserDetail = new FriendUserDetail();
                    friendUserDetail.setFriendImage(friend.getFriendImage());
                    friendUserDetail.setFriendName(friend.getFriendName());
                    friendUserDetail.setFriendPhoneNumber(friend.getFriendPhoneNumber());
                    friendsInsideGroup.add(friendUserDetail);
                }
                groupModel.setFriendUsers(friendsInsideGroup);
                groupList.add(groupModel);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
        return groupList;
    }


    public ArrayList<GroupModel> getListOfGroupFormDB(String roomId) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<GroupModel> groupList = new ArrayList<GroupModel>();
        RealmResults<ListOfGroupRealmModel> results = realm.where(ListOfGroupRealmModel.class).equalTo("room_id",roomId).sort("lastMessage", Sort.ASCENDING).findAll();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();

            realm.beginTransaction();

            for (int i = 0; i < results.size(); i++) {
                ArrayList<FriendUserDetail> friendsInsideGroup = new ArrayList<FriendUserDetail>();
                GroupModel groupModel = new GroupModel();
                groupModel.setAdmin(results.get(i).getAdmin());
                groupModel.setId(results.get(i).getId());
                groupModel.setRoom_id(results.get(i).getRoom_id());
                groupModel.setGroud_name(results.get(i).getGroup_name());
                groupModel.setLastMessage(results.get(i).getLastMessage());
                groupModel.setGroupImage(results.get(i).getGroupImage());
                for(FriendUserDetailRealm friend :results.get(i).getFriendUsers()){
                    FriendUserDetail friendUserDetail = new FriendUserDetail();
                    friendUserDetail.setFriendImage(friend.getFriendImage());
                    friendUserDetail.setFriendName(friend.getFriendName());
                    friendUserDetail.setFriendPhoneNumber(friend.getFriendPhoneNumber());
                    friendsInsideGroup.add(friendUserDetail);
                }
                groupModel.setFriendUsers(friendsInsideGroup);
                groupList.add(groupModel);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }
        return groupList;
    }

    public void removeGroupMemberOnIdBases(String id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ListOfGroupRealmModel> result = realm.where(ListOfGroupRealmModel.class).equalTo("id",id).findAll();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            result.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.commitTransaction();
        } finally {
            realm.close();
        }


    }


    /**
     * This method is used to fetch the local un-send message
     * according to its local-id
     *
     * @param localId
     * @return
     */
//    public ChatMessage fetchLocalChatMessageAccToLocalId(long localId) {
//        ChatMessage localChatMessage = new ChatMessage();
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            CustomDataModelLocalChatMessage customDataModelLocalChatMessage = realm.where(CustomDataModelLocalChatMessage.class).equalTo("localId", localId).findFirst();
//            if (customDataModelLocalChatMessage != null) {
//                mapCustomDataModelLocalChatMessageToLocalChatMessage(localChatMessage, customDataModelLocalChatMessage);
//            } else
//                localChatMessage = null;
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//        return localChatMessage;
//    }
//
//
//    /**
//     * Save new message in remote table
//     *
//     * @param chatMessage
//     */
//
//    public void saveMessageToRemoteDB(ChatMessage chatMessage) {
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            CustomDataModelChatMessage customDataModelChatMessage = new CustomDataModelChatMessage();
//            mapChatMessageToCustomModelChatMessage(chatMessage, customDataModelChatMessage);
//            realm.copyToRealmOrUpdate(customDataModelChatMessage);
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//    }
//
//
//    /**
//     * This method is used to save the local message data inside the database
//     * @param chatMessage
//     */
//
//    public void addLocalMessageToDb(ChatMessage chatMessage) {
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            CustomDataModelLocalChatMessage customDataModelLocalChatMessage = new CustomDataModelLocalChatMessage();
//            mapLocalChatMessageToCustomDataModelLocalChatMessage(chatMessage, customDataModelLocalChatMessage);
//            realm.copyToRealmOrUpdate(customDataModelLocalChatMessage);
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//    }
//
//
//    /**
//     * Delete message from local table on receiving sent ack from server
//     *
//     * @param localId
//     */
//    public void deleteCustomModelMessageFromLocalTable(long localId) {
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            CustomDataModelLocalChatMessage customDataModelLocalChatMessage = realm.where(CustomDataModelLocalChatMessage.class).equalTo("localId", localId).findFirst();
//            if (customDataModelLocalChatMessage != null) {
//                customDataModelLocalChatMessage.deleteFromRealm();
//            }
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//
//    }
//
//
//    /**
//     * Fatch the message to be sent to server
//     * @return
//     */
//
//    public ChatMessage fetchMessagesToBeSent() {
//        ChatMessage localChatMessage = new ChatMessage();
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            RealmResults<CustomDataModelLocalChatMessage> customDataModelMessageList = realm.where(CustomDataModelLocalChatMessage.class).findAll();
//            if (!customDataModelMessageList.isEmpty()) {
//                CustomDataModelLocalChatMessage customDataModelLocalChatMessage = customDataModelMessageList.sort("localId", Sort.ASCENDING).first();
//                mapCustomDataModelLocalChatMessageToLocalChatMessage(localChatMessage, customDataModelLocalChatMessage);
//            } else
//                localChatMessage = null;
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//        return localChatMessage;
//    }
//
//
//
//    public ArrayList<ChatMessage> fetchAllLocalMessages() {
//        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            RealmResults<CustomDataModelLocalChatMessage> localMessageList = realm.where(CustomDataModelLocalChatMessage.class).sort("localId",Sort.ASCENDING).findAll();
//            RealmResults<CustomDataModelChatMessage> remoteMessageList = realm.where(CustomDataModelChatMessage.class).sort("localId",Sort.ASCENDING).findAll();
//
//            for (int i = 0; i < localMessageList.size(); i++) {
//                ChatMessage localChatMessage = new ChatMessage();
//                mapCustomDataModelLocalChatMessageToLocalChatMessage(localChatMessage, localMessageList.get(i));
//                chatMessages.add(localChatMessage);
//            }
//            for (int i = 0; i < remoteMessageList.size(); i++) {
//                ChatMessage chatMessage = new ChatMessage();
//                mapCustomModelChatMessageToChatMessage(chatMessage, remoteMessageList.get(i));
//                chatMessages.add(chatMessage);
//            }
//
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//        return chatMessages;
//    }
//
//
//
//
//    /**
//     * This method is used to fetch the max id value inside the local data base
//     *
//     * @return
//     */
//    public long fetchMaxLocalId() {
//
//        long maxValue = 0;
//        long localMaxValue = getLocalMaxId();
//        long remoteMaxValue = getRemoteMaxId();
//
//        if (localMaxValue > remoteMaxValue) {
//            maxValue = localMaxValue;
//        } else {
//            maxValue = remoteMaxValue;
//        }
//
//
//        return maxValue + 1;
//    }
//
//    /**
//     * This method is used to fetch the max value of 'localId' from local database
//     *
//     * @return
//     */
//    private long getLocalMaxId() {
//        long localMaxValue = 0;
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            Number localNumber = realm.where(CustomDataModelLocalChatMessage.class).max("localId");
//            localMaxValue = localNumber.longValue();
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//
//
//        return localMaxValue;
//    }
//
//
//    /**
//     * This method is used to fetch the max value of 'localId' from remote database
//     *
//     * @return
//     */
//    private long getRemoteMaxId() {
//        long localMaxValue = 0;
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            if (realm.isInTransaction())
//                realm.commitTransaction();
//            realm.beginTransaction();
//            Number localNumber = realm.where(CustomDataModelChatMessage.class).max("localId");
//            localMaxValue = localNumber.longValue();
//            realm.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (realm.isInTransaction())
//                realm.cancelTransaction();
//        } finally {
//            realm.close();
//        }
//        return localMaxValue;
//    }
//
//
//    /**
//     * This method return data from 'local chat message model'
//     * to 'chat message model'
//     *
//     * @param localChatMessage                local chat message model
//     * @param customDataModelLocalChatMessage custom local model class
//     */
//    private void mapCustomDataModelLocalChatMessageToLocalChatMessage(ChatMessage localChatMessage, CustomDataModelLocalChatMessage customDataModelLocalChatMessage) {
//        localChatMessage.setLocalId(customDataModelLocalChatMessage.getLocalId());
//        localChatMessage.setStatusIsDelivered(customDataModelLocalChatMessage.isStatusIsDelivered());
//        localChatMessage.setId(customDataModelLocalChatMessage.getId());
//        localChatMessage.setFrom(customDataModelLocalChatMessage.getFrom());
//        localChatMessage.setTo(customDataModelLocalChatMessage.getTo());
//        localChatMessage.setBody(customDataModelLocalChatMessage.getBody());
//        localChatMessage.setTimestamp(customDataModelLocalChatMessage.getTimestamp());
//        localChatMessage.setLocation(customDataModelLocalChatMessage.getLocation());
//        localChatMessage.setType(customDataModelLocalChatMessage.getType());
//        localChatMessage.setContact(getContactModel(customDataModelLocalChatMessage));
//        localChatMessage.setAudioModelClass(getAudioModel(customDataModelLocalChatMessage));
//        localChatMessage.setVideoModelClass(getVideoModel(customDataModelLocalChatMessage));
//        localChatMessage.setImageModel(getImageModel(customDataModelLocalChatMessage));
//    }
//
//    /**
//     * This method return data from 'customDataModelLocalChatMessage'
//     * to 'localChatMessage'
//     *
//     * @param localChatMessage                local chat message model
//     * @param customDataModelLocalChatMessage custom local model class
//     */
//
//    private void mapLocalChatMessageToCustomDataModelLocalChatMessage(ChatMessage localChatMessage, CustomDataModelLocalChatMessage customDataModelLocalChatMessage) {
//        customDataModelLocalChatMessage.setLocalId(localChatMessage.getLocalId());
//        customDataModelLocalChatMessage.setStatusIsDelivered(localChatMessage.isStatusIsDelivered());
//        customDataModelLocalChatMessage.setId(localChatMessage.getId());
//        customDataModelLocalChatMessage.setFrom(localChatMessage.getFrom());
//        customDataModelLocalChatMessage.setTo(localChatMessage.getTo());
//        customDataModelLocalChatMessage.setBody(localChatMessage.getBody());
//        customDataModelLocalChatMessage.setTimestamp(localChatMessage.getTimestamp());
//        customDataModelLocalChatMessage.setLocation(localChatMessage.getLocation());
//        customDataModelLocalChatMessage.setType(localChatMessage.getType());
//        customDataModelLocalChatMessage.setContact(getContactModel(localChatMessage));
//        customDataModelLocalChatMessage.setAudioModelClass(getAudioModel(localChatMessage));
//        customDataModelLocalChatMessage.setVideoModelClass(getVideoModel(localChatMessage));
//        customDataModelLocalChatMessage.setImageModel(getImageModel(localChatMessage));
//
//    }
//
//
//    private void mapChatMessageToCustomModelChatMessage(ChatMessage chatMessage, CustomDataModelChatMessage customDataModelChatMessage) {
//        customDataModelChatMessage.setLocalId(chatMessage.getLocalId());
//        customDataModelChatMessage.setStatusIsDelivered(chatMessage.isStatusIsDelivered());
//        customDataModelChatMessage.setId(chatMessage.getId());
//        customDataModelChatMessage.setFrom(chatMessage.getFrom());
//        customDataModelChatMessage.setTo(chatMessage.getTo());
//        customDataModelChatMessage.setBody(chatMessage.getBody());
//        customDataModelChatMessage.setTimestamp(chatMessage.getTimestamp());
//        customDataModelChatMessage.setLocation(chatMessage.getLocation());
//        customDataModelChatMessage.setType(chatMessage.getType());
//        customDataModelChatMessage.setContact(getContactModel(chatMessage));
//        customDataModelChatMessage.setAudioModelClass(getAudioModel(chatMessage));
//        customDataModelChatMessage.setVideoModelClass(getVideoModel(chatMessage));
//        customDataModelChatMessage.setImageModel(getImageModel(chatMessage));
//    }
//
//
//    private void mapCustomModelChatMessageToChatMessage(ChatMessage chatMessage, CustomDataModelChatMessage customDataModelChatMessage) {
//        chatMessage.setLocalId(customDataModelChatMessage.getLocalId());
//        chatMessage.setStatusIsDelivered(customDataModelChatMessage.isStatusIsDelivered());
//        chatMessage.setId(customDataModelChatMessage.getId());
//        chatMessage.setFrom(customDataModelChatMessage.getFrom());
//        chatMessage.setTo(customDataModelChatMessage.getTo());
//        chatMessage.setBody(customDataModelChatMessage.getBody());
//        chatMessage.setTimestamp(customDataModelChatMessage.getTimestamp());
//        chatMessage.setLocation(customDataModelChatMessage.getLocation());
//        chatMessage.setType(customDataModelChatMessage.getType());
//        chatMessage.setContact(getContactModel(customDataModelChatMessage));
//        chatMessage.setAudioModelClass(getAudioModel(customDataModelChatMessage));
//        chatMessage.setVideoModelClass(getVideoModel(customDataModelChatMessage));
//        chatMessage.setImageModel(getImageModel(customDataModelChatMessage));
//    }
//
//
//    /**
//     * This method return the model of ImageUploadInfoRealm from CustomDataModelLocalChatMessage
//     *
//     * @param customDataModelLocalChatMessage
//     * @return
//     */
//
//    private ImageUploadInfoRealm getImageModel(CustomDataModelLocalChatMessage customDataModelLocalChatMessage) {
//        ImageUploadInfoRealm imageUploadInfoRealm = new ImageUploadInfoRealm(customDataModelLocalChatMessage.getImageModel().getImageName(),
//                customDataModelLocalChatMessage.getImageModel().getImageURL());
//        return imageUploadInfoRealm;
//    }
//
//
//    /**
//     * This method return the model of ImageUploadInfoRealm from ChatMessage
//     *
//     * @param localChatMessage
//     * @return
//     */
//    private ImageUploadInfoRealm getImageModel(ChatMessage localChatMessage) {
//        ImageUploadInfoRealm imageUploadInfoRealm = new ImageUploadInfoRealm(localChatMessage.getImageModel().getImageName(),
//                localChatMessage.getImageModel().getImageURL());
//        return imageUploadInfoRealm;
//    }
//
//    /**
//     * This method return the model of ImageUploadInfoRealm from CustomDataModelChatMessage
//     *
//     * @param customDataModelChatMessage
//     * @return
//     */
//
//    private ImageUploadInfoRealm getImageModel(CustomDataModelChatMessage customDataModelChatMessage) {
//        ImageUploadInfoRealm imageUploadInfoRealm = new ImageUploadInfoRealm(customDataModelChatMessage.getImageModel().getImageName(),
//                customDataModelChatMessage.getImageModel().getImageURL());
//        return imageUploadInfoRealm;
//    }
//
//
//    /**
//     * This method return the model of VideoModelClassRealm from CustomDataModelLocalChatMessage
//     *
//     * @param customDataModelLocalChatMessage
//     * @return
//     */
//    private VideoModelClassRealm getVideoModel(CustomDataModelLocalChatMessage customDataModelLocalChatMessage) {
//
//        VideoModelClassRealm videoModelClassRealm = new VideoModelClassRealm(customDataModelLocalChatMessage.getVideoModelClass().getTime(),
//                customDataModelLocalChatMessage.getVideoModelClass().getVideoLink(), customDataModelLocalChatMessage.getVideoModelClass().getVideoThumb());
//
//        return videoModelClassRealm;
//
//    }
//
//    /**
//     * This method return the model of VideoModelClassRealm from ChatMessage
//     *
//     * @param localChatMessage
//     * @return
//     */
//    private VideoModelClassRealm getVideoModel(ChatMessage localChatMessage) {
//        VideoModelClassRealm videoModelClassRealm = new VideoModelClassRealm(localChatMessage.getVideoModelClass().getTime(),
//                localChatMessage.getVideoModelClass().getVideoLink(), localChatMessage.getVideoModelClass().getVideoThumb());
//
//        return videoModelClassRealm;
//    }
//
//    /**
//     * This method return the model of VideoModelClassRealm from CustomDataModelChatMessage
//     *
//     * @param customDataModelChatMessage
//     * @return
//     */
//
//    private VideoModelClassRealm getVideoModel(CustomDataModelChatMessage customDataModelChatMessage) {
//        VideoModelClassRealm videoModelClassRealm = new VideoModelClassRealm(customDataModelChatMessage.getVideoModelClass().getTime(),
//                customDataModelChatMessage.getVideoModelClass().getVideoLink(), customDataModelChatMessage.getVideoModelClass().getVideoThumb());
//
//        return videoModelClassRealm;
//    }
//
//    /**
//     * This method return the model of AudioModelClassRealm from CustomDataModelLocalChatMessage
//     *
//     * @param customDataModelLocalChatMessage
//     * @return
//     */
//
//    private AudioModelClassRealm getAudioModel(CustomDataModelLocalChatMessage customDataModelLocalChatMessage) {
//        AudioModelClassRealm audioModelClassRealm = new AudioModelClassRealm(customDataModelLocalChatMessage.getAudioModelClass().getTime(),
//                customDataModelLocalChatMessage.getAudioModelClass().getAudioLink());
//        return audioModelClassRealm;
//    }
//
//    /**
//     * This method return the model of AudioModelClassRealm from ChatMessage
//     *
//     * @param localChatMessage
//     * @return
//     */
//
//    private AudioModelClassRealm getAudioModel(ChatMessage localChatMessage) {
//        AudioModelClassRealm audioModelClassRealm = new AudioModelClassRealm(localChatMessage.getAudioModelClass().getTime(),
//                localChatMessage.getAudioModelClass().getAudioLink());
//        return audioModelClassRealm;
//    }
//
//    /**
//     * This method return the model of AudioModelClassRealm from CustomDataModelChatMessage
//     *
//     * @param customDataModelChatMessage
//     * @return
//     */
//
//    private AudioModelClassRealm getAudioModel(CustomDataModelChatMessage customDataModelChatMessage) {
//        AudioModelClassRealm audioModelClassRealm = new AudioModelClassRealm(customDataModelChatMessage.getAudioModelClass().getTime(),
//                customDataModelChatMessage.getAudioModelClass().getAudioLink());
//        return audioModelClassRealm;
//    }
//
//
//    /**
//     * This method return the model of PhoneContactRealm from CustomDataModelLocalChatMessage
//     *
//     * @param customDataModelLocalChatMessage
//     * @return
//     */
//    private PhoneContactRealm getContactModel(CustomDataModelLocalChatMessage customDataModelLocalChatMessage) {
//        PhoneContactRealm phoneContactRealm = new PhoneContactRealm(customDataModelLocalChatMessage.getContact().getName(),
//                customDataModelLocalChatMessage.getContact().getPhone_number(),
//                customDataModelLocalChatMessage.getContact().getPhotoUri());
//        return phoneContactRealm;
//
//    }
//
//    /**
//     * This method return the model of PhoneContactRealm from ChatMessage
//     *
//     * @param localChatMessage
//     * @return
//     */
//
//    private PhoneContactRealm getContactModel(ChatMessage localChatMessage) {
//        PhoneContactRealm phoneContactRealm = new PhoneContactRealm(localChatMessage.getContact().getName(),
//                localChatMessage.getContact().getPhone_number(),
//                localChatMessage.getContact().getPhotoUri());
//        return phoneContactRealm;
//    }
//
//
//    /**
//     * This method return the model of PhoneContactRealm from CustomDataModelChatMessage
//     *
//     * @param customDataModelChatMessage
//     * @return
//     */
//    private PhoneContactRealm getContactModel(CustomDataModelChatMessage customDataModelChatMessage) {
//        PhoneContactRealm phoneContactRealm = new PhoneContactRealm(customDataModelChatMessage.getContact().getName(),
//                customDataModelChatMessage.getContact().getPhone_number(),
//                customDataModelChatMessage.getContact().getPhotoUri());
//        return phoneContactRealm;
//    }
}
