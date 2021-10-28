package com.ntok.chatmodule.utils;

import android.util.Log;

/**
 * Created by Sonam on 07-05-2018.
 */

public class Lg {

    static boolean isDebuggable = true;
    private static String TAG = "MovieApp";

    public static void debug(String tag, String text) {
        if (isDebuggable) {
            Log.d(tag, text);
        }
    }

    public static void d(String tag, String text) {
        if (isDebuggable) {
            Log.d(tag, text);
        }
    }

    public static void d(String tag, boolean text) {
        if (isDebuggable) {
            Log.d(tag, text + "");
        }
    }

    public static void d(String text) {
        if (isDebuggable) {
            Log.d(TAG, text + "");
        }
    }

    public static void d(String tag, String text, Exception e) {
        if (isDebuggable) {
            Log.d(tag, text, e);
        }
    }

    public static void info(String tag, String text) {
        if (isDebuggable) {
            Log.i(tag, text);
        }
    }

    public static void info(String text) {
        if (isDebuggable) {
            Log.i("unknown", text);
        }
    }

    public static void i(String tag, String text) {
        if (isDebuggable) {
            Log.i(tag, text);
        }
    }

    public static void error(String tag, String text) {
        if (isDebuggable) {
            Log.e(tag, text);
        }
    }

    public static void e(String tag, String text) {
        if (isDebuggable) {
            Log.e(tag, text);
        }
    }

    public static void e(String tag, String text, Exception e) {
        if (isDebuggable) {
            Log.e(tag, text, e);
        }
    }

    public static void verbose(String tag, String text) {
        if (isDebuggable) {
            Log.v(tag, text);
        }
    }

    public static void v(String tag, String text) {
        if (isDebuggable) {
            Log.v(tag, text);
        }
    }

    public static void w(String tag, String text) {
        if (isDebuggable) {
            Log.w(tag, text);
        }
    }

    public static void w(String tag, String text, Exception e) {
        if (isDebuggable) {
            Log.w(tag, text, e);
        }
    }

    public static void printStackTrace(Exception exception) {
        if (isDebuggable) {
            if (exception == null) {
                return;
            }
            exception.printStackTrace();
        }
    }


    public static void e(Exception exception) {
        if (isDebuggable) {
            if (exception == null) {
                return;
            }
            exception.printStackTrace();
        }
    }


    public static void e(String exception) {
        if (isDebuggable) {
            if (exception == null) {
                return;
            }
            Log.e(TAG, exception + "");
        }
    }


    public static void printStackTrace(Throwable exception) {
        if (isDebuggable) {
            if (exception == null) {
                return;
            }
            exception.printStackTrace();
        }
    }

    public static void printStackTrace(OutOfMemoryError exception) {
        if (isDebuggable) {
            if (exception == null) {
                return;
            }
            exception.printStackTrace();
        }
    }

    public static void debug(String s) {
        Lg.debug("Unknown Lg : ", s);
    }
}

