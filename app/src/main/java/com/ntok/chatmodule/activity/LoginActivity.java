package com.ntok.chatmodule.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntok.chatmodule.ImageDisplayer.ApplicationUtil;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.fragment.SearchCountryFragment;
import com.ntok.chatmodule.interfaces.GetSelectedCountryInterface;
import com.ntok.chatmodule.interfaces.LoginHelperInterface;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.model.CountryModel;
import com.ntok.chatmodule.sharedPreference.UserPreferences;
import com.ntok.chatmodule.utils.Constants;
import com.ntok.chatmodule.utils.Utility;
import com.ntok.chatmodule.view.OtpView;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginHelperInterface {

    /**
     * Id to identity READ_CONTACTS permission request.
     * /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private EditText mPhoneNumber,loginUserName;
    private TextView mCountryCode;
    private ImageView imageCountry;
    private View mProgressView;
    private View mLoginFormView;
    private View loginForm;
    private TextView verification_msg;
    private View verifyPhoneNumber;
    private LinearLayout llCountryCode;
    private AppCompatImageView back_button;
    private TextView resendWaitingMsg;
    private final int RESEND_WAITING_TIMER = 60000;
    private String countyCodeNumber = null;
    private OtpView otpView;
    private CountDownTimer countDownTimer;
    public TextView tvResendCode;
    public Toolbar searchtollbar;
    public Menu search_menu;
    private int userBlockInterval = 10;
    public static String UserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Set up the login form.
        initViews();

    }


    private void startResendOTPTimer() {
        resendWaitingMsg.setVisibility(View.VISIBLE);
        tvResendCode.setEnabled(false);
        tvResendCode.setTextColor(getResources().getColor(R.color.light_blackk));
        countDownTimer = new CountDownTimer(RESEND_WAITING_TIMER, 1000) {
            public void onTick(long millisUntilFinished) {
                resendWaitingMsg.setText("Resend OTP in " + (millisUntilFinished / 1000) + " seconds");
            }
            public void onFinish() {
                resendWaitingMsg.setVisibility(View.INVISIBLE);
                tvResendCode.setEnabled(true);
                tvResendCode.setTextColor(getResources().getColor(R.color.light_black));
            }
        }.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FireBaseSingleton.getInstance(this).setActivity(this, this);
        requestForSmSPermission();
    }

    SearchCountryFragment searchCountryFragment;
    public OnClickListener countryPickClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ApplicationUtil.hideSoftKeyboard(mCountryCode);
            searchCountryFragment = SearchCountryFragment.getSearchCountryFragment(countryModels, null, new GetSelectedCountryInterface() {
                @Override
                public void onReceiveSelectedCountries(CountryModel selectedCountry) {
                    try {
                        if (search_menu.findItem(R.id.action_filter_search).isActionViewExpanded()) {
                            search_menu.findItem(R.id.action_filter_search).collapseActionView();
                        }
                    } catch (Exception ex) {

                    }
                    mCountryCode.setText("+" + selectedCountry.getCountryCallingCode());
                    int id;
                    if (selectedCountry.getCountryCode().toLowerCase().trim().equalsIgnoreCase("do")) {
                        id = getResources().getIdentifier("country_" + selectedCountry.getCountryCode().toLowerCase().trim(), "drawable", getPackageName());
                    } else {
                        id = getResources().getIdentifier(selectedCountry.getCountryCode().toLowerCase().trim(), "drawable", getPackageName());
                    }
                    imageCountry.setImageResource(id);
                    onBackPressed();
                }
            });
//            isItemSearchVisible = true;
            invalidateOptionsMenu();
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, searchCountryFragment, "SearchCountryFragment").commit();
//            toolbar.setVisibility(View.VISIBLE);
        }
    };
    public MenuItem item_search;

    public void initViews() {
        mPhoneNumber = findViewById(R.id.phone_number);
        loginUserName = findViewById(R.id.loginUserName);
        otpView = findViewById(R.id.otp_view);
        loginForm = findViewById(R.id.login_form);
        mCountryCode = findViewById(R.id.tv_country_code);
        imageCountry = findViewById(R.id.image_country);
        llCountryCode = findViewById(R.id.ll_country_code);
        verifyPhoneNumber = findViewById(R.id.verify_number);
        llCountryCode.setOnClickListener(countryPickClickListener);
        back_button = findViewById(R.id.back_button_imageView);
        resendWaitingMsg = findViewById(R.id.resend_waiting_msg);
        tvResendCode = findViewById(R.id.resend_code);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        verification_msg = findViewById(R.id.verification_msg);

        /**
         * Back button on code verification page
         * */
        back_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyPhoneNumber.getVisibility() == View.VISIBLE) {
                    loginForm.setVisibility(View.VISIBLE);
                    verifyPhoneNumber.setVisibility(View.GONE);
                    back_button.setVisibility(View.GONE);
                } else {
                    finish();
                }
            }
        });
        setSearchtollbar();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if(loginUserName.getText().toString().equalsIgnoreCase("") ){
                    Utility.showToast(LoginActivity.this,"Please enter your name");
                    return;
                }else if(mPhoneNumber.getText().toString().equalsIgnoreCase("")){
                    Utility.showToast(LoginActivity.this,"Please enter your phone number");
                }else{
                    UserName = loginUserName.getText().toString();
                    showProgress(true);
                    attemptLogin();
                }

            }
        });


        Button mConfirmOTP = (Button) findViewById(R.id.confirm_otp_in_button);
        mConfirmOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otpView.getOTP().length() != 6) {
                    Utility.showAltertPopup(LoginActivity.this, "Invalid OTP", "Please fill the valid OTP?", new OnAlertPopupClickListener() {
                        @Override
                        public void onOkButtonpressed() {

                        }

                        @Override
                        public void onCancelButtonPressed() {

                        }
                    });
                } else {
                    showProgress(true);
                    FireBaseSingleton.getInstance(LoginActivity.this).verifyPhoneNumberWithCode(otpView.getOTP(), LoginActivity.this);
                }
            }
        });

        tvResendCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.block_chances < userBlockInterval) {
                    Utility.showToast(LoginActivity.this, "You have only "+ --userBlockInterval +" attempts left.");
                    startResendOTPTimer();
                    showProgress(true);
                    FireBaseSingleton.getInstance(LoginActivity.this).resendVerificationCode(mCountryCode.getText().toString() + mPhoneNumber.getText().toString());
                } else {
                    tvResendCode.setEnabled(false);
                    tvResendCode.setTextColor(getResources().getColor(R.color.light_blackk));
                    Utility.showToast(LoginActivity.this, "You have been blocked for 1 day.");
                    java.util.Date date = new java.util.Date();
                    UserPreferences.getInstance(LoginActivity.this).saveUserBlockTime(date.getTime());
                }
            }
        });

        try {
            getCountry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setSearchtollbar() {
        searchtollbar = (Toolbar) findViewById(R.id.searchtoolbar_login);
        if (searchtollbar != null) {
            searchtollbar.inflateMenu(R.menu.menu_search);
            search_menu = searchtollbar.getMenu();

            searchtollbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.searchtoolbar_login, 1, true, false);
                    else
                        searchtollbar.setVisibility(View.GONE);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);
            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.searchtoolbar_login, 1, true, false);
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
        closeButton.setImageResource(R.drawable.ic_close);


        // set hint and the text colors

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search..");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(getResources().getColor(R.color.white));


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


    /**
     * /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        FireBaseSingleton.getInstance(LoginActivity.this).startPhoneNumberVerification(mCountryCode.getText().toString() + mPhoneNumber.getText().toString());
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }


    public void hideProgress() {
        mProgressView.setVisibility(View.GONE);
    }


    @Override
    public void onRegisterComplete() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            // Invalid request
            // [START_EXCLUDE]
            mPhoneNumber.setError("Invalid phone number.");
            mProgressView.setVisibility(View.GONE);
            // [END_EXCLUDE]
        } else if (e instanceof FirebaseTooManyRequestsException) {
            // The SMS quota for the project has been exceeded
            // [START_EXCLUDE]
            Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                    Snackbar.LENGTH_SHORT).show();
            // [END_EXCLUDE]
        }

    }

    @Override
    public void onCodeSent() {
        showProgress(false);
        loginForm.setVisibility(View.GONE);
        verifyPhoneNumber.setVisibility(View.VISIBLE);
        back_button.setVisibility(View.VISIBLE);
        otpView.enableKeypad();
        countyCodeNumber = mCountryCode.getText().toString().trim() + mPhoneNumber.getText().toString().trim();
        verification_msg.setText(getResources().getString(R.string.verification_msg) + " " + countyCodeNumber);
        if (Constants.block_chances < userBlockInterval) {
            Constants.block_chances++;
           startResendOTPTimer();
        } else {
            if (Utility.checkUserCompleteOneDay(UserPreferences.getInstance(LoginActivity.this).getSavedBlockTime())) {
                Constants.block_chances = 0;
                startResendOTPTimer();
            }else{
                Utility.showToast(LoginActivity.this,"You have been blocked for 1 day.");
            }
        }
    }

    /**
     * Request for sms permission from user
     */
    private void requestForSmSPermission() {
        Dexter.withActivity(LoginActivity.this)
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


    public void recivedSms(String message) {

        otpView.setOTP(message);

    }

    private ArrayList<CountryModel> countryModels;

    public void getCountry() throws IOException {
        countryModels = new ArrayList<>();
        InputStream is = getResources().openRawResource(R.raw.dialing_country_code);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }

        String jsonString = writer.toString();
        String[] arrayCountry = jsonString.split(";");
        for (String s : arrayCountry) {
            //Do your stuff here
            String[] subString = s.split("-");
            CountryModel countryModelObject = new CountryModel();
            if (subString.length == 3) {
                countryModelObject.setCountryName(subString[0]);
                countryModelObject.setCountryCallingCode(subString[1]);
                countryModelObject.setCountryCode(subString[2]);
                countryModels.add(countryModelObject);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (verifyPhoneNumber.getVisibility() == View.VISIBLE) {
            back_button.performClick();
        } else {
            super.onBackPressed();
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
                    myView.setVisibility(View.GONE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();


    }

    /**
     * Called when no internet or wrong OTP from FireBaseSingleton class
     */
    public void clearOtpAndStartWithNewNumber() {
        otpView.simulateDeletePress();
        loginForm.setVisibility(View.VISIBLE);
        verifyPhoneNumber.setVisibility(View.GONE);
        mPhoneNumber.setText("");
    }


}

