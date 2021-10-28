package com.ntok.chatmodule.OTPVerification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.ntok.chatmodule.activity.LoginActivity;
import com.ntok.chatmodule.utils.Lg;

public class IncomingSms extends BroadcastReceiver
{
    LoginActivity loginActivity;
    @Override
    public void onReceive(Context context, Intent intent)
    {

        final Bundle bundle = intent.getExtras();
        loginActivity = new LoginActivity();
        try {
            if (bundle != null)
            {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj .length; i++)
                {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])                                                                                                    pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber ;
                    String message = currentMessage .getDisplayMessageBody();
                    try
                    {
                        if (message .contains("is your verification code"))
                        {
                            loginActivity.recivedSms(message );
                        }
                    }
                    catch(Exception e){
                        Lg.printStackTrace(e);
                    }

                }
            }

        } catch (Exception e)
        {

        }
    }

}

