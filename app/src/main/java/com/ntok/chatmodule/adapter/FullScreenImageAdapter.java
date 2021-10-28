package com.ntok.chatmodule.adapter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.ntok.chatmodule.fragment.FullScreenImageFragment;
import com.ntok.chatmodule.media.zoomEnabledGallery.GalleryViewPager;
import com.ntok.chatmodule.utils.Lg;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FullScreenImageAdapter extends FragmentPagerAdapter {
    private static final String TAG = FullScreenImageAdapter.class.getSimpleName();
    private final ViewPager viewPager;
    protected int mCurrentPosition = 0;
    protected OnItemChangeListener mOnItemChangeListener;
    private int imagePosition;
    private ArrayList<String> imageNames;
    private String imageType;
    private FragmentManager fragmentManager;
    private Context context;
    private Fragment fragment;
    private boolean isZoomable = true;
    private String bottomText;
    private boolean showCloseButton;

    public FullScreenImageAdapter(Context c, FragmentManager fm, ViewPager viewPager, ArrayList<String> imageNames, String imageType) {
        super(fm);
        context = c;
        this.viewPager = viewPager;
        this.imageNames = imageNames;
        this.imageType = imageType;

        try {
            Field mFragmentManager = FragmentPagerAdapter.class.getDeclaredField("mFragmentManager");
            mFragmentManager.setAccessible(true);

            fragmentManager = (FragmentManager) mFragmentManager.get(this);
        } catch (Exception e) {
            Lg.printStackTrace(e);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Lg.verbose(TAG, "destroyItem " + position);
        super.destroyItem(container, position, object);

        if (fragmentManager != null) {
            if (object != null) {
                fragmentManager.beginTransaction().remove((Fragment) object).detach((Fragment) object).commitAllowingStateLoss();
            }
            fragmentManager.executePendingTransactions();
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Lg.verbose(TAG, "setPrimaryItem " + position);
        if (mCurrentPosition == position) return;

        GalleryViewPager galleryContainer = ((GalleryViewPager) container);
        if (galleryContainer.mCurrentView != null) {
            try {
                galleryContainer.mCurrentView.resetScale();
            } catch (Exception e) {
            }
        }
        mCurrentPosition = position;
        if (mOnItemChangeListener != null) mOnItemChangeListener.onItemChange(mCurrentPosition);

        ((GalleryViewPager) container).mCurrentView = ((FullScreenImageFragment) object).getImageView();

    }

    public void setBottomText(String string) {
        this.bottomText = string;
    }

    @Override
    public int getCount() {
        if (imageNames == null) return 0;
        return imageNames.size();
    }

    @Override
    public Fragment getItem(int position) {
        Lg.verbose(TAG, "getItem " + position);

        if (position >= imageNames.size()) position = position % imageNames.size();
        String imageName = imageNames.get(position);
        if (imageName == null) {
            imageName = "";
        }

        FullScreenImageFragment fullScreenImageFragment = FullScreenImageFragment.newInstance(imageName, new FullScreenImageFragment.OnCloseListener() {
            @Override
            public void onClose() {

                if (fragment instanceof DialogFragment) {
                    ((DialogFragment) fragment).dismiss();
                }

            }
        });
        return fullScreenImageFragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setZoomable(boolean zoomable) {
        isZoomable = zoomable;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setOnItemChangeListener(OnItemChangeListener listener) {
        mOnItemChangeListener = listener;
    }

    public void showCloseButton(boolean showCloseButton) {
        this.showCloseButton = showCloseButton;
    }

    // Force a refresh of the page when a different fragment is displayed
    @Override
    public int getItemPosition(Object object) {
        // this method will be called for every fragment in the ViewPager
        if (object instanceof FullScreenImageFragment) {
            return POSITION_UNCHANGED; // don't force a reload
        } else {
            // POSITION_NONE means something like: this fragment is no longer valid
            // triggering the ViewPager to re-build the instance of this fragment.
            return POSITION_NONE;
        }
    }


    public void setImagePosition(int imagePosition) {
        this.imagePosition = imagePosition;
    }


    public static interface OnItemChangeListener {
        public void onItemChange(int currentPosition);
    }
}
