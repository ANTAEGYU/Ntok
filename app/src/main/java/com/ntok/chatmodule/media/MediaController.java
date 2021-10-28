package com.ntok.chatmodule.media;

import android.net.Uri;

import com.ntok.chatmodule.media.view.QBPlaybackControlView;


/**
 * Created by Roman on 16.07.2017.
 */

public interface MediaController {

    void onPlayClicked(QBPlaybackControlView view, Uri uri);

    void onPauseClicked();

    void onStartPosition();

    void onStopAnyPlayback();

    interface EventMediaController {
        void onPlayerInViewInit(QBPlaybackControlView view);
    }
}
