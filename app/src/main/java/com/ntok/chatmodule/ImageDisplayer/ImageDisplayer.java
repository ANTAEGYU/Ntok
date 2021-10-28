package com.ntok.chatmodule.ImageDisplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ntok.chatmodule.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageDisplayer {

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        try {
            Glide.with(context);
        } catch (Exception e) {
//            Lg.printStackTrace(e);
            return;
        }

    }


    public static void displayImage(String imageUrl, ImageView view, RequestListener callback, Context context) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        Log.v("displayImage imageUrl: ", imageUrl);

        try {
            if (callback != null)
                Glide.with(context).load(imageUrl).error(R.drawable.default_user_black).listener(callback).into(view);
            else
                Glide.with(context).load(imageUrl).error(R.drawable.default_user_black).into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void displayImage(final String imageUrl, final View view, final Context context) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        Log.v("displayImage imageUrl: ", imageUrl);

        try {


            Glide.with(context).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>(100, 100) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(drawable);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayImageFromURi(Context mContext, Uri pictureUri, ImageView imageView) {
        Glide.with(mContext)
                .load(pictureUri).into(imageView);
    }

    public static Bitmap getBitmapFromURL(String imageUrl, Context context) {
        try {
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } else {
                File image = new File((imageUrl.startsWith("content://")) ? ApplicationUtil.getFilePathFromURI(context, Uri.parse(imageUrl)) : imageUrl);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                return bitmap;
            }
        } catch (IOException e) {
            // Lg exception
            e.printStackTrace();
            return null;
        }

    }
}
