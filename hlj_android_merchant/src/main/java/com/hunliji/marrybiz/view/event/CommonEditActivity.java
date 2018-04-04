package com.hunliji.marrybiz.view.event;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.event.PropertyEnum;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 申请活动时通用的文本编辑界面
 * Created by chen_bin on 2016/9/26 0026.
 */
public class CommonEditActivity extends HljBaseActivity {
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.cg_prize)
    CheckableLinearGroup cgPrize;
    @BindView(R.id.cb_no_prize)
    CheckableLinearButton cbNoPrize;
    @BindView(R.id.cb_prize)
    CheckableLinearButton cbPrize;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    private int resId;
    private int hasPrize; //是否抽奖 0否1是

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_edit);
        ButterKnife.bind(this);
        String title = "";
        String hint = "";
        String tip = "";
        int maxLength = 50;
        int minHeight = CommonUtil.dp2px(this, 100);
        resId = getIntent().getIntExtra("res_id", 0);
        hasPrize = getIntent().getIntExtra("has_prize", 0);
        switch (resId) {
            //活动主题
            case R.id.title_layout:
                title = getString(R.string.label_event_title);
                hint = getString(R.string.msg_event_title_tip);
                maxLength = 30;
                break;
            //是否抽奖
            case R.id.winner_limit_layout:
                title = getString(R.string.label_winner_limit);
                tip = getString(R.string.msg_winner_limit_tip);
                contentLayout.setVisibility(View.GONE);
                cgPrize.setVisibility(View.VISIBLE);
                if (hasPrize == 0) {
                    cbNoPrize.setChecked(true);
                } else {
                    cbPrize.setChecked(true);
                }
                cgPrize.setOnCheckedChangeListener(new CheckableLinearGroup
                        .OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                        hasPrize = checkedId == R.id.cb_no_prize ? 0 : 1;
                    }
                });
                break;
            //名额
            case R.id.sign_up_limit_layout:
                title = getString(R.string.label_sign_up_limit);
                hint = getString(R.string.hint_enter_sign_up_limit);
                tip = getString(R.string.msg_sign_up_limit_tip);
                minHeight = CommonUtil.dp2px(this, 44);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
                        .getLayoutParams();
                params.topMargin = CommonUtil.dp2px(this, 10);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(title);
                etContent.setSingleLine(true);
                etContent.setGravity(Gravity.RIGHT);
                etContent.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            //活动内容
            case R.id.content_layout:
                title = getString(R.string.label_event_content);
                hint = PropertyEnum.getHint(Session.getInstance()
                        .getCurrentUser(this)
                        .getPropertyId());
                maxLength = 500;
                minHeight = CommonUtil.dp2px(this, 300);
                break;
            //商家简介
            case R.id.merchant_info_layout:
                title = getString(R.string.label_merchant_intro);
                hint = getString(R.string.msg_merchant_intro_tip);
                maxLength = 100;
                minHeight = CommonUtil.dp2px(this, 200);
                break;
            //到店礼
            case R.id.shop_gift_layout:
                title = getString(R.string.label_shop_gift);
                hint = getString(R.string.hint_enter_shop_gift);
                break;
            //好评礼
            case R.id.comment_gift_layout:
                title = getString(R.string.label_comment_gift);
                hint = getString(R.string.hint_enter_comment_gift);
                break;
            //下单礼
            case R.id.order_gift_layout:
                title = getString(R.string.label_order_gift);
                hint = getString(R.string.hint_enter_order_gift);
                break;
        }
        setTitle(title);
        tvTip.setText(tip);
        etContent.setMinHeight(minHeight);
        etContent.setText(getIntent().getStringExtra("content"));
        if (getIntent().getBooleanExtra("is_edit", true)) {
            setOkText(R.string.complete);
            etContent.setEnabled(true);
            etContent.setHint(hint);
            etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        } else {
            etContent.setEnabled(false);
            etContent.setHint(null);
            etContent.setSelection(etContent.length());
        }
    }

    @Override
    public void onOkButtonClick() {
        if (resId != R.id.winner_limit_layout && TextUtils.isEmpty(etContent.getText())) {
            ToastUtil.showToast(this, null, R.string.hint_enter_content);
            return;
        }
        Intent intent = getIntent();
        intent.putExtra("content",
                etContent.getText()
                        .toString());
        intent.putExtra("has_prize", hasPrize);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(etContent);
        super.onBackPressed();
    }
}