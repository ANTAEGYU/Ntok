package com.ntok.chatmodule.fragment;

/**
 * Created by Sonam on 08-05-2018.
 */

import android.support.v4.app.Fragment;


public abstract class RootFragment extends Fragment {
    private static final String TAG = RootFragment.class.getSimpleName();

    public abstract void onRefreshData();

    public abstract boolean onBackPressed();

    @Override
    public void onStop() {
        super.onStop();
    }

}
