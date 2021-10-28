package com.ntok.chatmodule.backend.firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.LoginActivity;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.ListPeopleAdapter;
import com.ntok.chatmodule.backend.Constancts;
import com.ntok.chatmodule.backend.sqlite_database.MessageDB;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.fragment.GroupProfileFragment;
import com.ntok.chatmodule.fragment.UserProfileFragment;
import com.ntok.chatmodule.interfaces.AddMessageInterface;
import com.ntok.chatmodule.interfaces.FirebaseDataUpdatedInterface;
import com.ntok.chatmodule.interfaces.LoginHelperInterface;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.model.AudioModelClass;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.FriendUserDetail;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.model.ImageUploadInfo;
import com.ntok.chatmodule.model.MessageModel;
import com.ntok.chatmodule.model.PhoneContact;
import com.ntok.chatmodule.model.User;
import com.ntok.chatmodule.model.VideoModelClass;
import com.ntok.chatmodule.sharedPreference.SharedPreference;
import com.ntok.chatmodule.utils.CommonMethord;
import com.ntok.chatmodule.utils.Lg;
import com.ntok.chatmodule.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sonam on 07-05-2018.
 */

public class FireBaseSingleton {

    static FireBaseSingleton instance;
    boolean firstTimeAccess;
    private ChildEventListener mChatChildEventListener = null;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private String TAG = "FireBaseSingleton";
    //  private AuthUtils authUtils;
    private FirebaseAuth mAuth;
    private Activity activity;
    private LoginHelperInterface loginHelperInterface;
    private FirebaseDataUpdatedInterface firebaseDataUpdatedInterface;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private Context context;
    private String mVerificationId;
    private LoginActivity loginActivity;
    private ArrayList<MessageModel> checkTheOldMessage = new ArrayList<>();
    private boolean isNewMessageAdded = false;

    private FireBaseSingleton() {
        //  instance = new FireBaseSingleton();
    }

