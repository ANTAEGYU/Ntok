package com.ntok.chatmodule.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.ListPeopleAdapter;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.FriendUserDetail;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.utils.Utility;

import java.util.ArrayList;


public class AddGroupFragment extends Fragment {

    private RecyclerView recyclerListFriend;
    private ListPeopleAdapter adapter;
    private ArrayList<FriendUser> listFriend;
    private ArrayList<FriendUserDetail> listIDChoose;
    private ArrayList<FriendUserDetail> listIDRemove;
    private EditText editTextGroupName;
    private ImageView imgGroupIcon;
    private ProgressDialog dialogWait;
    private boolean isEditGroup;
    private GroupModel groupEdit;
    private Button createGroup;

    public GroupModel getGroupEdit() {
        return groupEdit;
    }

    public void setGroupEdit(GroupModel groupEdit) {
        this.groupEdit = groupEdit;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_group_fragment, container, false);
        initViews(rootView);
        return rootView;
    }

    public void initViews(View rootView) {
        createGroup = (Button) rootView.findViewById(R.id.createGroup);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listFriend = FirebaseDataSingleton.getInstance(getContext()).getFriendList();
        listIDChoose = new ArrayList<>();
        listIDRemove = new ArrayList<>();
        FriendUserDetail friendUserDetail = new FriendUserDetail();
        friendUserDetail.setFriendImage(FirebaseDataSingleton.getInstance(getContext()).getUser().userImage);
        friendUserDetail.setFriendName(FirebaseDataSingleton.getInstance(getContext()).getUser().username);
        friendUserDetail.setFriendPhoneNumber(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
        listIDChoose.add(friendUserDetail);
        editTextGroupName = (EditText) rootView.findViewById(R.id.editGroupName);

        imgGroupIcon = (ImageView) rootView.findViewById(R.id.icon_group);
        dialogWait = new ProgressDialog(getActivity());
        dialogWait.setCancelable(false);
        editTextGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listIDChoose.size() < 3) {
                    Toast.makeText(getContext(), "Add at lease two people to create group", Toast.LENGTH_SHORT).show();
                } else {
                     if(Utility.isOnline(getActivity())) {
                         if (editTextGroupName.getText().length() == 0) {
                             Toast.makeText(getContext(), "Enter group name", Toast.LENGTH_SHORT).show();
                         } else {
                             if (isEditGroup) {
                                 editGroup();
                             } else {
                                 createGroup();
                             }
                         }
                     }else{
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
        });

        if (groupEdit != null) {
            isEditGroup = true;
            createGroup.setText("Save");
            createGroup.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            editTextGroupName.setText(groupEdit.getGroup_name());
        } else {
            isEditGroup = false;
        }

        recyclerListFriend = (RecyclerView) rootView.findViewById(R.id.recycleListFriend);
        recyclerListFriend.setLayoutManager(linearLayoutManager);
        adapter = new ListPeopleAdapter(getActivity(), listFriend, createGroup, listIDChoose, listIDRemove, isEditGroup, groupEdit);
        recyclerListFriend.setAdapter(adapter);


    }

    @Override
    public void onDestroy() {
//        ((MainActivity) getActivity()).manageDifferentFragmentTool();
        super.onDestroy();
    }


    private void editGroup() {
        //Show dialog wait
        dialogWait.setIcon(R.drawable.ic_add_group_dialog);
        dialogWait.setTitle("Editing....");
        dialogWait.show();
        //Delete group
        final String idGroup = groupEdit.getId();
        GroupModel groupModel = new GroupModel();
        for (FriendUserDetail  userDetail : listIDChoose) {
            FriendUserDetail friendUserDetail = new FriendUserDetail();
            friendUserDetail.setFriendName(userDetail.getFriendName());
            friendUserDetail.setFriendPhoneNumber(userDetail.getFriendPhoneNumber());
            friendUserDetail.setFriendImage(userDetail.getFriendImage());
            groupModel.getFriendUsers().add(friendUserDetail);
        }
        groupModel.setId(idGroup);
        groupModel.setRoom_id(idGroup);

        groupModel.setGroud_name(editTextGroupName.getText().toString());
        groupModel.setAdmin(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
        FireBaseSingleton.getInstance(getActivity()).addGroup(groupModel, listIDChoose, listIDRemove, new ListPeopleAdapter.OnCompletionListener() {
            @Override
            public void onComplete() {
                dialogWait.dismiss();
                ((MainActivity) getActivity()).onBackPressed();
            }
        });
    }

    private void createGroup() {
        //Show dialog wait
        dialogWait.setIcon(R.drawable.ic_add_group_dialog);
        dialogWait.setTitle("Registering....");
        dialogWait.show();
        groupEdit = new GroupModel();
        for (FriendUserDetail  userDetail : listIDChoose) {
            FriendUserDetail friendUserDetail = new FriendUserDetail();
            friendUserDetail.setFriendName(userDetail.getFriendName());
            friendUserDetail.setFriendPhoneNumber(userDetail.getFriendPhoneNumber());
            friendUserDetail.setFriendImage(userDetail.getFriendImage());
            groupEdit.getFriendUsers().add(friendUserDetail);
        }
        groupEdit.setGroud_name(editTextGroupName.getText().toString());
        groupEdit.setAdmin(editTextGroupName.getText().toString());
        groupEdit.setId(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number + "_" + System.currentTimeMillis());
        groupEdit.setRoom_id(groupEdit.getId());
        groupEdit.setAdmin(FirebaseDataSingleton.getInstance(getContext()).getUser().phone_number);
        groupEdit.setGroupImage("");
        FireBaseSingleton.getInstance(getActivity()).addGroup(groupEdit, listIDChoose, listIDRemove, new ListPeopleAdapter.OnCompletionListener() {
            @Override
            public void onComplete() {
                dialogWait.dismiss();
                ((MainActivity) getActivity()).onBackPressed();
            }
        });
    }

}

