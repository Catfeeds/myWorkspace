package me.suncloud.marrymemo.view.orders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.JsonPhoto;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.InstallmentPaymentActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.orders.hotelperoid.HotelPeriodBody;
import me.suncloud.marrymemo.model.orders.hotelperoid.HotelPeriodImageBody;
import me.suncloud.marrymemo.view.MyOrderListActivity;
import rx.Subscription;
import rx.internal.util.SubscriptionList;

/**
 * 婚宴酒店填写订单-上传图片activity
 * Created by jinxin on 2018/3/8 0008.
 */

public class HotelPeriodOrderPhotoActivity extends HljBaseActivity {

    public static final String ARG_NAME = "name";
    public static final String ARG_PHONE = "phone";
    public static final String ARG_MONEY = "money";
    public static final String ARG_MERCHANT_ID = "merchant_id";
    public static final String ARG_STAGE_NUM = "stage_num";
    public static final String ARG_WEDDING_DAY = "wedding_day";

    private static final int TYPE_PROPOSER_FRONT = 101;
    private static final int TYPE_PROPOSER_BACKWARD = 102;
    private static final int TYPE_MATE_FRONT = 103;
    private static final int TYPE_MATE_BACKWARD = 104;
    public static final int TYPE_CONTRACT = 105;
    public static final int TYPE_SELLER = 106;

    private static final int SELLER_PHOTO_LIMIT = 1;
    private static final int CONTRACT_PHOTO_LIMIT = 9;

    @BindView(R.id.layout_proposer_identity_front)
    LinearLayout layoutProposerIdentityFront;
    @BindView(R.id.img_proposer_identity_card_front)
    RoundedImageView imgProposerIdentityCardFront;
    @BindView(R.id.layout_proposer_identity_backward)
    LinearLayout layoutProposerIdentityBackward;
    @BindView(R.id.img_proposer_identity_card_backward)
    RoundedImageView imgProposerIdentityCardBackWard;
    @BindView(R.id.layout_mate_identity_front)
    LinearLayout layoutMateIdentityFront;
    @BindView(R.id.img_mate_identity_front)
    RoundedImageView imgMateIdentityFront;
    @BindView(R.id.layout_mate_identity_backward)
    LinearLayout layoutMateIdentityBackward;
    @BindView(R.id.img_mate_identity_backward)
    RoundedImageView imgMateIdentityBackward;
    @BindView(R.id.recycler_view_seller)
    RecyclerView recyclerViewSeller;
    @BindView(R.id.recycler_view_contract)
    RecyclerView recyclerViewContract;

