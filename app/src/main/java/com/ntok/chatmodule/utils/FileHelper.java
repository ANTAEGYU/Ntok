package com.ntok.chatmodule.utils;
import android.os.Environment;

import java.io.File;

/**
 * Created by Sonam on 15-05-2018.
 */

public class FileHelper {
    private static String SD_CARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static File getDirectory(String rootFolder) {
        File filesRoot = new File(SD_CARD_ROOT + "/" + rootFolder);
        if (!filesRoot.exists()) {
            boolean var2 = filesRoot.mkdir();
        }

        return filesRoot;
    }

}
