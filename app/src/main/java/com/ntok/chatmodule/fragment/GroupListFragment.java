package com.ntok.chatmodule.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.adapter.ListGroupsAdapter;
import com.ntok.chatmodule.adapter.ListPeopleAdapter;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.utils.Lg;

import java.util.ArrayList;

/**
 * Created by Sonam on 08-05-2018.
 */

public class GroupListFragment extends RootFragment {
    public static final int CONTEXT_MENU_DELETE = 1;
    public static final int CONTEXT_MENU_EDIT = 2;
    public static final int CONTEXT_MENU_LEAVE = 3;
    public static final int REQUEST_EDIT_GROUP = 0;
    public static final String CONTEXT_MENU_KEY_INTENT_DATA_POS = "pos";
    public static final String CONTEXT_MENU_KEY_INTENT_DATA_GROUP_ID = "groupid";
    RecyclerView recyclerViewFriends;
    ListGroupsAdapter listGroupsAdapter;
    TextView noGroupChat;
    private ArrayList<GroupModel> listGroup = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDataSingleton.getInstance(getActivity()).setGroupModels(RealamDatabase.getInstance().getAllListOfGroupFormDB());
        int time = 2000;
        if (FirebaseDataSingleton.getInstance(getActivity()).getUser() == null) {
            time = 6000;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FireBaseSingleton.getInstance(getActivity()).getAllGroups();
                FireBaseSingleton.getInstance(getActivity()).getListGroup();
            }
        }, time);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friend_list_fragment, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        recyclerViewFriends = rootView.findViewById(R.id.friend_list);
        noGroupChat = rootView.findViewById(R.id.noGroupChat);
        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        setUpTheAdapter();
        refreshTheAdapter();
        addScrollListener();


    }

    /*
     * On recycler view scrolling hide the Fab button
     * */
    private void addScrollListener() {
        recyclerViewFriends.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && ((MainActivity) getActivity()).addUser.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addUser.hide();
                } else if (dy < 0 && ((MainActivity) getActivity()).addUser.getVisibility() != View.VISIBLE) {
                    ((MainActivity) getActivity()).addUser.show();
                }
            }
        });
    }

    /*
    * This method is used to get the data from the database if the data
    * */

    private void refreshTheAdapter() {
        if (RealamDatabase.getInstance().getAllListOfGroupFormDB().size() == 0) {
            getTheListOfGroupFromFirebaseServer();
        } else {
            listGroup.clear();
            recyclerViewFriends.setVisibility(View.VISIBLE);
            noGroupChat.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            listGroup.addAll(RealamDatabase.getInstance().getAllListOfGroupFormDB());
            listGroupsAdapter = new ListGroupsAdapter(getActivity(), listGroup);
            recyclerViewFriends.setAdapter(listGroupsAdapter);
        }
    }

    private void getTheListOfGroupFromFirebaseServer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                if (RealamDatabase.getInstance().getAllListOfGroupFormDB().size() > 0) {
                    listGroup.clear();
                    recyclerViewFriends.setVisibility(View.VISIBLE);
                    noGroupChat.setVisibility(View.GONE);
                    listGroup.addAll(RealamDatabase.getInstance().getAllListOfGroupFormDB());
                    listGroupsAdapter = new ListGroupsAdapter(getActivity(), listGroup);
                    recyclerViewFriends.setAdapter(listGroupsAdapter);
                } else {
                    recyclerViewFriends.setVisibility(View.GONE);
                    noGroupChat.setVisibility(View.VISIBLE);
                    noGroupChat.setText("No Group chat available. Tap on the below right button to create your own group.");
                }


            }
        }, 4000);
    }

    private void setUpTheAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewFriends.setLayoutManager(linearLayoutManager);
        listGroupsAdapter = new ListGroupsAdapter(getActivity(), listGroup);
        recyclerViewFriends.setAdapter(listGroupsAdapter);
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

    public void initToolBar() {
        try {
            ((MainActivity) getActivity()).backButton.setVisibility(View.GONE);
            ((MainActivity) getActivity()).userImage.setVisibility(View.GONE);
            ((MainActivity) getActivity()).addUser.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setText("Group Chats");
            ((MainActivity) getActivity()).title.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).iconSelection.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).iconSearch.setVisibility(View.GONE);;
            ((MainActivity)getActivity()).iconRefresh.setVisibility(View.GONE);
            if (listGroupsAdapter != null) {
                listGroupsAdapter.notifyDataSetChanged();
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case CONTEXT_MENU_DELETE:
                int posGroup = item.getIntent().getIntExtra(CONTEXT_MENU_KEY_INTENT_DATA_POS, -1);
                String GroupId = item.getIntent().getStringExtra(CONTEXT_MENU_KEY_INTENT_DATA_GROUP_ID);
                Log.e("Tag", "Group_Id     " + GroupId);
                GroupModel group = FirebaseDataSingleton.getInstance(getContext()).getGroupModels().get(posGroup);
                if (group != null) {
                    FireBaseSingleton.getInstance(getActivity()).deleteGroup(group, GroupId, 0, new ListPeopleAdapter.OnCompletionListener() {
                        @Override
                        public void onComplete() {
                            listGroupsAdapter.notifyDataSetChanged();
                        }
                    });
                }

                break;
            case CONTEXT_MENU_EDIT:
                int posGroup1 = item.getIntent().getIntExtra(CONTEXT_MENU_KEY_INTENT_DATA_POS, -1);
                ((MainActivity) getActivity()).addGroupFragment(FirebaseDataSingleton.getInstance(getContext()).getGroupModels().get(posGroup1));
                break;

            case CONTEXT_MENU_LEAVE:
                int position = item.getIntent().getIntExtra(CONTEXT_MENU_KEY_INTENT_DATA_POS, -1);
                String groupId = item.getIntent().getStringExtra(CONTEXT_MENU_KEY_INTENT_DATA_GROUP_ID);


                FireBaseSingleton.getInstance(getActivity()).leaveGroup(listGroup, groupId, position, FirebaseDataSingleton.getInstance(getActivity()).getUser().phone_number, new ListPeopleAdapter.OnCompletionListener() {
                    @Override
                    public void onComplete() {
                        listGroupsAdapter.notifyDataSetChanged();
                    }
                });

                break;
        }

        return super.onContextItemSelected(item);
    }


}



