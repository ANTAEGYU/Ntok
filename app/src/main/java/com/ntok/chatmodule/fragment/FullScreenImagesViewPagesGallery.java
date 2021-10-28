package com.ntok.chatmodule.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.adapter.FullScreenImageAdapter;
import com.ntok.chatmodule.media.zoomEnabledGallery.GalleryViewPager;
import com.ntok.chatmodule.utils.Lg;

import java.util.ArrayList;

/**
 * Created by Sonam Gupta on 05/07/16.
 */
public class FullScreenImagesViewPagesGallery extends DialogFragment {
    private static final String TAG = FullScreenImagesViewPagesGallery.class.getSimpleName();
    public String imageType = "";
    int space = 0;
    private Context context;
    private int imagePosition;
    private Fragment parentFragment;
    private ArrayList<String> imageNames;
    private View rootView;
    private GalleryViewPager viewPager;
    private boolean isZoomable = true;
    private DialogInterface.OnDismissListener onDismissListener;
    private String strBottomText;
    private TextView bottomText;
    private TextView pageNumber;

    public FullScreenImagesViewPagesGallery() {
        super();
    }

    public FullScreenImagesViewPagesGallery init(Context context, Fragment parentFragment, int imagePosition, ArrayList<String> imageNames, String imageType) {
        this.context = context;
        this.imagePosition = imagePosition;
        this.imageNames = imageNames;
        this.parentFragment = parentFragment;
        this.imageType = imageType;
        return this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Lg.debug(FullScreenImagesViewPagesGallery.TAG, "onCreateDialog");

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageNumber = (TextView) rootView.findViewById(R.id.page_number);

//        close = (View) rootView.findViewById(R.id.close);
        viewPager = (GalleryViewPager) rootView.findViewById(R.id.view_pager_full_screen);
//        leftArrow = rootView.findViewById(R.id.left_arrow_images);
//        rightArrow = rootView.findViewById(R.id.right_arrow_images);
        bottomText = (TextView) rootView.findViewById(R.id.bottomText);
        bottomText.setMovementMethod(new ScrollingMovementMethod());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (imageNames.size() > 1) {
//                    if (position == 0) {
//                        leftArrow.setVisibility(View.GONE);
//                        rightArrow.setVisibility(View.VISIBLE);
//                    } else if (position == imageNames.size() - 1) {
//                        leftArrow.setVisibility(View.VISIBLE);
//                        rightArrow.setVisibility(View.GONE);
//                    } else {
//                        leftArrow.setVisibility(View.VISIBLE);
//                        rightArrow.setVisibility(View.VISIBLE);
//                    }

                    pageNumber.setText((position + 1) + "/" + imageNames.size());

//                } else {
//                    leftArrow.setVisibility(View.GONE);
//                    rightArrow.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (imageNames == null || imageNames.size() == 0) {
            viewPager.setVisibility(View.GONE);
            pageNumber.setVisibility(View.GONE);
        } else {
            if (imageNames.size() > 1) {
//                rightArrow.setVisibility(View.VISIBLE);
                pageNumber.setVisibility(View.VISIBLE);
                pageNumber.setText((1) + "/" + imageNames.size());
            } else {
                pageNumber.setVisibility(View.GONE);
            }
            viewPager.setVisibility(View.VISIBLE);
            initGallery(imageNames);
        }

        if (TextUtils.isEmpty(strBottomText)) {
            bottomText.setVisibility(View.GONE);
        } else {

            if (imageNames == null || imageNames.size() == 0) {
                // show textView only
                RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                bottomText.setGravity(Gravity.CENTER_VERTICAL);
                bottomText.setLayoutParams(lParams);
                bottomText.setMaxLines(6);
            } else {
                bottomText.setMaxLines(2);
            }

            bottomText.setVisibility(View.VISIBLE);
            bottomText.setText(strBottomText);
        }
//
//        if (getDialog() == null) {
//            close.setVisibility(View.GONE);
//        }
//
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            //All the code in the onCreateView
            rootView = inflater.inflate(R.layout.full_screen_view_pager, container, false);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(getDialog());
        }
        super.onDestroyView();
    }

    public void initGallery(ArrayList<String> imageNames) {
        if (imageNames == null || imageNames.isEmpty()) {
            Lg.e("imagesUrlsList empty");
            return;
        }

        if (getView() == null) {
            Lg.e("carouselController null");
            return;
        }
        Lg.d("initGallery");

        FullScreenImageAdapter adapter = new FullScreenImageAdapter(getActivity(), getChildFragmentManager(), viewPager, imageNames, imageType);
        adapter.setZoomable(isZoomable);
        if (getDialog() != null) {
            adapter.showCloseButton(false);
        }
        adapter.setImagePosition(imagePosition);
        adapter.setFragment(FullScreenImagesViewPagesGallery.this);
        try {
            viewPager.setOffscreenPageLimit(2);
            viewPager.setAdapter(adapter);

            viewPagerSetDefaultPage();

        } catch (Exception e) {
            Lg.printStackTrace(e);
        }

    }

    public void setIsZoomable(boolean isZoomable) {
        this.isZoomable = isZoomable;
    }

    public void setBottomText(String strBottomText) {
        this.strBottomText = strBottomText;
    }


    private void viewPagerSetDefaultPage() {
        Lg.d("viewPagerSetDefaultPage");

        if (viewPager == null) {
            return;
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
//                    listener.setCurrentPage(imagePosition);
//                    listener.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
//                    listener.onPageSelected(imagePosition);
                    viewPager.setCurrentItem(imagePosition, false);
                } catch (Exception e) {
                    Lg.printStackTrace(e);
                }
            }
        });
    }


    public void setCurrentPage(int currentPage) {
        Lg.v("setCurrentPage currentPage: %d", currentPage + "");
        this.imagePosition = currentPage;
    }


    public void setDialogFragmentDismissedListener(DialogInterface.OnDismissListener onDismiss) {
        this.onDismissListener = onDismiss;
    }


    public boolean onBackPressed() {
        return true;
    }


}
