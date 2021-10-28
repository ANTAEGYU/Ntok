package com.ntok.chatmodule.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.utils.fonts.FontsOverride;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by Sonam on 07-05-2018.
 */
public class MainApplication extends MultiDexApplication {

    public static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
        // initialize the AdMob app
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
//        MobileAds.initialize(this, getString(R.string.addmob_app_id));
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(config);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FireBaseSingleton.getInstance(this);
        manageFonts();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        ImageDisplayer.init(getApplicationContext());
    }


    private void manageFonts() {
        //changed app font to Roboto-Regular(works with some text views only, so for others added manually by using default_style)
        FontsOverride.setDefaultFont(this, "DEFAULT", "Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Roboto-Regular.ttf");
    }
    public static Context getAppContext() {
        return MainApplication.context;
    }
}
