package me.suncloud.marrymemo.jsinterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljinsurancelibrary.views.activities.AfterPayInsuranceProductActivity;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.models.wrappers.RxWebNoteComment;
import com.hunliji.hljnotelibrary.views.activities.NoteCommentListActivity;
import com.hunliji.hljnotelibrary.views.activities.NotebookActivity;
import com.hunliji.hljnotelibrary.views.activities.PostNoteCommentActivity;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CarProduct;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ShopProduct;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.AccountEditActivity;
import me.suncloud.marrymemo.view.BuyWorkActivity;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.CreditActivity;
import me.suncloud.marrymemo.view.CustomerPhotosActivity;
import me.suncloud.marrymemo.view.LightUpActivity;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.OrderConfirmActivity;
import me.suncloud.marrymemo.view.ThreadPicsPageViewActivity;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.CreateThreadActivity;
import me.suncloud.marrymemo.view.kefu.AdvHelperActivity;
import me.suncloud.marrymemo.view.kefu.EMChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchUtil;
import rx.Subscriber;
import rx.Subscription;


/**
 * Created by Suncloud on 2015/6/11.
 */
public class WebHandler extends BaseWebHandler {

    private City city;
    private MerchantUser merchant;
    private long groupId;
    private int kind;
    private Map<String, Serializable> trackMap;
    private String type;
    private long trackId;
    private Support support;
    private Dialog progressDialog;
    private ShareInfo shareInfo;
    private Subscription payInsuranceSub;

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public WebHandler(
            Context context, String path, WebView webView, Handler handler) {
        super(context, path, webView, handler);
        if (city == null) {
            city = Session.getInstance()
                    .getMyCity(context);
        }
    }


