package com.ntok.chatmodule.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.model.MessageModel;
import com.ntok.chatmodule.model.User;
import com.ntok.chatmodule.utils.Lg;


/**
 * Created by jiffy02 on 3/2/2017.
 */

public class LoadVideoWebviewFragment extends DialogFragment {
    ProgressBar progressBar;
    View rootView;
    PopupWindow popupWindow;
    private WebView webview;
    private TextView tvTitle;
    private TextView tvDescription;
    private ImageView userImage;
    private MessageModel messageModel;
    private VideoView videoView;
    private RelativeLayout downloadVideo;
    private MediaController mediaController;
    private User sender;
    private OrientationEventListener orientationEventListener;
    private int currentAngle;
    private boolean stopInterval = true;

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.web_view_load_video, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_sin);

        webview = (WebView) rootView.findViewById(R.id.webview_sin);
        tvTitle = (TextView) rootView.findViewById(R.id.title);
        tvTitle.setSelected(true);
        tvDescription = (TextView) rootView.findViewById(R.id.status_textView);
        userImage = (ImageView) rootView.findViewById(R.id.user_image);
        videoView = (VideoView) rootView.findViewById(R.id.videoView_sin);
        downloadVideo = (RelativeLayout) rootView.findViewById(R.id.download_video);
        tvTitle.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
//            if (ApplicationUtil.isVideoFile(videoUrl)) {
        downloadVideo.setVisibility(View.GONE);
        webview.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        String videoUrl = messageModel.getVideoModelClass().getLocalVideoLink();
        Uri uri = Uri.parse(videoUrl);

        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        videoView.setVideoPath(videoUrl);
        rootView.setBackgroundColor(getResources().getColor(R.color.black));
        videoView.setMediaController(new MediaController(getActivity()));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.setLooping(true);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp,
                                                   int width, int height) {

                        //addMediaController();
                    }
                });

                videoView.setKeepScreenOn(true);
                videoView.start();
                //  addMediaController();
                stopInterval = false;
                startInterval();


            }
        });
        return rootView;
    }

    private void startInterval() {
        try {
            if (videoView != null && mediaController != null) {
                videoView.requestFocus();
                mediaController.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startInterval();
                    }
                }, 1000);
            }
        } catch (Exception e) {
            Lg.printStackTrace(e);
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            WindowManager.LayoutParams a = dialog.getWindow().getAttributes();
            a.dimAmount = 0;
            dialog.getWindow().setAttributes(a);

            makeDialogFocusable();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        makeDialogFocusable();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // allow current editText touch
        makeDialogFocusable();
    }

    private void makeDialogFocusable() {
        if (getDialog() != null) {
            getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        destroyWebview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyWebview();
    }

    private void destroyWebview() {
        if (webview != null) {
            webview.destroy();
            webview = null;
        }
        if (videoView != null) {
            videoView.clearFocus();
            if (mediaController != null) {
                mediaController.clearFocus();
            }
            mediaController = null;
            videoView.stopPlayback();
            videoView = null;
        }
        //      removeOrientationListener();
    }

//    private void initOrientationListener() {
//
//        orientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                if (orientation == -1) {
//                    return;
//                }
//                if (currentAngle != ConstantDefinition.ORIENTATION_REVERSE_LANDSCAPE_ANGLE && orientation > ConstantDefinition.ORIENTATION_REVERSE_LANDSCAPE_ANGLE - 10 && orientation < ConstantDefinition.ORIENTATION_REVERSE_LANDSCAPE_ANGLE + 10) {
//                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//                    currentAngle = ConstantDefinition.ORIENTATION_REVERSE_LANDSCAPE_ANGLE;
//                } else if (currentAngle != ConstantDefinition.ORIENTATION_LANDSCAPE_ANGLE && orientation > ConstantDefinition.ORIENTATION_LANDSCAPE_ANGLE - 10 && orientation < ConstantDefinition.ORIENTATION_LANDSCAPE_ANGLE + 10) {
//                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    currentAngle = ConstantDefinition.ORIENTATION_LANDSCAPE_ANGLE;
//                } else if (currentAngle != ConstantDefinition.ORIENTATION_PORTRAIT_ANGLE && (orientation > 350 || orientation <= ConstantDefinition.ORIENTATION_PORTRAIT_ANGLE)) {
//                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    currentAngle = ConstantDefinition.ORIENTATION_PORTRAIT_ANGLE;
//                }
//            }
//        };
//
//        if (orientationEventListener.canDetectOrientation()) {
//            orientationEventListener.enable();
//        }
//    }
//
//    private void removeOrientationListener() {
//        if (orientationEventListener != null) {
//            orientationEventListener.disable();
//            orientationEventListener = null;
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//
//    }

}

