package com.hunliji.marrybiz.view;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.NewMerchant;
import com.hunliji.marrybiz.model.User;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.HttpPostTask;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 生意罗盘竞对activity
 * Created by jinxin on 2016/6/21.
 */
public class CompassCompeteDetailActivity extends HljBaseNoBarActivity {
    private int rectLogoSize;
    private NewMerchant merchant;
    private View progressBar;
    private TextView addHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        merchant = (NewMerchant) getIntent().getSerializableExtra("merchant");
        DisplayMetrics dm = getResources().getDisplayMetrics();
        rectLogoSize = Math.round(dm.density * 64);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_compete);
        progressBar = findViewById(R.id.progressBar);
        addHint = (TextView) findViewById(R.id.add_hint);
        if (merchant != null || merchant.getId() > 0) {
            setOtherHeaderView();
        } else {
            setEmptyView();
        }
        setDefaultStatusBarPadding();
    }

    private void setEmptyView() {
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.img_empty_hint).setVisibility(View.VISIBLE);
        TextView textEmptyHint = (TextView) findViewById(R.id.text_empty_hint);
        textEmptyHint.setText(getString(R.string.no_item));
        textEmptyHint.setVisibility(View.VISIBLE);
        findViewById(R.id.empty_hint_layout).setVisibility(View.VISIBLE);
    }


    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    public void onCancel(View view) {
        onAttention(merchant);
    }

    private void onAttention(final NewMerchant merchant) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        if (merchant.isFocus()) {
            //取消竞对
            params.put("user_id", String.valueOf(merchant.getUserId()));

            String url = Constants.getAbsUrl(Constants.HttpPath.CANCEL_COMPETE,
                    merchant.getId());
            new HttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    if (obj != null) {
                        JSONObject res = (JSONObject) obj;
                        if (res != null) {
                            JSONObject status = res.optJSONObject("status");
                            if (status != null) {
                                String msg = status.optString("msg");
                                if (msg != null && msg.equals("success")) {
                                    merchant.setFocus(false);
                                    addHint.setText(getString(R.string
                                            .label_add_compete));
                                    Toast.makeText
                                            (CompassCompeteDetailActivity.this,
                                            getString(R.string
                                                    .label_cancel_compete_success),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText
                                            (CompassCompeteDetailActivity.this,
                                            getString(R.string
                                                    .label_cancel_compete_fail),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CompassCompeteDetailActivity.this,
                            getString(R.string.label_cancel_compete_fail),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(url, params);
        } else {
            //添加竞对
            params.put("merchant_id", merchant.getId().toString());
            String url = Constants.getAbsUrl(Constants.HttpPath.ADD_COMPETE,
                    merchant.getUserId());
            new HttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    if (obj != null) {
                        JSONObject res = (JSONObject) obj;
                        if (res != null) {
                            JSONObject status = res.optJSONObject("status");
                            if (status != null) {
                                String msg = status.optString("msg");
                                if (msg != null && msg.equals("success")) {
                                    merchant.setFocus(true);
                                    addHint.setText(getString(R.string
                                            .label_cancel_compete));
                                    Toast.makeText
                                            (CompassCompeteDetailActivity.this,
                                            getString(R.string
                                                    .label_add_compete_success),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText
                                            (CompassCompeteDetailActivity.this,
                                            getString(R.string
                                                    .label_add_compete_fail),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CompassCompeteDetailActivity.this,
                            getString(R.string.label_add_compete_fail),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(url, params);
        }
    }

    private void setOtherHeaderView() {
        addHint.setText(getString(merchant.isFocus() ? R.string
                .label_cancel_compete : R.string.label_add_compete));
        MerchantUser currentUser = Session.getInstance().getCurrentUser(this);
        if(currentUser != null && currentUser.getId().equals(merchant.getUserId())){
            addHint.setVisibility(View.GONE);
        }else{
            addHint.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.rank_layout).setVisibility(View.GONE);
        findViewById(R.id.merchant_header_layout).setVisibility(View.VISIBLE);
        View merchantView = findViewById(R.id.merchant_header);
        setMerchantView(merchantView, merchant);
        TextView address = (TextView) findViewById(R.id.address);
        address.setText(getString(R.string.label_merchant_address,
                merchant.getAddress()));
        findViewById(R.id.address_layout).setVisibility(JSONUtil.isEmpty(
                merchant.getAddress()) ? View.GONE : View.VISIBLE);
        ArrayList<String> changeBack = merchant.getChargeBack();
        ArrayList<String> merchantPromise = merchant.getMerchantPromise();
        String refundText = null;
        String promiseText = null;
        TextView refundTextView = (TextView) findViewById(R.id.refund_text);
        TextView promiseTextView = (TextView) findViewById(R.id.promise_text);
        if (changeBack != null && changeBack.size() > 0) {
            Paint paint = refundTextView.getPaint();
            StringBuilder builder = new StringBuilder();
            for (String value : changeBack) {
                builder.append(addSpace(value, paint));
            }
            refundText = builder.toString()
                    .trim();
            refundTextView.setText(refundText);
        }
        if (merchantPromise != null && merchantPromise.size() > 0) {
            Paint paint = promiseTextView.getPaint();
            StringBuilder builder = new StringBuilder();
            for (String value : merchantPromise) {
                builder.append(addSpace(value, paint));
            }
            promiseText = builder.toString()
                    .trim();
            promiseTextView.setText(promiseText);
        }
        if (JSONUtil.isEmpty(refundText) && JSONUtil.isEmpty(promiseText) &&
                merchant.getActiveWorkCount() == 0) {
            findViewById(R.id.promise_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.promise_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.refund).setVisibility(JSONUtil.isEmpty
                    (refundText) ? View.GONE : View.VISIBLE);
            findViewById(R.id.promise).setVisibility(JSONUtil.isEmpty(
                    promiseText) ? View.GONE : View.VISIBLE);
            findViewById(R.id.free).setVisibility(merchant.getActiveWorkCount
                    () > 0 ? View.VISIBLE : View.GONE);
        }

        findViewById(R.id.promise_layout_right).setVisibility(View.GONE);

        String shopGift = merchant.getShopGift();
        if (!JSONUtil.isEmpty(shopGift)) {
            findViewById(R.id.shop_gift_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.gift_layout).setVisibility(View.VISIBLE);
            TextView shopGiftView = (TextView) findViewById(R.id
                    .shop_gift_content);
            findViewById(R.id.shop_gift_right).setVisibility(View.GONE);
            shopGiftView.setText(shopGift);
        }

        String costEffective = merchant.getCostEffective();
        if (!JSONUtil.isEmpty(costEffective)) {
            findViewById(R.id.cost_effective_layout).setVisibility(View
                    .VISIBLE);
            findViewById(R.id.cost_effective_right).setVisibility(View.GONE);
            findViewById(R.id.gift_layout).setVisibility(View.VISIBLE);
            TextView costEffectiveView = (TextView) findViewById(R.id
                    .cost_effective_content);
            costEffectiveView.setText(costEffective);
        }

        if (!JSONUtil.isEmpty(shopGift) || !JSONUtil.isEmpty(costEffective)) {
            findViewById(R.id.gift_layout).setVisibility(View.VISIBLE);
        }

    }

    private String addSpace(String text, Paint paint) {
        String space = "";
        while (getWidth(space, paint) < 24) {
            space += " ";
        }
        return "• " + text + space;
    }

    private float getWidth(String t, Paint paint) {
        if (JSONUtil.isEmpty(t)) {
            return 0;
        }
        return paint.measureText(t);
    }


    /**
     * 商家等级 logo Resource
     *
     * @param grade
     * @return
     */
    private int getMerchantLevelImageResource(int grade) {
        int source = -1;
        switch (grade) {
            case 0:
                source = R.drawable.icon_merchant_level0;
                break;
            case 1:
                source = R.drawable.icon_merchant_level1;
                break;
            case 2:
                source = R.drawable.icon_merchant_level2;
                break;
            case 3:
                source = R.drawable.icon_merchant_level3;
                break;
            case 4:
                source = R.drawable.icon_merchant_level4;
                break;
        }
        return source;
    }

    private void setMerchantView(View view, NewMerchant merchant) {
        if (merchant == null) {
            return;
        }
        RoundedImageView logo = (RoundedImageView) view.findViewById(R.id.logo);
        setRoundImageView(logo, merchant.getLogoPath(), rectLogoSize);
        TextView name = (TextView) view.findViewById(R.id.name);
        ImageView levelIcon = (ImageView) view.findViewById(R.id.level_icon);
        View bondIcon = view.findViewById(R.id.bond_icon);
        View freeIcon = view.findViewById(R.id.free_icon);
        View promiseIcon = view.findViewById(R.id.promise_icon);
        View refundIcon = view.findViewById(R.id.refund_icon);
        View giftIcon = view.findViewById(R.id.gift_icon);
        TextView tvLabel1 = (TextView) view.findViewById(R.id.tv_label1);
        TextView tvLabel2 = (TextView) view.findViewById(R.id.tv_label2);
        TextView tvLabel4 = (TextView) view.findViewById(R.id.tv_label4);
        TextView tvLabel3 = (TextView) view.findViewById(R.id.tv_label3);

        levelIcon.setImageDrawable(getResources().getDrawable(
                getMerchantLevelImageResource(merchant.getGrade())));
        if(merchant.getGrade() != 0){
            levelIcon.setVisibility(View.VISIBLE);
        }else{
            levelIcon.setVisibility(View.GONE);
        }
        name.setText(merchant.getName());

        giftIcon.setVisibility(JSONUtil.isEmpty(merchant.getPlatformGift()) ?
                View.GONE : View.VISIBLE);
        bondIcon.setVisibility(merchant.getBondSign() != null ? View.VISIBLE
                : View.GONE);
        freeIcon.setVisibility(merchant.getActiveWorkCount() > 0 ? View
                .VISIBLE : View.GONE);
        promiseIcon.setVisibility(merchant.getMerchantPromise() != null &&
                merchant.getMerchantPromise()
                .size() > 0 ? View.VISIBLE : View.GONE);
        refundIcon.setVisibility(merchant.getChargeBack() != null && merchant
                .getChargeBack()
                .size() > 0 ? View.VISIBLE : View.GONE);
        tvLabel1.setText(getString(R.string.label_work_count,
                merchant.getActiveWorkCount()));
        tvLabel2.setText(getString(R.string.label_case_count,
                merchant.getActiveCaseCount()));
        tvLabel4.setText(getString(R.string.merchant_collect_count,
                merchant.getFansCount()));
        tvLabel3.setText(getString(R.string.merchant_twitter_count,
                merchant.getFeedCount()));
    }

    private void setRoundImageView(
            RoundedImageView imageView, String path, int size) {
        path = JSONUtil.getImagePath(path, size);
        if (!JSONUtil.isEmpty(path)) {
            ImageLoadTask task = new ImageLoadTask(imageView, 0);
            imageView.setTag(path);
            AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(),
                    R.mipmap.icon_avatar_primary,
                    task);
            task.loadImage(path, rectLogoSize, ScaleMode.ALL, image);
        }
    }
}
