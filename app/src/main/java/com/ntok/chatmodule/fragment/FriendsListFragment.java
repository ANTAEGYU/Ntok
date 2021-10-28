package com.ntok.chatmodule.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.FriendListAdapter;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.utils.Lg;

import java.util.ArrayList;

/**
 * Created by Sonam on 08-05-2018.
 */

public class FriendsListFragment extends RootFragment {
    RecyclerView recyclerViewFriends;
    FriendListAdapter friendListAdapter;
    ProgressBar progressBar;
    TextView noGroupChat;
    private ArrayList<FriendUser> friendList;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friend_list_fragment, container, false);
        FireBaseSingleton.getInstance(getContext()).getFriends();
        initViews(rootView);
        return rootView;
    }


    /**
     * Initializing the view
     * @param rootView
     */


    private void initViews(View rootView) {
        recyclerViewFriends = rootView.findViewById(R.id.friend_list);
        noGroupChat = rootView.findViewById(R.id.noGroupChat);
        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        friendList = new ArrayList<>();
        setUpTheAdapter();
        addOnScrollListener();

        if(RealamDatabase.getInstance().getAllFriends().size() == 0){
            getTheListOfFriendFromFirebaseServer();
        }else {
            friendList.clear();
            recyclerViewFriends.setVisibility(View.VISIBLE);
            noGroupChat.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            RealamDatabase.getInstance().saveTheListOfFriendsInsideDatabase(FirebaseDataSingleton.getInstance(getActivity()).getFriendList());
            friendList.addAll(RealamDatabase.getInstance().getAllFriends());
            friendListAdapter.notifyDataSetChanged();
        }
    }

    /*
    * On recycler view scrolling hide the Fab button
    * */
    private void addOnScrollListener() {
        recyclerViewFriends.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && ((MainActivity)getActivity()).addUser.getVisibility() == View.VISIBLE) {
                    ((MainActivity)getActivity()).addUser.hide();
                } else if (dy < 0 && ((MainActivity)getActivity()).addUser.getVisibility() != View.VISIBLE) {
                    ((MainActivity)getActivity()).addUser.show();
                }
            }
        });

    }


    /**
     *  Get the list of data from the firebase server
     */
    private void getTheListOfFriendFromFirebaseServer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                if(FirebaseDataSingleton.getInstance(getActivity()).getFriendList().size() > 0) {
                    friendList.clear();
                    recyclerViewFriends.setVisibility(View.VISIBLE);
                    noGroupChat.setVisibility(View.GONE);
                    RealamDatabase.getInstance().saveTheListOfFriendsInsideDatabase(FirebaseDataSingleton.getInstance(getActivity()).getFriendList());
                    friendList.addAll(RealamDatabase.getInstance().getAllFriends());
                    friendListAdapter.notifyDataSetChanged();
                }else{
                    recyclerViewFriends.setVisibility(View.GONE);
                    noGroupChat.setVisibility(View.VISIBLE);
                    noGroupChat.setText("No chat available. Tap on the below right button to start chatting wit your contacts");
                }
            }
        },5000);
    }


    /**
     * This method is used to setup the the recycler view
     */

    private void setUpTheAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewFriends.setLayoutManager(linearLayoutManager);
        friendListAdapter = new FriendListAdapter(getContext(),friendList);
        recyclerViewFriends.setAdapter(friendListAdapter);
    }


    @Override
    public void onRefreshData() {
        View view = getView();
        initViews(view);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    /**
     * Init the toolbar dor the activity
     */
    public void initToolBar() {
        try {
            ((MainActivity) getActivity()).backButton.setVisibility(View.GONE);
            ((MainActivity) getActivity()).userImage.setVisibility(View.GONE);
            ((MainActivity) getActivity()).addUser.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setText("Chats");
            ((MainActivity)getActivity()).title.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).onlineStatus.setVisibility(View.GONE);
            ((MainActivity)getActivity()).toolbar.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).iconSelection.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()). iconSearch.setVisibility(View.GONE);;
            ((MainActivity)getActivity()).iconRefresh.setVisibility(View.GONE);

            if (friendListAdapter != null)
                friendListAdapter.notifyDataSetChanged();
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
    }


    public void getLetestMessage() {
        friendList.clear();
        recyclerViewFriends.setVisibility(View.VISIBLE);
        RealamDatabase.getInstance().saveTheListOfFriendsInsideDatabase(FirebaseDataSingleton.getInstance(getActivity()).getFriendList());
        friendList.addAll(RealamDatabase.getInstance().getAllFriends());
        friendListAdapter.notifyDataSetChanged();
    }


}



