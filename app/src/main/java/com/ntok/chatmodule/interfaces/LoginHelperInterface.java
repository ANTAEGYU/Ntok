package com.ntok.chatmodule.interfaces;

import com.google.firebase.FirebaseException;

/**
 * Created by Sonam on 07-05-2018.
 */

public interface LoginHelperInterface {

    public void onRegisterComplete();

    public void onVerificationFailed(FirebaseException var1);

    public void onCodeSent();
}
