package com.ntok.chatmodule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.AllFriendListAdapter;
import com.ntok.chatmodule.adapter.PhoneContactListAdapter;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUserDetail;
import com.ntok.chatmodule.model.MessageModel;
import com.ntok.chatmodule.model.PhoneContact;
import com.ntok.chatmodule.model.PhoneContactsData;
import com.ntok.chatmodule.model.User;
import com.ntok.chatmodule.utils.Lg;
import com.ntok.chatmodule.utils.Utility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class SearchContactFragment extends RootFragment {

    Button btn_send;
    RecyclerView contactListRecyclerView;
    private PhoneContactListAdapter adapter;
    private AllFriendListAdapter friendAdapter;
    private ArrayList<PhoneContact> contactListData = new ArrayList<>();
    private ArrayList<PhoneContact> contactList = new ArrayList<>();
    private boolean isShowFriendList = false;
    AllFriendListAdapter.ItemClick itemClick;
    private MessageModel messageModel;
    private boolean isGroupMessage;
    private ArrayList<FriendUserDetail> groupUSer;
    private boolean isShareContact;

    public void setShowFriendList(boolean showFriendList) {
        isShowFriendList = showFriendList;
    }

    public void setItemClick(AllFriendListAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public void setGroupUSer(ArrayList<FriendUserDetail> groupUSer) {
        this.groupUSer = groupUSer;
    }

//    public boolean isShareContact() {
//        return isShareContact;
//    }

    public void setShareContact(boolean isGroupMessage, boolean shareContact, MessageModel messageModel) {
        isShareContact = shareContact;
        this.messageModel = messageModel;
        this.isGroupMessage = isGroupMessage;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.phone_contact_list, container, false);

        ((MainActivity) getActivity()).setTabVisibilty();

        initViews(rootView);
        initToolBar();

        return rootView;
    }

    public void setContactListForFriends() {
        friendAdapter = new AllFriendListAdapter(getActivity().getApplicationContext(), FirebaseDataSingleton.getInstance(getActivity()).getFriendList());

        contactListRecyclerView.setAdapter(friendAdapter);
        Utility.hideProgressDialog();
        btn_send.setVisibility(View.VISIBLE);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendAdapter.selectedPosition == -1) {
                    Toast.makeText(getActivity(), "Pleas select a contact", Toast.LENGTH_LONG).show();
                } else {
                    itemClick.onItemClickListener(friendAdapter.friendListFiltered.get(friendAdapter.selectedPosition), v);
                }
            }
        });
    }

    private void initToolBar() {
        ((MainActivity) getActivity()).searchViewCustom.setVisibility(View.VISIBLE);
        if (adapter != null) {
            ((MainActivity) getActivity()).search_data_edittext.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).search_data_edittext.setCursorVisible(true);
            ((MainActivity) getActivity()).search_data_edittext.setFocusable(true);
        }else{
            ((MainActivity) getActivity()).search_data_edittext.setVisibility(View.GONE);
            ((MainActivity) getActivity()).search_data_edittext.setCursorVisible(true);
            ((MainActivity) getActivity()).search_data_edittext.setFocusable(true);
        }

        ((MainActivity) getActivity()).backButton.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).userImage.setVisibility(View.GONE);
        ((MainActivity) getActivity()).addUser.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
        ((MainActivity) getActivity()).title.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).onlineStatus.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconSelection.setVisibility(View.GONE);
        ((MainActivity) getActivity()).iconSearch.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).iconRefresh.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).isItemSearchVisible = true;
        ((MainActivity) getActivity()).fullToolBar.setVisibility(View.GONE);

        getActivity().invalidateOptionsMenu();

        ((MainActivity) getActivity()).search_data_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    if (adapter != null) {
                        callSearch(s.toString());
                    } else {
                        callFriendsSearch(s.toString());
                    }
                }
                else {
                    if (adapter != null) {
                        callSearch("");
                    } else {
                        callFriendsSearch("");
                    }
                }
            }
        });
    }

    private void initViews(View rootView) {
        btn_send = rootView.findViewById(R.id.btn_send);
        contactListRecyclerView = rootView.findViewById(R.id.list);
        contactListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (!isShowFriendList) {
            if (RealamDatabase.getInstance().getAllContact().size() == 0) {
                Utility.showProgressDialog(getActivity(), "Fetching contacts. Please Wait....");
                PhoneContactsData.getInstance().setOnCompletionListener(new ReadPhoneContactsAndFriendFragment.OnCompletionListener() {
                    @Override
                    public void onComplete() {
                        Utility.hideProgressDialog();
                        contactList = PhoneContactsData.getInstance().contactList;
                        RealamDatabase.getInstance().saveContactsToRealmDatabase(contactList);
                        setContactList();
                    }
                });
            } else {
                setContactList();
            }
        } else {
            setContactListForFriends();
        }
    }

    private void setContactList() {
        contactListData.addAll(RealamDatabase.getInstance().getAllContact());
        adapter = new PhoneContactListAdapter(getActivity().getApplicationContext(), contactListData);
        contactListRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Utility.hideProgressDialog();
        adapter.setItemClickListener(new PhoneContactListAdapter.ItemClick() {
            @Override
            public void onItemClickListener(PhoneContact phoneUser, View view) {
                if (Utility.isOnline(getActivity())) {
                    if (isShareContact) {
                        messageModel.setContact(phoneUser);
                        FireBaseSingleton.getInstance(getActivity()).addMessageToFireBase(messageModel.getFrom(), messageModel.getTo(), ChatFragment.roomId, messageModel, isGroupMessage, groupUSer);
                        getActivity().onBackPressed();
                    } else {
                        if (phoneUser.getIsOurContact()) {
                            for (User user : FirebaseDataSingleton.getInstance(getContext()).getAllUsers()) {
                                if (PhoneContactsData.phoeNumberWithOutCountryCode(user.phone_number).equalsIgnoreCase(PhoneContactsData.phoeNumberWithOutCountryCode(phoneUser.getPhone_number()))) {
                                    FireBaseSingleton.getInstance(getActivity()).addFriends(user, phoneUser, (MainActivity) getActivity());
                                    break;
                                }
                            }
                        } else {
                            try {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                                String shareString = "\nLet me recommend you this application\n\n";
                                shareString = shareString + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + " \n\n";
                                i.putExtra(Intent.EXTRA_TEXT, shareString);
                                startActivity(Intent.createChooser(i, "choose one"));
                            } catch (Exception e) {
                                Lg.printStackTrace(e);
                            }
                        }
                    }
                }
            }
        });
    }

    private ArrayList<PhoneContact> removeDuplicate(ArrayList<PhoneContact> contactList) {

        Set set = new TreeSet(new Comparator<PhoneContact>() {
            @Override
            public int compare(PhoneContact o1, PhoneContact o2) {
                if (o1.getPhone_number() != null && o2.getPhone_number() != null)
                    if (o1.getPhone_number().equalsIgnoreCase(o2.getPhone_number())) {
                        return 0;
                    }
                return 1;
            }
        });

        set.addAll(contactList);
        return new ArrayList<PhoneContact>(set);
    }

    @Override
    public void onRefreshData() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    /*
     *
     * Calling the search item
     * */
    public void callSearch(String query) {
       adapter.getFilter().filter(query);

    }
    private void callFriendsSearch(String query) {
        friendAdapter.getFilter().filter(query);
    }

    @Override
    public void onDestroy() {
        PhoneContactsData.getInstance().setOnCompletionListener(null);
        ((MainActivity) getActivity()).isItemSearchVisible = false;
        getActivity().invalidateOptionsMenu();
        super.onDestroy();
    }


}
