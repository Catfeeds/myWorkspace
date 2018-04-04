package com.hunliji.marrybiz.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.models.NotebookType;
import com.hunliji.hljnotelibrary.utils.NoteDialogUtil;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljnotelibrary.views.activities.CreatePhotoNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.CreateVideoNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.MerchantNoteListActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteEduActivity;
import com.hunliji.hljquestionanswer.activities.MyMerchantQaActivity;
import com.hunliji.hljquestionanswer.fragments.MerchantQuestionFragment;
import com.hunliji.hljvideolibrary.activities.BaseVideoTrimActivity;
import com.hunliji.hljvideolibrary.activities.VideoChooserActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.FlowAdapter;
import com.hunliji.marrybiz.api.college.CollegeApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Poster;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.BusinessCollegeActivity;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.LiveActivity;
import com.hunliji.marrybiz.widget.TabPageIndicator;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by werther on 16/9/9.
 * 互动页面
 */
public class InteractionFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.posters_view)
    SliderLayout postersView;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.banner_layout)
    RelativeLayout bannerLayout;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.fragment_layout)
    FrameLayout fragmentLayout;
    @BindView(R.id.scroll_view)
    ScrollableLayout scrollView;
    @BindView(R.id.my_feed_view)
    LinearLayout myFeedView;
    @BindView(R.id.my_qa_view)
    LinearLayout myQaView;
    @BindView(R.id.my_live_view)
    LinearLayout myLiveView;
    @BindView(R.id.head_view)
    LinearLayout headView;
    @BindView(R.id.img_new_biz_tag)
    ImageView imgNewBizTag;
    @BindView(R.id.action_layout_holder)
    FrameLayout actionLayoutHolder;

    private int height;
    private int headHeight;
    private FlowAdapter flowAdapter;

    private MerchantQuestionFragment allQuestionAnswerFragment;
    private MerchantQuestionFragment recQuestionAnswerFragment;

    private Unbinder unbinder;
    private MerchantUser user;
    private HljHttpSubscriber newBizSub;

    public static InteractionFragment newInstance() {
        InteractionFragment interactionFragment = new InteractionFragment();
        return interactionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_interaction, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        HljBaseActivity.setActionBarPadding(getContext(), actionLayoutHolder);

        return rootView;
    }

    @Override
    public void onViewCreated(
            View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initValue();
        initView();
        initLoad();
    }

    private void initValue() {
        Point point = JSONUtil.getDeviceSize(getActivity());
        height = Math.round(point.x * 5 / 16);
        user = Session.getInstance()
                .getCurrentUser(getActivity());
    }

    private void initView() {
        flowAdapter = new FlowAdapter(getActivity());
        bannerLayout.getLayoutParams().height = height;
        postersView.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(postersView);
        postersView.setCustomIndicator(flowIndicator);
        postersView.setPresetTransformer(4);
        if (flowAdapter.getCount() > 0) {
            bannerLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() > 1) {
                postersView.startAutoCycle();
            } else {
                postersView.stopAutoCycle();
            }
        } else {
            bannerLayout.setVisibility(View.GONE);
        }

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity()
                .getSupportFragmentManager());
        indicator.setTabViewId(R.layout.menu_tab_widget3);
        indicator.setPagerAdapter(mSectionsPagerAdapter);
        indicator.setOnTabChangeListener(this);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                if (scrollView.getHelper()
                        .getScrollableView() == null) {
                    scrollView.getHelper()
                            .setCurrentScrollableContainer(getCurrentScrollableContainer());
                }
            }
        });
        fragmentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.onTouchEvent(event);
                return true;
            }
        });
        headHeight = headView.getHeight();
        scrollView.setExtraHeight(headHeight);
        scrollView.addOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                scrollView.getHelper()
                        .setCurrentScrollableContainer(getCurrentScrollableContainer());
            }
        });
    }

    private void initLoad() {
        new GetQaBannerTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.POSTER_BLOCK_URL,
                        Constants.BLOCK_ID.InteractionFragment,
                        user.getCity_code()));

        newBizSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        HomeActivity activity = getHomeActivity();
                        if (activity != null) {
                            activity.setInteractionTagVisible(aBoolean);
                        }
                    }
                })
                .build();
        CollegeApi.hasNewLessons()
                .subscribe(newBizSub);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class GetQaBannerTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                if (jsonObject != null) {
                    return jsonObject.optJSONObject("floors");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() == null || getActivity().isFinishing() || isDetached()) {
                return;
            }
            List<Poster> posterList = Util.getPosterList(jsonObject,
                    Constants.POST_SITES.SERVICE_MERCHANT_INTERACTION_BANNER,
                    false);
            flowAdapter.setmDate(posterList);
            if (bannerLayout != null && postersView != null) {
                if (flowAdapter.getCount() == 0) {
                    postersView.stopAutoCycle();
                    bannerLayout.setVisibility(View.GONE);
                } else {
                    bannerLayout.setVisibility(View.VISIBLE);
                    if (flowAdapter.getCount() > 1) {
                        postersView.startAutoCycle();
                    } else {
                        postersView.stopAutoCycle();
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @OnClick({R.id.btn_camera, R.id.my_feed_view, R.id.my_qa_view, R.id.my_live_view, R.id
            .business_school_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                if (PopupRule.getDefault()
                        .showShopReview(getActivity(), user)) {
                    return;
                }
                NoteDialogUtil.showCreateNoteMenuDialog(getContext(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        if (NotePrefUtil.getInstance(getContext())
                                .isShowNoteEdu()) {
                            intent.setClass(getContext(), NoteEduActivity.class);
                            intent.putExtra("note_type", Note.TYPE_NORMAL);
                        } else {
                            intent.setClass(getContext(), CreatePhotoNoteActivity.class);
                            intent.putExtra("notebook_type", NotebookType.TYPE_WEDDING_PERSON);
                        }
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        if (NotePrefUtil.getInstance(getContext())
                                .isShowNoteEdu()) {
                            intent.setClass(getContext(), NoteEduActivity.class);
                            intent.putExtra("note_type", Note.TYPE_VIDEO);
                            startActivityForResult(intent, HljNote.RequestCode.NOTE_HELP);
                        } else {
                            intent.setClass(getContext(), VideoChooserActivity.class);
                            intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                                    HljNote.NOTE_MAX_VIDEO_LENGTH);
                            startActivityForResult(intent, HljNote.RequestCode.CHOOSE_VIDEO);
                        }
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                })
                        .show();
                break;
            case R.id.my_feed_view:
                startActivity(new Intent(getContext(), MerchantNoteListActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.my_qa_view:
                startActivity(new Intent(getContext(), MyMerchantQaActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.my_live_view:
                Intent intent = new Intent(getContext(), LiveActivity.class);
                intent.putExtra("id", user.getMerchantId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.business_school_view:
                intent = new Intent(getContext(), BusinessCollegeActivity.class);
                startActivity(intent);
                break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (recQuestionAnswerFragment == null) {
                        recQuestionAnswerFragment = MerchantQuestionFragment.newInstance(0);
                    }
                    return recQuestionAnswerFragment;
                case 1:
                    if (allQuestionAnswerFragment == null) {
                        allQuestionAnswerFragment = MerchantQuestionFragment.newInstance(1);
                    }
                    return allQuestionAnswerFragment;
                default:
                    if (allQuestionAnswerFragment == null) {
                        allQuestionAnswerFragment = MerchantQuestionFragment.newInstance(0);
                    }
                    return allQuestionAnswerFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_recommend_question___qa).toUpperCase();
                case 1:
                    return getString(R.string.label_new_question___qa).toUpperCase();
                default:
                    return getString(R.string.label_recommend_question___qa).toUpperCase();
            }
        }
    }

    /**
     * 获取当前fragment
     *
     * @return
     */
    private ScrollableHelper.ScrollableContainer getCurrentScrollableContainer() {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof
                SectionsPagerAdapter) {
            SectionsPagerAdapter adapter = (SectionsPagerAdapter) viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment != null && fragment instanceof ScrollableHelper.ScrollableContainer) {
                return (ScrollableHelper.ScrollableContainer) fragment;
            }
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.NOTE_HELP:
                    if (data != null) {
                        int noteType = data.getIntExtra("note_type", Note.TYPE_NORMAL);
                        if (noteType == Note.TYPE_VIDEO) {
                            Intent intent = new Intent(getContext(), VideoChooserActivity.class);
                            intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                                    HljNote.NOTE_MAX_VIDEO_LENGTH);
                            startActivityForResult(intent, HljNote.RequestCode.CHOOSE_VIDEO);
                            getActivity().overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                    break;
                case HljNote.RequestCode.CHOOSE_VIDEO:
                    if (data != null) {
                        Photo photo = data.getParcelableExtra("photo");
                        if (photo != null) {
                            Intent intent = new Intent(getContext(), CreateVideoNoteActivity.class);
                            intent.putExtra("photo", photo);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_up,
                                    R.anim.activity_anim_default);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(newBizSub);
    }

    private HomeActivity getHomeActivity() {
        HomeActivity activity = null;
        if (getActivity() instanceof HomeActivity) {
            activity = (HomeActivity) getActivity();
        }

        return activity;
    }
}