    public static FireBaseSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new FireBaseSingleton();
            instance.context = context;
            instance.initFirebase();

        }

        return instance;
    }

    public void deleteAllInstance() {
        mAuth = null;
        mDatabase = null;
        storage = null;
        storageReference = null;
        //  authUtils = new AuthUtils();
        mAuthListener = null;
        instance = null;
    }

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //  authUtils = new AuthUtils();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Constancts.UID = user.getUid();
                    Lg.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        Toast.makeText(context, "login successfull", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

    }


    //A network error (such as timeout, interrupted connection or unreachable host) has occurred.
    // [START sign_in_with_phone]
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            writeNewUser(user.getPhoneNumber(), user.getPhoneNumber());
                            // [START_EXCLUDE]
                            //   updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Lg.d("Invalid code.");
                                if (loginActivity != null) {
                                    loginActivity.hideProgress();
                                    Utility.showAltertPopup(loginActivity, "Invalid code", context.getResources().getString(R.string.invalid_code), new OnAlertPopupClickListener() {
                                        @Override
                                        public void onOkButtonpressed() {
                                            loginActivity.clearOtpAndStartWithNewNumber();
                                        }

                                        @Override
                                        public void onCancelButtonPressed() {

                                        }
                                    });
                                }
                            } else {
                                if (loginActivity != null) {
                                    loginActivity.hideProgress();
                                    Utility.showAltertPopup(loginActivity, "Network Problem", context.getResources().getString(R.string.network_problem), new OnAlertPopupClickListener() {
                                        @Override
                                        public void onOkButtonpressed() {
                                            loginActivity.clearOtpAndStartWithNewNumber();
                                        }

                                        @Override
                                        public void onCancelButtonPressed() {

                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }

    public void verifyPhoneNumberWithCode(String code, LoginActivity loginActivity) {
        // [START verify_with_code]
        this.loginActivity = loginActivity;
        if (code != null && code.length() > 0) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            // [END verify_with_code]
            signInWithPhoneAuthCredential(credential);
        }
    }

    // [START resend_verification]
    public void resendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);             // ForceResendingToken from callbacks
    }

    public void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Lg.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                // mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                loginHelperInterface.onVerificationFailed(e);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                // Write new user
                // [START_EXCLUDE]
                // Update UI
                loginHelperInterface.onCodeSent();

                //   updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

    }

    public void writeNewUser(final String userId, final String phone_number) {

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lg.e(dataSnapshot.toString());
                User user = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    user = dataSnapshot.getValue(User.class);
                }
                if (user == null) {
                    addnewUser(userId, phone_number);
                    return;
                }
                FirebaseDataSingleton.getInstance(activity).setUser(user);
                sendRegistrationToServer();
                loginHelperInterface.onRegisterComplete();
                getFriends();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Lg.d(databaseError.toString());
            }
        });

    }

    public void addnewUser(String userId, String phone_number) {
        final User user = new User(LoginActivity.UserName,phone_number, null);
        mDatabase.child("users").child(userId).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    FirebaseDataSingleton.getInstance(activity).setUser(user);
                    loginHelperInterface.onRegisterComplete();
                    sendRegistrationToServer();
                } else {
                    loginHelperInterface.onVerificationFailed(null);
                }
            }
        });
    }

    public void updateUserProfile(final User user) {
        mDatabase
                .child("users")
                .child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number)
                .child("about")
                .setValue(user.about);
        mDatabase
                .child("users")
                .child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number)
                .child("username")
                .setValue(user.username);
        mDatabase
                .child("users")
                .child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number)
                .child("userImage")
                .setValue(user.userImage);
        FirebaseDataSingleton.getInstance(activity).setUser(user);

    }

    public void setActivity(Activity activity, LoginHelperInterface loginHelper) {
        this.activity = activity;
        this.loginHelperInterface = loginHelper;
    }

    public void setActivity(Activity activity, FirebaseDataUpdatedInterface firebaseDataUpdatedInterface) {
        this.activity = activity;
        this.firebaseDataUpdatedInterface = firebaseDataUpdatedInterface;
    }

    public void getUserFromId(final String id) {
        mDatabase.child("users").child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        // Get user value
                        if (user == null) {
                            loginHelperInterface.onVerificationFailed(null);
                            return;
                        }
                        FirebaseDataSingleton.getInstance(activity).setUser(user);
                        sendRegistrationToServer();
                        FirebaseDataSingleton.getInstance(activity).setFriendList(RealamDatabase.getInstance().getAllFriends());
                        if (loginHelperInterface != null)
                            loginHelperInterface.onRegisterComplete();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getFriend:onCancelled", databaseError.toException());
                        loginHelperInterface.onVerificationFailed(null);
                        return;
                    }
                });
    }

    public void getUserFromId(final String id, final boolean isFriend) {
        if (id != null && id.length() > 0) {
            mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap mapUser = (HashMap) dataSnapshot.getValue();
                    if (mapUser != null && mapUser.size() > 0) {
                        User user = new User((String) mapUser.get("username"),
                                (String) mapUser.get("phone_number"),
                                null);
                        user.onlineStatus = (String) mapUser.get("onlineStatus");
                        user.notificationTokens = (String) mapUser.get("notificationTokens");
                        user.userImage = (String) mapUser.get("userImage");
                        if (FirebaseDataSingleton.getInstance(activity).getUser() != null && FirebaseDataSingleton.getInstance(activity).getUser().phone_number.equalsIgnoreCase(user.phone_number)) {
                            // Get user value
                            if (user == null) {
                                loginHelperInterface.onVerificationFailed(null);
                                return;
                            }
                            FirebaseDataSingleton.getInstance(activity).setUser(user);
                            loginHelperInterface.onRegisterComplete();

                        } else {
                            FirebaseDataSingleton.getInstance(activity).upDateFriend(null, user);
                            if (firebaseDataUpdatedInterface != null) {
                                firebaseDataUpdatedInterface.onFriendsUpdate();
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public void getUsers() {
        if (FirebaseDataSingleton.getInstance(activity).getAllUsers().size() > 0)
            return;
        mDatabase.child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        ArrayList<User> users = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            User user = messageSnapshot.getValue(User.class);
                            users.add(user);
                        }
                        FirebaseDataSingleton.getInstance(activity).setAllUsers(users);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getFriend:onCancelled", databaseError.toException());
                    }
                });
    }

    public void addFriends(User friend, PhoneContact phoneUser, final MainActivity activity) {

        final FriendUser friendUser = new FriendUser();
        friendUser.room_id = CommonMethord.generateRoomID(friend.phone_number, FirebaseDataSingleton.getInstance(activity).getUser().phone_number);
        friendUser.userID = friend.phone_number;
        friendUser.phone_number = friend.phone_number;
        friendUser.userImage = phoneUser.getPhotoUri();
        friendUser.username = phoneUser.getName();
        friendUser.lastMessage = RealamDatabase.getInstance().getLastMessageFromFriendDatabase(friend.phone_number);

        mDatabase.child("friends").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number).child(friendUser.userID).setValue(friendUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {

                    //      loginHelperInterface.onRegisterComplete();
                }
            }
        });


        //Add to my friend list
        FriendUser userFriend = new FriendUser();
        userFriend.room_id = friendUser.room_id;
        userFriend.userID = FirebaseDataSingleton.getInstance(activity).getUser().phone_number;
        userFriend.phone_number = FirebaseDataSingleton.getInstance(activity).getUser().phone_number;
        userFriend.username = FirebaseDataSingleton.getInstance(activity).getUser().username;
        userFriend.lastMessage = RealamDatabase.getInstance().getLastMessageFromFriendDatabase(friend.phone_number);

        mDatabase.child("friends").child(friendUser.userID).child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number).setValue(userFriend, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    ((MainActivity) activity).startChartFragment(null, friendUser, null);
                }
            }
        });
    }

    public void getFriends() {
        mDatabase.child("friends").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {   // Get user value
                HashMap friendUserHashMAp = (HashMap) dataSnapshot.getValue();
                FriendUser friendUser = new FriendUser();
                friendUser.room_id = ((String) friendUserHashMAp.get("room_id"));
                friendUser.lastMessage = ((String) friendUserHashMAp.get("lastMessage"));
                friendUser.userID = ((String) friendUserHashMAp.get("userID"));
                friendUser.userImage = ((String) friendUserHashMAp.get("userImage"));
                friendUser.username = ((String) friendUserHashMAp.get("username"));
                friendUser.phone_number = ((String) friendUserHashMAp.get("phone_number"));
                Log.e("Tag", "fdshfksjf          " + friendUser);
                if (friendUser.userID != null) {
                    getUserFromId(friendUser.userID, true);
                    FirebaseDataSingleton.getInstance(activity).addFriend(friendUser);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap friendUserHashMAp = (HashMap) dataSnapshot.getValue();
                FriendUser friendUser = new FriendUser();
                friendUser.room_id = ((String) friendUserHashMAp.get("room_id"));
                friendUser.lastMessage = ((String) friendUserHashMAp.get("lastMessage"));
                friendUser.userID = ((String) friendUserHashMAp.get("userID"));
                friendUser.userImage = ((String) friendUserHashMAp.get("userImage"));
                friendUser.username = ((String) friendUserHashMAp.get("username"));
                friendUser.phone_number = ((String) friendUserHashMAp.get("phone_number"));

                FirebaseDataSingleton.getInstance(activity).upDateFriend(friendUser, null);
                if (firebaseDataUpdatedInterface != null) {
                    firebaseDataUpdatedInterface.onFriendsUpdate();
                }
                Lg.d("friendAdded");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Lg.d("friendRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Lg.d("friendMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Lg.d("friendCancle");
            }
        });

    }

    public void getUserMessage(final String room_id, long timeStamp, final AddMessageInterface addMessageInterface) {
        Query queue;

        queue = mDatabase.child("chat").child(room_id).orderByChild("timestamp").startAt(timeStamp);

        mChatChildEventListener = queue.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                checkTheOldMessage.clear();
                checkTheOldMessage = MessageDB.getInstance(instance.context).getListMessage(room_id);


                Collections.sort(checkTheOldMessage, new Comparator<MessageModel>() {
                    @Override
                    public int compare(MessageModel o1, MessageModel o2) {
                        return String.valueOf(o1.getTimestamp()).compareTo(String.valueOf(o2.getTimestamp()));
                    }
                });
                if (dataSnapshot.getValue() != null) {
                    HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                    if (checkTheOldMessage.size() == 0) {
                        MessageModel newMessage = new MessageModel();
                        newMessage.setFrom((String) mapMessage.get("from"));
                        newMessage.setId((String) mapMessage.get("id"));
                        newMessage.setTo((String) mapMessage.get("to"));
                        newMessage.setBody((String) mapMessage.get("body"));
                        newMessage.setTimestamp((long) mapMessage.get("timestamp"));
                        newMessage.setType((String) mapMessage.get("type"));
                        newMessage.setLocation((String) mapMessage.get("location"));
                        HashMap userImage = (HashMap) mapMessage.get("imageModel");
                        HashMap audioLink = (HashMap) mapMessage.get("audioModelClass");
                        HashMap videoLink = (HashMap) mapMessage.get("videoModelClass");
                        HashMap contact = (HashMap) mapMessage.get("contact");
                        if (userImage != null) {
                            newMessage.setImageModel(new ImageUploadInfo((String) userImage.get("imageName"), (String) userImage.get("imageURL")));
                        }
                        if (audioLink != null) {
                            newMessage.setAudioModelClass(new AudioModelClass((long) audioLink.get("time"), (String) audioLink.get("audioLink")));
                        }
                        if (videoLink != null) {
                            newMessage.setVideoModelClass(new VideoModelClass((long) videoLink.get("time"), (String) videoLink.get("videoLink"), (String) videoLink.get("videoThumb")));
                        }
                        if (contact != null) {
                            newMessage.setContact(new PhoneContact((String) contact.get("name"), (String) contact.get("phone_number"), (String) contact.get("photoUri")));
                        }
                        if (FirebaseDataSingleton.getInstance(activity).getMessageListHashMap().containsKey(room_id)) {
                            FirebaseDataSingleton.getInstance(activity).getMessageListHashMap().get(room_id).add(newMessage);
                        } else {
                            ArrayList<MessageModel> messageModels = new ArrayList<>();
                            messageModels.add(newMessage);
                            FirebaseDataSingleton.getInstance(activity).getMessageListHashMap().put(room_id, messageModels);
                        }
                        MessageDB.getInstance(activity).addMessage(newMessage, room_id);
                        addMessageInterface.onAddMessage();
                    } else if (!checkTheOldMessage.get(checkTheOldMessage.size() - 1).getId().contains((String) mapMessage.get("id"))) {
                        MessageModel newMessage = new MessageModel();
                        newMessage.setFrom((String) mapMessage.get("from"));
                        newMessage.setId((String) mapMessage.get("id"));
                        newMessage.setTo((String) mapMessage.get("to"));
                        newMessage.setBody((String) mapMessage.get("body"));
                        newMessage.setTimestamp((long) mapMessage.get("timestamp"));
                        newMessage.setType((String) mapMessage.get("type"));
                        newMessage.setLocation((String) mapMessage.get("location"));
                        HashMap userImage = (HashMap) mapMessage.get("imageModel");
                        HashMap audioLink = (HashMap) mapMessage.get("audioModelClass");
                        HashMap videoLink = (HashMap) mapMessage.get("videoModelClass");
                        HashMap contact = (HashMap) mapMessage.get("contact");
                        if (userImage != null) {
                            newMessage.setImageModel(new ImageUploadInfo((String) userImage.get("imageName"), (String) userImage.get("imageURL")));
                        }
                        if (audioLink != null) {
                            newMessage.setAudioModelClass(new AudioModelClass((long) audioLink.get("time"), (String) audioLink.get("audioLink")));
                        }
                        if (videoLink != null) {
                            newMessage.setVideoModelClass(new VideoModelClass((long) videoLink.get("time"), (String) videoLink.get("videoLink"), (String) videoLink.get("videoThumb")));
                        }
                        if (contact != null) {
                            newMessage.setContact(new PhoneContact((String) contact.get("name"), (String) contact.get("phone_number"), (String) contact.get("photoUri")));
                        }
                        if (FirebaseDataSingleton.getInstance(activity).getMessageListHashMap().containsKey(room_id)) {
                            FirebaseDataSingleton.getInstance(activity).getMessageListHashMap().get(room_id).add(newMessage);
                        } else {
                            ArrayList<MessageModel> messageModels = new ArrayList<>();
                            messageModels.add(newMessage);
                            FirebaseDataSingleton.getInstance(activity).getMessageListHashMap().put(room_id, messageModels);
                        }
                        MessageDB.getInstance(activity).addMessage(newMessage, room_id);
                        addMessageInterface.onAddMessage();
                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Lg.d("onChildchage");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Lg.d("onChildchage");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Lg.d("onChildchage");
            }
        });
    }


    public void updateUserStatus(String value) {
        mDatabase.child("users").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number).child("onlineStatus").setValue(value);
    }

    public void removeChatChildEventListener(final String room_id) {
        if (mChatChildEventListener != null)
            mDatabase.child("chat").child(room_id).removeEventListener(mChatChildEventListener);
        mChatChildEventListener = null;
    }

    public void addMessageToFireBase(String userId, String friendID, String roomId, MessageModel newMessage, boolean isGroupMessage, ArrayList<FriendUserDetail> groupUsers) {
        addMessageToFireBase(userId, friendID, roomId, newMessage, isGroupMessage, groupUsers, null);
    }

    public void addMessageToFireBase(String userId, String friendID, String roomId, MessageModel newMessage, boolean isGroupMessage, ArrayList<FriendUserDetail> groupUsers, Uri filePath) {

        String id = mDatabase.child("chat").child(roomId).push().getKey();
        newMessage.setId(id);
        mDatabase.child("chat").child(roomId).child(id).setValue(newMessage);

        try {
            if (!isGroupMessage) {
                mDatabase.child("friends").child(userId).child(friendID).child("lastMessage").setValue(newMessage.getBody());
                mDatabase.child("friends").child(friendID).child(userId).child("lastMessage").setValue(newMessage.getBody());
                sendNotificationToUserFr(friendID, newMessage);
            } else {
                mDatabase.child("group/" + friendID).child("lastMessage").setValue(newMessage.getBody());
                for (FriendUserDetail user : groupUsers) {
                    if(!user.getFriendPhoneNumber().equalsIgnoreCase(FirebaseDataSingleton.getInstance(instance.context).getUser().phone_number)) {
                        sendNotificationToUser(user, newMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteMessage(String roomId, String messageId) {
        mDatabase.child("chat").child(roomId).child(messageId).removeValue();
        FirebaseDataSingleton.getInstance(activity).removeMessage(roomId, messageId);

    }

    public void sendNotificationToUser(FriendUserDetail user, final MessageModel message) {
        mDatabase.child("notifications").child(message.getFrom()).child(user.getFriendPhoneNumber()).setValue(message);
    }

    public void sendNotificationToUserFr(String user, final MessageModel message) {
        mDatabase.child("notifications").child(message.getFrom()).child(user).setValue(message);
    }

    public void UploadFileToFirebase(final Uri filePath, final MessageModel messageModel, final String roomId, final Bitmap bitmap, final boolean isGroupMessage, final ArrayList<FriendUserDetail> groupUser, final UserProfileFragment.ImageUploadResultInterface imageUploadResultInterface) {
        if (filePath != null) {
            final StorageReference ref;
            ref = storageReference.child("users").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number).child(System.currentTimeMillis() + "");
            final UploadTask uploadTask = ref.putFile(filePath);
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uploadTask.cancel();
                    dialog.dismiss();
                }
            });
            progressDialog.show();


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Hiding the progressDialog after done uploading.
                    progressDialog.dismiss();
                    // Getting image name from EditText and store into string variable.
                    String TempImageName = "temp";

                    // Showing toast message after done uploading.
                    Toast.makeText(activity, "Image Uploaded Successfully ", Toast.LENGTH_LONG).show() ;

                    if (messageModel == null) {
                        imageUploadResultInterface.imageUploaded(taskSnapshot.getDownloadUrl().toString());
                    } else {
                        if (messageModel.getType().equalsIgnoreCase(MessageModel.IMAGE_TYPE)) {

                            messageModel.getImageModel().imageURL = taskSnapshot.getDownloadUrl().toString();
                            addMessageToFireBase(messageModel.getFrom(), messageModel.getTo(), roomId, messageModel, isGroupMessage, groupUser, filePath);

                        } else if (messageModel.getType().equalsIgnoreCase(MessageModel.AUDIO_TYPE)) {
                            messageModel.getAudioModelClass().setAudioLink(taskSnapshot.getDownloadUrl().toString());
                            addMessageToFireBase(messageModel.getFrom(), messageModel.getTo(), roomId, messageModel, isGroupMessage, groupUser, filePath);

                        } else if (messageModel.getType().equalsIgnoreCase(MessageModel.VIDEO_TYPE)) {
                            messageModel.getVideoModelClass().setVideoLink(taskSnapshot.getDownloadUrl().toString());
                            uploadeBitmapToServer(bitmap, messageModel, roomId, isGroupMessage, groupUser, filePath);
                        }

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void uploadeBitmapToServer(Bitmap bitmap, final MessageModel messageModel, final String roomId, final boolean isGroupMessage, final ArrayList<FriendUserDetail> groupUser, final Uri filePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference ref = storageReference.child("users").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number).child(messageModel.getTimestamp() + "");
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                messageModel.getVideoModelClass().setVideoThumb(downloadUrl.toString());
                addMessageToFireBase(messageModel.getFrom(), messageModel.getTo(), roomId, messageModel, isGroupMessage, groupUser, filePath);

            }
        });
    }

    public void sendRegistrationToServer() {
        // TODO: Implement this method to send token to your app server.


        String instanceId = SharedPreference.getInstance(activity).getDeviceToken();
        if (FirebaseDataSingleton.getInstance(activity).getUser().phone_number != null) {
            mDatabase
                    .child("users")
                    .child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number)
                    .child("notificationTokens")
                    .setValue(instanceId);
            FirebaseDataSingleton.getInstance(activity).getUser().notificationTokens = instanceId;
        } else {
            Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addGroup(final GroupModel groupModel, final ArrayList<FriendUserDetail> listIDChoose, final ArrayList<FriendUserDetail> listIDChooseRemove, final ListPeopleAdapter.OnCompletionListener onCompletionListener) {
        mDatabase.child("group/" + groupModel.getId()).setValue(groupModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                addGroupForUser(groupModel.getRoom_id(), listIDChoose, listIDChooseRemove, 0, onCompletionListener);
            }
        });
    }

    public void addGroupForUser(final String roomId, final ArrayList<FriendUserDetail> listIDChoose, final ArrayList<FriendUserDetail> listIDChooseRemove, final int userIndex, final ListPeopleAdapter.OnCompletionListener onCompletionListener) {
        if (userIndex == listIDChoose.size()) {
            if (listIDChooseRemove.size() > 0) {
                removeGroupForUser(roomId, listIDChoose, listIDChooseRemove, 0, onCompletionListener);
            } else {
                onCompletionListener.onComplete();
            }
        } else {
            ArrayList<FriendUserDetail> arrayList = new ArrayList<>();
            arrayList.addAll(listIDChoose);
            mDatabase.child("users/" + arrayList.get(userIndex).getFriendPhoneNumber() + "/group/" + roomId).setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    addGroupForUser(roomId, listIDChoose, listIDChooseRemove, userIndex + 1, onCompletionListener);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    public void removeGroupForUser(final String roomId, final ArrayList<FriendUserDetail> listIDChoose, final ArrayList<FriendUserDetail> listIDChooseRemove, final int userIndex, final ListPeopleAdapter.OnCompletionListener onCompletionListener) {
        ArrayList<FriendUserDetail> arrayList = new ArrayList<>();
        arrayList.addAll(listIDChoose);
        mDatabase.child("users/" + arrayList.get(userIndex).getFriendPhoneNumber() + "/group/" + roomId).setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                addGroupForUser(roomId, listIDChoose, listIDChooseRemove, userIndex + 1, onCompletionListener);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void getAllGroups() {
        mDatabase.child("users/" + FirebaseDataSingleton.getInstance(activity).getUser().phone_number + "/group").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        ArrayList<String> group = new ArrayList<String>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            group.add(child.getValue(String.class));
                        }

                        FirebaseDataSingleton.getInstance(activity).checkDeleteGroup(group);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getFriend:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getListGroup() {
        if (FirebaseDataSingleton.getInstance(activity).getUser() != null)
            mDatabase.child("users/" + FirebaseDataSingleton.getInstance(activity).getUser().phone_number + "/group").addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        String groupId = (String) dataSnapshot.getValue();
                        GroupModel groupModel = new GroupModel();
                        groupModel.setId(groupId);
                        FirebaseDataSingleton.getInstance(activity).addGroup(groupModel);
                        getGroupInfo(groupId);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        String groupId = (String) dataSnapshot.getValue();
                        GroupModel groupModel = new GroupModel();
                        groupModel.setId(groupId);
                        FirebaseDataSingleton.getInstance(activity).upDateGroup(groupModel);
                        getGroupInfo(groupId);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String groupId = (String) dataSnapshot.getValue();
                        GroupModel groupModel = new GroupModel();
                        groupModel.setId(groupId);
                        FirebaseDataSingleton.getInstance(activity).removeGroup(groupModel);
                        firebaseDataUpdatedInterface.onGroupUpdate();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Lg.d("group move");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Lg.d("group cancle");
                }
            });
    }

    private void getGroupInfo(String groupId) {
        if (groupId != null) {
            mDatabase.child("group/" + groupId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        GroupModel groupModel = new GroupModel();
                        HashMap mapGroup = (HashMap) dataSnapshot.getValue();
                        ArrayList<FriendUserDetail> memberDetails = (ArrayList<FriendUserDetail>) mapGroup.get("friendUsers");
                        String json = new Gson().toJson(memberDetails);
                        Log.e("Tag", "json---   " + json);
                        try {
                            JSONArray jsonArray = new JSONArray(json);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                if (object != null) {
                                    FriendUserDetail friendUserDetail = new FriendUserDetail();
                                    if (!object.optString("friendName").isEmpty()) {
                                        friendUserDetail.setFriendName(object.optString("friendName"));
                                    }
                                    if (!object.optString("friendPhoneNumber").isEmpty()) {
                                        friendUserDetail.setFriendPhoneNumber(object.optString("friendPhoneNumber"));
                                    }
                                    if (!object.optString("friendImage").isEmpty()) {
                                        friendUserDetail.setFriendImage(object.optString("friendImage"));
                                    }
                                    groupModel.getFriendUsers().add(friendUserDetail);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        groupModel.setGroud_name((String) mapGroup.get("group_name"));
                        groupModel.setAdmin((String) mapGroup.get("admin"));
                        groupModel.setRoom_id((String) mapGroup.get("room_id"));
                        groupModel.setId((String) mapGroup.get("id"));
                        groupModel.setGroupImage((String) mapGroup.get("groupImage"));
                        RealamDatabase.getInstance().saveTheListOfGroupDatainsideTheDatabase(groupModel);
                        FirebaseDataSingleton.getInstance(activity).upDateGroup(groupModel);
                        firebaseDataUpdatedInterface.onGroupUpdate();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    public void deleteGroup(final GroupModel group, final String groupId, final int index, final ListPeopleAdapter.OnCompletionListener onCompletionListener) {
        Log.e("Tag", "-----------" + group.getId());
        if (index == group.getFriendUsers().size()) {
            FirebaseDatabase.getInstance().getReference().child("group/" + groupId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            RealamDatabase.getInstance().removeGroupMemberOnIdBases(group.getId());
                            onCompletionListener.onComplete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
            ;
        } else {
            FirebaseDatabase.getInstance().getReference().child("users/" + group.getFriendUsers().get(index).getFriendPhoneNumber() + "/group/" + groupId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            deleteGroup(group, groupId, index + 1, onCompletionListener);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

    }


    public void sendGroupProfilePicToFirebaseServer(Context context, Uri contentUri, final String id, final GroupProfileFragment.ImageUploadResultInterface imageUploadResultInterface) {
        if (contentUri != null) {
            final StorageReference ref;
            ref = storageReference.child("users").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number).child(System.currentTimeMillis() + "");
            final UploadTask uploadTask = ref.putFile(contentUri);
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uploadTask.cancel();
                    dialog.dismiss();
                }
            });
            progressDialog.show();
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Hiding the progressDialog after done uploading.
                    progressDialog.dismiss();
                    // Getting image name from EditText and store into string variable.
                    String TempImageName = "temp";

                    // Showing toast message after done uploading.
                    Toast.makeText(activity, "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                    String uploaded_Image_Url = taskSnapshot.getDownloadUrl().toString();
                    mDatabase
                            .child("group")
                            .child(id)
                            .child("groupImage")
                            .setValue(uploaded_Image_Url);
                    imageUploadResultInterface.imageUploaded(uploaded_Image_Url);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    public void leaveGroup(ArrayList<GroupModel> listGroup, String groupId, int position, String phone_number, ListPeopleAdapter.OnCompletionListener onCompletionListener) {

        int correctIndex = 0;

        for (int i = 0; i < listGroup.get(position).getFriendUsers().size(); i++) {
            if (listGroup.get(position).getFriendUsers().get(i).getFriendPhoneNumber().equalsIgnoreCase(phone_number)) {
                correctIndex = i;
            }
        }

        mDatabase.child("group")
                .child(groupId)
                .child("friendUsers")
                .child(String.valueOf(correctIndex))
                .removeValue();
        mDatabase.child("users").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number)
                .child("group").child(groupId).removeValue();
    }

    public void leaveGroupFromGroupProfile(GroupModel groupModel, final GroupProfileFragment.LeaveGroupResultInterface leaveGroupResultInterface) {
        int correctIndex = 0;


        for (int i = 0; i < groupModel.getFriendUsers().size(); i++) {
            if (groupModel.getFriendUsers().get(i).getFriendPhoneNumber().equalsIgnoreCase(FirebaseDataSingleton.getInstance(instance.context).getUser().phone_number)) {
                correctIndex = i;

            }
        }

        mDatabase.child("group")
                .child(groupModel.getId())
                .child("friendUsers")
                .child(String.valueOf(correctIndex))
                .removeValue();

        mDatabase.child("users").child(FirebaseDataSingleton.getInstance(activity).getUser().phone_number)
                .child("group").child(groupModel.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                leaveGroupResultInterface.leaveGroup();
            }
        });
    }

    public void deleteGroupFromGroupProfile(final GroupModel groupModel, final int i, final GroupProfileFragment.DeleteGroupResultInterface deleteGroupResultInterface) {

        if (i == groupModel.getFriendUsers().size()) {
            FirebaseDatabase.getInstance().getReference().child("group/" + groupModel.getId()).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            RealamDatabase.getInstance().removeGroupMemberOnIdBases(groupModel.getId());
                            deleteGroupResultInterface.deleteGroup();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
            ;
        } else {
            FirebaseDatabase.getInstance().getReference().child("users/" + groupModel.getFriendUsers().get(i).getFriendPhoneNumber() + "/group/" + groupModel.getId()).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            deleteGroupFromGroupProfile(groupModel, i + 1, deleteGroupResultInterface);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

    }
}
