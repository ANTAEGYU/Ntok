package com.ntok.chatmodule.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.LoginActivity;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.backend.sqlite_database.MessageDB;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.interfaces.OnTowButtonDialogInterface;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.User;
import com.ntok.chatmodule.sharedPreference.SharedPreference;
import com.ntok.chatmodule.utils.Constants;
import com.ntok.chatmodule.utils.Lg;
import com.ntok.chatmodule.utils.Utility;
import com.ntok.chatmodule.view.HeaderView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


@SuppressLint("ValidFragment")
public class UserProfileFragment extends RootFragment implements AppBarLayout.OnOffsetChangedListener {

    private HeaderView toolbarHeaderView;
    private HeaderView floatHeaderView;
    private AppBarLayout appBarLayout;
    private EditText tv_username;
    private EditText tv_about;
    private TextView tv_phone_number;
    private ImageButton edit_name;
    private ImageButton editAbout;
    private CardView btnDone;
    private boolean b = false;
    private LinearLayout mLiBtnLayout;
    private boolean isHideToolbarView = false;
    private ImageView ivUserGroupImage;
    private ImageView ivUserBackUpperButton;
    FriendUser friendUser;
    private Uri imagePath;
    private CardView logOut;


    @SuppressLint("ValidFragment")
    public UserProfileFragment(boolean b) {
        this.b = b;
    }

    public UserProfileFragment() {

    }

    public void setFriendUser(FriendUser friendUser) {
        this.friendUser = friendUser;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_profile_fragment, container, false);
        initViews(rootView);
        Utility.setMargins(((MainActivity) getActivity()).frame_content, 0, 0, 0, 0);
        ((MainActivity) getActivity()).setTabVisibilty();
        if (friendUser == null)
            setData(FirebaseDataSingleton.getInstance(getContext()).getUser());
        else
            setFriendData(friendUser);


