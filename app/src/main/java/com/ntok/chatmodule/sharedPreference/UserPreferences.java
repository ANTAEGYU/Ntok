package com.ntok.chatmodule.sharedPreference;


import android.content.Context;
import android.content.SharedPreferences;

import com.ntok.chatmodule.utils.Constants;

public class UserPreferences {
    private static UserPreferences mPref;
    private static Context mContext;
    private static SharedPreferences sp;

    private SharedPreferences getPref(String table){
        return mContext.getSharedPreferences(table,
                Context.MODE_PRIVATE);
    }
    private SharedPreferences.Editor getEditor(String table){
        android.content.SharedPreferences sp=mContext.getSharedPreferences(table,
                Context.MODE_PRIVATE);
        return  sp.edit();
    }
    /**
     * GET current class instance.
     * @param context
     * @return
     */
    public static UserPreferences getInstance(Context context){
        mPref=new UserPreferences();
        mContext=context;

        return mPref;

    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        getEditor(Constants.SESSION_TABLE).putBoolean(Constants.IS_FIRST_TIME_LAUNCH,isFirstTime).commit();
    }

    public boolean isFirstTimeLaunch() {
        return getPref(Constants.SESSION_TABLE).getBoolean(Constants.IS_FIRST_TIME_LAUNCH,true);
    }

    public void saveUserBlockTime(long blockCurrentTime){
        getEditor(Constants.SESSION_TABLE).putLong(Constants.UserStartBlockTime,blockCurrentTime).commit();
    }
    public long getSavedBlockTime() {
        return getPref(Constants.SESSION_TABLE).getLong(Constants.UserStartBlockTime,0);
    }

}

