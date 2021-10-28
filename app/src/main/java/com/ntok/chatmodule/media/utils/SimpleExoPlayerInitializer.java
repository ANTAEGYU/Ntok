package com.ntok.chatmodule.media.utils;

import android.content.Context;
import android.net.Uri;

import com.ntok.chatmodule.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * Created by roman on 7/14/17.
 */

public class SimpleExoPlayerInitializer {

    public static SimpleExoPlayer initializeExoPlayer(Context context) {
        return ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());
    }

    public static MediaSource buildMediaSource(Uri uri, Context context) {
        String userAgent = Util.getUserAgent(context, context.getResources().getString(R.string.app_name));
        return new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(), null, null);
    }
}
