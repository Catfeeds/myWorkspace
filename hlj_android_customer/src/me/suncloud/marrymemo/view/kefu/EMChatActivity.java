package me.suncloud.marrymemo.view.kefu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljkefulibrary.moudles.EMTrack;
import com.hunliji.hljkefulibrary.view.EMChatActivityViewHolder;
import com.hunliji.hljkefulibrary.view.activities.BaseEMChatActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.CarOrderDetailActivity;
import me.suncloud.marrymemo.view.ProductOrderDetailActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * Created by wangtao on 2017/10/26.
 */

public class EMChatActivity extends BaseEMChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat___kefu);
        initEMChatData();
        loginCheck();
    }

    private void initEMChatData() {
        support = getIntent().getParcelableExtra("support");
        track = getIntent().getParcelableExtra("track");
        if (support != null) {
            currentName = support.getHxIm();
        } else {
            currentName = getIntent().getStringExtra("name");
        }
        if (support != null && !TextUtils.isEmpty(support.getPhone())) {
            setOkButton(R.mipmap.icon_call_round_primary_54_54);
        }
        setTitle(support == null ? currentName : support.getNick());
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        onCall();
    }

    @Override
    protected EMChatActivityViewHolder initViewHolderBind(Activity activity) {
        return new ActivityViewHolder();
    }

    @Override
    protected void userIsLogin() {
        initData();
        initView();
        loadMessage();
    }

    @Override
    public void onTrackClick(EMTrack track) {
        super.onTrackClick(track);
        switch (track.getTrackType()) {
            case EMTrack.TRACK_TYPE_WORK:
                Intent intent = new Intent(EMChatActivity.this, WorkActivity.class);
                intent.putExtra("id", track.getTrackId());
                startActivity(intent);
                break;
            case EMTrack.TRACK_TYPE_SHOP_PRODUCT:
                intent = new Intent(EMChatActivity.this, ShopProductDetailActivity.class);
                intent.putExtra("id", track.getTrackId());
                startActivity(intent);
                break;
            case EMTrack.TRACK_TYPE_CAR_PRODUCT:
                intent = new Intent(EMChatActivity.this, WeddingCarProductDetailActivity.class);
                intent.putExtra(WeddingCarProductDetailActivity.ARG_ID, track.getTrackId());
                startActivity(intent);
                break;
            case EMTrack.TRACK_TYPE_HOTEL_V2:
            case EMTrack.TRACK_TYPE_HOTEL:
                intent = new Intent(EMChatActivity.this, MerchantDetailActivity.class);
                intent.putExtra("id", track.getTrackId());
                startActivity(intent);
                break;
            case EMTrack.TRACK_TYPE_PRODUCT_ORDER:
                intent = new Intent(EMChatActivity.this, ProductOrderDetailActivity.class);
                intent.putExtra("id", track.getTrackId());
                startActivity(intent);
                break;
            case EMTrack.TRACK_TYPE_CAR_ORDER:
                intent = new Intent(EMChatActivity.this, CarOrderDetailActivity.class);
                intent.putExtra("id", track.getTrackId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onMerchantClick(Merchant merchant) {
        super.onMerchantClick(merchant);
        Intent intent = new Intent(EMChatActivity.this, MerchantDetailActivity.class);
        intent.putExtra("id", merchant.getId());
        startActivity(intent);
    }

    private class ActivityViewHolder extends EMChatActivityViewHolder {
        @Override
        public void bindView(Activity activity) {
            rcChat = activity.findViewById(R.id.chat_list);
            btnImage = activity.findViewById(R.id.btn_image);
            btnVoice = activity.findViewById(R.id.btn_voice);
            btnSpeak = activity.findViewById(R.id.btn_speak);
            etContent = activity.findViewById(R.id.et_content);
            btnFace = activity.findViewById(R.id.btn_face);
            btnSend = activity.findViewById(R.id.btn_send);
            menuLayout = activity.findViewById(R.id.menu_layout);
            speakEditLayout = activity.findViewById(R.id.speak_edit_layout);
            editBarLayout = activity.findViewById(R.id.edit_bar_layout);
            recordView = activity.findViewById(R.id.record_view);
            progressBar = activity.findViewById(R.id.progress_bar);
            layout = activity.findViewById(R.id.layout);
        }
    }
}
