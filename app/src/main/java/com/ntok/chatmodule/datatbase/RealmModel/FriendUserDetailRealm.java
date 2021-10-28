package com.ntok.chatmodule.datatbase.RealmModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FriendUserDetailRealm extends RealmObject{

        private String FriendName;
        @PrimaryKey
        private String FriendPhoneNumber;
        private String FriendImage;

        public String getFriendName() {
            return FriendName;
        }

        public void setFriendName(String friendName) {
            FriendName = friendName;
        }

        public String getFriendPhoneNumber() {
            return FriendPhoneNumber;
        }

        public void setFriendPhoneNumber(String friendPhoneNumber) {
            FriendPhoneNumber = friendPhoneNumber;
        }

        public String getFriendImage() {
            return FriendImage;
        }

        public void setFriendImage(String friendImage) {
            FriendImage = friendImage;
        }

}
