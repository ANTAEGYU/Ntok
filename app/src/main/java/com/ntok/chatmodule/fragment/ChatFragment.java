package com.ntok.chatmodule.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ntok.chatmodule.ImageDisplayer.ApplicationUtil;
import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.AllFriendListAdapter;
import com.ntok.chatmodule.adapter.ListMessageAdapter;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.backend.sqlite_database.MessageDB;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.interfaces.AddMessageInterface;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.interfaces.QBChatAttachClickListener;
import com.ntok.chatmodule.interfaces.QBLinkPreviewClickListener;
import com.ntok.chatmodule.interfaces.QBMediaPlayerListener;
import com.ntok.chatmodule.media.utils.Utils;
import com.ntok.chatmodule.media.video.ui.VideoPlayerActivity;
import com.ntok.chatmodule.model.AudioModelClass;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.model.ImageUploadInfo;
import com.ntok.chatmodule.model.MessageModel;
import com.ntok.chatmodule.model.VideoModelClass;
import com.ntok.chatmodule.utils.CircleImageView;
import com.ntok.chatmodule.utils.Constants;
import com.ntok.chatmodule.utils.CustomRecyclerView;
import com.ntok.chatmodule.utils.ImagePicker;
import com.ntok.chatmodule.utils.Lg;
import com.ntok.chatmodule.utils.LocationConstants;
import com.ntok.chatmodule.utils.LocationUtils;
import com.ntok.chatmodule.utils.Utility;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by Sonam on 08-05-2018.
 */

