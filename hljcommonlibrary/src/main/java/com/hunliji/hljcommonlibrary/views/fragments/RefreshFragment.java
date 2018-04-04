/**
 *
 */
package com.hunliji.hljcommonlibrary.views.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.TrackerService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTrackerParameter;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.view_tracker.TrackedFragmentInterface;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.view_tracker.models.TrackerPage;
import com.hunliji.hljcommonlibrary.view_tracker.models.TrackerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity.FRAGMENT_PAGE_EXTRA;

/**
 * @author iDay
 */
@RuntimePermissions
public abstract class RefreshFragment extends Fragment implements TrackedFragmentInterface {

    private long startTimeMillis;
    private long resumeTimeMillis;

    protected OnTabTextChangeListener onTabTextChangeListener;
    public final static String CHILD_FRAGMENT_PAGE_EXTRA = "hlj_child_fragment_page_name";

    public void setOnTabTextChangeListener(OnTabTextChangeListener onTabTextChangeListener) {
        this.onTabTextChangeListener = onTabTextChangeListener;
    }

    public abstract void refresh(Object... params);

    @Override
    public void onViewCreated(
            View view, @Nullable Bundle savedInstanceState) {
        view.setTag(R.id.hlj_fragment_root_view, getClass().getName());
        super.onViewCreated(view, savedInstanceState);
    }


