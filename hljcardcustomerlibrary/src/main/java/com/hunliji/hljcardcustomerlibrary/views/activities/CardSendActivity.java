package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import com.hunliji.hljcardcustomerlibrary.utils.ProductRedPacketDialog;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.views.activities.BaseCardSendActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.MinProgramShareInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

/**
 * Created by  hua_rong  on 2017/6/24.
 * 用户端发送页面
 */
@Route(path = RouterPath.IntentPath.Card.CARD_SEND)
public class CardSendActivity extends BaseCardSendActivity {

    protected Dialog praiseDialog;

    private EditText etTitle;
    private EditText etShareContent;
    private HljHttpSubscriber redPacketSub;
    private ProductRedPacketDialog redPacketDialog;
    @Override
    protected void initView() {
        findViewById(R.id.ll_share).setVisibility(View.VISIBLE);
        etTitle = findViewById(R.id.et_title);
        etShareContent = findViewById(R.id.et_share_content);
    }

    @Override
    public void showEditContentWithTitle(MinProgramShareInfo shareInfo) {
        if (shareInfo != null) {
            etTitle.setText(shareInfo.getTitle());
            etShareContent.setText(shareInfo.getDesc());
            etShareContent.setSelection(etShareContent.length());
        }
    }

    @Override
    protected void onPraiseDialog() {
        boolean hasShowed = SPUtils.getBoolean(this,
                HljCommon.SharedPreferencesNames.SHOW_PRAISE_DIALOG,
                false);
        if (hasShowed) {
            if (HljCard.isCardMaster(this)) {
                initProductRedPacket();
            }
            return;
        }
        SPUtils.put(this, HljCommon.SharedPreferencesNames.SHOW_PRAISE_DIALOG, true);
        if (praiseDialog == null) {
            praiseDialog = DialogUtil.createDoubleButtonDialogWithImage(this,
                    getString(R.string.label_msg_praise_error_hint___cm,
                            getString(R.string.app_name)),
                    R.mipmap.icon_new_praise___cm,
                    "给好评",
                    "吐槽",
                    true,
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToPraise();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToFeedback();
                        }
                    });

        }
        if (praiseDialog != null && !praiseDialog.isShowing()) {
            praiseDialog.show();
        }
    }

    private void initProductRedPacket() {
        if (UserSession.getInstance()
                .getUser(this) == null) {
            return;
        }
        redPacketSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        setRedPacketResult(jsonElement);
                    }
                })
                .build();
        CustomerCardApi.getUserRedPacketList()
                .subscribe(redPacketSub);
    }

    private void setRedPacketResult(JsonElement jsonElement) {
        if (jsonElement != null) {
            String headImg = jsonElement.getAsJsonObject()
                    .get("head_img")
                    .getAsString();
            JsonElement list = jsonElement.getAsJsonObject()
                    .get("list")
                    .getAsJsonArray();
            List<RedPacket> redPacketList = null;
            if (list != null) {
                redPacketList = GsonUtil.getGsonInstance()
                        .fromJson(list, new TypeToken<List<RedPacket>>() {}.getType());
            }
            showRedPacketDialog(headImg, redPacketList);
        }
    }

    //新人有礼红包
    private void showRedPacketDialog(String headImg, List<RedPacket> redPacketList) {
        if (UserSession.getInstance()
                .getUser(this) == null) {
            return;
        }

        if (redPacketList == null || redPacketList.isEmpty()) {
            return;
        }
        if (redPacketDialog != null && redPacketDialog.isShowing()) {
            return;
        }
        if (redPacketDialog == null) {
            redPacketDialog = new ProductRedPacketDialog(this, R.style.BubbleDialogTheme);
            redPacketDialog.setContentView(View.inflate(this,
                    R.layout.dialog_product_red_packet,
                    null));
            if (HljCard.isCardMaster(this)) {
                redPacketDialog.setUserOnClickListener(new ProductRedPacketDialog
                        .onUserClickListener() {
                    @Override
                    public void onUserClick() {
                        HljWeb.startWebView(CardSendActivity.this, HljCard.CARD_MASTER_RED_POCKET);
                    }
                });
            }
        }
        redPacketDialog.setHeadImg(headImg);
        redPacketDialog.setRedPacketList(redPacketList);
        redPacketDialog.show();
    }

    private void goToFeedback() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.FEED_BACK_ACTIVITY)
                .navigation(this);
    }

    private void goToPraise() {
        if (HljCard.isCustomer(this)) {
            try {
                String marketUri = "market://details?id=" + getPackageName();
                Uri uri = Uri.parse(marketUri);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                this.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, R.string.label_msg_praise_error___cm, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (HljCard.isCardMaster(this)) {
            HljWeb.startWebView(this, HljCard.CARD_MASTER_RED_POCKET);
        }
    }

    @Override
    protected void onHandShareMessage(Message msg) {
        super.onHandShareMessage(msg);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(redPacketSub);
    }
}
