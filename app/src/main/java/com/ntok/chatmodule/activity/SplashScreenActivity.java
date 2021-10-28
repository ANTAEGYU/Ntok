package com.ntok.chatmodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.interfaces.LoginHelperInterface;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.sharedPreference.SharedPreference;
import com.google.firebase.FirebaseException;

/**
 * Created by Sonam on 07-05-2018.
 */

public class SplashScreenActivity extends Activity implements LoginHelperInterface {

    String senderID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FireBaseSingleton.getInstance(this).setActivity(this, this);
//        checkUser();
    }

    public void checkUser() {
        if (SharedPreference.getInstance(getApplicationContext()).getUser() == null) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            FirebaseDataSingleton.getInstance(this).setUser(SharedPreference.getInstance(getApplicationContext()).getUser());
            FirebaseDataSingleton.getInstance(this).setFriendList(RealamDatabase.getInstance().getAllFriends());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRegisterComplete() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onVerificationFailed(FirebaseException var1) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            senderID = intent.getStringExtra("serder_id");
            setIntent(null);
        } catch (NullPointerException ex) {
            senderID = null;
        }
    }

    @Override
    public void onCodeSent() {

    }
}
