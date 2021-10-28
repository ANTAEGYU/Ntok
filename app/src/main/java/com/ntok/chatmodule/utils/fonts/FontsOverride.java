package com.ntok.chatmodule.utils.fonts;

import android.content.Context;
import android.graphics.Typeface;

import com.ntok.chatmodule.utils.Lg;

import java.lang.reflect.Field;


/**
 * Created by Sonam Gupta 8/05/208.
 */
public final class FontsOverride {
    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            Lg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Lg.printStackTrace(e);
        }
    }
}
