package com.ntok.chatmodule.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.media.zoomEnabledGallery.TouchView.TouchImageView;
import com.bumptech.glide.Glide;

public class FullScreenImageFragment extends Fragment {

    private static final String LOG_TAG = "FullScreenImageFragment";
    private TouchImageView mImageView;
    private View rootView;
    private OnCloseListener onCloseListener;
    private String defaultUrl;
    private ProgressBar progressBar;

    public static FullScreenImageFragment newInstance(String defaultUrl, OnCloseListener onCloseListener) {
        FullScreenImageFragment fullScreenImageDialogFragment = new FullScreenImageFragment();

        fullScreenImageDialogFragment.defaultUrl = defaultUrl;
        fullScreenImageDialogFragment.onCloseListener = onCloseListener;

        return fullScreenImageDialogFragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fullscreen_image_fragment_layout, container, false);

        mImageView = (TouchImageView) rootView.findViewById(R.id.image_view);
        mImageView.setZoomable(true);
        setUserVisibleHint(true);

        Glide.with(getActivity())
                .load(defaultUrl).asBitmap()
                .placeholder(R.drawable.progress)
                .into(mImageView);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    public TouchImageView getImageView() {
        return mImageView;
    }


    public interface OnCloseListener {
        void onClose();
    }


}
