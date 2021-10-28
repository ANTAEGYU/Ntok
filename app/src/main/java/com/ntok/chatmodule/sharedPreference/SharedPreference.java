package com.ntok.chatmodule.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.ntok.chatmodule.model.User;
import com.google.gson.Gson;


/**
 * Created by Sonam on 07-05-2018.
 */

public class SharedPreference {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USER = "user";
    public static final String DEVICE_TOKEN = "device_token";
    public static SharedPreference sharedPreference;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private static Context mContext;

    private SharedPreference(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        pref = context.getSharedPreferences(MyPREFERENCES, 0);
        editor = sharedpreferences.edit();
    }

    public static SharedPreference getInstance(Context context) {
        if (sharedPreference == null) {
            sharedPreference = new SharedPreference(context);
        }
        return sharedPreference;
    }

    public User getUser() {
        Gson gson = new Gson();
        String json = pref.getString(USER, "");
        User obj = gson.fromJson(json, User.class);
        return obj;

    }

    public void setUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(USER, json);
        editor.commit();
    }

    public String getDeviceToken() {
        return pref.getString(DEVICE_TOKEN, null);

    }

    public void setDeviceToken(String device_token) {

        editor.putString(DEVICE_TOKEN, device_token);
        editor.commit();
    }

    public void clearSharedPReference(){
        editor.clear().commit();
    }
}