public class ChatFragment extends RootFragment implements View.OnClickListener, AddMessageInterface, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    public static String roomId;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public GroupModel groupModel;
    public Bitmap bitmapAvataUser;

    private CustomRecyclerView recyclerChat;
    private ListMessageAdapter chatAdapter;
    private CircleImageView btnSend;
    private ImageButton btnAttach;
    public CardView layout_attach;
    private ProgressBar progressBar;
    private EditText editWriteMessage;
    private String messageID;
    private FriendUser friend;
    private LinearLayoutManager linearLayoutManager;
    private int RESULT_OK = -1;
    private File mFile;
    private Bitmap selectedImage;
    private String imageFilePath;
    String TAG = "ChatFragment";
    public ArrayList<GroupModel> listGroupRoomIds = new ArrayList<>();


    // Google listener
    private GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public Location myLocation;
    private static final int REQUEST_APP_SETTINGS = 168;

    public void setGroupModel(GroupModel groupModel) {
        this.groupModel = groupModel;
        if (groupModel != null)
            this.roomId = groupModel.getRoom_id();
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public void setFriend(FriendUser friend) {
        this.friend = friend;
        if (friend != null) {
            this.roomId = friend.room_id;

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);
        initViews(rootView);
        ((MainActivity) getActivity()).setTabVisibilty();
        initApis();
        return rootView;
    }

    public void initApis() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    public void initViews(View rootView) {
        btnSend = rootView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        btnAttach = rootView.findViewById(R.id.btnAttach);
        btnAttach.setOnClickListener(this);

        progressBar = rootView.findViewById(R.id.progress_bar);
        layout_attach = rootView.findViewById(R.id.layout_attach);
        layout_attach.setVisibility(View.GONE);

        rootView.findViewById(R.id.llImageView).setOnClickListener(this);
        rootView.findViewById(R.id.llShareLocation).setOnClickListener(this);
        rootView.findViewById(R.id.llShareContact).setOnClickListener(this);
        rootView.findViewById(R.id.llShareAudio).setOnClickListener(this);
        rootView.findViewById(R.id.llShareVideo).setOnClickListener(this);
        rootView.findViewById(R.id.llShareCameraImage).setOnClickListener(this);

        editWriteMessage = (EditText) rootView.findViewById(R.id.editWriteMessage);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerChat = rootView.findViewById(R.id.recyclerChat);
        recyclerChat.setLayoutManager(linearLayoutManager);
        recyclerChat.setNestedScrollingEnabled(false);

        editWriteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_attach.setVisibility(View.GONE);
            }
        });

        /*
         * Including the touch listener
         * */

        recyclerChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (layout_attach.getVisibility() == View.VISIBLE) {
                        layout_attach.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        });

        ((MainActivity) getActivity()).iconForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchContactFragment searchContactFragment = new SearchContactFragment();
                searchContactFragment.setShowFriendList(true);
                searchContactFragment.setItemClick(new AllFriendListAdapter.ItemClick() {
                    @Override
                    public void onItemClickListener(FriendUser user, View view) {
                        Log.e("Tag",user.toString());
                        forwardMessage(user);
                        getActivity().onBackPressed();

                    }
                });
                ((MainActivity) getActivity()).addFragment(searchContactFragment, "searchContactFragment");
            }
        });
        ((MainActivity) getActivity()).iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage();
            }
        });
        ((MainActivity) getActivity()).iconCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<Integer, MessageModel> entry : chatAdapter.selected.entrySet()) {
                    MessageModel messageModel = entry.getValue();
                    copyMessage(messageModel.getBody());
                    break;
                }

            }
        });

        if (friend != null || groupModel != null) {

            if (FirebaseDataSingleton.getInstance(getContext()).getMessageListHashMap().get(roomId) == null) {
                FirebaseDataSingleton.getInstance(getContext()).getMessageListHashMap().put(roomId, MessageDB.getInstance(getContext()).getListMessage(roomId));
            }
            long timeStamp = 0;
            try {
                timeStamp = FirebaseDataSingleton.getInstance(getActivity()).getMessageListHashMap().get(roomId).get(FirebaseDataSingleton.getInstance(getActivity()).getMessageListHashMap().get(roomId).size() - 1).getTimestamp();
                progressBar.setVisibility(View.GONE);
            } catch (Exception ex) {

            }
            FireBaseSingleton.getInstance(getContext()).getUserMessage(roomId, timeStamp + 1, this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 10000);
            chatAdapter = new ListMessageAdapter(getActivity(), bitmapAvataFriend, friend, groupModel, bitmapAvataUser, ChatFragment.this);
            chatAdapter.setMessageTextViewLinkClickListener(new QBChatAttachClickListener() {
                @Override
                public void onLinkClicked(MessageModel linkText, int positionInAdapter) {
                    Log.d(TAG, "onLinkClicked: linkText - " + linkText
                            + " linkType - " + linkText
                            + " positionInAdapter - " + positionInAdapter);
                    if (!chatAdapter.selected.isEmpty()) {
                        if (chatAdapter.selected.containsKey(positionInAdapter)) {
                            chatAdapter.selected.remove(positionInAdapter);
                        } else {
                            chatAdapter.selected.put(positionInAdapter, chatAdapter.getItem(positionInAdapter));
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                }

            }, false);
            chatAdapter.setAttachImageClickListener(new QBChatAttachClickListener() {
                @Override
                public void onLinkClicked(MessageModel imageAttach, int positionInAdapter) {
                    Log.d(TAG, "setAttachImageClickListener: positionInAdapter - " + positionInAdapter);
                    Log.d(TAG, "setAttachImageClickListener: attachment - " + imageAttach.getImageModel().getImageURL());
                    if (!chatAdapter.selected.isEmpty()) {
                        if (chatAdapter.selected.containsKey(positionInAdapter)) {
                            chatAdapter.selected.remove(positionInAdapter);
                        } else {
                            chatAdapter.selected.put(positionInAdapter, chatAdapter.getItem(positionInAdapter));
                        }
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        getImageTypeMessages(imageAttach);
                    }


                }
            });
            chatAdapter.setAttachLocationClickListener(new QBChatAttachClickListener() {
                @Override
                public void onLinkClicked(MessageModel locationAttach, int positionInAdapter) {
                    if (!chatAdapter.selected.isEmpty()) {
                        if (chatAdapter.selected.containsKey(positionInAdapter)) {
                            chatAdapter.selected.remove(positionInAdapter);
                        } else {
                            chatAdapter.selected.put(positionInAdapter, chatAdapter.getItem(positionInAdapter));
                        }
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        Pair<Double, Double> latLng = LocationUtils.getLatLngFromJson(locationAttach.getLocation());
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + latLng.first + "," + latLng.second));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
                        getActivity().startActivity(i);
                        Log.d(TAG, "setAttachImageClickListener: positionInAdapter - " + positionInAdapter);
                        Log.d(TAG, "setAttachImageClickListener: attachment - " + locationAttach.getLocation());
                    }

                }

            });

            chatAdapter.setLinkPreviewClickListener(new QBLinkPreviewClickListener() {
                @Override
                public void onLinkPreviewClicked(String link, MessageModel linkPreview, int position) {
                    Log.d(TAG, "onLinkPreviewClicked: link = " + link + ", position = " + position);
                }

                @Override
                public void onLinkPreviewLongClicked(String link, MessageModel linkPreview, int position) {
                    Log.d(TAG, "onLinkPreviewLongClicked: link = " + link + ", position = " + position);
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("link", link);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Link " + link + " was copied to clipboard", Toast.LENGTH_LONG).show();
                }
            }, false);


            chatAdapter.setAttachAudioClickListener(new QBChatAttachClickListener() {
                @Override
                public void onLinkClicked(MessageModel audioAttach, int positionInAdapter) {
                    if (!chatAdapter.selected.isEmpty()) {
                        if (chatAdapter.selected.containsKey(positionInAdapter)) {
                            chatAdapter.selected.remove(positionInAdapter);
                        } else {
                            chatAdapter.selected.put(positionInAdapter, audioAttach);
                        }
                        chatAdapter.notifyDataSetChanged();
                    } else {
                    }
                    Log.d(TAG, "onClick: audioAttach - " + audioAttach + " positionInAdapter = " + positionInAdapter);
                }

            });

            chatAdapter.setAttachVideoClickListener(new QBChatAttachClickListener() {
                @Override
                public void onLinkClicked(MessageModel videoAttach, int positionInAdapter) {
                    Log.d(TAG, "onClick: videoAttach - " + videoAttach + " positionInAdapter = " + positionInAdapter);
                    Log.d(TAG, "onClick: videoAttach - " + videoAttach + " positionInAdapter = " + positionInAdapter);
                    if (chatAdapter.selected.isEmpty()) {
                        if (videoAttach.getVideoModelClass().getLocalVideoLink() != null) {
                            VideoPlayerActivity.start(getActivity(), Uri.parse(videoAttach.getVideoModelClass().getLocalVideoLink()));
                        } else {
                            Toast.makeText(getActivity(), "Fie does not exsit", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (chatAdapter.selected.containsKey(positionInAdapter)) {
                            chatAdapter.selected.remove(positionInAdapter);
                        } else {
                            chatAdapter.selected.put(positionInAdapter, videoAttach);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                }

            });
            chatAdapter.setAttachContactClickListener(new QBChatAttachClickListener() {
                @Override
                public void onLinkClicked(MessageModel attachment, int positionInAdapter) {
                    if (chatAdapter.selected.isEmpty()) {
                        Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
                        addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                        addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, attachment.getContact().getPhone_number());
                        addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, attachment.getContact().getName());
                        startActivity(addContactIntent);

                    } else {
                        if (chatAdapter.selected.containsKey(positionInAdapter)) {
                            chatAdapter.selected.remove(positionInAdapter);
                        } else {
                            chatAdapter.selected.put(positionInAdapter, attachment);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                }
            });
            chatAdapter.setMediaPlayerListener(new QBMediaPlayerListener() {
                @Override
                public void onStart(Uri uri) {
                    Log.d(TAG, "onStart uri= " + uri);
                }

                @Override
                public void onResume(Uri uri) {
                    Log.d(TAG, "onResume");
                }

                @Override
                public void onPause(Uri uri) {
                    Log.d(TAG, "onPause");
                }

                @Override
                public void onStop(Uri uri) {
                    Log.d(TAG, "onStop");
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }
            });
            recyclerChat.setAdapter(chatAdapter);
            if (FirebaseDataSingleton.getInstance(getContext()).getMessageListHashMap().get(friend != null ? friend.room_id : groupModel.getRoom_id()).size() > 1)
                linearLayoutManager.scrollToPosition(FirebaseDataSingleton.getInstance(getContext()).getMessageListHashMap().get(friend != null ? friend.room_id : groupModel.getRoom_id()).size() - 1);
        }
        initToolBar();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FireBaseSingleton.getInstance(getContext()).removeChatChildEventListener(roomId);

    }

    public void initToolBar() {
        try {
            ((MainActivity) getActivity()).backButton.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).userImage.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).addUser.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).iconSelection.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconSearch.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconRefresh.setVisibility(View.GONE);
            ((MainActivity) getActivity()).onlineStatus.setVisibility(View.GONE);
            editWriteMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                      btnSend.performClick();
                    }
                    return false;
                }
            });



            if (friend != null && friend.username != null) {
                    ((MainActivity) getActivity()).title.setText(friend != null ? friend.username : groupModel.getGroup_name());
          } else {
                ((MainActivity) getActivity()).title.setText(groupModel.getGroup_name());
            }
            if (friend != null && friend.userImage != null) {
                ImageDisplayer.displayImage(friend.userImage, ((MainActivity) getActivity()).userImage, null, getActivity());
            } else if (groupModel != null)
                Glide.with(getActivity()).load(groupModel.getGroupImage()).placeholder(R.drawable.default_user_black).error(R.drawable.default_user_black).into(((MainActivity) getActivity()).userImage);

            ((MainActivity) getActivity()).userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (friend != null)
                        ((MainActivity) getActivity()).showFriendProfile(friend);
                    else if (groupModel != null)
                        ((MainActivity) getActivity()).showGroupProfile(groupModel);
                }
            });
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
    }

    @Override
    public void onRefreshData() {

    }

    private void chooseImage() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(getActivity(), "Gallery permission", "Need read or write permission to share your image", new OnAlertPopupClickListener() {
                                @Override
                                public void onOkButtonpressed() {
                                    goToSettings();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                        } else if (!report.areAllPermissionsGranted()) {
                            chooseImage();
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            ChatFragment.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST_For_chat_Contact);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }


    @Override
    public boolean onBackPressed() {
        try {
            if (layout_attach.getVisibility() == View.VISIBLE) {
                layout_attach.setVisibility(View.GONE);
                Utility.hideKeyboard(getActivity());
                return false;
            }
            ((MainActivity) getActivity()).popAllTheFragment();
        } catch (NullPointerException ex) {
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        listGroupRoomIds.clear();
        listGroupRoomIds.addAll(RealamDatabase.getInstance().getAllListOfGroupFormDB());
        if (view.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                editWriteMessage.setText("");
                MessageModel newMessage = new MessageModel();
                newMessage.setBody(content);
                newMessage.setFrom(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
                newMessage.setTo(friend != null ? friend.phone_number : groupModel.getRoom_id());
                newMessage.setTimestamp(System.currentTimeMillis());
                newMessage.setType(MessageModel.TEXT_TYPE);
                if (groupModel != null) {
                    if (chechTheCondition()) {
                        newMessage.setIsGroup("true");
                        FireBaseSingleton.getInstance(getActivity()).addMessageToFireBase(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number, friend != null ? friend.userID : roomId, roomId, newMessage, isGroupMessage(), groupModel != null ? groupModel.getFriendUsers() : null);
                    } else {
                        Utility.showToast(getActivity(), "Oops! Admin has been deleted the group.");
                        getActivity().onBackPressed();
                    }

                } else {
                    newMessage.setIsGroup("false");
                    FireBaseSingleton.getInstance(getActivity()).addMessageToFireBase(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number, friend != null ? friend.userID : roomId, roomId, newMessage, isGroupMessage(), groupModel != null ? groupModel.getFriendUsers() : null);
                }
            }
        } else if (view.getId() == R.id.btnAttach) {
            if(layout_attach.getVisibility() == View.VISIBLE){
                Utility.hideKeyboard(getActivity());
                layout_attach.setVisibility(View.GONE);
            }else {
                Utility.hideKeyboard(getActivity());
                layout_attach.setVisibility(View.VISIBLE);
            }

        } else if (view.getId() == R.id.llImageView) {
            layout_attach.setVisibility(View.GONE);
            if (groupModel != null) {
                if (chechTheCondition()) {
                    chooseImage();
                } else {
                    Utility.showToast(getActivity(), "Oops! Admin has been deleted the group.");
                    getActivity().onBackPressed();
                }
            } else {
                chooseImage();
            }

        } else if (view.getId() == R.id.llShareLocation) {
            layout_attach.setVisibility(View.GONE);


            if (mGoogleApiClient.isConnected()) {
                checkConnection();
            } else {
                mGoogleApiClient.connect();
            }


        } else if (view.getId() == R.id.llShareContact) {
            layout_attach.setVisibility(View.GONE);
            if (groupModel != null) {
                if (chechTheCondition()) {
                    needContactsPermisstion();
                } else {
                    Utility.showToast(getActivity(), "Oops! Admin has been deleted the group.");
                    getActivity().onBackPressed();
                }
            } else {
                needContactsPermisstion();
            }
        } else if (view.getId() == R.id.llShareAudio) {
            layout_attach.setVisibility(View.GONE);
            if (groupModel != null) {
                if (chechTheCondition()) {
                    checkAudioPermisiion();
                } else {
                    Utility.showToast(getActivity(), "Oops! Admin has been deleted the group.");
                    getActivity().onBackPressed();
                }
            } else {
                checkAudioPermisiion();
            }
        } else if (view.getId() == R.id.llShareVideo) {
            layout_attach.setVisibility(View.GONE);
            if (groupModel != null) {
                if (chechTheCondition()) {
                    checkVideo();
                } else {
                    Utility.showToast(getActivity(), "Oops! Admin has been deleted the group.");
                    getActivity().onBackPressed();
                }
            } else {
                checkVideo();
            }

        } else if (view.getId() == R.id.llShareCameraImage) {
            layout_attach.setVisibility(View.GONE);
            if (groupModel != null) {
                if (chechTheCondition()) {
                    chackCamerapermission();
                } else {
                    Utility.showToast(getActivity(), "Oops! Admin has been deleted the group.");
                    getActivity().onBackPressed();
                }
            } else {
                chackCamerapermission();
            }
        }
    }


    public void needContactsPermisstion() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(getActivity(), "Contacts", "Need contact permission to start the chat", new OnAlertPopupClickListener() {
                                @Override
                                public void onOkButtonpressed() {
                                    goToSettings();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                        } else if (!report.areAllPermissionsGranted()) {
                            needContactsPermisstion();
                        } else {
                            shareContact();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    /**
     * Need gallery permission to get audio file
     */
    private void checkAudioPermisiion() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(getActivity(), "Gallery permission", "Need storage permission to share your video", new OnAlertPopupClickListener() {
                                @Override
                                public void onOkButtonpressed() {
                                    goToSettings();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                        } else if (!report.areAllPermissionsGranted()) {
                            checkAudioPermisiion();
                        } else {
                            Intent intent_upload = new Intent();
                            intent_upload.setType("audio/*");
                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                            ChatFragment.this.startActivityForResult(intent_upload, Constants.PICK_AUDIO_FILE_REQUEST);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    /**
     * Check Camera for permission
     */
    private void chackCamerapermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(getActivity(), "Camera & Storage permissions", "Need camera and gallery permissions to share your photos", new OnAlertPopupClickListener() {
                                @Override
                                public void onOkButtonpressed() {
                                    goToSettings();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                        } else if (!report.areAllPermissionsGranted()) {
                            chackCamerapermission();
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Intent chooseImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (chooseImageIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    //Create a file to store the image
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        // Error occurred while creating the File
                                    }
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.ntok.chatmodule.provider", photoFile);
                                        chooseImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                photoURI);
                                        ChatFragment.this.startActivityForResult(chooseImageIntent,
                                                Constants.REQUEST_IMAGE_CAPTURE);
                                    }
                                }
                            } else {
                                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
                                ChatFragment.this.startActivityForResult(chooseImageIntent, Constants.REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /*
     * Create a image file
     * */
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                "",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    /**
     * Share contact
     */
    private void shareContact() {
        MessageModel newMessage = new MessageModel();
        newMessage.setFrom(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
        newMessage.setTo(friend != null ? friend.phone_number : groupModel.getRoom_id());
        newMessage.setTimestamp(System.currentTimeMillis());
        newMessage.setType(MessageModel.Contact_Type);
        newMessage.setBody(MessageModel.Contact_Type);
        ((MainActivity) getActivity()).addContactFragmentNew(isGroupMessage(), true, newMessage, groupModel != null ? groupModel.getFriendUsers() : null);


    }

    /**
     * Check Video for permission
     */

    private void checkVideo() {

        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(getActivity(), "Gallery permission", "Need storage permission to share your video", new OnAlertPopupClickListener() {
                                @Override
                                public void onOkButtonpressed() {
                                    goToSettings();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                        } else if (!report.areAllPermissionsGranted()) {
                            checkVideo();
                        } else {
                            Intent intent_upload = new Intent();
                            intent_upload.setType("video/*");
                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                            ChatFragment.this.startActivityForResult(intent_upload, Constants.PICK_VIDEO_FILE_REQUEST);

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


    }

    public boolean isGroupMessage() {
        return groupModel != null;
    }


    @Override
    public void onAddMessage() {
        progressBar.setVisibility(View.GONE);
        chatAdapter.notifyDataSetChanged();
        if (FirebaseDataSingleton.getInstance(getContext()).getMessageListHashMap().get(friend != null ? friend.room_id : groupModel.getRoom_id()).size() > 1)
            linearLayoutManager.scrollToPosition(FirebaseDataSingleton.getInstance(getContext()).getMessageListHashMap().get(friend != null ? friend.room_id : groupModel.getRoom_id()).size() - 1);
    }

    @Override
    public void onMessageDelete() {
        progressBar.setVisibility(View.GONE);
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    public void attachFile(Uri uri, String type) {
        MessageModel newMessage = new MessageModel();
        newMessage.setFrom(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
        newMessage.setTo(friend != null ? friend.phone_number : groupModel.getRoom_id());
        newMessage.setTimestamp(System.currentTimeMillis());
        Bitmap bitmap = null;

        if (type.equalsIgnoreCase(MessageModel.IMAGE_TYPE)) {
            newMessage.setType(MessageModel.IMAGE_TYPE);
            ImageUploadInfo imageUploadInfo = new ImageUploadInfo("Image", uri.toString());
            newMessage.setImageModel(imageUploadInfo);
            newMessage.setBody(MessageModel.IMAGE_TYPE);
        } else if (type.equalsIgnoreCase(MessageModel.AUDIO_TYPE)) {
            String filePath;
            filePath = ApplicationUtil.getFilePathFromURI(getActivity(), uri);
            if (filePath == null) {
                filePath = uri.getPath();
            }
            File file = new File(filePath);
            // Get the number of bytes in the file
            long sizeInBytes = file.length();
            //transform in MB
            long sizeInMb = sizeInBytes / (1024 * 1024);
            if (sizeInMb > Constants.MAX_FILE_SIZE_TO_UPLOAD) {
                layout_attach.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Can not upload file larger than " + Constants.MAX_FILE_SIZE_TO_UPLOAD + " MB", Toast.LENGTH_LONG).show();
                return;
            } else {
                long time = Utils.getDurationFromAttach(uri, getContext());
                AudioModelClass audioModelClass = new AudioModelClass();
                audioModelClass.setTime(time);
                newMessage.setAudioModelClass(audioModelClass);
                newMessage.setType(MessageModel.AUDIO_TYPE);
                newMessage.setBody(MessageModel.AUDIO_TYPE);
            }


        } else if (type.equalsIgnoreCase(MessageModel.VIDEO_TYPE)) {
            String filePath;
            filePath = ApplicationUtil.getFilePathFromURI(getContext(), uri);
            File file = new File(filePath);
            // Get the number of bytes in the file
            long sizeInBytes = file.length();
            //transform in MB
            long sizeInMb = sizeInBytes / (1024 * 1024);
            if (sizeInMb > Constants.MAX_FILE_SIZE_TO_UPLOAD) {
                layout_attach.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Can not upload file larger than " + Constants.MAX_FILE_SIZE_TO_UPLOAD + " MB", Toast.LENGTH_LONG).show();
                return;
            } else {
                File videoFile = new File(filePath);
                bitmap = ThumbnailUtils.createVideoThumbnail(videoFile.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

                long time = Utils.getDurationFromAttach(uri, getContext());
                VideoModelClass videoModelClass = new VideoModelClass();
                videoModelClass.setTime(time);
                newMessage.setVideoModelClass(videoModelClass);
                newMessage.setType(MessageModel.VIDEO_TYPE);
                newMessage.setBody(MessageModel.VIDEO_TYPE);
            }
        }


        FireBaseSingleton.getInstance(getContext()).UploadFileToFirebase(uri, newMessage, friend != null ? friend.room_id : groupModel.getRoom_id(), bitmap, isGroupMessage(), groupModel != null ? groupModel.getFriendUsers() : null, null);
        layout_attach.setVisibility(View.GONE);

    }


    /**
     * This method is used to share the location of user by chat
     */

    public void shareLocation() {
        MapViewFragment mapViewFragment = new MapViewFragment(myLocation);
        mapViewFragment.setShareLocationInterface(new MapViewFragment.shareLocationInterface() {
            @Override
            public void shareLocation() {
                if (Utility.isOnline(getActivity())) {
                    if (myLocation != null) {
                        MessageModel newMessage = new MessageModel();
                        newMessage.setFrom(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
                        newMessage.setTo(friend != null ? friend.phone_number : groupModel.getRoom_id());
                        newMessage.setType(MessageModel.LOCATION_TYPE);
                        newMessage.setTimestamp(System.currentTimeMillis());
                        newMessage.setBody(MessageModel.LOCATION_TYPE);
                        JSONObject jsonObj = new JSONObject();
                        try {
                            jsonObj.put("latitude", myLocation.getLatitude()); // Set the first name/pair
                            jsonObj.put("longitude", myLocation.getLongitude());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        newMessage.setLocation(jsonObj.toString());

                        if (groupModel != null) {
                            if (chechTheCondition()) {
                                newMessage.setIsGroup("true");
                                FireBaseSingleton.getInstance(getActivity()).addMessageToFireBase(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number, friend != null ? friend.userID : roomId, roomId, newMessage, isGroupMessage(), groupModel != null ? groupModel.getFriendUsers() : null);
                            } else {
                                Utility.showToast(getActivity(), "Oops! Admin has been deleted the group.");
                                getActivity().onBackPressed();
                            }
                        } else {
                            newMessage.setIsGroup("false");
                            FireBaseSingleton.getInstance(getActivity()).addMessageToFireBase(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number, friend != null ? friend.userID : roomId, roomId, newMessage, isGroupMessage(), groupModel != null ? groupModel.getFriendUsers() : null);
                        }
                        layout_attach.setVisibility(View.GONE);
                    }
                } else {
                    Utility.showAltertPopup(getActivity(), "Internet Connection!", getResources().getString(R.string.no_internet_image), new OnAlertPopupClickListener() {
                        @Override
                        public void onOkButtonpressed() {

                        }

                        @Override
                        public void onCancelButtonPressed() {

                        }
                    });
                }
            }
        });
        ((MainActivity) getActivity()).addFragment(mapViewFragment, "MapViewFragment");


    }

    /**
     * Search messages list for image type messages
     *
     * @return the list of found images (image name)
     */
    private void getImageTypeMessages(MessageModel messageModel) {
        int selectedPosition = 0;
        ArrayList<MessageModel> messageList = FirebaseDataSingleton.getInstance(getContext()).getMessageListHashMap().get(friend != null ? friend.room_id : groupModel.getRoom_id());
        if (messageList == null || messageList.isEmpty()) {
            return;
        }

        ArrayList<String> imagesList = new ArrayList<String>();
        for (MessageModel messageObject : messageList) {
            if (messageObject.getType().equalsIgnoreCase(MessageModel.IMAGE_TYPE)) {
                imagesList.add(messageObject.getImageModel().getImageURL());
                if (messageModel.getTimestamp() == messageObject.getTimestamp()) {
                    selectedPosition = imagesList.size() - 1;
                }
            }
        }
        Log.e("Tag", "imagesList" + imagesList.toString());
        showFullScreenImageForUrl(imagesList, selectedPosition, MessageModel.IMAGE_TYPE);
    }

    private void showFullScreenImageForUrl(ArrayList<String> arrayList, int index, String imageType) {

        if (arrayList != null && arrayList.size() > 0) {
            FullScreenImagesViewPagesGallery gallaryDialog = new FullScreenImagesViewPagesGallery().init(getActivity(), this, index, arrayList, imageType);
            ((MainActivity) getActivity()).addFragment(gallaryDialog, "FullScreenImagesViewPagesGallery");
        }
    }

    public void copyMessage(String text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
        chatAdapter.selected = new HashMap<>();
        chatAdapter.notifyDataSetChanged();
        ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);

    }

    public void forwardMessage(FriendUser friend) {
        for (Map.Entry<Integer, MessageModel> entry : chatAdapter.selected.entrySet()) {
            MessageModel messageModel = entry.getValue();
            MessageModel newMessage = new MessageModel();
            newMessage.setBody(messageModel.getBody());
            newMessage.setVideoModelClass(messageModel.getVideoModelClass());
            newMessage.setAudioModelClass(messageModel.getAudioModelClass());
            newMessage.setImageModel(messageModel.getImageModel());
            newMessage.setContact(messageModel.getContact());
            newMessage.setLocation(messageModel.getLocation());
            newMessage.setFrom(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
            newMessage.setTo(friend.phone_number);
            newMessage.setTimestamp(System.currentTimeMillis());
            newMessage.setType(messageModel.getType());
            newMessage.setIsGroup("false");
            FireBaseSingleton.getInstance(getActivity()).addMessageToFireBase(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number, friend != null ? friend.userID : roomId, friend.room_id, newMessage, false, null);
        }
        chatAdapter.selected = new HashMap<>();
        chatAdapter.notifyDataSetChanged();
        ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);


    }

    public void deleteMessage() {
        for (Map.Entry<Integer, MessageModel> entry : chatAdapter.selected.entrySet()) {
            MessageModel messageModel = entry.getValue();
            FireBaseSingleton.getInstance(getActivity()).deleteMessage(roomId, messageModel.getId());
            chatAdapter.selected = new HashMap<>();
            chatAdapter.notifyDataSetChanged();
            ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("MainActivity", "ApiClient: OnConnected");
        checkConnection();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ChatFragment", connectionResult.toString());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        shareLocation();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    /**
     * Check for the location service with the permission.
     * If user allow the permission then user able to share his current location by chat.
     */

    public void checkConnection() {
        // check if permission is granted

        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(getActivity(), "Location permission", "Need location permission to share your current location", new OnAlertPopupClickListener() {
                                @Override
                                public void onOkButtonpressed() {
                                    goToSettings();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                        } else if (!report.areAllPermissionsGranted()) {
                            checkConnection();
                        } else {
                            GetTheCurrentLocation();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    /**
     * Get the current latitude and longitude of user ....
     */
    private void GetTheCurrentLocation() {
        // permission has been granted, continue as usual
        //check location is enable or not
        if (LocationConstants.isLocationEnabled(getActivity())) {
            startLocationUpdates();
            ;
        } else {
            // show dialog if location is not enavble
            LocationConstants.enableLocationDialog(getActivity());
        }
    }


    /**
     * User will be redirected to setting screen
     */

    private void goToSettings() {

        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ChatFragment.this.startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE_REQUEST_For_chat_Contact) {
            if (Utility.isOnline(getActivity())) {
                if (data != null && data.getData() != null) {
                    Uri filePath = data.getData();
                    attachFile(filePath, MessageModel.IMAGE_TYPE);
                }
            } else {
                Utility.showAltertPopup(getActivity(), "Internet Connection!", getResources().getString(R.string.no_internet_image), new OnAlertPopupClickListener() {
                    @Override
                    public void onOkButtonpressed() {
                    }

                    @Override
                    public void onCancelButtonPressed() {
                    }
                });
            }
        } else if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            if (Utility.isOnline(getActivity())) {
                File compressedImage = null;
                Uri contentUri = null;
                try {
                    compressedImage = new Compressor(getActivity())
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(new File(imageFilePath));
                    contentUri = Uri.fromFile(compressedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("TAg", imageFilePath + "          " + new File(imageFilePath).getAbsolutePath() + "           " + contentUri);
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent mediaScanIntent = new Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(contentUri);
                            getActivity().sendBroadcast(mediaScanIntent);
                        } else {
                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                    contentUri));
                        }

                        attachFile(contentUri, MessageModel.IMAGE_TYPE);
                    } else {
                        selectedImage = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                        saveImage(selectedImage);
                    }

                }
            } else {
                Utility.showAltertPopup(getActivity(), "Internet Connection!", getResources().getString(R.string.no_internet_image), new OnAlertPopupClickListener() {
                    @Override
                    public void onOkButtonpressed() {
                    }

                    @Override
                    public void onCancelButtonPressed() {
                    }
                });
            }

        } else if (requestCode == Constants.PICK_VIDEO_FILE_REQUEST) {
            if (Utility.isOnline(getActivity())) {
                if (data != null && data.getData() != null) {
                    Uri filePath = data.getData();
                    attachFile(filePath, MessageModel.VIDEO_TYPE);
                }
            } else {
                Utility.showAltertPopup(getActivity(), "Internet Connection!", getResources().getString(R.string.no_internet_video), new OnAlertPopupClickListener() {
                    @Override
                    public void onOkButtonpressed() {
                    }

                    @Override
                    public void onCancelButtonPressed() {
                    }
                });
            }

        } else if (requestCode == Constants.PICK_AUDIO_FILE_REQUEST) {
            if (Utility.isOnline(getActivity())) {
                if (data != null && data.getData() != null) {
                    Uri filePath = data.getData();
                    attachFile(filePath, MessageModel.AUDIO_TYPE);
                }
            } else {
                Utility.showAltertPopup(getActivity(), "Internet Connection!", getResources().getString(R.string.no_internet_image), new OnAlertPopupClickListener() {
                    @Override
                    public void onOkButtonpressed() {
                    }

                    @Override
                    public void onCancelButtonPressed() {
                    }
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasGallerypermission(String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != getActivity().checkSelfPermission(permission))
                return false;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != getActivity().checkSelfPermission(permission))
                return false;
        return true;
    }


    /*
     * Save the image into gallery
     *
     * */
    public void saveImage(Bitmap selectedImage) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object

            int time = (int) (System.currentTimeMillis());
            Timestamp tsTemp = new Timestamp(time);
            String ts = tsTemp.toString();
            createFile(ts);
            FileOutputStream fo = new FileOutputStream(mFile);
            fo.write(baos.toByteArray());
            Uri contentUri = Uri.fromFile(mFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);
            } else {
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        contentUri));
            }
            attachFile(contentUri, MessageModel.IMAGE_TYPE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
     * Create a temporary file
     *
     * */
    private void createFile(String filename) {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Images";

        File projDir = new File(dirPath);

        if (!projDir.exists())
            projDir.mkdirs();


        mFile = new File(projDir, filename);
    }


    private boolean chechTheCondition() {
        boolean isTrue = false;
        if (listGroupRoomIds.size() > 0) {
            int index = -1;
            for (GroupModel groupModel : listGroupRoomIds) {
                if (groupModel.getRoom_id().equalsIgnoreCase(roomId)) {
                    index = 0;
                    isTrue = true;
                    break;
                } else {
                    index = 1;
                }
            }
            if (index == 1) {
                isTrue = false;
            }

        } else {
            isTrue = false;
        }
        return isTrue;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}


