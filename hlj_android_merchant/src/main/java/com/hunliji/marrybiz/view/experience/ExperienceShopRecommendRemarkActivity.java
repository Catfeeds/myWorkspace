package com.hunliji.marrybiz.view.experience;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.experienceshop.ExperienceShopApi;
import com.hunliji.marrybiz.util.TextCountWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by jinxin on 2017/12/20 0020.
 */

public class ExperienceShopRecommendRemarkActivity extends HljBaseActivity {

    public static final String ARG_STATUS = "status";
    public static final String ARG_ID = "id";

    public static final int FILL_REMARK = 1;//提交备注
    public static final int CONFIRM_ORDER = 2;//确认成单
    public static final int FOLLOW_FAILED = 3;//跟进失败

    @BindView(R.id.edit_remark)
    EditText editRemark;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_confirm2)
    Button btnConfirm2;
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private long id;
    private int status;
    private TextCountWatcher countWatcher;
    private HljHttpSubscriber fillRemarkSub;
    private int contentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_shop_recommend_remark);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
    }

    private void initConstant() {
        if (getIntent() != null) {
            status = getIntent().getIntExtra(ARG_STATUS, 0);
            id = getIntent().getLongExtra(ARG_ID, 0L);
        }
        countWatcher = new TextCountWatcher(editRemark, tvCount, 500);
    }

    private void initWidget() {
        editRemark.addTextChangedListener(countWatcher);
        editRemark.setHint(getEditHint(status));
        contentLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (btnConfirm.getVisibility() != View.GONE) {
                    contentHeight = contentLayout.getMeasuredHeight();
                }
            }
        });
        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom - top > contentHeight) {
                    btnConfirm2.setVisibility(View.GONE);
                    btnConfirm.setVisibility(View.VISIBLE);
                } else {
                    btnConfirm2.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.GONE);
                }
            }
        });
    }

    private String getEditHint(int status) {
        switch (status) {
            case FILL_REMARK:
                return "请输入跟进备注（必填）";
            case CONFIRM_ORDER:
                return "已成单，请输入备注（选填）";
            case FOLLOW_FAILED:
                return "跟进失败，请输入原因（选填）";
            default:
                break;
        }
        return null;
    }

    @OnClick({R.id.btn_confirm, R.id.btn_confirm2})
    void onConfirm() {
        String remark = editRemark.getText()
                .toString()
                .trim();
        if (status == FILL_REMARK && TextUtils.isEmpty(remark)) {
            Toast.makeText(this, "请输入跟进备注", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        CommonUtil.unSubscribeSubs(fillRemarkSub);
        fillRemarkSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        if (result != null) {
                            if (result.getStatus() != null) {
                                if (result.getStatus()
                                        .getRetCode() == 0) {
                                    setResult(RESULT_OK);
                                    finish();
                                    Toast.makeText(ExperienceShopRecommendRemarkActivity.this,
                                            "提交成功",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(ExperienceShopRecommendRemarkActivity.this,
                                            result.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                Toast.makeText(ExperienceShopRecommendRemarkActivity.this,
                                        "提交失败",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Toast.makeText(ExperienceShopRecommendRemarkActivity.this,
                                    "提交失败",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                })
                .build();
        Observable<HljHttpResult> obb = null;

        switch (status) {
            case FILL_REMARK:
                obb = ExperienceShopApi.fillRemark(id, remark);
                break;
            case CONFIRM_ORDER:
                obb = ExperienceShopApi.confirmOrder(id, remark);
                break;
            case FOLLOW_FAILED:
                obb = ExperienceShopApi.followFailed(id, remark);
                break;
            default:
                break;
        }

        if (obb != null) {
            obb.subscribe(fillRemarkSub);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(fillRemarkSub);
    }
}
