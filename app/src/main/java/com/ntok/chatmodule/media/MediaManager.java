package com.ntok.chatmodule.media;

import android.net.Uri;

import com.ntok.chatmodule.media.view.QBPlaybackControlView;


/**
 * Created by roman on 7/14/17.
 */

public interface MediaManager {

    void playMedia(QBPlaybackControlView playerView, Uri uri);

    void pauseMedia();

    void stopAnyPlayback();

    void resetMediaPlayer();
}