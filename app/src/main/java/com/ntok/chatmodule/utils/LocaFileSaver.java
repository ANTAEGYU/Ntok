package com.ntok.chatmodule.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import com.ntok.chatmodule.ImageDisplayer.CommonImageUtil;
import com.ntok.chatmodule.model.MessageModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by Sonam Gupta 21-05-2018
 */

public class LocaFileSaver extends AsyncTask<Void, String, Boolean> {

    private final SaveCompletionInterface saveCompletionInterface;
    private final String originalImageUrl;
    private final Context context;
    private String imageNameToSave;
    private String savedImagePath;
    private String fUrl;
    private String type;

    public LocaFileSaver(Context context, String imageNameToSave, String originalImageUrl, SaveCompletionInterface saveCompletionInterface, String type) {
        this.context = context;
        this.saveCompletionInterface = saveCompletionInterface;
        this.imageNameToSave = null;
        this.originalImageUrl = originalImageUrl;
        this.type = type;
    }

    /**
     * Downloading file in background thread
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Boolean doInBackground(Void... f_url) {

        Lg.debug(LocaFileSaver.class.getName(), originalImageUrl);
        this.fUrl = originalImageUrl;

        FileOutputStream output = null;
        InputStream is = null;
        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(fUrl);
            HttpContext context = new BasicHttpContext();
            HttpResponse response = client.execute(get, context);
            is = response.getEntity().getContent();
            int status = response.getStatusLine().getStatusCode();

            if (status == 200 && is != null) {

                if (imageNameToSave == null) {
                    String extension = "";
                    if (type.equalsIgnoreCase(MessageModel.VIDEO_TYPE)) {
                        extension = "mp4";
                    } else if (type.equalsIgnoreCase(MessageModel.IMAGE_TYPE)) {
                        extension = "png";
                    } else if (type.equalsIgnoreCase(MessageModel.AUDIO_TYPE)) {
                        extension = "mp3";
                    }


                    String fileName = "";//originalImageUrl.substring(originalImageUrl.lastIndexOf("/") + 1); // Without dot jpg, png
                    fileName = "Chat_App" + Calendar.getInstance().getTimeInMillis() + "." + extension;
                    imageNameToSave = fileName;
                }


                Uri savedImagePathUri = CommonImageUtil.createImageFile(imageNameToSave);
                savedImagePath = savedImagePathUri.getPath();
                // Output stream to write file
                output = new FileOutputStream(savedImagePathUri.getPath());

                int read = 0;
                byte[] buffer = new byte[32768];
                while ((read = is.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                is.close();
                return true;
            }

        } catch (ClientProtocolException e) {
            Lg.printStackTrace(e);
        } catch (IOException e) {
            Lg.printStackTrace(e);
        } catch (Exception e) {
            Lg.printStackTrace(e);
        } finally {

            // flushing output
            try {
                if (output != null) {
                    output.flush();
                }
            } catch (IOException e) {
                Lg.printStackTrace(e);
            }
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                Lg.printStackTrace(e);
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Lg.printStackTrace(e);
            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        saveCompletionInterface.onSaved(result, savedImagePath);
    }

    public interface SaveCompletionInterface {
        public void onSaved(boolean result, String imageNameToSave);
    }
}