    private void initTracker(View view) {
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
                View v = view.findViewById(getResources().getIdentifier(trackerView.getId(),
                        "id",
                        view.getContext()
                                .getPackageName()));
                if (v != null) {
                    trackerView.tag(v, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Intent intent) {
        if (System.currentTimeMillis() - startTimeMillis > 1000) {
            startTimeMillis = System.currentTimeMillis();
            super.startActivity(intent);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (System.currentTimeMillis() - startTimeMillis > 1000) {
            startTimeMillis = System.currentTimeMillis();
            super.startActivityForResult(intent, requestCode);
        }
    }

    public void callUp(Uri uri) {
        if (!uri.toString()
                .contains(",")) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, uri);
            super.startActivity(phoneIntent);
        } else {
            RefreshFragmentPermissionsDispatcher.onCallUpWithCheck(this, uri);
        }
    }


    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void onCallUp(Uri uri) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL, uri);
        super.startActivity(phoneIntent);
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    void onRationaleCallUP(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(getContext(),
                request,
                getString(com.hunliji.hljcommonlibrary.R.string.msg_permission_r_for_call_up___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        RefreshFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFragmentTagOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        setFragmentTagOnPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onFragmentPageStart();
        initTracker(getView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onFragmentPageEnd();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        setFragmentTagOnHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setFragmentTagOnUserVisibleChanged(isVisibleToUser);
    }

    @Override
    public void setFragmentTagOnResume() {
        if (isHidden(this) || !getUserVisibleHint(this) || !isResumed()) {
            return;
        }
        if (resumeTimeMillis == 0) {
            resumeTimeMillis = System.currentTimeMillis();
        }
        HljViewTracker.INSTANCE.setCurrentFragment(this, getFragmentPageTagName());
        try {
            if (getContext() == null) {
                return;
            }
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof RefreshFragment) {
                    ((RefreshFragment) fragment).setFragmentTagOnResume();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFragmentTagOnPause() {
        if (resumeTimeMillis > 0) {
            HljViewTracker.fireActivityPageOutEvent(this, resumeTimeMillis);
            resumeTimeMillis = 0;
        }
        try {
            if (getContext() == null) {
                return;
            }
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof RefreshFragment) {
                    ((RefreshFragment) fragment).setFragmentTagOnPause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Subscription pageSubscription;

    public void onFragmentPageStart() {
        if (isHidden(this) || !getUserVisibleHint(this)) {
            return;
        }
        String fragmentPageName = fragmentPageName();
        if (TextUtils.isEmpty(fragmentPageName)) {
            return;
        }
        CommonUtil.unSubscribeSubs(pageSubscription);
        pageSubscription = Observable.timer(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (isHidden(RefreshFragment.this) || !getUserVisibleHint(RefreshFragment
                                .this)) {
                            return;
                        }
                        String fragmentPageName = fragmentPageName();
                        if (!TextUtils.isEmpty(fragmentPageName)) {
                            if (CommonUtil.getAppType() == CommonUtil
                                    .PacketType
                                    .CUSTOMER) {
                                TrackerService trackerService = (TrackerService) ARouter
                                        .getInstance()
                                        .build(RouterPath.ServicePath.TRACKER)
                                        .navigation(getContext());
                                if (trackerService != null) {
                                    trackerService.onFragmentPageResume(RefreshFragment.this);
                                }
                            }

                            Fragment fragment = RefreshFragment.this;
                            while (fragment.getParentFragment() != null) {
                                fragment = fragment.getParentFragment();
                                Bundle arguments = fragment.getArguments();
                                if (arguments == null) {
                                    arguments = new Bundle();
                                    fragment.setArguments(arguments);
                                }
                                String lastChildFragmentPageName = arguments.getString(
                                        CHILD_FRAGMENT_PAGE_EXTRA);
                                if (!fragmentPageName.equals(lastChildFragmentPageName)) {
                                    arguments.putString(CHILD_FRAGMENT_PAGE_EXTRA,
                                            fragmentPageName);
                                }
                            }
                            if (getActivity() != null) {
                                String lastFragmentPageName = getActivity().getIntent()
                                        .getStringExtra(FRAGMENT_PAGE_EXTRA);
                                if (!fragmentPageName.equals(lastFragmentPageName)) {
                                    HljViewTracker.fireFragmentPageJumpEvent(getActivity(),
                                            RefreshFragment.this);
                                    getActivity().getIntent()
                                            .putExtra(FRAGMENT_PAGE_EXTRA, fragmentPageName);
                                }
                            }
                        }
                    }
                });

    }

    public void onFragmentPageEnd() {
        String fragmentPageName = fragmentPageName();
        if (TextUtils.isEmpty(fragmentPageName)) {
            return;
        }
        CommonUtil.unSubscribeSubs(pageSubscription);
        pageSubscription = Observable.timer(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        final String fragmentPageName = getFragmentPageTagName();
                        if (!TextUtils.isEmpty(fragmentPageName)) {
                            Fragment fragment = RefreshFragment.this;
                            String newFragmentPageName = null;
                            while (fragment.getParentFragment() != null) {
                                fragment = fragment.getParentFragment();
                                if (fragment.isVisible() && getUserVisibleHint(fragment)) {
                                    if (TextUtils.isEmpty(newFragmentPageName) && fragment
                                            instanceof RefreshFragment) {
                                        newFragmentPageName = ((RefreshFragment) fragment)
                                                .getFragmentPageTagName();
                                    }
                                }
                                Bundle arguments = fragment.getArguments();
                                if (arguments == null) {
                                    arguments = new Bundle();
                                    fragment.setArguments(arguments);
                                }
                                String lastChildFragmentPageName = arguments.getString(
                                        CHILD_FRAGMENT_PAGE_EXTRA);
                                if (fragmentPageName.equals(lastChildFragmentPageName)) {
                                    arguments.putString(CHILD_FRAGMENT_PAGE_EXTRA,
                                            newFragmentPageName);
                                }
                            }
                            if (getActivity() != null) {
                                String lastFragmentPageName = getActivity().getIntent()
                                        .getStringExtra(FRAGMENT_PAGE_EXTRA);
                                if (fragmentPageName.equals(lastFragmentPageName)) {
                                    getActivity().getIntent()
                                            .putExtra(FRAGMENT_PAGE_EXTRA, newFragmentPageName);
                                }
                            }
                        }
                    }
                });

    }

    private boolean isHidden(Fragment fragment) {
        if (fragment.isHidden()) {
            return fragment.isHidden();
        }
        if (fragment.getParentFragment() != null) {
            return isHidden(fragment.getParentFragment());
        }
        return false;
    }

    private boolean getUserVisibleHint(Fragment fragment) {
        if (!fragment.getUserVisibleHint()) {
            return fragment.getUserVisibleHint();
        }
        if (fragment.getParentFragment() != null) {
            return getUserVisibleHint(fragment.getParentFragment());
        }
        return true;
    }


    @Override
    public void setFragmentTagOnHiddenChanged(boolean hidden) {
        if (!hidden) {
            setFragmentTagOnResume();
            onFragmentPageStart();
        } else {
            setFragmentTagOnPause();
            onFragmentPageEnd();
        }
    }

    @Override
    public void setFragmentTagOnUserVisibleChanged(boolean isVisible) {
        if (isVisible) {
            setFragmentTagOnResume();
            onFragmentPageStart();
        } else {
            setFragmentTagOnPause();
            onFragmentPageEnd();
        }
    }

    public String fragmentPageName() {
        String pageName = null;
        if (getArguments() != null) {
            pageName = getArguments().getString(CHILD_FRAGMENT_PAGE_EXTRA);
        }
        if (TextUtils.isEmpty(pageName)) {
            pageName = getFragmentPageTagName();
        }
        return pageName;
    }

    @Override
    public String getFragmentPageTagName() {
        String pageTag = fragmentPageTrackTagName2();
        if (TextUtils.isEmpty(pageTag)) {
            pageTag = fragmentPageTrackTagName();
        }
        return pageTag;
    }

    @Override
    public VTMetaData getPageTrackData() {
        VTMetaData vtMetaData = pageTrackData2();
        if (vtMetaData == null) {
            vtMetaData = pageTrackData();
        }
        return vtMetaData;
    }

    @Override
    public String fragmentPageTrackTagName() {
        return null;
    }

    @Override
    public VTMetaData pageTrackData() {
        return null;
    }

    public String fragmentPageTrackTagName2() {
        TrackerPage page = HljTrackerParameter.INSTANCE.getPage(this.getClass()
                .getName());
        if (page != null) {
            return page.getTrackerName(this);
        }
        return null;
    }

    private VTMetaData pageTrackData2() {
        TrackerPage page = HljTrackerParameter.INSTANCE.getPage(this.getClass()
                .getName());
        if (page != null) {
            return page.getPageData(this);
        }
        return null;
    }
}
