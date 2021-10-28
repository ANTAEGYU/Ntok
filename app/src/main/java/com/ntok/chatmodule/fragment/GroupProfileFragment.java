package com.ntok.chatmodule.fragment;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.GroupMemberAdapter;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.utils.Constants;
import com.ntok.chatmodule.utils.Utility;
import com.ntok.chatmodule.view.HeaderView;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class GroupProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private HeaderView toolbarHeaderView;
    private HeaderView floatHeaderView;
    private AppBarLayout appBarLayout;
    private boolean isHideToolbarView = false;
    private GroupModel groupModel;
    private GroupMemberAdapter groupMemberAdapter;
    private RecyclerView groupmemberRecyclerView;
    private TextView groupParticepentCount;
    private ImageView ivUserGroupImage;
    private int RESULT_OK = -1;
    private LinearLayout mLeaveOrDeleteGroup;
    private TextView tvLeaveOrDelete;
    private String PhoneNumber = null;
    private ImageView ivUserBackUpperButton;


    public GroupProfileFragment() {
        // Required empty public constructor
    }

    public void setGroupMembers(GroupModel groupModel) {
        this.groupModel = groupModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group_profile, container, false);
        ((MainActivity) getActivity()).toolbar.setVisibility(View.GONE);
        Utility.setMargins(((MainActivity) getActivity()).frame_content, 0, 0, 0, 0);
        initViews(rootView);

        return rootView;
    }


    /*
     * Method is used to initialized the all views
     * */
    private void initViews(View rootView) {
        toolbarHeaderView = rootView.findViewById(R.id.toolbar_header_view);
        floatHeaderView = rootView.findViewById(R.id.float_header_view);
        appBarLayout = rootView.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
        groupmemberRecyclerView = rootView.findViewById(R.id.particepentRecyclerView);
        groupParticepentCount = rootView.findViewById(R.id.groupParticepentCount);
        ivUserGroupImage = rootView.findViewById(R.id.user_group_image);
        mLeaveOrDeleteGroup = rootView.findViewById(R.id.leave_or_delete_group);
        tvLeaveOrDelete = rootView.findViewById(R.id.leave_or_delete);
        ivUserBackUpperButton = rootView.findViewById(R.id.ic_back_button);
        groupParticepentCount.setText("Participant Count: " + groupModel.getFriendUsers().size());
        setUpAdapter();
        toolbarHeaderView.bindTo(groupModel != null ? groupModel.getGroup_name() : groupModel.getAdmin(), "");
        floatHeaderView.bindTo(groupModel != null ? groupModel.getGroup_name() : groupModel.getAdmin(), "");

        if(!groupModel.getAdmin().equalsIgnoreCase(FirebaseDataSingleton.getInstance(getActivity()).getUser().phone_number)) {
            tvLeaveOrDelete.setText("Leave Group");
        } else {
            tvLeaveOrDelete.setText("Delete Group");
        }

        mLeaveOrDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!groupModel.getAdmin().equalsIgnoreCase(FirebaseDataSingleton.getInstance(getActivity()).getUser().phone_number)) {
                    Utility.askForDialogConfermation(getActivity(), "Leave Group", "Are you sure you want to leave this group?", new ConfermationInterface() {
                        @Override
                        public void onYes() {

                                FireBaseSingleton.getInstance(getActivity()).leaveGroupFromGroupProfile(groupModel, new LeaveGroupResultInterface() {
                                    @Override
                                    public void leaveGroup() {
                                        Utility.showToast(getActivity(),"You have successfully leave "+groupModel.getGroup_name()+" group.");
                                        ((MainActivity)getActivity()).popAllTheFragments();
                                    }
                                });
                        }
                    });

                }else{
                    Utility.askForDialogConfermation(getActivity(), "Delete Group", "Are you sure you want to delete this group?", new ConfermationInterface() {
                        @Override
                        public void onYes() {

                            FireBaseSingleton.getInstance(getActivity()).deleteGroupFromGroupProfile(groupModel ,0,new DeleteGroupResultInterface() {
                                @Override
                                public void deleteGroup() {
                                    Utility.showToast(getActivity(),"You have successfully delete "+groupModel.getGroup_name()+" group.");
                                    ((MainActivity)getActivity()).popAllTheFragments();
                                }
                            } ) ;
                        }
                    });

                }




            }
        });
        Glide.with(getActivity()).load(groupModel.getGroupImage()).placeholder(R.drawable.default_user_black).into(ivUserGroupImage);


        toolbarHeaderView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });


        ivUserGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                ((MainActivity) getActivity()).onBackPressed();
            }
        });


    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        GroupProfileFragment.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);

    }

    /**
     * Check for the Camera and gallery permission
     */

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
        GroupProfileFragment.this.startActivityForResult(myAppSettings, 1);
    }


    private void setUpAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        groupmemberRecyclerView.setLayoutManager(linearLayoutManager);
        groupMemberAdapter = new GroupMemberAdapter(getActivity(), GroupProfileFragment.this, groupModel);
        groupmemberRecyclerView.setAdapter(groupMemberAdapter);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Utility.isOnline(getActivity())) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null) {
                Uri filePath = data.getData();
                sendGruopImageToFireBase(filePath);
            }
        } else {
            Utility.showToast(getActivity(), "Please check your internet connection.");
        }
    }

    private void sendGruopImageToFireBase(Uri contentUri) {
        FireBaseSingleton.getInstance(getActivity()).sendGroupProfilePicToFirebaseServer(getActivity(), contentUri, groupModel.getId(), new ImageUploadResultInterface() {
            @Override
            public void imageUploaded(String uploadedPath) {
                Glide.with(getActivity()).load(uploadedPath).placeholder(R.drawable.default_user_black).into(ivUserGroupImage);
                groupModel.setGroupImage(uploadedPath);
                RealamDatabase.getInstance().saveTheListOfGroupDatainsideTheDatabase(groupModel);
            }
        });

    }

    public interface ImageUploadResultInterface {
        public void imageUploaded(String uploadedPath);
    }

    public interface LeaveGroupResultInterface {
        public void leaveGroup();
    }

    public interface DeleteGroupResultInterface {
        public void deleteGroup();
    }

    public interface ConfermationInterface {

         void onYes();

    }

}
