package com.hunliji.marrybiz.view.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.shoptheme.ShopThemeApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;

import rx.Observable;

/**
 * Created by hua_rong on 2017/5/22.
 * 店铺管理（主题预览、店铺服务、店铺预览） web_view
 */

public class ShopWebViewActivity extends HljWebViewActivity implements HljWebViewActivity
        .OnOkTextInterface {

    private MerchantUser user;
    private HljHttpSubscriber subscriber;
    private long theme = -1;
    private int type;//主题预览:2 店铺预览:3
    private boolean isSelectTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setOkTextInterface(this);
        theme = getIntent().getLongExtra("theme", -1);
        type = getIntent().getIntExtra("type", 0);
        isSelectTheme = getIntent().getBooleanExtra("is_select_theme", false);
        super.onCreate(savedInstanceState);
        user = Session.getInstance()
                .getCurrentUser(this);
    }

    @Override
    public boolean onOkTextEnable() {
        return type != 0;
    }

    @Override
    public int okTextColor() {
        return ContextCompat.getColor(this, R.color.colorPrimary);
    }

    @Override
    public int okTextSize() {
        return 14;
    }

    @Override
    public String okText() {
        String okText = "";
        switch (type) {
            case 2:
                okText = getString(R.string.label_use_template);
                break;
            case 3:
                okText = getString(R.string.store_complie);
                break;
        }
        return okText;
    }

    @Override
    public void onOkButtonPressed() {
        switch (type) {
            case 2:
                goShopThemePreview();
                break;
            case 3:
                onEditMerchant();
                break;
            default:
                break;
        }
    }

    /**
     * 编辑店铺
     */
    public void onEditMerchant() {
        Intent intent = new Intent(this, EditShopActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 使用模板
     */
    private void goShopThemePreview() {
        PopupRule popupRule = PopupRule.getDefault();
        if (theme > 0) {
            if (popupRule.showShopReview(this, user)) {
                return;
            }
            if (user.getIsPro() < 2) {
                popupRule.showProDialog(this, progressBar);
            } else {
                useTemplate();
            }
        } else if (theme != -1) {
            useTemplate();
        }
    }


    /**
     * 使用模板
     */
    private void useTemplate() {
        if (isSelectTheme) {
            ShopWebViewActivity.this.finish();
        } else {
            CommonUtil.unSubscribeSubs(subscriber);
            Observable observable = ShopThemeApi.postChooseTheme(theme);
            subscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {

                        @Override
                        public void onNext(Object o) {
                            Intent intent = getIntent();
                            intent.putExtra("theme", theme);
                            setResult(RESULT_OK, intent);
                            ShopWebViewActivity.this.finish();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(subscriber);
        }
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(subscriber);
        super.onFinish();
    }
}
