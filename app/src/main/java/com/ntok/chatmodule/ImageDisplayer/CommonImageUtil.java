package com.ntok.chatmodule.ImageDisplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by Sonam Gupta on 19/10/16.
 */
public class CommonImageUtil {

    //error --http://stackoverflow.com/questions/32623304/when-take-photo-get-java-lang-throwable-file-uri-exposed-through-clipdata
//    public static Uri createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "05Media_JPEG_" + timeStamp + "_";
//
//        File storageDir = new File(getNormalImagesDirectory());
//        boolean success = true;
//        if (!storageDir.exists()) {
//            success = storageDir.mkdirs();
//        }
//        if (success) {
//            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//            return Uri.fromFile(image);
//        }
//        return null;
//    }

    public static Uri createGIFImageFile(String urlName) throws IOException {
        String imageFileName;

        imageFileName = getGifFileName(urlName);

        File storageDir = new File(getGifImageDirectory());
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File exist = new File(storageDir.getPath() + imageFileName);
            if (exist.exists()) {
                return Uri.fromFile(exist);
            }
            File image = File.createTempFile(imageFileName, "", storageDir);
//            File image = new File(storageDir + "/" + imageFileName);
//            image.createNewFile();

            return Uri.fromFile(image);
        }
        return null;
    }

    public static Uri createImageFile(String imageFileName) throws IOException {

        String stored = null;

        File sdcard = Environment.getExternalStorageDirectory();

        File folder = new File(sdcard.getAbsoluteFile(), "ChatApp");//the dot makes this directory hidden to the user
        folder.mkdir();
        File file = new File(folder.getAbsoluteFile(), imageFileName);
        if (!file.exists())
            file.createNewFile();

        return Uri.fromFile(file);
    }

    private static String getGifFileName(String urlName) {

        String imageFileName;
        if (urlName.equals("")) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "chat_GIF_" + timeStamp + "_";
        } else {
            String lastPart = urlName.toString().substring(urlName.lastIndexOf('/') + 1).trim();
            String secondLastPart = urlName.replace("/" + lastPart, "");
            secondLastPart = secondLastPart.toString().substring(secondLastPart.lastIndexOf('/') + 1).trim();

            urlName = secondLastPart + "_" + lastPart;

            imageFileName = "chat_GIF_" + urlName;
        }
        return imageFileName;
    }

    public static boolean isFileAlreadyExists(File file) {
        Bitmap b = null;
        String path = file.getAbsolutePath();

        if (path != null) b = BitmapFactory.decodeFile(path);

        if (b == null || b.equals("")) {
            return false;
        }
        return true;
    }

//    public static String isFileAlreadyExists(String imageFileName) {
//
//        File storageDir = new File(getGifImageDirectory());
//        boolean success = true;
//        if (!storageDir.exists()) {
//            success = storageDir.mkdirs();
//        }
//        if (success) {
//            File exist = new File(storageDir.getPath() + "/" + imageFileName);
//            if (exist.exists() || exist.canRead() || exist.isFile()) {
//                return imageFileName;
//            }
//        }
//        return null;
//    }

    private static String getGifImageDirectory() {
        return Environment.getExternalStorageDirectory() + "/ChatApp/GifImages";
    }

    private static String getSavedImagesDirectory() {
        return Environment.getExternalStorageDirectory() + "/ChatApp/SavedImages";
    }

    private static String getNormalImagesDirectory() {
        return Environment.getExternalStorageDirectory() + "/ChatApp/CameraImages";
    }

    // For to Delete the directory inside list of files and inner Directory
    public static boolean deleteGIFDirectory(File path) {

        if (path == null) {
            path = new File(getGifImageDirectory());
        }
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteGIFDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    // For to Delete the directory inside list of files and inner Directory
    public static boolean deleteFile(File file) {
        if (file != null && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    //error --http://stackoverflow.com/questions/32623304/when-take-photo-get-java-lang-throwable-file-uri-exposed-through-clipdata
    public static Uri createCaptureImageFile(Context ctx) {
//        ContentValues values = new ContentValues(1);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//        Uri mCameraTempUri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //  Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //    File f = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
        //  chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        //  Uri mCameraTempUri = Uri.fromFile(f);
        // startActivityForResult(chooserIntent, 100);

        return Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));

        //   return mCameraTempUri;
    }

    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "camera");

        createMediaStorageDir(mediaStorageDir);

        return createFile(type, mediaStorageDir);
    }

    private static void createMediaStorageDir(File mediaStorageDir) // Used to be 'private void ...'
    {
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs(); // Used to be 'mediaStorage.mkdirs();'
        }
    } // Was flipped the other way

    private static File createFile(int type, File mediaStorageDir) // Used to be 'private File ...'
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        }
        return mediaFile;
    }


    public static boolean isUriPath(String path) {
        if (path != null && path.contains("content://")) {
            return true;
        }
        return false;
    }

    /**
     * Returns filePath by converting uri to path / path with  "file://"
     *
     * @param context
     * @param path
     * @return
     */
    public static String getCorrectFilePath(Context context, String path) {
        if (isUriPath(path)) {
            Uri uri = Uri.parse(path);
            path = ApplicationUtil.getFilePathFromURI(context, uri);
            if (!path.startsWith("file://")) {
                return "file://" + path;
            }
            return path;
        }
        if (!path.startsWith("file://")) {
            path = "file://" + path;
            Uri uri = Uri.parse(path);
            path = ApplicationUtil.getFilePathFromURI(context, uri);
            if (!path.startsWith("file://")) {
                return "file://" + path;
            }
            return path;
        }
        return path;
    }

    /*
    Returns Uri with by storagePath (without content://)
     */
    public static Uri getCorrectFileUri(Context context, String path) {
        Uri uri = Uri.parse(getCorrectFilePath(context, path));
        return uri;
    }

    /**
     * Returns URI with original path, for Adobe edited image only
     *
     * @param context
     * @param editedImageUri
     * @return
     */
    public static Uri getValidAdobeImageUri(Context context, Uri editedImageUri) {
        if (!isUriPath(editedImageUri + "")) {
            if (!editedImageUri.toString().startsWith("file://")) {
                return Uri.parse("file://" + editedImageUri);
            } else {
                String path = editedImageUri.toString().replace("file://", "");
                return Uri.parse("file://" + path);
            }
        } else if (editedImageUri.toString().startsWith("file://")) {
            return Uri.parse("" + editedImageUri.toString().replace("file://", ""));
        }
        return getCorrectFileUri(context, editedImageUri + "");
    }

    public static void removeImageFromImageView(ImageView imageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(null);
        }
        imageView.setImageDrawable(null);
    }

//    public static void loadWithIon(ImageView imageView, String url, boolean showPlaceHolder) {
//        removeImageFromImageView(imageView);
//        if (showPlaceHolder) {
//            Ion.with(imageView).fitCenter().placeholder(R.drawable.gif_place_holder).load(url);
//        } else {
//            Ion.with(imageView).fitCenter().load(url);
//        }
//    }


    public static void setBackground(Context context, View imageView, int drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(context.getResources().getDrawable(drawable));
        } else {
            imageView.setBackgroundResource(drawable);
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
