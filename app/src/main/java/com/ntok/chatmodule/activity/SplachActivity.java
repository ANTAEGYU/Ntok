package com.ntok.chatmodule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.datatbase.RealamDatabase;
import com.ntok.chatmodule.interfaces.LoginHelperInterface;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.sharedPreference.SharedPreference;
import com.google.firebase.FirebaseException;

public class SplachActivity extends AppCompatActivity implements LoginHelperInterface {
    String senderID;

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splach);

        FireBaseSingleton.getInstance(this).setActivity(this, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkUser();
            }
        },2000);
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