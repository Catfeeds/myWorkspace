package com.hunliji.marrybiz.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrePhoneRegisterActivity extends HljBaseActivity {

    public static final int MERCHANT_SERVICE = 0;
    public static final int MERCHANT_PRODUCT = 1;

    @BindView(R.id.img_merchant_service)
    RoundedImageView imgMerchantService;
    @BindView(R.id.merchant_service_view)
    View merchantServiceView;
    @BindView(R.id.img_merchant_product)
    RoundedImageView imgMerchantProduct;
    @BindView(R.id.merchant_work_product)
    View merchantWorkProduct;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.cb_service)
    CheckBox cbService;
    @BindView(R.id.cb_product)
    CheckBox cbProduct;


    private int merchantType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_phone_register);
        ButterKnife.bind(this);
        initValue();
        initView();
    }

    private void initValue() {
        merchantType = -1;
    }

    private void initView() {
        imgMerchantService.setImageResource(R.mipmap.icon_merchant_service);
        imgMerchantProduct.setImageResource(R.mipmap.icon_merchant_product);
    }

    @OnClick(R.id.merchant_service_view)
    public void onMerchantServiceViewClicked() {
        if (cbService.isChecked()){
            cbService.setChecked(false);
            merchantType = 0;
            btnNext.setEnabled(false);
        }else {
            cbService.setChecked(true);
            merchantType = MERCHANT_SERVICE;
            btnNext.setEnabled(true);
        }
    }

    @OnClick(R.id.merchant_work_product)
    public void onMerchantWorkProductClicked() {
        DialogUtil.createSingleButtonDialog(this, "婚品电商暂不支持APP注册\n请前往婚礼纪网页端进行注册", null, null)
                .show();
    }

    @OnClick(R.id.btn_next)
    public void onBtnNextClicked() {
        if (merchantType == MERCHANT_SERVICE) {
            Intent intent = new Intent();
            intent.setClass(PrePhoneRegisterActivity.this, PhoneRegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
