//package chatapp.abhiandroid_1.com.chatapp.backup.notification;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.support.v4.content.LocalBroadcastManager;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.firebase.messaging.RemoteMessage;
//import com.notification.fcm.library.Service.SaveFCMIdService;
//import com.notification.fcm.library.helpers.SharedPrefManager;
//
///**
// * manages the registration & deregistration of
// */
//public class NotificationManager {
//    /**
//     * Class instance
//     */
//    private static NotificationManager instance = null;
//
//    /**
//     * Holding Context
//     */
//    private Context mContext;
//
//    /**
//     * FCM Listener
//     */
//    private NotificationListener mNotificationListener;
//
//    /**
//     * Private constructor
//     *
//     * @param mContext
//     */
//    private NotificationManager(Context mContext) {
//        this.mContext = mContext;
//        init();
//    }
//
//    /**
//     * Singleton instance method
//     *
//     * @param context
//     * @return
//     */
//    public static NotificationManager getInstance(Context context) {
//        if (instance == null)
//            instance = new NotificationManager(context);
//        return instance;
//    }
//
//    /**
//     * Register listener
//     *
//     * @param notificationListener
//     */
//    public void registerListener(NotificationListener notificationListener) {
//        this.mNotificationListener = notificationListener;
//    }
//
//    /**
//     * Unregister listener. No longer need to notify.
//     */
//    public void unRegisterListener() {
//        mNotificationListener = null;
//    }
//
//    /**
//     * Initializes shared preferences, checks google play services if available,
//     * and register device to FCM server.
//     */
//    public void init() {
//
//        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
//                new IntentFilter(SharedPrefManager.REGISTRATION_COMPLETE));
//
//        /**
//         * check if device has Google play service on it
//         */
//        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
//        int status = api.isGooglePlayServicesAvailable(mContext);
//
//        /**
//         * If true, process the {@link SaveFCMIdService}
//         */
//        if (status == ConnectionResult.SUCCESS) {
//            mContext.startService(new Intent(mContext, SaveFCMIdService.class));
//        } else {
//            if (mNotificationListener != null)
//                mNotificationListener.onPlayServiceError();
//        }
//    }
//
//    /**
//     * Message receiver onReceive method called when registration ID(Token) is
//     * available
//     */
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (SharedPrefManager.hasFCMToken(context) && mNotificationListener != null) {
//                mNotificationListener.onDeviceRegistered(SharedPrefManager.getFCMToken(context));
//            }
//        }
//    };
//
//    /**
//     * Called by service when message received. Notify Listener
//     * if it s not null.
//     */
//    public void onMessage(RemoteMessage remoteMessage) {
//        if (mNotificationListener != null)
//            mNotificationListener.onMessage(remoteMessage);
//    }
//
//
//
//}