    private HljHttpSubscriber submitSub;
    private String name;
    private String phone;
    private String payMoney;
    private String weddingDay;
    private long merchantId;
    private int selectedStageNum;
    private int imgSize;
    private SparseArray<ArrayList<Photo>> photoList;
    private ArrayList<Photo> contractPhotoList;
    private ArrayList<Photo> sellerPhotoList;
    private DraggableImgGridAdapter sellerAdapter;
    private RecyclerViewDragDropManager recyclerViewDragDropManager;
    private RecyclerView.Adapter contractDragAdapter;
    private int imgCardWidth;
    private int imgCardHeight;
    private int photoItemId;
    private HljRoundProgressDialog roundProgressDialog;
    private SubscriptionList uploadSubscriptionList;
    private Subscription payRxBusSub;
    private HotelPeriodBody hotelPeriodBody;
    private HotelPeriodImageBody hotelPeriodImageBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_period_order_photo);
        ButterKnife.bind(this);

        initValues();
        initWidget();
        registerPaymentRxBus();
    }

    private void initValues() {
        if (getIntent() != null) {
            name = getIntent().getStringExtra(ARG_NAME);
            phone = getIntent().getStringExtra(ARG_PHONE);
            payMoney = getIntent().getStringExtra(ARG_MONEY);
            merchantId = getIntent().getLongExtra(ARG_MERCHANT_ID, 0L);
            selectedStageNum = getIntent().getIntExtra(ARG_STAGE_NUM, -1);
            weddingDay = getIntent().getStringExtra(ARG_WEDDING_DAY);
        }
        imgCardWidth = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 44)) / 2;
        imgCardHeight = CommonUtil.dp2px(this, 108);
        imgSize = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 62)) / 4 ;
        photoList = new SparseArray<>();
        contractPhotoList = new ArrayList<>();
        sellerPhotoList = new ArrayList<>();
        SellerAdapterListener sellerAdapterListener = new SellerAdapterListener();
        sellerAdapter = new DraggableImgGridAdapter(this,
                sellerPhotoList,
                imgSize,
                SELLER_PHOTO_LIMIT);
        sellerAdapter.setOnItemAddListener(sellerAdapterListener);
        sellerAdapter.setOnItemClickListener(sellerAdapterListener);
        sellerAdapter.setOnItemDeleteListener(sellerAdapterListener);

        recyclerViewDragDropManager = new RecyclerViewDragDropManager();
        recyclerViewDragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable)
                ContextCompat.getDrawable(
                this,
                R.drawable.sp_dragged_shadow));
        recyclerViewDragDropManager.setInitiateOnLongPress(true);
        recyclerViewDragDropManager.setInitiateOnMove(false);
        recyclerViewDragDropManager.setLongPressTimeout(500);

        DraggableImgGridAdapter contractAdapter = new DraggableImgGridAdapter(this,
                contractPhotoList,
                imgSize,
                CONTRACT_PHOTO_LIMIT);
        ContractAdapterListener contractAdapterListener = new ContractAdapterListener();
        contractAdapter.setOnItemAddListener(contractAdapterListener);
        contractAdapter.setOnItemClickListener(contractAdapterListener);
        contractAdapter.setOnItemDeleteListener(contractAdapterListener);
        contractDragAdapter = recyclerViewDragDropManager.createWrappedAdapter(contractAdapter);
    }

    private void initWidget() {
        GridLayoutManager managerSellerManager = new GridLayoutManager(this, 4);
        recyclerViewSeller.setLayoutManager(managerSellerManager);
        recyclerViewSeller.setAdapter(sellerAdapter);

        GridLayoutManager contractSellerManager = new GridLayoutManager(this, 4);
        GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
        recyclerViewContract.setLayoutManager(contractSellerManager);
        recyclerViewContract.setAdapter(contractDragAdapter);
        recyclerViewContract.setItemAnimator(animator);
        recyclerViewDragDropManager.attachRecyclerView(recyclerViewContract);
    }

    private void registerPaymentRxBus() {
        payRxBusSub = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        switch (payRxEvent.getType()) {
                            case PAY_CANCEL:
                                Intent cancelIntent = new Intent(HotelPeriodOrderPhotoActivity.this,
                                        MyOrderListActivity.class);
                                cancelIntent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                .ARG_BACK_MAIN,
                                        true);
                                cancelIntent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                .ARG_SELECT_TAB,
                                        RouterPath.IntentPath.Customer.MyOrder.Tab.HOTEL_PERIOD_ORDER);
                                startActivity(cancelIntent);
                                finish();
                                overridePendingTransition(R.anim.activity_anim_default,
                                        R.anim.slide_out_to_bottom);
                                break;
                            case INSTALLMENT_PAY_SUCCESS:
                                //分期支付成功
                                Intent successIntent = new Intent(HotelPeriodOrderPhotoActivity.this,
                                        MyBillListActivity.class);
                                successIntent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                .ARG_SELECT_TAB,
                                        RouterPath.IntentPath.Customer.MyOrder.Tab
                                                .HOTEL_PERIOD_ORDER);
                                successIntent.putExtra(MyBillListActivity.ARG_IS_BACK_ORDER_LIST,
                                        true);
                                startActivity(successIntent);
                                finish();
                                overridePendingTransition(0, 0);
                                break;
                        }
                    }
                });
    }

    @OnClick({R.id.btn_submit, R.id.layout_mate_identity_backward, R.id
            .layout_mate_identity_front, R.id.layout_proposer_identity_backward, R.id
            .layout_proposer_identity_front})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_proposer_identity_front:
                //申请人身份证正面
                onTakePhoto(TYPE_PROPOSER_FRONT);
                break;
            case R.id.layout_proposer_identity_backward:
                //申请人身份证反面
                onTakePhoto(TYPE_PROPOSER_BACKWARD);
                break;
            case R.id.layout_mate_identity_front:
                //配偶身份证正面
                onTakePhoto(TYPE_MATE_FRONT);
                break;
            case R.id.layout_mate_identity_backward:
                //配偶身份证反面
                onTakePhoto(TYPE_MATE_BACKWARD);
                break;
            case R.id.btn_submit:
                onSubmit();
                break;
            default:
                break;
        }
    }

    private void onTakePhoto(int type) {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putExtra(ImageChooserActivity.INTENT_LIMIT, 1);
        startActivityForResult(intent, type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Photo> selectPhotos = data.getParcelableArrayListExtra(ImageChooserActivity
                    .ARG_SELECTED_PHOTOS);
            setSelectPhoto(selectPhotos, requestCode);
        }
    }

    private void setSelectPhoto(ArrayList<Photo> selectPhotos, int type) {
        if (CommonUtil.isCollectionEmpty(selectPhotos)) {
            return;
        }
        switch (type) {
            case TYPE_PROPOSER_FRONT:
                photoList.put(type, selectPhotos);
                setImageView(imgProposerIdentityCardFront, selectPhotos.get(0));
                break;
            case TYPE_PROPOSER_BACKWARD:
                photoList.put(type, selectPhotos);
                setImageView(imgProposerIdentityCardBackWard, selectPhotos.get(0));
                break;
            case TYPE_MATE_FRONT:
                photoList.put(type, selectPhotos);
                setImageView(imgMateIdentityFront, selectPhotos.get(0));
                break;
            case TYPE_MATE_BACKWARD:
                photoList.put(type, selectPhotos);
                setImageView(imgMateIdentityBackward, selectPhotos.get(0));
                break;
            case TYPE_SELLER:
                sellerPhotoList.addAll(selectPhotos);
                photoList.put(type, sellerPhotoList);
                sellerAdapter.notifyDataSetChanged();
                break;
            case TYPE_CONTRACT:
                contractPhotoList.addAll(selectPhotos);
                photoList.put(type, contractPhotoList);
                refreshContractPhotoId();
                contractDragAdapter.notifyDataSetChanged();
                refreshContractRecyclerView();
                break;
            default:
                break;
        }
    }

    private void refreshContractPhotoId() {
        for (Photo p : contractPhotoList) {
            if (p.getId() <= 0) {
                p.setId(++photoItemId);
            }
        }
    }

    public void refreshContractRecyclerView() {
        int size = contractDragAdapter.getItemCount();
        int rows = (int) Math.ceil(size * 1F / 4);
        recyclerViewContract.getLayoutParams().height = imgSize * rows + CommonUtil.dp2px(this,
                rows * 10);
        recyclerViewContract.postInvalidate();
    }

    private void setImageView(RoundedImageView imageView, Photo photo) {
        if (imageView == null || photo == null) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(ImagePath.buildPath(photo.getImagePath())
                        .width(imgCardWidth)
                        .height(imgCardHeight)
                        .cropPath())
                .into(imageView);
    }

    private void onSubmit() {
        if (photoList.get(TYPE_PROPOSER_FRONT) == null) {
            Toast.makeText(this, "请上传申请人身份证正面", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (photoList.get(TYPE_PROPOSER_BACKWARD) == null) {
            Toast.makeText(this, "请上传申请人身份证反面", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (photoList.get(TYPE_MATE_FRONT) == null) {
            Toast.makeText(this, "请上传配偶身份证正面", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (photoList.get(TYPE_MATE_BACKWARD) == null) {
            Toast.makeText(this, "请上传配偶身份证反面", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (CommonUtil.isCollectionEmpty(sellerPhotoList)) {
            Toast.makeText(this, "请上传与酒店销售（顾问）合照", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (CommonUtil.isCollectionEmpty(contractPhotoList)) {
            Toast.makeText(this, "请上传订单合同", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        uploadPhoto();
    }

    private void uploadPhoto() {
        hotelPeriodBody = null;
        hotelPeriodImageBody = null;
        if (uploadSubscriptionList == null) {
            uploadSubscriptionList = new SubscriptionList();
        }
        if (roundProgressDialog == null) {
            roundProgressDialog = DialogUtil.getRoundProgress(this);
            roundProgressDialog.setCancelable(true);
        }
        roundProgressDialog.show();
        ArrayList<Photo> photos = new ArrayList<>();
        for (int i = 0; i < photoList.size(); i++) {
            List<Photo> pl = photoList.valueAt(i);
            if (pl != null) {
                photos.addAll(pl);
            }
        }

        new PhotoListUploadUtil(this,
                photos,
                roundProgressDialog,
                uploadSubscriptionList,
                new OnFinishedListener() {
                    @Override
                    public void onFinished(Object... objects) {
                        makeOrder();
                    }
                }).startUpload();
    }

    private void makeOrder() {
        if (roundProgressDialog != null) {
            roundProgressDialog.dismiss();
        }
        if (hotelPeriodBody == null) {
            hotelPeriodBody = new HotelPeriodBody();
        }
        if (hotelPeriodImageBody == null) {
            hotelPeriodImageBody = new HotelPeriodImageBody();
        }

        for (int i = 0; i < photoList.size(); i++) {
            int type = photoList.keyAt(i);
            List<Photo> pl = photoList.get(type);
            switch (type) {
                case TYPE_PROPOSER_FRONT:
                    hotelPeriodImageBody.setUserCardFront(new JsonPhoto(pl.get(0)));
                    break;
                case TYPE_PROPOSER_BACKWARD:
                    hotelPeriodImageBody.setUserCardBack(new JsonPhoto(pl.get(0)));
                    break;
                case TYPE_MATE_FRONT:
                    hotelPeriodImageBody.setPartnerCardFront(new JsonPhoto(pl.get(0)));
                    break;
                case TYPE_MATE_BACKWARD:
                    hotelPeriodImageBody.setPartnerCardBack(new JsonPhoto(pl.get(0)));
                    break;
                case TYPE_CONTRACT:
                    List<JsonPhoto> jsonPhotoList = new ArrayList<>();
                    for (Photo p : pl) {
                        JsonPhoto photo = new JsonPhoto(p);
                        jsonPhotoList.add(photo);
                    }
                    hotelPeriodImageBody.setContract(jsonPhotoList);
                    break;
                case TYPE_SELLER:
                    hotelPeriodImageBody.setPhotoWithSaler(new JsonPhoto(pl.get(0)));
                    break;
                default:
                    break;
            }
        }
        hotelPeriodBody.setImages(hotelPeriodImageBody);
        hotelPeriodBody.setActualMoney(payMoney);
        hotelPeriodBody.setMerchantId(merchantId);
        hotelPeriodBody.setUserName(name);
        hotelPeriodBody.setUserPhone(phone);
        hotelPeriodBody.setWeddingDay(weddingDay);
        CommonUtil.unSubscribeSubs(submitSub);
        submitSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        setMakeOrderResult(jsonElement, payMoney);
                    }
                })
                .build();
        OrderApi.submitHotelPeriodOrderObb(hotelPeriodBody)
                .subscribe(submitSub);
    }

    private void setMakeOrderResult(JsonElement jsonElement, String payMoney) {
        if (jsonElement != null) {
            String orderId = jsonElement.getAsJsonObject()
                    .get("id")
                    .getAsString();
            if (TextUtils.isEmpty(orderId)) {
                Toast.makeText(this, "生成订单失败", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            Toast.makeText(this, "成功生成订单", Toast.LENGTH_SHORT)
                    .show();
            JSONObject params = new JSONObject();
            try {
                params.put("agent", PayAgent.XIAO_XI_PAY);
                params.put("period", selectedStageNum);
                params.put("order_id", orderId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, InstallmentPaymentActivity.class);
            intent.putExtra(InstallmentPaymentActivity.ARG_ORDER_PAY_TYPE,
                    InstallmentPaymentActivity.ORDER_PAY_TYPE_HOTEL);
            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PATH,
                    Constants.getAbsUrl(Constants.HttpPath.HOTEL_PERIOD_ORDER_PAYMENT));
            intent.putExtra(InstallmentPaymentActivity.ARG_PRICE, Double.parseDouble(payMoney));
            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PARAMS, params.toString());
            intent.putExtra(InstallmentPaymentActivity.ARG_INSTALLMENT_STAGE_NUM, selectedStageNum);
            intent.putExtra(InstallmentPaymentActivity.ARG_WEDDING_DAY, weddingDay);
            startActivity(intent);
        } else {
            Toast.makeText(this, "生成订单失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(submitSub);
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager.release();
            recyclerViewDragDropManager = null;
        }
        if (contractDragAdapter != null) {
            WrapperAdapterUtils.releaseAll(contractDragAdapter);
            contractDragAdapter = null;
        }
        CommonUtil.unSubscribeSubs(submitSub, uploadSubscriptionList, payRxBusSub);
    }

    private class ContractAdapterListener implements DraggableImgGridAdapter.OnItemAddListener,
            DraggableImgGridAdapter.OnItemDeleteListener, OnItemClickListener<Photo> {

        @Override
        public void onItemClick(int position, Photo object) {
            if (contractPhotoList != null && !contractPhotoList.isEmpty()) {
                Intent intent = new Intent(HotelPeriodOrderPhotoActivity.this,
                        PicsPageViewActivity.class);
                intent.putExtra("photos", contractPhotoList);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }

        @Override
        public void onItemDelete(int position) {
            if (position < contractPhotoList.size()) {
                contractPhotoList.remove(position);
                refreshContractPhotoId();
                contractDragAdapter.notifyDataSetChanged();
                refreshContractRecyclerView();
            }
        }

        @Override
        public void onItemAdd(Object... objects) {
            if (contractPhotoList.size() == CONTRACT_PHOTO_LIMIT) {
                ToastUtil.showToast(HotelPeriodOrderPhotoActivity.this,
                        null,
                        R.string.hint_choose_photo_limit_out);
                return;
            }
            Intent intent = new Intent(HotelPeriodOrderPhotoActivity.this,
                    ImageChooserActivity.class);
            intent.putExtra("limit", CONTRACT_PHOTO_LIMIT - contractPhotoList.size());
            startActivityForResult(intent, TYPE_CONTRACT);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private class SellerAdapterListener implements DraggableImgGridAdapter.OnItemAddListener,
            DraggableImgGridAdapter.OnItemDeleteListener, OnItemClickListener<Photo> {

        @Override
        public void onItemClick(int position, Photo object) {
            if (sellerPhotoList != null && !sellerPhotoList.isEmpty()) {
                Intent intent = new Intent(HotelPeriodOrderPhotoActivity.this,
                        PicsPageViewActivity.class);
                intent.putExtra("photos", sellerPhotoList);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }

        @Override
        public void onItemDelete(int position) {
            if (position < sellerPhotoList.size()) {
                sellerPhotoList.remove(position);
                sellerAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemAdd(Object... objects) {
            if (sellerPhotoList.size() == SELLER_PHOTO_LIMIT) {
                ToastUtil.showToast(HotelPeriodOrderPhotoActivity.this,
                        null,
                        R.string.hint_choose_photo_limit_out);
                return;
            }
            Intent intent = new Intent(HotelPeriodOrderPhotoActivity.this,
                    ImageChooserActivity.class);
            intent.putExtra("limit", SELLER_PHOTO_LIMIT - sellerPhotoList.size());
            startActivityForResult(intent, TYPE_SELLER);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

}
