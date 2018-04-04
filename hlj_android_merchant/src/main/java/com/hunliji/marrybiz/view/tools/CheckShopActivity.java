package com.hunliji.marrybiz.view.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.models.NotebookType;
import com.hunliji.hljnotelibrary.utils.NoteDialogUtil;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljnotelibrary.views.activities.CreatePhotoNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.CreateVideoNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteEduActivity;
import com.hunliji.hljvideolibrary.activities.BaseVideoTrimActivity;
import com.hunliji.hljvideolibrary.activities.VideoChooserActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.tools.viewholder.CheckShopViewHolder;
import com.hunliji.marrybiz.api.tools.ToolsApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.tools.CheckInfo;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.widget.ArcProgressBar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 店铺诊断
 * Created by chen_bin on 2016/8/30 0030.
 */
public class CheckShopActivity extends HljBaseNoBarActivity implements
        OnItemClickListener<CheckInfo> {
    @BindView(R.id.check_list_layout)
    LinearLayout checkListLayout;
    @BindView(R.id.header_view)
    LinearLayout headerView;
    @BindView(R.id.tv_status_hint)
    TextView tvStatusHint;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.progress_bar)
    ArcProgressBar progressBar;
    @BindView(R.id.btn_check)
    Button btnCheck;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    private ArrayList<CheckInfo> checks;
    private MerchantUser user;
    private Handler handler;
    private String errorMsg;
    private boolean isCheckSucceed;//是否检测完成
    private boolean isCheckFailed;
    private float checkProgress;  //检测完成时的进度条
    private float minProgress;
    private float middleProgress;
    private float maxProgress;
    private float progress;
    private int pointer;  //指针的左右标志位，0代表指针需要往右边指，1代需要往左指
    private HljHttpSubscriber postSub;
    private final static String PREF_CHECK_JSON = "pref_check_json_";
    private final static String PREF_CHECK_PROGRESS = "pref_check_progress_";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_shop);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);
        initValue();
        initLoad();
    }

    private void initValue() {
        checks = new ArrayList<>();
        handler = new Handler();
        minProgress = getMax() / 6.0f;
        middleProgress = getMax() / 2.0f;
        maxProgress = getMax() * 5.0f / 6.0f;
        user = Session.getInstance()
                .getCurrentUser(this);
        progress = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getFloat(
                PREF_CHECK_PROGRESS + user.getMerchantId(),
                -1);
        if (progress < 0) {
            progress = middleProgress;
        } else {
            isCheckSucceed = true;
        }
        progressBar.setProgress(progress);
        checkSucceed(progress);
    }

    private void initLoad() {
        String json = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getString(
                PREF_CHECK_JSON + user.getMerchantId(),
                "");
        if (!TextUtils.isEmpty(json)) {
            List<CheckInfo> list = Arrays.asList(GsonUtil.getGsonInstance()
                    .fromJson(json, CheckInfo[].class));
            if (!CommonUtil.isCollectionEmpty(list)) {
                checks.addAll(list);
            }
        }
        int count = checkListLayout.getChildCount();
        int size = 8;
        if (count > size) {
            checkListLayout.removeViews(size, count - size);
        }
        for (int i = 0; i < size; i++) {
            View view = null;
            CheckShopViewHolder checkShopViewHolder;
            if (count > i) {
                view = checkListLayout.getChildAt(i);
            }
            if (view == null) {
                View.inflate(this, R.layout.check_shop_list_item, checkListLayout);
                view = checkListLayout.getChildAt(checkListLayout.getChildCount() - 1);
            }
            checkShopViewHolder = (CheckShopViewHolder) view.getTag();
            if (checkShopViewHolder == null) {
                checkShopViewHolder = new CheckShopViewHolder(view);
                view.setTag(checkShopViewHolder);
            }
            CheckInfo checkInfo;
            if (i < checks.size()) {
                checkInfo = checks.get(i);
            } else {
                checkInfo = new CheckInfo();
                checks.add(checkInfo);
            }
            checkShopViewHolder.setOnItemClickListener(this);
            checkShopViewHolder.setShowBottomLiveView(i < size - 1);
            checkShopViewHolder.setView(this, checkInfo, i, 0);
        }
    }

    @Override
    public void onItemClick(int position, CheckInfo checkInfo) {
        if (position == CheckInfo.WORK_CHECK || position == CheckInfo.WORK_QUALITY || position ==
                CheckInfo.CASE_CHECK || position == CheckInfo.CASE_QUALITY) {
            ToastUtil.showToast(CheckShopActivity.this, null, R.string.hint_login_web_optimize);
        } else if (position == CheckInfo.MARKET_TOOL) {
            if (PopupRule.getDefault()
                    .showShopReview(CheckShopActivity.this, user)) {
                return;
            }
            Intent intent = new Intent(CheckShopActivity.this, HomeActivity.class);
            intent.putExtra("page_index", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else if (position == CheckInfo.MERCHANT_FEED_COUNT) {
            if (PopupRule.getDefault()
                    .showShopReview(CheckShopActivity.this, user)) {
                return;
            }
            NoteDialogUtil.showCreateNoteMenuDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (NotePrefUtil.getInstance(CheckShopActivity.this)
                            .isShowNoteEdu()) {
                        intent.setClass(CheckShopActivity.this, NoteEduActivity.class);
                        intent.putExtra("note_type", Note.TYPE_NORMAL);
                    } else {
                        intent.setClass(CheckShopActivity.this, CreatePhotoNoteActivity.class);
                        intent.putExtra(CreatePhotoNoteActivity.ARG_NOTEBOOK_TYPE,
                                NotebookType.TYPE_WEDDING_PERSON);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (NotePrefUtil.getInstance(CheckShopActivity.this)
                            .isShowNoteEdu()) {
                        intent.setClass(CheckShopActivity.this, NoteEduActivity.class);
                        intent.putExtra("note_type", Note.TYPE_VIDEO);
                        startActivityForResult(intent, HljNote.RequestCode.NOTE_HELP);
                    } else {
                        intent.setClass(CheckShopActivity.this, VideoChooserActivity.class);
                        intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                                HljNote.NOTE_MAX_VIDEO_LENGTH);
                        startActivityForResult(intent, HljNote.RequestCode.CHOOSE_VIDEO);
                    }
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            })
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.NOTE_HELP:
                    if (data != null) {
                        int noteType = data.getIntExtra("note_type", Note.TYPE_NORMAL);
                        if (noteType == Note.TYPE_VIDEO) {
                            Intent intent = new Intent(this, VideoChooserActivity.class);
                            intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                                    HljNote.NOTE_MAX_VIDEO_LENGTH);
                            startActivityForResult(intent, HljNote.RequestCode.CHOOSE_VIDEO);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                    break;
                case HljNote.RequestCode.CHOOSE_VIDEO:
                    if (data != null) {
                        Photo photo = data.getParcelableExtra("photo");
                        if (photo != null) {
                            Intent intent = new Intent(this, CreateVideoNoteActivity.class);
                            intent.putExtra("photo", photo);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up,
                                    R.anim.activity_anim_default);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            if (progress <= minProgress) {
                pointer = 0;
            } else if (progress >= maxProgress) {
                pointer = 1;
            }
            progressBar.setProgress(progress);
            if (isCheckSucceed && progress == checkProgress) {
                checkSucceed(checkProgress);
                handler.removeCallbacks(this);
            } else if (isCheckFailed) {
                checkFailed();
                handler.removeCallbacks(this);
            } else {
                progress = pointer == 0 ? progress + 2 : progress - 2;
                handler.postDelayed(this, 2);
            }
        }
    };

    @OnClick(R.id.btn_check)
    public void onCheck() {
        this.errorMsg = "";
        this.isCheckFailed = false;
        this.isCheckSucceed = false;
        this.pointer = 0;
        this.checkProgress = maxProgress;
        btnCheck.setVisibility(View.INVISIBLE);
        tvStatusHint.setText(R.string.hint_checking_shop);
        tvStatus.setText(R.string.label_checking_shop);
        postCheckMerchant(0);
        handler.removeCallbacks(mRunnable);
        handler.post(mRunnable);
    }

    private void postCheckMerchant(final int position) {
        final CheckInfo checkInfo = checks.get(position);
        final CheckShopViewHolder holder = (CheckShopViewHolder) checkListLayout.getChildAt
                (position)
                .getTag();
        holder.itemView.setClickable(false);
        holder.imgNext.setVisibility(View.GONE);
        holder.tvContent.setText(R.string.label_checking_shop2);
        holder.tvContent.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
        CommonUtil.unSubscribeSubs(postSub);
        postSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<CheckInfo>() {
                    @Override
                    public void onNext(CheckInfo object) {
                        checkInfo.setStatus(1);
                        checkInfo.setNum(object.getNum());
                        checkInfo.setCheck(object.isCheck());
                        checkInfo.setExtra(object.getExtra());
                        if (!checkInfo.isCheck()) {
                            if (position == CheckInfo.WORK_CHECK || position == CheckInfo
                                    .WORK_QUALITY || position == CheckInfo.CASE_QUALITY) {
                                checkProgress = minProgress;
                            } else if (checkProgress != minProgress) {
                                checkProgress = middleProgress;
                            }
                        }
                        holder.setView(CheckShopActivity.this, checkInfo, position, 0);
                        if (position < checks.size() - 1) {
                            postCheckMerchant(position + 1);
                        } else {
                            isCheckSucceed = true;
                            getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                                    .putFloat(PREF_CHECK_PROGRESS + user.getMerchantId(),
                                            checkProgress)
                                    .putString(PREF_CHECK_JSON + user.getMerchantId(),
                                            GsonUtil.getGsonInstance()
                                                    .toJson(checks))
                                    .apply();
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        isCheckFailed = true;
                        errorMsg = o.toString();
                    }
                })
                .build();
        ToolsApi.checkShopObb(CheckInfo.urls[position])
                .subscribe(postSub);
    }

    //检测完成
    private void checkSucceed(float progress) {
        this.pointer = 0;
        btnCheck.setVisibility(View.VISIBLE);
        if (!isCheckSucceed) {
            headerView.setBackgroundColor(Color.parseColor("#ff5454"));
            actionHolderLayout.setBackgroundColor(Color.parseColor("#ff5454"));
            btnCheck.setTextColor(Color.parseColor("#ff5454"));
            btnCheck.setText(R.string.label_check_immediately);
            tvStatus.setText(R.string.label_not_checked);
            tvStatusHint.setText(R.string.hint_not_checked);
        } else if (isBadMerchant(progress)) {
            headerView.setBackgroundColor(Color.parseColor("#ff5454"));
            actionHolderLayout.setBackgroundColor(Color.parseColor("#ff5454"));
            btnCheck.setTextColor(Color.parseColor("#ff5454"));
            btnCheck.setText(R.string.label_reset_check);
            tvStatus.setText(R.string.label_checked_shop_bad);
            tvStatusHint.setText(R.string.hint_checked_shop_bad);
        } else if (isGeneralMerchant(progress)) {
            headerView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_orange4));
            actionHolderLayout.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.color_orange4));
            btnCheck.setTextColor(ContextCompat.getColor(this, R.color.color_orange4));
            btnCheck.setText(R.string.label_reset_check);
            tvStatus.setText(R.string.label_checked_shop_general);
            tvStatusHint.setText(R.string.hint_checked_shop_general);
        } else {
            headerView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_green6));
            actionHolderLayout.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.color_green6));
            btnCheck.setTextColor(ContextCompat.getColor(this, R.color.color_green6));
            btnCheck.setText(R.string.label_reset_check);
            tvStatus.setText(R.string.label_checked_shop_good);
            tvStatusHint.setText(R.string.hint_checked_shop_good);
        }
    }

    //检测失败
    private void checkFailed() {
        ToastUtil.showToast(this, errorMsg, 0);
        this.pointer = 0;
        btnCheck.setVisibility(View.VISIBLE);
        btnCheck.setText(R.string.label_reset_check);
        tvStatus.setText(R.string.label_checked_fail);
        tvStatusHint.setText(R.string.hint_checked_shop_fail);
        initLoad();
    }

    //差劲
    private boolean isBadMerchant(float progress) {
        return progress >= 0 && progress < getMax() / 3.0f;
    }

    //良好
    private boolean isGeneralMerchant(float progress) {
        return progress >= getMax() / 3 && progress < getMax() * 2.0f / 3.0f;
    }

    private float getMax() {
        return 192.0f;
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        handler.removeCallbacks(mRunnable);
        CommonUtil.unSubscribeSubs(postSub);
    }
}