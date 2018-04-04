package com.hunliji.hljcommonlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTrackerParameter;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.view_tracker.TrackedActivityInterface;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.view_tracker.models.TrackerPage;
import com.hunliji.hljcommonlibrary.view_tracker.models.TrackerView;

import java.util.List;

/**
 * Created by wangtao on 2017/12/18.
 */

public class HljTrackedActivity extends AppCompatActivity implements TrackedActivityInterface {

    public String lastPageName;

    public final static String LAST_PAGE_EXTRA = "hlj_last_page_name";
    public final static String FRAGMENT_PAGE_EXTRA = "hlj_fragment_page_name";

    @Override
    public void startActivityForResult(
            Intent intent, int requestCode, @Nullable Bundle options) {
        intent.putExtra(LAST_PAGE_EXTRA, HljViewTracker.getCurrentPageName(this));
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initTracker();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initTracker();
    }

    private void initTracker() {
        try {
            List<TrackerView> trackerViews = HljTrackerParameter.INSTANCE.getViews(this.getClass()
                    .getName());
            if (CommonUtil.isCollectionEmpty(trackerViews)) {
                return;
            }
            for (TrackerView trackerView : trackerViews) {
                if (TextUtils.isEmpty(trackerView.getId())) {
                    continue;
                }
                View view = findViewById(getResources().getIdentifier(trackerView.getId(),
                        "id",
                        getPackageName()));
                if (view != null) {
                    trackerView.tag(view, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TrackedActivityInterface
     */
    @Override
    public void setActivityNameOnResume() {
        HljViewTracker.INSTANCE.setCurrentActivity(this,
                HljViewTracker.getCurrentPageName(this),
                getLastPageName(),
                HljViewTracker.getCurrentPageTrackData(this));
    }

    /**
     * TrackedActivityInterface
     */
    @Override
    public void clearFragmentNameOnPause() {
        HljViewTracker.INSTANCE.clearCurrentFragment();
    }

    /**
     * TrackedActivityInterface
     *
     * @return
     */
    @Override
    public String getLastPageName() {
        if (TextUtils.isEmpty(lastPageName) && getIntent() != null) {
            lastPageName = getIntent().getStringExtra(LAST_PAGE_EXTRA);
        }
        return lastPageName;
    }

    /**
     * TrackedActivityInterface
     *
     * @return
     */
    @Override
    public VTMetaData pageTrackData() {
        return null;
    }

    public VTMetaData pageTrackData2() {
        TrackerPage page = HljTrackerParameter.INSTANCE.getPage(this.getClass()
                .getName());
        if (page != null) {
            return page.getPageData(this);
        }
        return null;
    }


    /**
     * TrackedActivityInterface
     *
     * @return
     */
    @Override
    public String pageTrackTagName() {
        return null;
    }

    public String pageTrackTagName2() {
        TrackerPage page = HljTrackerParameter.INSTANCE.getPage(this.getClass()
                .getName());
        if (page != null) {
            return page.getTrackerName(this);
        }
        return null;
    }

    public String getFragmentPageTrackTagName() {
        if (getIntent() != null) {
            return getIntent().getStringExtra(FRAGMENT_PAGE_EXTRA);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setActivityNameOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearFragmentNameOnPause();
    }
}
