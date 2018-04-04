package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.tools.ToolsApi;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TextShareUtil;
import me.suncloud.marrymemo.util.TrackerUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.MyStoryListActivity;
import me.suncloud.marrymemo.view.WXWallActivity;
import me.suncloud.marrymemo.view.WeddingDayProgramsActivity;
import me.suncloud.marrymemo.view.WeddingPreparedListActivity;
import me.suncloud.marrymemo.view.WeddingRegisterActivity;
import me.suncloud.marrymemo.view.budget.NewWeddingBudgetActivity;
import me.suncloud.marrymemo.view.budget.NewWeddingBudgetFigureActivity;
import me.suncloud.marrymemo.view.marry.MarryBookActivity;
import me.suncloud.marrymemo.view.marry.MarryTaskActivity;
import me.suncloud.marrymemo.view.tools.WeddingCalendarActivity;
import me.suncloud.marrymemo.view.tools.WeddingTableListActivity;
import me.suncloud.marrymemo.view.wallet.FinancialHomeActivity;
import rx.Subscription;

/**
 * Created by Suncloud on 2015/1/29.
 */
public class ToolsFragment extends RefreshFragment {

    @BindView(R.id.btn_back)
    public ImageButton btnBack;
    @BindView(R.id.tools_share)
    TextView toolsShare;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.image_card)
    ImageView imageCard;
    @BindView(R.id.gift_notice_view)
    View giftNoticeView;
    @BindView(R.id.card_btn)
    LinearLayout cardBtn;
    @BindView(R.id.image_wedding_loan)
    ImageView imageWeddingLoan;
    @BindView(R.id.image_wedding_loan_new)
    ImageView imageWeddingLoanNew;
    @BindView(R.id.wedding_loan)
    LinearLayout weddingLoan;
    @BindView(R.id.image_budget)
    ImageView imageBudget;
    @BindView(R.id.image_budget_new)
    ImageView imageBudgetNew;
    @BindView(R.id.budget_btn)
    LinearLayout budgetBtn;
    @BindView(R.id.task_btn_prepared)
    LinearLayout taskBtnPrepared;
    @BindView(R.id.task_btn)
    LinearLayout taskBtn;
    @BindView(R.id.register_btn)
    LinearLayout registerBtn;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.image_wall_new)
    ImageView imageWallNew;
    @BindView(R.id.wall_btn)
    LinearLayout wallBtn;
    @BindView(R.id.calendar_btn)
    LinearLayout calendarBtn;
    @BindView(R.id.seat_btn)
    LinearLayout seatBtn;
    @BindView(R.id.plan_btn)
    LinearLayout planBtn;
    @BindView(R.id.account_btn)
    LinearLayout accountBtn;
    @BindView(R.id.story_btn)
    LinearLayout storyBtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    Unbinder unbinder;
    @BindView(R.id.tools_layout)
    ScrollView toolsLayout;

    private Dialog progressDialog;
    private Dialog shareDialog;
    private TextShareUtil shareUtil;
    private Subscription rxBusEventSub;//接收rxEvent事件
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                    trackerShare("QQZone");
                    break;
                case HljShare.RequestCode.SHARE_TO_QQ:
                    trackerShare("QQ");
                    break;
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    trackerShare("Timeline");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    trackerShare("Session");
                    break;
            }
            return false;
        }
    });
    private boolean isHomePage; // 是否显示在主页上
    private HljHttpSubscriber getBudgetSub;
    private HljHttpSubscriber afterShareSub;

    public static ToolsFragment newInstance(boolean isHomePage) {
        ToolsFragment toolsFragment = new ToolsFragment();
        Bundle args = new Bundle();
        args.putSerializable("is_home_page", isHomePage);
        toolsFragment.setArguments(args);

        return toolsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            isHomePage = args.getBoolean("is_home_page", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tools, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        HljBaseActivity.setActionBarPadding(getContext(), actionHolderLayout);
        if (isHomePage) {
            btnBack.setVisibility(View.GONE);
        } else {
            btnBack.setVisibility(View.VISIBLE);
        }
        initTracker();
        onAppSubmit();
        return rootView;
    }


    /**
     * 审核特殊处理
     */
    private void onAppSubmit() {
        if (FinancialSwitch.INSTANCE.isClosed(getContext())) {
            weddingLoan.setVisibility(View.GONE);
        }
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(toolsLayout, "tool_list");

        HljVTTagger.buildTagger(cardBtn)
                .tagName("tool_card_item")
                .hitTag();
        HljVTTagger.buildTagger(budgetBtn)
                .tagName("tool_budget_item")
                .hitTag();
        HljVTTagger.buildTagger(weddingLoan)
                .tagName("tool_loan_item")
                .hitTag();
        HljVTTagger.buildTagger(taskBtnPrepared)
                .tagName("tool_weddinglist_item")
                .hitTag();
        HljVTTagger.buildTagger(taskBtn)
                .tagName("tool_task_item")
                .hitTag();
        HljVTTagger.buildTagger(registerBtn)
                .tagName("tool_register_item")
                .hitTag();
        HljVTTagger.buildTagger(wallBtn)
                .tagName("tool_wall_item")
                .hitTag();
        HljVTTagger.buildTagger(calendarBtn)
                .tagName("tool_calendar_item")
                .hitTag();
        HljVTTagger.buildTagger(seatBtn)
                .tagName("tool_seat_item")
                .hitTag();
        HljVTTagger.buildTagger(planBtn)
                .tagName("tool_dayflow_item")
                .hitTag();
        HljVTTagger.buildTagger(accountBtn)
                .tagName("tool_cashgift_item")
                .hitTag();
        HljVTTagger.buildTagger(storyBtn)
                .tagName("item_story_item")
                .hitTag();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            TrackerUtil.onTCAgentPageStart(getActivity(), "工具");
        } else {
            TrackerUtil.onTCAgentPageEnd(getActivity(), "工具");
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        if (!isHidden()) {
            TrackerUtil.onTCAgentPageEnd(getActivity(), "工具");
        }
        super.onPause();
    }


    @Override
    public void onResume() {
        if (!isHidden()) {
            TrackerUtil.onTCAgentPageStart(getActivity(), "工具");
        }
        registerRxBusEvent();

        com.hunliji.hljcommonlibrary.models.User user = UserSession.getInstance()
                .getUser(getContext());
        long cardNewsCount = 0;
        if (user != null && user.getId() > 0) {
            Realm realm = Realm.getDefaultInstance();
            //收到请帖未读信息数
            cardNewsCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .beginGroup()
                    .equalTo("notifyType", Notification.NotificationType.GIFT)
                    .or()
                    .equalTo("notifyType", Notification.NotificationType.SIGN)
                    .endGroup()
                    .count();
            realm.close();
        }
        giftNoticeView.setVisibility(cardNewsCount > 0 ? View.VISIBLE : View.GONE);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(getBudgetSub, afterShareSub);
    }

    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case NEW_CARD_NOTICE:
                                    //收到请帖礼物通知
                                    giftNoticeView.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    });
        }
    }

    @OnClick({R.id.calendar_btn, R.id.register_btn, R.id.wedding_loan, R.id.tools_share, R.id
            .task_btn, R.id.card_btn, R.id.wall_btn, R.id.plan_btn, R.id.account_btn, R.id
            .seat_btn, R.id.story_btn, R.id.budget_btn, R.id.task_btn_prepared, R.id.btn_back})
    public void onViewClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            case R.id.calendar_btn:
                intent = new Intent(getActivity(), WeddingCalendarActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.register_btn:
                intent = new Intent(getActivity(), WeddingRegisterActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.task_btn:
                if (Util.loginBindChecked(getActivity(), Constants.Login.WEDDING_TASKS_LOGIN)) {
                    //                    long userId = Session.getInstance()
                    //                            .getCurrentUser(getActivity())
                    //                            .getId();
                    //                    // 从配置文件中读取用户是否配置过任务模板和婚礼时间
                    //                    boolean taskSetuped = getActivity()
                    // .getSharedPreferences(Constants.PREF_FILE,
                    //                            Context.MODE_PRIVATE)
                    //                            .getBoolean("task_set_up_" + userId, false);
                    //                    // 从配置文件中读取用户是否配置过任务模板和婚礼时间
                    //                    boolean taskWeddingDate = getActivity()
                    // .getSharedPreferences(Constants
                    //                                    .PREF_FILE,
                    //                            Context.MODE_PRIVATE)
                    //                            .getBoolean("task_wedding_date_" + userId, false);
                    //                    if (taskSetuped && taskWeddingDate) {
                    //                        // 设置过任务模板也设置过时间
                    //                        intent = new Intent(getActivity(),
                    // WeddingTaskListActivity.class);
                    //                    } else {
                    //                        intent = new Intent(getActivity(),
                    // WeddingTaskTemplatesActivity.class);
                    //                    }
                    intent = new Intent(getContext(), MarryTaskActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.card_btn:
                if (Util.loginBindChecked(getActivity(), Constants.Login.CARD_LOGIN)) {
                    if (getActivity() instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null && mainActivity.cardNewsIcon != null) {
                            mainActivity.cardNewsIcon.setVisibility(View.GONE);
                        }
                    }
                    intent = new Intent(getActivity(), CardListActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }

                break;
            case R.id.wall_btn:
                if (Util.loginBindChecked(getActivity(), Constants.Login.WALL_LOGIN)) {
                    intent = new Intent(getActivity(), WXWallActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.plan_btn:
                if (Util.loginBindChecked(this,
                        getActivity(),
                        Constants.Login.WEDDING_DAY_PROGRAMS_LOGIN)) {
                    intent = new Intent(getActivity(), WeddingDayProgramsActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.account_btn:
                if (Util.loginBindChecked(this, getActivity(), Constants.Login.CARD_LOGIN)) {
                    intent = new Intent(getContext(), MarryBookActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.seat_btn:
                if (Util.loginBindChecked(this, getActivity(), Constants.Login.CARD_LOGIN)) {
                    startActivity(new Intent(getContext(), WeddingTableListActivity.class));
                }
                break;
            case R.id.story_btn:
                if (Util.loginBindChecked(this, getActivity(), Constants.Login.MY_STORY_LOGIN)) {
                    intent = new Intent(getActivity(), MyStoryListActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.tools_share:
                onShare();
                break;
            case R.id.budget_btn:
                if (Util.loginBindChecked(getActivity(), Constants.Login.BUDGET_LOGIN)) {
                    User user = Session.getInstance()
                            .getCurrentUser(getActivity());
                    // 从配置文件中读取用户是否设置过预算
                    boolean aBoolean = getActivity().getSharedPreferences(Constants.PREF_FILE,
                            Context.MODE_PRIVATE)
                            .getBoolean(HljCommon.SharedPreferencesNames.WEDDING_BUDGET + user
                                            .getId(),
                                    false);
                    if (aBoolean) {
                        intent = new Intent(getActivity(), NewWeddingBudgetActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else {
                        showBudget();
                    }
                }
                break;
            case R.id.task_btn_prepared:
                intent = new Intent(getActivity(), WeddingPreparedListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.wedding_loan:
                //新增新婚贷，跳转到金融超市
                intent = new Intent(getActivity(), FinancialHomeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            default:
                break;
        }
    }

    private void showBudget() {
        if (getBudgetSub == null || getBudgetSub.isUnsubscribed()) {
            getBudgetSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            Intent intent;
                            String detail = CommonUtil.getAsString(jsonElement, "detail");
                            if (CommonUtil.isEmpty(detail)) {
                                intent = new Intent(getContext(),
                                        NewWeddingBudgetFigureActivity.class);
                                intent.putExtra(NewWeddingBudgetFigureActivity.ARG_FROM,
                                        ToolsFragment.class.getSimpleName());
                            } else {
                                JsonArray jsonArray = GsonUtil.getGsonInstance()
                                        .fromJson(detail, JsonArray.class);
                                if (jsonArray == null || jsonArray.size() <= 0) {
                                    intent = new Intent(getContext(),
                                            NewWeddingBudgetFigureActivity.class);
                                    intent.putExtra(NewWeddingBudgetFigureActivity.ARG_FROM,
                                            ToolsFragment.class.getSimpleName());
                                } else {
                                    intent = new Intent(getActivity(),
                                            NewWeddingBudgetActivity.class);
                                    User user = Session.getInstance()
                                            .getCurrentUser(getContext());
                                    getContext().getSharedPreferences(Constants.PREF_FILE,
                                            Context.MODE_PRIVATE)
                                            .edit()
                                            .putBoolean(HljCommon.SharedPreferencesNames
                                                            .WEDDING_BUDGET + user.getId(),
                                                    true)
                                            .apply();
                                }
                            }
                            startActivity(intent);
                        }
                    })
                    .setProgressDialog(com.hunliji.hljcommonlibrary.utils.DialogUtil
                            .createProgressDialog(

                            getContext()))
                    .build();
            ToolsApi.getBudgetInfoObb()
                    .subscribe(getBudgetSub);
        }
    }

    @Override
    public void refresh(Object... params) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                    trackerShare("TXWeibo");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    trackerShare("Weibo");
                    break;
                case Constants.Login.MY_STORY_LOGIN:
                    User user = Session.getInstance()
                            .getCurrentUser(getActivity());
                    if (user != null && user.getId() != 0) {
                        Intent intent = new Intent(getActivity(), MyStoryListActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
                case Constants.Login.WEDDING_DAY_PROGRAMS_LOGIN:
                    user = Session.getInstance()
                            .getCurrentUser(getActivity());
                    if (user != null && user.getId() != 0) {
                        Intent intent = new Intent(getActivity(), WeddingDayProgramsActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
                case Constants.Login.WALL_LOGIN:
                    user = Session.getInstance()
                            .getCurrentUser(getActivity());
                    if (user != null && user.getId() != 0) {
                        Intent intent = new Intent(getActivity(), WXWallActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
                case Constants.Login.WEDDING_TASKS_LOGIN:
                    user = Session.getInstance()
                            .getCurrentUser(getActivity());
                    if (user != null && user.getId() > 0) {
                        Intent intent = new Intent(getContext(), MarryTaskActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_up_to_top,
                                R.anim.activity_anim_default);
                    }
                    break;
                case Constants.Login.BUDGET_LOGIN:
                    showBudget();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean getShare;

    private void onShare() {
        if (shareUtil == null) {
            if (!getShare) {
                if (progressDialog == null) {
                    progressDialog = DialogUtil.createProgressDialog(getActivity());
                }
                progressDialog.show();
                new GetShareInfoTask().execute();
            }
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareDialog == null) {
            shareDialog = Util.initTextShareDialog(getActivity(),
                    null,
                    shareUtil,
                    new ShareActionViewHolder.OnShareClickListener() {
                        @Override
                        public void onShare(View v, ShareAction action) {
                            afterShareApp();
                        }
                    });
        }
        shareDialog.show();
    }


    private void afterShareApp() {
        afterShareSub = HljHttpSubscriber.buildSubscriber(getContext())
                .build();
        UserApi.afterShareApp()
                .subscribe(afterShareSub);
    }

    private class GetShareInfoTask extends AsyncTask<Object, Object, JSONObject> {

        private GetShareInfoTask() {
            getShare = true;
        }

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
            getShare = false;
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
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
                    url = "http://dwz.cn/IRby7";
                }
                shareUtil = new TextShareUtil(getActivity(),
                        url,
                        title,
                        description,
                        webDescription,
                        handler);
            } else {
                shareUtil = new TextShareUtil(getActivity(),
                        "http://dwz" + ".cn/IRby7",
                        getString(R.string.tools_share_title),
                        getString(R.string.tools_share_msg),
                        getString(R.string.tools_share_msg),
                        handler);
            }
            onShare();
            super.onPostExecute(jsonObject);
        }
    }

    private void trackerShare(String shareInfo) {
        new HljTracker.Builder(getContext()).screen("tab_tools")
                .action("share")
                .additional(shareInfo)
                .build()
                .send();
    }
}
