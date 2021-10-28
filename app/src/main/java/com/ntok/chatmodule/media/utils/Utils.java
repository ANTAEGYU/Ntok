package com.ntok.chatmodule.media.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.ntok.chatmodule.model.MessageModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Created by roman on 8/7/17.
 */

public class Utils {

    public static String formatTimeSecondsToMinutes(long totalSecs) {
        long minutes = TimeUnit.SECONDS.toMinutes(totalSecs);
        totalSecs -= TimeUnit.MINUTES.toSeconds(minutes);
        long seconds = TimeUnit.SECONDS.toSeconds(totalSecs);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public static Uri getUriFromAttachPublicUrl(MessageModel attachment) {
        if (attachment.getAudioModelClass().getLocalAudioLink() == null)
            return Uri.parse(attachment.getAudioModelClass().getAudioLink());
        else
            return Uri.parse("file://" + attachment.getAudioModelClass().getLocalAudioLink());
    }

    public static long getDurationFromAttach(Uri attachment, Context context) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, attachment);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        return millSecond / 1000;
    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(5, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

}