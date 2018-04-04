package me.suncloud.marrymemo.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljsharelibrary.HljShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.TemplateV2ListFragment;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.TemplateV2;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TextShareUtil;
import me.suncloud.marrymemo.widget.TabPageIndicator;

/**
 * Created by Suncloud on 2016/5/5.
 */
public class TemplateV2ListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener, View.OnClickListener {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.hint_layout)
    View hintLayout;
    @BindView(R.id.iv_hint)
    ImageView ivHint;
    private ArrayList<TemplateV2> singleArray;
    private ArrayList<TemplateV2> towArray;
    private ArrayList<TemplateV2> multiArray;
    private ArrayList<TemplateV2> textArray;
    private SparseArray<RefreshFragment> fragments;
    private CardV2 card;
    private Dialog shareDialog;
    private TextShareUtil shareUtil;
    private GetShareInfoTask getShareInfoTask;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    CardResourceUtil.getInstance()
                            .setUserThemeLockV2(false);
                    new NewHttpPostTask(TemplateV2ListActivity.this,
                            null).execute(Constants.getAbsUrl(Constants.HttpPath
                            .UNLOCK_THEME_V2_URL));
                    if (isFinishing()) {
                        break;
                    }
                    if (fragments != null && fragments.size() > 0) {
                        for (int i = 0, size = fragments.size(); i < size; i++) {
                            fragments.get(i)
                                    .refresh(false);
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragments = new SparseArray<>();
        card = (CardV2) getIntent().getSerializableExtra("card");
        super.onCreate(savedInstanceState);
        if (card == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_template_v2_list);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        tabIndicator.setOnTabChangeListener(this);
        tabIndicator.setTabViewId(R.layout.menu_tab_widget4);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
                if (hintLayout.getVisibility() == View.VISIBLE) {
                    hintLayout.setVisibility(View.GONE);
                }
                super.onPageSelected(position);
            }
        });
        new GetTemplatesTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.TEMPLATE_V2_LIST, card.getThemeId()));
        EventBus.getDefault()
                .register(this);
    }

    @Override
    public void onTabChanged(int position) {
        pager.setCurrentItem(position);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_btn:
                shareDialog.dismiss();
                if (shareUtil != null) {
                    shareUtil.shareToPengYou();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    if (getShareInfoTask == null) {
                        getShareInfoTask = new GetShareInfoTask();
                        getShareInfoTask.execute();
                    }
                }
                break;
            case R.id.close_btn:
                shareDialog.dismiss();
                break;
        }
    }

    private class GetTemplatesTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isFinishing()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                JSONArray array = jsonObject.optJSONArray("singleArr");
                if (array != null && array.length() > 0) {
                    singleArray = new ArrayList<>();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        TemplateV2 template = new TemplateV2(array.optJSONObject(i));
                        template.onSaveCheck(TemplateV2ListActivity.this);
                        singleArray.add(template);
                    }
                }
                array = jsonObject.optJSONArray("twoArr");
                if (array != null && array.length() > 0) {
                    towArray = new ArrayList<>();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        TemplateV2 template = new TemplateV2(array.optJSONObject(i));
                        template.onSaveCheck(TemplateV2ListActivity.this);
                        towArray.add(template);
                    }
                }
                array = jsonObject.optJSONArray("multiArr");
                if (array != null && array.length() > 0) {
                    multiArray = new ArrayList<>();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        TemplateV2 template = new TemplateV2(array.optJSONObject(i));
                        template.onSaveCheck(TemplateV2ListActivity.this);
                        multiArray.add(template);
                    }
                }
                array = jsonObject.optJSONArray("textArr");
                if (array != null && array.length() > 0) {
                    textArray = new ArrayList<>();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        TemplateV2 template = new TemplateV2(array.optJSONObject(i));
                        template.onSaveCheck(TemplateV2ListActivity.this);
                        textArray.add(template);
                    }
                }
                TemplateAdapter templateAdapter = new TemplateAdapter(getSupportFragmentManager());
                pager.setAdapter(templateAdapter);
                tabIndicator.setPagerAdapter(templateAdapter);
                showCardDragHint();
            }
            super.onPostExecute(jsonObject);
        }
    }

    public class TemplateAdapter extends FragmentPagerAdapter {

        public TemplateAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            RefreshFragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = TemplateV2ListFragment.newInstance(getData(position), card);
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        private ArrayList<TemplateV2> getData(int position) {
            switch (position) {
                case 0:
                    return singleArray;
                case 1:
                    return towArray;
                case 2:
                    return multiArray;
                default:
                    return textArray;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_template_single);
                case 1:
                    return getString(R.string.label_template_two);
                case 2:
                    return getString(R.string.label_template_multi);
                default:
                    return getString(R.string.label_template_text);
            }
        }
    }

    public void onShare() {
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareDialog == null) {
            shareDialog = new Dialog(this, R.style.BubbleDialogTheme);
            shareDialog.setContentView(R.layout.dialog_theme_share);
            shareDialog.findViewById(R.id.share_btn)
                    .setOnClickListener(this);
            shareDialog.findViewById(R.id.close_btn)
                    .setOnClickListener(this);
            Window window = shareDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
        }
        shareDialog.show();
    }

    private class GetShareInfoTask extends AsyncTask<Object, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .TOOLS_SHARE));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            getShareInfoTask = null;
            if (jsonObject != null) {
                String description = jsonObject.optString("desc",
                        getString(R.string.tools_share_msg));
                if (JSONUtil.isEmpty(description)) {
                    description = getString(R.string.tools_share_msg);
                }
                String webDescription = jsonObject.optString("desc2",
                        getString(R.string.tools_share_msg));
                if (JSONUtil.isEmpty(webDescription)) {
                    webDescription = getString(R.string.tools_share_msg);
                }
                String title = jsonObject.optString("title", getString(R.string.tools_share_title));
                if (JSONUtil.isEmpty(title)) {
                    title = getString(R.string.tools_share_title);
                }
                String url = jsonObject.optString("url", "http://dwz.cn/IRby7");
                if (JSONUtil.isEmpty(url)) {
                    title = "http://dwz.cn/IRby7";
                }
                shareUtil = new TextShareUtil(TemplateV2ListActivity.this,
                        url,
                        title,
                        description,
                        webDescription,
                        handler);
            } else {
                shareUtil = new TextShareUtil(TemplateV2ListActivity.this,
                        "http://dwz.cn/IRby7",
                        getString(R.string.tools_share_title),
                        getString(R.string.tools_share_msg),
                        getString(R.string.tools_share_msg),
                        handler);
            }
            shareUtil.shareToPengYou();
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    protected void onFinish() {
        EventBus.getDefault()
                .unregister(this);
        super.onFinish();
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CARD_UPDATE_FLAG) {
            finish();
        }
    }

    private void showCardDragHint() {
        boolean b = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getBoolean(
                "cardTemplate",
                false);
        if (b) {
            hintLayout.setVisibility(View.GONE);
            return;
        }
        getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                .putBoolean("cardTemplate", true)
                .apply();
        hintLayout.setVisibility(View.VISIBLE);
        hintLayout.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(hintLayout,
                        "translationY",
                        -hintLayout.getHeight() * 0.1f)
                        .setDuration(300);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setRepeatCount(3);
                animator.setStartDelay(100);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isFinishing()) {
                            return;
                        }
                        if (hintLayout.getVisibility() == View.VISIBLE) {
                            hintLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            }
        });

    }
}
