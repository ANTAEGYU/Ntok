package com.ntok.chatmodule.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.sharedPreference.UserPreferences;
import com.ntok.chatmodule.view.NonSwipeableViewPager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private NonSwipeableViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
//    private LinearLayout dotsLayout;
//    private TextView[] dots;
    private int[] layouts;
    private LinearLayout btn_nextll;
    private TextView btnNext ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent


        if (!UserPreferences.getInstance(WelcomeActivity.this).isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager =  findViewById(R.id.view_pager);
        btnNext =   findViewById(R.id.btn_next);
        btn_nextll =   findViewById(R.id.btn_nextll);

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide5,
                R.layout.welcome_slide7};


        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btn_nextll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {

                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    Log.e("Tag", "Current item" + current);
                    /*if (current == 7) {
                        askForRuntimePermissionSMS(WelcomeActivity.this);
                        viewPager.setCurrentItem(current);
                    }else*/ if(current ==6){
                        askForRuntimePermissionSMS(WelcomeActivity.this);
                        viewPager.setCurrentItem(current);
                    }
                    else if(current ==5){
                        askForRuntimePermissionStorage(WelcomeActivity.this);
                        viewPager.setCurrentItem(current);
                    }
                    else if(current ==4){
                        askForRuntimePermissionLocation(WelcomeActivity.this);
                        viewPager.setCurrentItem(current);
                    }
                    else if(current ==3){
                        askForRuntimePermissionCamera(WelcomeActivity.this);
                        viewPager.setCurrentItem(current);
                    }
                    else if(current ==2){
                        askForRuntimePermissionContact(WelcomeActivity.this);
                        viewPager.setCurrentItem(current);
                    }else{
                        viewPager.setCurrentItem(current);
                    }
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    /**
     * Run time permission for sms
     * */
    private void askForRuntimePermissionSMS(WelcomeActivity welcomeActivity) {

        Dexter.withActivity(welcomeActivity)
                .withPermissions(Manifest.permission.READ_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }

    /**
     * Run time permission for Location
     * */
    private void askForRuntimePermissionLocation(WelcomeActivity welcomeActivity) {

        Dexter.withActivity(welcomeActivity)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }

    /**
     * Run time permission for camera
     * */
    private void askForRuntimePermissionCamera(WelcomeActivity welcomeActivity) {

        Dexter.withActivity(welcomeActivity)
                .withPermissions(
                        Manifest.permission.CAMERA )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }
    /**
     * Run time permission for storage
     * */
    private void askForRuntimePermissionStorage(WelcomeActivity welcomeActivity) {

        Dexter.withActivity(welcomeActivity)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }
    /**
     * Run time permission for contact
     * */
    private void askForRuntimePermissionContact(WelcomeActivity welcomeActivity) {

        Dexter.withActivity(welcomeActivity)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }



//    private void addBottomDots(int currentPage) {
//        dots = new TextView[layouts.length];
//
//        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
//        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
//
//        dotsLayout.removeAllViews();
//        for (int i = 0; i < dots.length; i++) {
//            dots[i] = new TextView(this);
//            dots[i].setText(Html.fromHtml("&#8226;"));
//            dots[i].setTextSize(35);
//            dots[i].setTextColor(colorsInactive[currentPage]);
//            dotsLayout.addView(dots[i]);
//        }
//
//        if (dots.length > 0)
//            dots[currentPage].setTextColor(colorsActive[currentPage]);
//    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        UserPreferences.getInstance(WelcomeActivity.this).setFirstTimeLaunch(false);

        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }


//    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
//            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));

            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
