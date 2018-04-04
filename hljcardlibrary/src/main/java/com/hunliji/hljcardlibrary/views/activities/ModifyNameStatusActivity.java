package com.hunliji.hljcardlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.ModifyData;
import com.hunliji.hljcardlibrary.models.ModifyNameResult;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.OriginalImageScaleListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModifyNameStatusActivity extends HljBaseActivity {

    @BindView(R2.id.img_status)
    ImageView imgStatus;
    @BindView(R2.id.tv_status)
    TextView tvStatus;
    @BindView(R2.id.et_groom)
    EditText etGroom;
    @BindView(R2.id.groom_layout)
    View groomLayout;
    @BindView(R2.id.et_bride)
    EditText etBride;
    @BindView(R2.id.bride_layout)
    View brideLayout;
    @BindView(R2.id.tv_type_label)
    TextView tvTypeLabel;
    @BindView(R2.id.tv_card_type)
    TextView tvCardType;
    @BindView(R2.id.card_type_layout)
    RelativeLayout cardTypeLayout;
    @BindView(R2.id.tv_label_groom)
    TextView tvLabelGroom;
    @BindView(R2.id.img_groom_card)
    ImageView imgGroomCard;
    @BindView(R2.id.tv_label_bride)
    TextView tvLabelBride;
    @BindView(R2.id.img_bride_card)
    ImageView imgBrideCard;
    @BindView(R2.id.card_images_layout)
    LinearLayout cardImagesLayout;
    @BindView(R2.id.info_layout)
    LinearLayout infoLayout;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_status_message)
    TextView tvStatusMessage;
    @BindView(R2.id.img_arrow)
    ImageView imgArrow;

    private long cardId;
    private int imgHeight;
    private int imgWidth;
    private ModifyNameResult result;
    private HljHttpSubscriber loadSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name_status);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        cardId = getIntent().getLongExtra("id", 0);
        imgWidth = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 32);
        imgHeight = imgWidth * 193 / 343;
    }

    private void initViews() {
        imgBrideCard.getLayoutParams().height = imgHeight;
        imgGroomCard.getLayoutParams().height = imgHeight;
        etBride.setEnabled(false);
        etGroom.setEnabled(false);
        etGroom.setFocusable(false);
        etBride.setFocusable(false);
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(loadSub);
        loadSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setContentView(scrollView)
                .setOnNextListener(new SubscriberOnNextListener<ModifyNameResult>() {
                    @Override
                    public void onNext(ModifyNameResult modifyNameResult) {
                        // 先检测当前审核状态，如果是通过则提示并推出页面，否则显示详情
                        if (modifyNameResult.getStatus() == ModifyNameResult.STATUS_MODIFY_PASS) {
                            // 审核已经通过了
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ModifyNameStatusActivity.this,
                                    "审核已通过",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            ModifyNameStatusActivity.this.onBackPressed();
                        } else {
                            result = modifyNameResult;
                            setStatusViews();
                        }
                    }
                })
                .build();
        CardApi.checkNameModifyState(cardId)
                .subscribe(loadSub);
    }

    private void setStatusViews() {
        etGroom.setText(result.getModifyData()
                .getGroomNewName());
        etGroom.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
        etBride.setText(result.getModifyData()
                .getBrideNewName());
        etBride.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
        if (result.getStatus() == ModifyNameResult.STATUS_MODIFY_FAIL) {
            imgStatus.setImageResource(R.mipmap.image_rename_review_denied);
            tvStatus.setText("审核未通过");
            tvStatusMessage.setText(result.getModifyData()
                    .getReviewRemark());
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            setOkText("重新申请");
            showOkText();
        } else if (result.getStatus() == ModifyNameResult.STATUS_MODIFY_REVIEWING) {
            imgStatus.setImageResource(R.mipmap.image_rename_reviewing);
            tvStatus.setText("审核中");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.colorLink));
            tvStatusMessage.setText("预计一个工作日内审核完成");
            hideOkText();
        }
        cardImagesLayout.setVisibility(View.VISIBLE);
        imgArrow.setVisibility(View.GONE);
        cardTypeLayout.setEnabled(false);
        if (result.getModifyData()
                .getDocumentType() == ModifyData.D_TYPE_ID_CARD) {
            tvCardType.setText("身份证");
            tvLabelGroom.setText("新郎身份证正面");
            tvLabelBride.setText("新娘身份证正面");
        } else {
            tvCardType.setText("结婚证");
            tvLabelGroom.setText("新郎结婚证");
            tvLabelBride.setText("新娘结婚证");
        }
        Glide.with(this)
                .load(ImagePath.buildPath(result.getModifyData()
                        .getGroomPhotoPath())
                        .width(imgWidth)
                        .path())
                .listener(new OriginalImageScaleListener(imgGroomCard, imgWidth, 0))
                .into(imgGroomCard);
        Glide.with(this)
                .load(ImagePath.buildPath(result.getModifyData()
                        .getBridePhotoPath())
                        .width(imgWidth)
                        .path())
                .listener(new OriginalImageScaleListener(imgBrideCard, imgWidth, 0))
                .into(imgBrideCard);
    }

    @Override
    public void onOkButtonClick() {
        OncePrefUtil.doneThis(this,
                HljCommon.SharedPreferencesNames.SHOWED_CARD_RENAME_DENIED + "_" + cardId);
        Intent intent = new Intent(this, ModifyNameApplyActivity.class);
        intent.putExtra("id", cardId);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSub);
    }
}
