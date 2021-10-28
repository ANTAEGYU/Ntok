package com.ntok.chatmodule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.MyFriendListAdapter;
import com.ntok.chatmodule.adapter.MyUnFriendListAdapter;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.PhoneContact;
import com.ntok.chatmodule.model.PhoneContactsData;
import com.ntok.chatmodule.model.User;
import com.ntok.chatmodule.utils.Lg;
import com.ntok.chatmodule.utils.Utility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Sonam on 09-05-2018.
 */

public class ReadPhoneContactsAndFriendFragment extends RootFragment {

    //    boolean isShareContact;
//    private RecyclerView mListView;
    private RecyclerView friendRecyclerView, unFfriendRecyclerView;
    private Handler updateBarHandler;
    //    private PhoneContactListAdapter adapter;
    private MyUnFriendListAdapter myUnFriendListAdapter;
    private MyFriendListAdapter myFriendListAdapter;

    private ArrayList<PhoneContact> contactList = new ArrayList<>();
    private ArrayList<PhoneContact> contactListData;
    private ArrayList<PhoneContact> myFriendContactList;
    private ArrayList<PhoneContact> myUnFriendContactList;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_phone_contacts, container, false);

        ((MainActivity) getActivity()).setTabVisibilty();

        initViews(rootView);
        initToolBar();
        return rootView;
    }

    public void initViews(View rootView) {

        friendRecyclerView = rootView.findViewById(R.id.friendRecyclerView);
        unFfriendRecyclerView = rootView.findViewById(R.id.unFfriendRecyclerView);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        unFfriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateBarHandler = new Handler();
        // Since reading contacts takes more time, let's run it on a separate thread.

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

    }



    /**
     * Callback received when a permissions request has been completed.
     */

    public void setContactList() {
        contactListData = new ArrayList<>();
        contactListData.addAll(RealamDatabase.getInstance().getAllContact());

        myFriendContactList = new ArrayList<>();
        myUnFriendContactList = new ArrayList<>();
        seprateFatchData();
        myUnFriendListAdapter = new MyUnFriendListAdapter(getActivity().getApplicationContext(), myUnFriendContactList);
        myFriendListAdapter = new MyFriendListAdapter(getActivity().getApplicationContext(), myFriendContactList);
        unFfriendRecyclerView.setAdapter(myUnFriendListAdapter);
        friendRecyclerView.setAdapter(myFriendListAdapter);
        unFfriendRecyclerView.setNestedScrollingEnabled(false);
        friendRecyclerView.setNestedScrollingEnabled(false);
        Utility.hideProgressDialog();


        /**
         * On friend list click listener
         * */

        myFriendListAdapter.setItemClickListener(new MyFriendListAdapter.ItemClick() {
            @Override
            public void onItemClickListener(PhoneContact phoneUser, View view) {
                if (Utility.isOnline(getActivity())) {
                    for (User user : FirebaseDataSingleton.getInstance(getContext()).getAllUsers()) {
                        if (PhoneContactsData.phoeNumberWithOutCountryCode(user.phone_number).equalsIgnoreCase(PhoneContactsData.phoeNumberWithOutCountryCode(phoneUser.getPhone_number()))) {
                            FireBaseSingleton.getInstance(getActivity()).addFriends(user, phoneUser, (MainActivity) getActivity());
                            break;
                        }
                    }
                } else {
                    Utility.showAltertPopup(getActivity(), "Internt Error!", "Please connect to internet.", new OnAlertPopupClickListener() {
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


        /**
         * On Un-friend list click listener
         * */

        myUnFriendListAdapter.setItemClickListener(new MyUnFriendListAdapter.ItemClick() {
            @Override
            public void onItemClickListener(PhoneContact user, View view) {
                if (Utility.isOnline(getActivity())) {
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
                } else {
                    Utility.showAltertPopup(getActivity(), "Internt Error!", "Please connect to internet.", new OnAlertPopupClickListener() {
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

    }

    /**
     * This method is used to separate the data from the main contact list to
     * first our friend list
     * second un friend list
     */
    private void seprateFatchData() {
        myFriendContactList.clear();
        myUnFriendContactList.clear();
        for (int i = 0; i < contactListData.size(); i++) {
            if (contactListData.get(i).getIsOurContact()) {
                myFriendContactList.add(contactListData.get(i));
            } else {
                myUnFriendContactList.add(contactListData.get(i));
            }
        }
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


    public void initToolBar() {
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
        getActivity().invalidateOptionsMenu();

        ((MainActivity) getActivity()).title.setText("Phone contacts");
    }


    @Override
    public void onRefreshData() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        PhoneContactsData.getInstance().setOnCompletionListener(null);
        ((MainActivity) getActivity()).isItemSearchVisible = false;
        getActivity().invalidateOptionsMenu();
        super.onDestroy();
    }

    public void getTheLatestAddedContactFromInternalStorage() {
        Utility.showProgressDialog(getActivity(), "Fetching contacts. Please Wait....");
        PhoneContactsData.getInstance().getContacts(getActivity());
        PhoneContactsData.getInstance().setOnCompletionListener(new ReadPhoneContactsAndFriendFragment.OnCompletionListener() {
            @Override
            public void onComplete() {
                Utility.hideProgressDialog();
                contactListData.clear();
                contactListData = new ArrayList<>();
                contactList = PhoneContactsData.getInstance().contactList;
                ArrayList<PhoneContact> phoneContactNoDuplicate = removeDuplicate(contactList);
                RealamDatabase.getInstance().saveContactsToRealmDatabase(phoneContactNoDuplicate);
                contactListData.addAll(RealamDatabase.getInstance().getAllContact());
                seprateFatchData();
                myFriendListAdapter.notifyDataSetChanged();
                myUnFriendListAdapter.notifyDataSetChanged();
            }
        });
    }

    public interface OnCompletionListener {
        public void onComplete();
    }

}