    @JavascriptInterface
    public void showMerchant(String idStr) {
        try {
            long id = Long.valueOf(idStr);
            if (id > 0) {
                Intent intent = new Intent(context, MerchantDetailActivity.class);
                intent.putExtra("id", id);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        } catch (NumberFormatException ignored) {

        }
    }

    @JavascriptInterface
    public void contactMerchant(String idStr, String avatarUrl, String merchantNick) {
        contactMerchant(idStr, avatarUrl, merchantNick, null, "0");
    }

    @JavascriptInterface
    public void contactMerchant(String string) {
        if (JSONUtil.isEmpty(string)) {
            return;
        }
        ArrayList<String> strings = stringSplit(string);
        String idStr = null, avatarUrl = null, merchantNick = null, kind = null, idStr2 = null;
        if (strings.size() > 0) {
            idStr = strings.get(0);
        }
        if (strings.size() > 1) {
            avatarUrl = strings.get(1);
        }
        if (strings.size() > 2) {
            merchantNick = strings.get(2);
        }
        if (strings.size() > 3) {
            kind = strings.get(3);
        }
        if (strings.size() > 4) {
            idStr2 = strings.get(4);
        }
        contactMerchant(idStr, avatarUrl, merchantNick, kind, idStr2);

    }

    @JavascriptInterface
    public void contactMerchant(
            String idStr, String avatarUrl, String merchantNick, String kind, String idStr2) {
        if (JSONUtil.isEmpty(idStr)) {
            return;
        }
        if (idStr.contains(":")) {
            contactMerchant(idStr);
            return;
        }
        long id = 0;
        try {
            id = Long.valueOf(idStr);
        } catch (NumberFormatException ignored) {

        }
        if (id == 0) {
            return;
        }
        trackId = 0;
        if (!JSONUtil.isEmpty(idStr)) {
            try {
                trackId = Long.valueOf(idStr2);
            } catch (NumberFormatException ignored) {

            }
        }
        type = kind;
        merchant = new MerchantUser();
        merchant.setNick(merchantNick);
        merchant.setId(id);
        merchant.setAvatar(avatarUrl);
        if (Util.loginBindChecked(((Activity) context), Constants.Login.CONTACT_LOGIN)) {
            contactWithTrack(type, trackId, merchant, null);
        }
    }

    @JavascriptInterface
    public void showWork(String idStr, String commodityTypeStr) {
        long id = 0;
        int commodityType = 0;
        try {
            id = Long.valueOf(idStr);
            commodityType = Integer.valueOf(commodityTypeStr);
        } catch (NumberFormatException ignored) {

        }
        if (id == 0) {
            return;
        }
        Intent intent;
        if (commodityType == 0) {
            intent = new Intent(context, WorkActivity.class);
        } else {
            intent = new Intent(context, CaseDetailActivity.class);
        }
        intent.putExtra("id", id);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void cars() {
        DataConfig.gotoWeddingCarActivity(context, city);
    }

    @JavascriptInterface
    public void thread(String idStr) {
        long id = 0;
        try {
            id = Long.valueOf(idStr);
        } catch (NumberFormatException ignored) {

        }
        if (id == 0) {
            return;
        }
        Intent intent = new Intent(context, CommunityThreadDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void hotels() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.MerchantListActivityPath
                        .MERCHANT_LIST_ACTIVITY)
                .withLong(RouterPath.IntentPath.Customer.MerchantListActivityPath.ARG_PROPERTY_ID,
                        RouterPath.IntentPath.Customer.MerchantListActivityPath.Property.HOTEL)
                .navigation(context);
    }

    @JavascriptInterface
    public void showWorkList() {
        Intent intent = new Intent(context, BuyWorkActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void bannerAction(String bannerStr) {
        if (JSONUtil.isEmpty(bannerStr)) {
            return;
        }
        ArrayList<String> strings = stringSplit(bannerStr);
        int property = 0;
        long forwardId = 0;
        String url = null;
        if (strings.size() > 0) {
            String string = strings.get(0);
            if (!JSONUtil.isEmpty(string)) {
                try {
                    property = Integer.valueOf(string);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        if (strings.size() > 1) {
            String string = strings.get(1);
            if (!JSONUtil.isEmpty(string)) {
                try {
                    forwardId = Long.valueOf(string);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        if (strings.size() > 2) {
            url = strings.get(2);
        }
        City c = null;
        if (strings.size() > 3) {
            String string = strings.get(3);
            if (!JSONUtil.isEmpty(string)) {
                try {
                    long cityId = Long.valueOf(string);
                    c = new City(cityId, url);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        BannerUtil.bannerAction(context, property, forwardId, url, c == null ? city : c, false);

    }

    @JavascriptInterface
    public void sendThread(String groupIdStr) {
        long id = 0;
        try {
            id = Long.valueOf(groupIdStr);
        } catch (NumberFormatException ignored) {

        }
        if (id == 0) {
            return;
        }
        if (Util.loginBindChecked(((Activity) context), Constants.Login.SEND_THREAD_LOGIN)) {
            Intent intent = new Intent(context, CreateThreadActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_from_bottom,
                    R.anim.activity_anim_default);
        } else {
            groupId = id;
        }
    }

    @JavascriptInterface
    public void search() {
        Intent intent = new Intent(context, NewSearchActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void login() {
        Util.loginBindChecked(((Activity) context), Constants.Login.WEB_LOGIN);
    }

    @JavascriptInterface
    public void setMealBuy2(String string) {
        if (JSONUtil.isEmpty(string)) {
            return;
        }
        if (Util.loginBindChecked(((Activity) context), Constants.Login.CONFIRM_LOGIN)) {
            Pattern facePattern = Pattern.compile("\\w+\\:\\d+\\:\\d+");
            Matcher matcher = facePattern.matcher(string);
            JSONArray orderArray = new JSONArray();
            String orderType = null;
            while (matcher.find()) {
                String orderString = matcher.group(0);
                if (!JSONUtil.isEmpty(orderString)) {
                    String[] strings = orderString.split(":");
                    if (strings.length > 2 && !JSONUtil.isEmpty(strings[0]) && !JSONUtil.isEmpty(
                            strings[1]) && !JSONUtil.isEmpty(strings[2])) {
                        String typeStr = strings[0];
                        long itemId = 0;
                        int itemNum = 0;
                        try {
                            itemId = Long.valueOf(strings[1]);
                            itemNum = Integer.valueOf(strings[2]);
                        } catch (NumberFormatException ignored) {

                        }
                        if ((typeStr.equalsIgnoreCase(orderType) || JSONUtil.isEmpty(orderType))
                                && itemId > 0 && itemNum > 0) {
                            int type = -1;
                            switch (typeStr) {
                                case "work":
                                    type = 0;
                                    break;
                                case "car":
                                    type = 1;
                                    break;
                                case "car_set":
                                    type = 2;
                                    break;
                                default:
                                    break;
                            }
                            if (type >= 0) {
                                orderType = typeStr;
                                try {
                                    JSONObject orderJson = new JSONObject();
                                    orderJson.put("id", itemId);
                                    orderJson.put("num", itemNum);
                                    orderJson.put("type", type);
                                    orderArray.put(orderJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }

                }
            }
            if (orderArray.length() > 0) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("prds", orderArray);
                    Intent intent = new Intent(context, OrderConfirmActivity.class);
                    if (!JSONUtil.isEmpty(orderType) && (orderType.equals("car") || orderType
                            .equals(
                            "car_set"))) {
                        intent.putExtra("is_car_order", true);
                    }
                    intent.putExtra("orderStr", jsonObject.toString());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @JavascriptInterface
    public void talkToSupport(String string) {
        if (JSONUtil.isEmpty(string)) {
            return;
        }
        ArrayList<String> strings = stringSplit(string);
        String kindStr, type = null, idStr2 = null;
        if (strings.size() > 0) {
            kindStr = strings.get(0);
        } else {
            talkToSupport(string, null, "0");
            return;
        }
        if (strings.size() > 1) {
            type = strings.get(1);
        }
        if (strings.size() > 2) {
            idStr2 = strings.get(2);
        }
        talkToSupport(kindStr, type, idStr2);

    }

    @JavascriptInterface
    public void talkToSupport(String kindStr, String type, String idStr2) {
        if (JSONUtil.isEmpty(kindStr)) {
            return;
        }
        if (kindStr.contains(":")) {
            talkToSupport(kindStr);
            return;
        }
        kind = 0;
        try {
            kind = Integer.valueOf(kindStr);
        } catch (Exception ignored) {
        }
        if (kind == Support.SUPPORT_KIND_DEFAULT) {
            kind = Support.SUPPORT_KIND_DEFAULT_ROBOT;
        }
        trackId = 0;
        try {
            trackId = Long.valueOf(idStr2);
        } catch (NumberFormatException ignored) {

        }
        this.type = type;
        talkSupport();

    }

    @JavascriptInterface
    public void qrEncodeAndSave(String code) {
        if (JSONUtil.isEmpty(code)) {
            return;
        }
        ArrayList<String> strings = stringSplit(code);
        if (strings.size() > 1) {
            String url = strings.get(0);
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            new QRCodeTask().execute(url, strings.get(1));
        }
    }

    @JavascriptInterface
    public void addShopCart() {
        Session.getInstance()
                .setNewCart(true);
    }

    @JavascriptInterface
    public void guest_photos_page(String idStr) {
        long propertyId = 0;
        if (!JSONUtil.isEmpty(idStr)) {
            try {
                propertyId = Long.valueOf(idStr);
            } catch (NumberFormatException ignored) {
            }
        }
        if (propertyId > 0) {
            Intent intent = new Intent(context, CustomerPhotosActivity.class);
            intent.putExtra("city", city);
            intent.putExtra("property_id", propertyId);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }

    }

    @JavascriptInterface
    public void share(
            String thirdparty, String title, String desc1, String desc2, String url, String icon) {
        if (JSONUtil.isEmpty(thirdparty)) {
            return;
        }
        if (thirdparty.contains(":")) {
            share(thirdparty);
            return;
        }
        ShareUtil shareUtil = null;
        if (shareInfo != null) {
            shareUtil = new ShareUtil(context,
                    shareInfo.getUrl(),
                    shareInfo.getTitle(),
                    shareInfo.getDesc(),
                    shareInfo.getDesc2(),
                    shareInfo.getIcon(),
                    handler);
        }
        if (shareUtil == null || (!JSONUtil.isEmpty(title) && !JSONUtil.isEmpty(url))) {
            shareUtil = new ShareUtil(context, url, title, desc1, desc2, icon, handler);
        }
        if (shareUtil == null) {
            return;
        }
        switch (thirdparty) {
            case "WechatFriend":
                shareUtil.shareToPengYou();
                break;
            case "Wechat":
                shareUtil.shareToWeiXin();
                break;
            case "SinaWeibo":
                shareUtil.shareToWeiBo();
                break;
            case "QQ":
                shareUtil.shareToQQ();
                break;
        }
    }

    @JavascriptInterface
    public void user_credit_duiba(String url) {
        if (JSONUtil.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, CreditActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void user_credit_duiba_product(String url) {
        user_credit_duiba(url);
    }

    @JavascriptInterface
    public void user_info_edit() {
        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user != null && user.getId() > 0) {
            Intent intent = new Intent(context, AccountEditActivity.class);
            ((Activity) context).startActivityForResult(intent,
                    Constants.RequestCode.EDIT_USER_INFO);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @JavascriptInterface
    public void show_adviser_from_intro() {
        if (Util.loginBindChecked((Activity) context, Constants.Login.ADV_HELPER_LOGIN)) {
            if (city == null) {
                city = Session.getInstance()
                        .getMyCity(context);
            }
            boolean b = false;
            DataConfig dataConfig = Session.getInstance()
                    .getDataConfig(context);
            if (city != null && city.getId() > 0 && dataConfig != null && dataConfig.getAdvCids()
                    != null && !dataConfig.getAdvCids()
                    .isEmpty()) {
                b = dataConfig.getAdvCids()
                        .contains(city.getId());
            }
            Intent intent;
            int enterAnim = R.anim.slide_in_right;
            if (b) {
                intent = new Intent(context, AdvHelperActivity.class);
            } else {
                intent = new Intent(context, LightUpActivity.class);
                intent.putExtra("city", city);
                intent.putExtra("type", 3);
                enterAnim = R.anim.fade_in;
            }
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(enterAnim, R.anim.activity_anim_default);
        }
    }

    @JavascriptInterface
    public void back_to_main() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("action", "weddingExpoFragment");
        context.startActivity(intent);

    }

    @JavascriptInterface
    public void image_preview(String imgStr) {
        ArrayList<String> strings = stringSplit(imgStr);
        if (strings.size() <= 1) {
            return;
        }
        ArrayList<Photo> photos = new ArrayList<>();
        for (int i = 1, size = strings.size(); i < size; i++) {
            String path = null;
            try {
                path = URLDecoder.decode(strings.get(i), "UTF-8")
                        .split("\\?")[0];
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (JSONUtil.isEmpty(path)) {
                continue;
            }
            Photo photo = new Photo(new JSONObject());
            photo.setPath(path);
            photos.add(photo);
        }
        Intent intent = new Intent(context, ThreadPicsPageViewActivity.class);
        intent.putExtra("photos", photos);
        try {
            intent.putExtra("position", Integer.valueOf(strings.get(0)));
        } catch (NumberFormatException e) {
            intent.putExtra("position", 0);
        }
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void applyPraise() {
        if (handler != null) {
            handler.sendEmptyMessage(Constants.RequestCode.POST_PRAISE);
        }
    }

    //回答详情用户跳转
    @JavascriptInterface
    public void jumpToUserPage(String kind, String idStr) {
        try {
            long id = Long.valueOf(idStr);
            Intent intent = new Intent();
            switch (kind) {
                case "1":
                    intent.setClass(context, MerchantDetailActivity.class);
                    break;
                default:
                    intent.setClass(context, UserProfileActivity.class);
                    break;

            }
            intent.putExtra("id", id);
            context.startActivity(intent);
        } catch (NumberFormatException ignored) {
        }
    }

    //回答详情用户跳转
    @JavascriptInterface
    public void jumpToUserPage(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        long id = jsonObject.optLong("id");
        int kind = jsonObject.optInt("kind");
        Intent intent = new Intent();
        switch (kind) {
            case 1:
                intent.setClass(context, MerchantDetailActivity.class);
                break;
            default:
                intent.setClass(context, UserProfileActivity.class);
                break;

        }
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private class QRCodeTask extends AsyncTask<String, Integer, Bitmap> {

        private QRCodeTask() {
            if (progressDialog == null) {
                progressDialog = DialogUtil.createProgressDialog(context);
            }
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Hashtable<EncodeHintType, Object> qrParam = new Hashtable<>();
            qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            String content = params[0];
            int size = 0;
            if (params.length > 1) {
                try {
                    size = Integer.valueOf(params[1]);
                } catch (NumberFormatException ignored) {

                }
            }
            if (size == 0) {
                size = 400;
            }
            try {
                BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                        BarcodeFormat.QR_CODE,
                        size,
                        size,
                        qrParam);
                int w = bitMatrix.getWidth();
                int h = bitMatrix.getHeight();
                int[] data = new int[w * h];
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        if (bitMatrix.get(x, y))
                            data[y * w + x] = 0xff000000;
                        else
                            data[y * w + x] = -1;
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(data, 0, w, 0, 0, w, h);
                return bitmap;
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (result != null && !result.isRecycled()) {
                String string = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        result,
                        null,
                        null);
                result.recycle();
                result = null;
                System.gc();
                if (!JSONUtil.isEmpty(string)) {
                    Uri uri = Uri.parse(string);
                    string = JSONUtil.getImagePathForUri(uri, context);
                    Toast.makeText(context,
                            context.getString(R.string.msg_saved_success2) + string,
                            Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                }
            }
            super.onPostExecute(result);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.Login.ADV_HELPER_LOGIN:
                    User user = Session.getInstance()
                            .getCurrentUser(context);
                    if (user != null && user.getId() > 0) {
                        if (city == null) {
                            city = Session.getInstance()
                                    .getMyCity(context);
                        }
                        boolean b = false;
                        DataConfig dataConfig = Session.getInstance()
                                .getDataConfig(context);
                        if (city != null && city.getId() > 0 && dataConfig != null && dataConfig
                                .getAdvCids() != null && !dataConfig.getAdvCids()
                                .isEmpty()) {
                            b = dataConfig.getAdvCids()
                                    .contains(city.getId());
                        }
                        Intent intent;
                        int enterAnim = R.anim.slide_in_right;
                        if (b) {
                            intent = new Intent(context, AdvHelperActivity.class);
                        } else {
                            intent = new Intent(context, LightUpActivity.class);
                            intent.putExtra("city", city);
                            intent.putExtra("type", 3);
                            enterAnim = R.anim.fade_in;
                        }
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(enterAnim,
                                R.anim.activity_anim_default);
                    }
                    break;
                case Constants.Login.WEB_LOGIN:
                case Constants.Login.CONFIRM_LOGIN:
                case Constants.Login.CONTACT_LOGIN:
                case Constants.Login.APPLY_IOU_LOGIN:
                case Constants.Login.COUPON_GET_LOGIN:
                case Constants.Login.SEND_THREAD_LOGIN:
                case Constants.Login.SUPPORT_LOGIN:
                    user = Session.getInstance()
                            .getCurrentUser(context);
                    if (user != null && user.getId() > 0) {
                        path += (path.contains("?") ? "&" : "?") + "user_id=" + user.getId();
                        Map<String, String> header = WebUtil.getWebHeaders(context);
                        ;
                        if (!header.isEmpty()) {
                            webView.loadUrl(path, header);
                        } else {
                            webView.loadUrl(path);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                webView.clearHistory();
                            }
                        }, 1000);
                        if (requestCode == Constants.Login.CONTACT_LOGIN && merchant != null &&
                                merchant.getId() > 0) {
                            contactWithTrack(type, trackId, merchant, null);
                        } else if (requestCode == Constants.Login.SEND_THREAD_LOGIN && groupId >
                                0) {
                            Intent intent = new Intent(context, CreateThreadActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim
                                            .slide_in_from_bottom,
                                    R.anim.activity_anim_default);
                        } else if (requestCode == Constants.Login.SUPPORT_LOGIN) {
                            talkSupport();
                        }
                    }
                    break;
                case Constants.RequestCode.APPLY_IOU:
                case Constants.RequestCode.EDIT_USER_INFO:
                    webView.reload();
                    break;
                default:
                    break;
            }
        }
    }

    private ArrayList<String> stringSplit(String string) {
        ArrayList<String> strings = new ArrayList<>();
        Pattern facePattern = Pattern.compile("[a-zA-z]+://[^:]+|[^:]+");
        Matcher matcher = facePattern.matcher(string);
        while (matcher.find()) {
            strings.add(matcher.group(0));
        }
        return strings;
    }

    public ShareUtil getShareUtil() {
        return shareUtil;
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(context);
        }
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void talkSupport() {
        if (Util.loginBindChecked((Activity) context, Constants.Login.SUPPORT_LOGIN)) {
            showProgress();
            SupportUtil.getInstance(context)
                    .getSupport(context, kind, new SupportUtil.SimpleSupportCallback() {

                        @Override
                        public void onSupportCompleted(Support support) {
                            super.onSupportCompleted(support);
                            hideProgress();
                            WebHandler.this.support = support;
                            contactWithTrack(type, trackId, null, support);
                        }

                        @Override
                        public void onFailed() {
                            super.onFailed();
                            hideProgress();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Util.showToast(context, null, R.string.msg_get_supports_error);
                                }
                            });
                        }

                    });
        }
    }

    private void contactWithTrack(
            String kind, long trackId, MerchantUser merchant, Support support) {
        Serializable track = null;
        if (!JSONUtil.isEmpty(kind) && trackId > 0) {
            if (trackMap == null) {
                trackMap = new HashMap<>();
            }
            track = trackMap.get(kind + trackId);
            if (track == null) {
                String url = null;
                switch (kind) {
                    case "work":
                        url = Constants.getAbsUrl(String.format(Constants.HttpPath.NEW_WORK_INFO,
                                trackId));
                        break;
                    case "product":
                        url = Constants.getAbsUrl(String.format(Constants.HttpPath
                                        .SHOP_PRODUCT_INFO,
                                trackId));
                        break;
                    case "car":
                        url = Constants.getAbsUrl(String.format(Constants.HttpPath
                                        .GET_CAR_DETAIL_URL,
                                trackId));
                        break;
                }
                if (!JSONUtil.isEmpty(url)) {
                    if (merchant != null) {
                        new GetTrackTask(merchant, kind, trackId).execute(url);
                    } else if (support != null) {
                        new GetTrackTask(support, kind, trackId).execute(url);
                    }
                }
                return;
            }
        }
        Intent intent = null;
        if (merchant != null) {
            intent = new Intent(context, WSCustomerChatActivity.class);
            intent.putExtra("user", merchant);
            intent.putExtra("ws_track", ModuleUtils.getWSTrack(track));
        } else if (support != null) {
            intent = new Intent(context, EMChatActivity.class);
            intent.putExtra("support", support);
            if (track != null) {
                intent.putExtra("extra_obj", track);
            }
        }
        if (intent != null) {
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    private class GetTrackTask extends AsyncTask<String, Object, JSONObject> {

        private MerchantUser merchant;
        private String kind;
        private long id;
        private Support sup;

        private GetTrackTask(MerchantUser merchant, String kind, long id) {
            this.merchant = merchant;
            this.kind = kind;
            this.id = id;
            showProgress();
        }

        private GetTrackTask(Support support, String kind, long id) {
            this.sup = support;
            this.kind = kind;
            this.id = id;
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(strings[0]);
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
            if (((Activity) context).isFinishing()) {
                return;
            }
            hideProgress();
            if (jsonObject != null) {
                Identifiable track = null;
                if ("work".equals(kind) && jsonObject.optJSONObject("data") != null) {
                    track = new Work(jsonObject.optJSONObject("data")
                            .optJSONObject("work"));
                } else if ("product".equals(kind)) {
                    track = new ShopProduct(jsonObject.optJSONObject("data"));
                } else if ("car".equals(kind)) {
                    track = new CarProduct(jsonObject.optJSONObject("data"));
                }
                if (track != null && track.getId() == id) {
                    if (trackMap == null) {
                        trackMap = new HashMap<>();
                    }
                    trackMap.put(kind + id, track);
                    if (merchant != null && this.merchant.equals(merchant) && kind.equals(type)
                            && id == trackId) {
                        Intent intent = new Intent(context, WSCustomerChatActivity.class);
                        intent.putExtra("user", merchant);
                        intent.putExtra("ws_track", ModuleUtils.getWSTrack(track));
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else if (support != null && sup.equals(support) && kind.equals(type) && id
                            == trackId) {
                        Intent intent = new Intent(context, EMChatActivity.class);
                        intent.putExtra("support", support);
                        intent.putExtra("extra_obj", track);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }

                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    @JavascriptInterface
    public void payInsurance(String price, String type) {
        final Activity activity = (Activity) context;
        if (!AuthUtil.loginBindCheck(activity) || TextUtils.isEmpty(price)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (payInsuranceSub == null || payInsuranceSub.isUnsubscribed()) {
            payInsuranceSub = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            Intent intent = new Intent(context,
                                    AfterPayInsuranceProductActivity.class);
                            JsonObject jsonObject = (JsonObject) rxEvent.getObject();
                            if (jsonObject != null && jsonObject.has("insurance")) {
                                intent.putExtra("policy_detail",
                                        GsonUtil.getGsonInstance()
                                                .fromJson(jsonObject.get("insurance"),
                                                        PolicyDetail.class));
                            }
                            activity.startActivity(intent);
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                            break;
                    }
                }
            };
        }
        ArrayList<String> payTypes = null;
        if (Session.getInstance()
                .getDataConfig(activity) != null) {
            payTypes = Session.getInstance()
                    .getDataConfig(activity)
                    .getPayTypes();
        }
        new PayConfig.Builder(activity).params(jsonObject)
                .path(Constants.HttpPath.PAY_INSURANCE)
                .price(Double.valueOf(price))
                .subscriber((Subscriber<PayRxEvent>) payInsuranceSub)
                .payAgents(payTypes, DataConfig.getWalletPayAgents())
                .build()
                .pay();
    }

    /**
     * 跳转到笔记本
     *
     * @param notebookId
     */
    @JavascriptInterface
    public void jumpToNotebook(String notebookId) {
        try {
            long id = Long.valueOf(notebookId);
            if (id > 0) {
                Intent intent = new Intent(context, NotebookActivity.class);
                intent.putExtra("note_book_id", id);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        } catch (NumberFormatException ignored) {

        }
    }


    /**
     * 发布或回复评论
     */
    @JavascriptInterface
    public void publishComment(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        long entityId = jsonObject.optLong("noteId");
        long replyId = jsonObject.optLong("replyId");
        String replyAuthorName = jsonObject.optString("replyAuthorName");
        String callback = jsonObject.optString("callback");
        Intent intent = new Intent(context, PostNoteCommentActivity.class);
        intent.putExtra("id", entityId);
        if (replyId != 0) {
            intent.putExtra("reply_id", replyId);
            intent.putExtra("replied_auth_name", replyAuthorName);
        }
        intent.putExtra("callback", callback);
        intent.putExtra("entity_type", HljNote.NOTE_TYPE);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    private Dialog moreSettingDlg;

    @JavascriptInterface
    public void deleteComment(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        final long commentId = jsonObject.optLong("commentId");
        final String callback = jsonObject.optString("callback");
        LinkedHashMap<String, View.OnClickListener> map = new LinkedHashMap<>();
        map.put("删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreSettingDlg.dismiss();
                RxBus.getDefault()
                        .post(new RxEvent(RxEvent.RxEventType.DELETE_NOTE_COMMENT,
                                new RxWebNoteComment(commentId, callback)));
            }
        });
        moreSettingDlg = com.hunliji.hljcommonlibrary.utils.DialogUtil.createBottomMenuDialog(
                context,
                map,
                null);
        moreSettingDlg.show();
    }

    /**
     * 跳转到笔记评论列表
     */
    @JavascriptInterface
    public void jumpToNoteCommentList(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        long id = jsonObject.optLong("noteId");
        int notebookType = jsonObject.getInt("noteBookType");
        if (id > 0) {
            Intent intent = new Intent(context, NoteCommentListActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("notebook_type", notebookType);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    /**
     * 跳转到笔记标签搜索结果页
     *
     * @param markName
     */
    @JavascriptInterface
    public void jumpToSearchNoteResult(String markName) {
        NewSearchUtil.performSearchResultJump(context,
                markName,
                NewSearchApi.SearchType.SEARCH_TYPE_NOTE.getType());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(payInsuranceSub);
    }
}
