package com.ntok.chatmodule.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ntok.chatmodule.fragment.GroupProfileFragment;
import com.ntok.chatmodule.interfaces.OnAlertPopupClickListener;
import com.ntok.chatmodule.interfaces.OnTowButtonDialogInterface;
import com.ntok.chatmodule.model.ContactName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Utility {

    private static String currentCameraFileName = "";
    private static int screenWidth = 0;
    private static ProgressDialog dialog;

    public static void showAltertPopup(Context context, String title, String message, final OnAlertPopupClickListener onAlertPopupClickListener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onAlertPopupClickListener.onOkButtonpressed();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onAlertPopupClickListener.onCancelButtonPressed();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static boolean  isOnline(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showToast(Context context,String message){
          Toast mToast = null;

        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static Intent getCameraIntent(Context context) throws IOException {
        currentCameraFileName = "outputImage" + System.currentTimeMillis() + ".jpg";
        File imagesDir = new File(context.getFilesDir(), "images");
        imagesDir.mkdirs();
        File file = new File(imagesDir, currentCameraFileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.d("Tag", "openCamera: coudln't crate ");
            e.printStackTrace();
        }
        Log.d("TAG", "openCamera: file exists " + file.exists() + " " + file.toURI().toString());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String authority = context.getPackageName() + ".smart-image-picket-provider";
        final Uri outputUri = FileProvider.getUriForFile(
                context.getApplicationContext(),
                authority,
                file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        context.grantUriPermission(
                "com.google.android.GoogleCamera",
                outputUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION
        );
        return cameraIntent;
    }

    public static Uri getURI(Context context, File file){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider",file);
        else
            return Uri.fromFile(file);
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }


    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void showProgressDialog(Context context,String message){
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
    }
    public static void hideProgressDialog() {
        if(dialog != null){
            dialog.dismiss();
        }
    }

    public static void askForDialogConfermation(FragmentActivity activity, String title, String message, final GroupProfileFragment.ConfermationInterface confermationInterface) {

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        confermationInterface.onYes();
                    }})
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean checkUserCompleteOneDay(long savedBlockTime) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp1 = new Timestamp(savedBlockTime);
        Timestamp timestamp2 = new Timestamp(date.getTime());

        long milliseconds = timestamp2.getTime() - timestamp1.getTime();
        int seconds = (int) milliseconds / 1000;
        seconds = (seconds % 3600) % 60;
        if(seconds >120 /*86400*/){
            return true;
        }
        return false;
    }




    public static void logoutConfermation(Activity activity, final OnTowButtonDialogInterface onTowButtonDialogInterface){

        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onTowButtonDialogInterface.ok();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onTowButtonDialogInterface.cancel();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static String getLastnCharacters(String inputString,
                                     int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }


    public static void saveArrayList(Context context, ArrayList<ContactName> arrayList){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(Constants.LISTOFNAME, json);
        editor.commit();
    }

    public static ArrayList<ContactName> getArrayList(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(Constants.LISTOFNAME, "");
        Type type = new TypeToken<ArrayList<ContactName>>() {}.getType();
         return gson.fromJson(json, type);
    }


}