        setHasOptionsMenu(false);
        initToolBar();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private void initViews(View rootView) {
        toolbarHeaderView = rootView.findViewById(R.id.toolbar_header_view);
        floatHeaderView = rootView.findViewById(R.id.float_header_view);
        appBarLayout = rootView.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
        tv_username = rootView.findViewById(R.id.tv_username);
        tv_about = rootView.findViewById(R.id.tv_about);
        tv_phone_number = rootView.findViewById(R.id.tv_phone_number);
        ivUserGroupImage = rootView.findViewById(R.id.user_group_image);
        ivUserBackUpperButton = rootView.findViewById(R.id.ic_back_button);
        edit_name = rootView.findViewById(R.id.edit_name);
        editAbout = rootView.findViewById(R.id.editAbout);
        btnDone = rootView.findViewById(R.id.btn_save);
        logOut = rootView.findViewById(R.id.btn_logout);
        mLiBtnLayout = rootView.findViewById(R.id.btn_layout);

        setUpTheLogoutView(logOut);


        if (b) {
            mLiBtnLayout.setVisibility(View.GONE);
            tv_username.setEnabled(false);
            tv_about.setEnabled(false);
        }
        else {
            mLiBtnLayout.setVisibility(View.VISIBLE);
            tv_username.setEnabled(true);
            tv_about.setEnabled(true);
        }


        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveView(btnDone, logOut);
                tv_username.requestFocus();
                int pos = tv_username.getText().length();
                tv_username.setSelection(pos);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(tv_username, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        editAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveView(btnDone, logOut);
                tv_about.requestFocus();
                int pos = tv_about.getText().length();
                tv_about.setSelection(pos);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(tv_about, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.logoutConfermation(getActivity(), new OnTowButtonDialogInterface() {
                    @Override
                    public void ok() {
                        SharedPreference.getInstance(getContext()).clearSharedPReference();
                        MessageDB.getInstance(getContext()).dropDB();
                        FirebaseDataSingleton.getInstance(getActivity()).deleteInstance();
                        FireBaseSingleton.getInstance(getActivity()).deleteAllInstance();
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void cancel() {

                    }
                });

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
            }
        });

        ivUserGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveView(btnDone, logOut);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionOfCameraAndStorage();
                } else {
                    chooseImage();
                }
            }
        });


        ivUserBackUpperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbarHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    private void checkPermissionOfCameraAndStorage() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(getActivity(), "Need all permission", "Need camera or read and write permission.", new OnAlertPopupClickListener() {
                                @Override
                                public void onOkButtonpressed() {
                                    goToSettings();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                        } else if (!report.areAllPermissionsGranted()) {
                            checkPermissionOfCameraAndStorage();
                        } else {
                            chooseImage();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void goToSettings() {

        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        UserProfileFragment.this.startActivityForResult(myAppSettings, 1);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (imagePath == null) {
            setUpTheLogoutView(logOut);
        }
    }

    private void showSaveView(CardView btnDone, CardView logOut) {
        btnDone.setVisibility(View.VISIBLE);
        logOut.setVisibility(View.VISIBLE);
    }

    private void setUpTheLogoutView(CardView logOut) {
        btnDone.setVisibility(View.GONE);
        logOut.setVisibility(View.VISIBLE);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
    }

    private void setData(User user) {

            tv_username.setText(user.username);
            toolbarHeaderView.bindTo(user.username , "");
            floatHeaderView.bindTo(user.username , "");


        tv_about.setText(user.about);
        tv_phone_number.setText(user.phone_number);
        tv_username.setText(user.username);
        ivUserGroupImage.setClickable(true);
        if (user.userImage != null) {
            ImageDisplayer.displayImage(user.userImage, ivUserGroupImage, null, getContext());
        }

    }

    private void setFriendData(FriendUser user) {

            tv_username.setText(user.username);
            toolbarHeaderView.bindTo(user.username , "");
            floatHeaderView.bindTo(user.username , "");


        tv_about.setText(user.about);
        tv_phone_number.setText(user.phone_number);
        if (user.userImage != null) {
            ImageDisplayer.displayImage(user.userImage, ivUserGroupImage, null, getContext());
        }
        ivUserGroupImage.setClickable(false);

        edit_name.setVisibility(View.GONE);
        editAbout.setVisibility(View.GONE);
    }

    @Override
    public void onRefreshData() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void initToolBar() {
        try {
            ((MainActivity) getActivity()).backButton.setVisibility(View.GONE);
            ((MainActivity) getActivity()).userImage.setVisibility(View.GONE);
            ((MainActivity) getActivity()).addUser.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).onlineStatus.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setText("Profile");
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
    }

    public void setImagePath(Uri path) {
        imagePath = path;
        ImageDisplayer.displayImageFromURi(getContext(), path, ivUserGroupImage);
    }

    public void saveUserDetails() {

        if (imagePath != null) {
            FireBaseSingleton.getInstance(getActivity()).UploadFileToFirebase(imagePath, null, null, null, false, null, new ImageUploadResultInterface() {
                @Override
                public void imageUploaded(String uploadedPath) {
                    User user = FirebaseDataSingleton.getInstance(getContext()).getUser();
                    user.username = tv_username.getText().toString();
                    user.about = tv_about.getText().toString();
                    user.userImage = uploadedPath;
                    FireBaseSingleton.getInstance(getActivity()).updateUserProfile(user);
                    Utility.showToast(getActivity(),"User Profile updated successfully.");
                    btnDone.setVisibility(View.GONE);


                }
            });
        } else {
            User user = FirebaseDataSingleton.getInstance(getContext()).getUser();
            user.username = tv_username.getText().toString();
            user.about = tv_about.getText().toString();
            FireBaseSingleton.getInstance(getActivity()).updateUserProfile(user);
            Utility.showToast(getActivity(),"User Profile updated successfully.");
            btnDone.setVisibility(View.GONE);
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            ivUserBackUpperButton.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            ivUserBackUpperButton.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    public interface ImageUploadResultInterface {
        public void imageUploaded(String uploadedPath);
    }

}



