package com.ntok.chatmodule.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.fragment.AddGroupFragment;
import com.ntok.chatmodule.fragment.ChatFragment;
import com.ntok.chatmodule.fragment.FriendsListFragment;
import com.ntok.chatmodule.fragment.GroupListFragment;
import com.ntok.chatmodule.fragment.GroupProfileFragment;
import com.ntok.chatmodule.fragment.ReadPhoneContactsAndFriendFragment;
import com.ntok.chatmodule.fragment.SearchContactFragment;
import com.ntok.chatmodule.fragment.UserProfileFragment;
import com.ntok.chatmodule.interfaces.FirebaseDataUpdatedInterface;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.FriendUserDetail;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.model.MessageModel;
import com.ntok.chatmodule.model.PhoneContactsData;
import com.ntok.chatmodule.sharedPreference.SharedPreference;
import com.ntok.chatmodule.utils.CircleImageView;
import com.ntok.chatmodule.utils.Constants;
import com.ntok.chatmodule.utils.Lg;
import com.ntok.chatmodule.utils.Utility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseDataUpdatedInterface {

    public LinearLayout toolbar;
    public Toolbar searchtollbar;
    public CircleImageView userImage;
    public TextView title;
    public TextView onlineStatus;
    public ImageView backButton;
    public ImageView iconForward;
    public ImageView iconDelete;
    public ImageView iconCopy;
    public Menu search_menu;
    public MenuItem item_search;
    public boolean isItemSearchVisible = false;
    public FloatingActionButton addUser;
    public FrameLayout frame_content;
    public ImageView iconSelection;
    public ImageView iconSearch;
    public ImageView iconRefresh;
    public RelativeLayout fullToolBar;
    public LinearLayout searchViewCustom;
    public ImageView search_back_button;
    public TextInputEditText search_data_edittext;
    private InterstitialAd mInterstitialAd;
    int pressbackCount = 0;
    ViewPagerAdapter adapter;
    String senderID;
    boolean isGroup = false;
    String receiverId;
    ChatFragment chatFragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean directLaunchMAinActivity;
    private static final int REQUEST_APP_SETTINGS = 168;
    private PopupMenu popupMenu;
    boolean doubleBackToExitPressedOnce = false;
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FireBaseSingleton.getInstance(getApplicationContext()).setActivity(this, this);
        checkUser();
        initViews();
        setUpTabs();
        onNewIntent(getIntent());
    }



    public void checkUser() {
        if (SharedPreference.getInstance(getApplicationContext()).getUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            FirebaseDataSingleton.getInstance(this).setUser(SharedPreference.getInstance(getApplicationContext()).getUser());
            FirebaseDataSingleton.getInstance(this).setFriendList(RealamDatabase.getInstance().getAllFriends());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initViews() {
        addUser = findViewById(R.id.fab);
        title = findViewById(R.id.title);
        onlineStatus = findViewById(R.id.online_status);
        backButton = findViewById(R.id.back_button);
        iconForward = findViewById(R.id.icon_forward);
        iconDelete = findViewById(R.id.icon_delete);
        iconCopy = findViewById(R.id.icon_copy);
        userImage = findViewById(R.id.user_image);
        frame_content = findViewById(R.id.frame_content);
        iconSelection = findViewById(R.id.icon_selection);
        iconSearch = findViewById(R.id.icon_search);
        iconRefresh = findViewById(R.id.icon_refresh);
        fullToolBar = findViewById(R.id.full_tool_bar);
        searchViewCustom = findViewById(R.id.search_view_custom);
        search_back_button = findViewById(R.id.search_back_button);
        search_data_edittext = findViewById(R.id.search_data_edittext);
        toolbar = findViewById(R.id.toolbar);
        addUser.setImageResource(R.drawable.add_friend);
        searchViewCustom.setVisibility(View.GONE);
        search_data_edittext.setVisibility(View.GONE);


        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();



        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("37DB9D9783A18F497EFEADA8143BBC7D")
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("Tag","Code to be executed when an ad finishes loading.");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d("esx", "onAdFailedToLoad: " + errorCode+"--"+AdRequest.ERROR_CODE_NO_FILL+"-"+AdRequest.ERROR_CODE_NETWORK_ERROR+"-"+AdRequest.ERROR_CODE_INVALID_REQUEST+"-"+AdRequest.ERROR_CODE_INTERNAL_ERROR+"-");
                // Save app state before going to the ad overlay.

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("Tag","Code to be executed when an ad opens an overlay that");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.e("Tag","Code to be executed when the user has left the app.");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.e("Tag","Code to be executed when when the user is about to return");
            }
        });

        addUser.setVisibility(View.VISIBLE);

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    needContactsPermisstion();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PhoneContactsData.getInstance().getContacts(MainActivity.this);
                        }
                    }, 20000);
                    addContactFragment(false, false, null, null);
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onBackPressed();
            }
        });
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchContactFragment searchContactFragment = new SearchContactFragment();
                addFragment(searchContactFragment, "searchContactFragment");
            }
        });
        search_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

        iconRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment tempFragment = getSupportFragmentManager().findFragmentByTag("ReadPhoneContactsAndFriendFragment");
                if (tempFragment != null) {
                    ((ReadPhoneContactsAndFriendFragment) tempFragment).getTheLatestAddedContactFromInternalStorage();
                }
            }
        });

        iconSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(MainActivity.this, v);
                popupMenu.setOnDismissListener(new OnDismissListener());
                popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener());
                popupMenu.inflate(R.menu.main);
                popupMenu.show();
            }
        });

        setSearchtollbar();
        initToolBar();
    }

    public void addContactFragmentNew(boolean isGroupMessage, boolean isShareContact, MessageModel messageModel, ArrayList<FriendUserDetail> groupUSer) {
        SearchContactFragment searchContactFragment = new SearchContactFragment();
        searchContactFragment.setShareContact(isGroupMessage, isShareContact, messageModel);
        searchContactFragment.setGroupUSer(groupUSer);
        addFragment(searchContactFragment, "searchContactFragment");
    }


    private class OnDismissListener implements PopupMenu.OnDismissListener {

        @Override
        public void onDismiss(PopupMenu menu) {
            // TODO Auto-generated method stub

        }

    }

    private class OnMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()) {
                case R.id.action_profile:
                    showUserProfile();
                    return true;
            }
            return false;
        }
    }

    private void needContactsPermisstion() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showAltertPopup(MainActivity.this, "Contacts", "Need contact permission to start the chat", new OnAlertPopupClickListener() {
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    PhoneContactsData.getInstance().getContacts(MainActivity.this);
                                }
                            }, 2000);
                            addContactFragment(false, false, null, null);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void goToSettings() {

        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.this.startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    public void setSearchtollbar() {
        searchtollbar = findViewById(R.id.searchtoolbar);
        if (searchtollbar != null) {
            searchtollbar.inflateMenu(R.menu.menu_search);
            search_menu = searchtollbar.getMenu();

            searchtollbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.searchtoolbar, 1, true, false);
                    else
                        searchtollbar.setVisibility(View.GONE);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);
            isItemSearchVisible = false;
            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.searchtoolbar, 1, true, false);
                    } else
                        searchtollbar.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });

            initSearchView();


        } else
            Log.d("toolbar", "setSearchtollbar: NULL");
    }

    public SearchView searchView;

    public void initSearchView() {
        searchView =
                (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.close_white_lines);


        // set hint and the text colors

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search..");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(getResources().getColor(R.color.appTheme));


        // set the cursor

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setUpTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    public void setTabVisibilty() {
        tabLayout.setVisibility(View.GONE);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsListFragment(), "FriendChat");
        adapter.addFragment(new GroupListFragment(), "GroupChat");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (adapter.getItem(position) instanceof FriendsListFragment) {
                    addUser.setVisibility(View.VISIBLE);
                    ((FriendsListFragment) adapter.getItem(position)).initToolBar();
                    addUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                needContactsPermisstion();
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        PhoneContactsData.getInstance().getContacts(MainActivity.this);
                                    }
                                }, 20000);
                                addContactFragment(false, false, null, null);
                            }
                        }
                    });
                    addUser.setImageResource(R.drawable.add_friend);
                } else if (adapter.getItem(position) instanceof GroupListFragment) {
                    addUser.setVisibility(View.VISIBLE);
                    addUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addGroupFragment(null);
                        }
                    });
                    ((GroupListFragment) adapter.getItem(position)).initToolBar();
                    addUser.setImageResource(R.drawable.ic_float_add_group);
                } else {
                    addUser.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void addContactFragment(boolean isGroupMessage, boolean isShareContact, MessageModel messageModel, ArrayList<FriendUserDetail> groupUSer) {
        ReadPhoneContactsAndFriendFragment readPhoneContactsAndFriendFragment = new ReadPhoneContactsAndFriendFragment();
        addFragment(readPhoneContactsAndFriendFragment, "ReadPhoneContactsAndFriendFragment");
    }

    public void addGroupFragment(GroupModel groupModel) {
        AddGroupFragment addGroupFragment = new AddGroupFragment();
        addGroupFragment.setGroupEdit(groupModel);
        addFragment(addGroupFragment, "addGroupFragment");
    }


    public void initToolBar() {
        backButton.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
        iconForward.setVisibility(View.GONE);
        iconDelete.setVisibility(View.GONE);
        iconCopy.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        onlineStatus.setVisibility(View.GONE);
        iconSelection.setVisibility(View.VISIBLE);
        iconSearch.setVisibility(View.GONE);
        iconRefresh.setVisibility(View.GONE);
        title.setText("Chats");
    }

    @Override
    public void onBackPressed() {
        pressbackCount++;
        Log.e("Tag","pressbackCount ==== "+pressbackCount);
        if(pressbackCount == 4){
            pressbackCount = 0;
            showInterstitial();
        }
        try {
            if (search_menu.findItem(R.id.action_filter_search).isActionViewExpanded()) {
                search_menu.findItem(R.id.action_filter_search).collapseActionView();
                return;
            }
        } catch (Exception ex) {

        }
        try {
            if (getSupportFragmentManager().findFragmentByTag("UserProfileFragment") != null) {
                if (getSupportFragmentManager().findFragmentByTag("ReadPhoneContactsAndFriendFragment") != null) {
                    Utility.hideKeyboard(MainActivity.this);
                    toolbar.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    fullToolBar.setVisibility(View.VISIBLE);
                    searchViewCustom.setVisibility(View.GONE);
                    Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._50sdp), 0, 0);

                    ((ReadPhoneContactsAndFriendFragment) getSupportFragmentManager().findFragmentByTag("ReadPhoneContactsAndFriendFragment")).initToolBar();

                } else if (chatFragment != null) {
                    Utility.hideKeyboard(MainActivity.this);
                    toolbar.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._60sdp), 0, 0);
                    chatFragment.initToolBar();
                    super.onBackPressed();
                    return;
                } else {
                    Utility.hideKeyboard(MainActivity.this);
                    toolbar.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._60sdp), 0, 0);
                    manageDifferentFragmentTool();
                    super.onBackPressed();
                    return;
                }
            }else if(getSupportFragmentManager().findFragmentByTag("searchContactFragment") != null){
                if(chatFragment != null){
                    Utility.hideKeyboard(MainActivity.this);
                    toolbar.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    searchViewCustom.setVisibility(View.GONE);
                    fullToolBar.setVisibility(View.VISIBLE);
                    Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._60sdp), 0, 0);
                    chatFragment.initToolBar();
                    super.onBackPressed();
                    return;
                }else {
                    Utility.hideKeyboard(MainActivity.this);
                    search_data_edittext.setText("");
                    fullToolBar.setVisibility(View.VISIBLE);
                    searchViewCustom.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    fullToolBar.setVisibility(View.VISIBLE);
                    manageDifferentFragmentTool();
                    super.onBackPressed();
                    return;
                }
            }
            else if (getSupportFragmentManager().findFragmentByTag("ReadPhoneContactsAndFriendFragment") != null) {
                Utility.hideKeyboard(MainActivity.this);
                tabLayout.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                fullToolBar.setVisibility(View.VISIBLE);
                searchViewCustom.setVisibility(View.GONE);
                manageDifferentFragmentTool();
                super.onBackPressed();
                return;
            } else if (getSupportFragmentManager().findFragmentByTag("FullScreenImagesViewPagesGallery") != null) {
                Utility.hideKeyboard(MainActivity.this);
                manageDifferentFragmentTool();
                super.onBackPressed();
                return;
            }//MapViewFragment
            else if (getSupportFragmentManager().findFragmentByTag("MapViewFragment") != null) {
                Utility.hideKeyboard(MainActivity.this);
                super.onBackPressed();
                return;
            } else if (getSupportFragmentManager().findFragmentByTag("UserGroupProfileFragment") != null) {
                Utility.hideKeyboard(MainActivity.this);
                toolbar.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._60sdp), 0, 0);
                super.onBackPressed();
                return;
            } else if (chatFragment != null) {
                if (chatFragment.onBackPressed()) {
                    Utility.hideKeyboard(MainActivity.this);
                    tabLayout.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._60sdp), 0, 0);
                    chatFragment = null;
                    manageDifferentFragmentTool();
                    Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                    if (viewPager.getCurrentItem() == 0 && page != null) {
                        ((FriendsListFragment) page).getLetestMessage();
                    }
                }

            }
            else if(getSupportFragmentManager().findFragmentByTag("addGroupFragment") != null){
                super.onBackPressed();
            }
            else{
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        } catch (Exception ex) {
             ex.printStackTrace();
        }

    }

    public void manageDifferentFragmentTool() {
        if (adapter.getCurrentFragment() instanceof FriendsListFragment) {
            ((FriendsListFragment) adapter.getCurrentFragment()).initToolBar();
        } else if (adapter.getCurrentFragment() instanceof GroupListFragment) {
            ((GroupListFragment) adapter.getCurrentFragment()).initToolBar();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            senderID = intent.getStringExtra("sender_id");
            isGroup = intent.getBooleanExtra("is_group", false);
            if (isGroup)
                receiverId = intent.getStringExtra("receiver_id");
            else
                receiverId = null;

        } catch (NullPointerException ex) {
            senderID = null;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startChatFragmentWithNotification();
            }
        }, 100);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseDataSingleton.getInstance(this).getUser() != null)
            FireBaseSingleton.getInstance(this).updateUserStatus("Online");

    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        try {
            FireBaseSingleton.getInstance(this).updateUserStatus("Offline");
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
        super.onPause();
    }

    public void addFragment(Fragment fragment, String fragmentTAG) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame_content, fragment, fragmentTAG);
        fragmentTransaction.addToBackStack(fragmentTAG);
        fragmentTransaction.commit();
    }


    public void startChartFragment(GroupModel groupModel, FriendUser friendUser, String messageID) {
        Fragment tempFragment = getSupportFragmentManager().findFragmentByTag("ReadPhoneContactsAndFriendFragment");
        if (tempFragment != null) {
            onBackPressed();
        }
        chatFragment = new ChatFragment();
        chatFragment.setFriend(friendUser);
        chatFragment.setGroupModel(groupModel);
        chatFragment.setMessageID(messageID);
        addFragment(chatFragment, "ChatFragment");
    }

    @Override
    public void onUserUpdate() {

    }

    private void showUserProfile() {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        addFragment(userProfileFragment, "UserProfileFragment");
    }

    public void showFriendProfile(FriendUser user) {
        UserProfileFragment userProfileFragment = new UserProfileFragment(true);
        userProfileFragment.setFriendUser(user);
        addFragment(userProfileFragment, "UserProfileFragment");
    }

    public void showGroupProfile(GroupModel groupModel) {
        GroupProfileFragment groupProfileFragment = new GroupProfileFragment();
        groupProfileFragment.setGroupMembers(groupModel);
        addFragment(groupProfileFragment, "UserGroupProfileFragment");


    }

    @Override
    public void onFriendsUpdate() {
        if (directLaunchMAinActivity) {
            FireBaseSingleton.getInstance(this).updateUserStatus("Online");
            FireBaseSingleton.getInstance(this).updateUserStatus("Online");
            directLaunchMAinActivity = false;
        }
        if ((adapter.getCurrentFragment()) instanceof FriendsListFragment) {
            ((FriendsListFragment) adapter.getCurrentFragment()).onRefreshData();
            startChatFragmentWithNotification();

        }
    }

    public void startChatFragmentWithNotification() {
        if (senderID != null) {
            if (!isGroup) {
                FriendUser user = FirebaseDataSingleton.getInstance(MainActivity.this).getFriendFromUserID(senderID);
                if (user != null) {
                    startChartFragment(null, user, null);
                    senderID = null;
                }
            } else {
                List<GroupModel> group = RealamDatabase.getInstance().getListOfGroupFormDB(receiverId);
                if (group != null) {
                    startChartFragment(group.get(0), null, null);
                    senderID = null;
                }
            }
        }
    }

    @Override
    public void onGroupUpdate() {
        if ((adapter.getCurrentFragment()) instanceof GroupListFragment) {
            ((GroupListFragment) adapter.getCurrentFragment()).onRefreshData();
        }
    }


    public void popAllTheFragments() {

        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }

        toolbar.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._50sdp), 0, 0);
        manageDifferentFragmentTool();
        viewPager.setCurrentItem(1);
    }


    public void popAllTheFragment() {

        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }

        toolbar.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        Utility.setMargins(findViewById(R.id.frame_content), 0, (int) getResources().getDimension(R.dimen._50sdp), 0, 0);
        manageDifferentFragmentTool();
        viewPager.setCurrentItem(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Fragment tempFragment = getSupportFragmentManager().findFragmentByTag("UserProfileFragment");

            Uri filePath = data.getData();
            if (tempFragment != null) {
                ((UserProfileFragment) tempFragment).setImagePath(filePath);
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private Fragment mCurrentFragment;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);

        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel();
            }
        });
        return interstitialAd;
    }
    private void goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }
    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            goToNextLevel();
        }
    }
}
