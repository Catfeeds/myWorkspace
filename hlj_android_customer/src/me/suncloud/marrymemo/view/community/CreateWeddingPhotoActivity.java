package me.suncloud.marrymemo.view.community;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.CreateWeddingPhotoAdapter;
import me.suncloud.marrymemo.broadcast.NetworkReceiver;
import me.suncloud.marrymemo.model.realm.RealmJsonPic;
import me.suncloud.marrymemo.model.realm.WeddingPhotoDraft;
import me.suncloud.marrymemo.model.realm.WeddingPhotoItemDraft;
import me.suncloud.marrymemo.util.WeddingPhotoListUploader;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/5/3.发布晒婚纱照
 */

public class CreateWeddingPhotoActivity extends HljBaseNoBarActivity implements View
        .OnClickListener, CreateWeddingPhotoAdapter.OnGroupItemClickListener {

    public final static int GROUP_LIMIT_PHOTO_COUNT = 20;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.divider_view)
    View dividerView;
    @BindView(R.id.no_wifi_tip)
    RelativeLayout noWifiTip;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;

    private CreateWeddingPhotoAdapter adapter;
    private FooterViewHolder footerViewHolder;
    private HeaderViewHolder headerViewHolder;

    private WeddingPhotoDraft draft;//草稿
    private WeddingPhotoDraft realmResult;//数据库中的草稿
    private Merchant merchant;
    private RealmList<WeddingPhotoItemDraft> weddingPhotoItems;
    private ArrayList<ArrayList<Subscription>> subscriptionList;
    private ArrayList<WeddingPhotoListUploader> uploaderList;
    private ArrayList<ArrayList<String>> groupSelectedPaths;
    private NetworkReceiver mNetworkStateReceiver;
    private RealmList<RealmJsonPic> coverPhotos;//封面图片列表，实际只有一张
    private ArrayList<Subscription> coverUploadSubscriptions;
    private WeddingPhotoListUploader coverUpLoad;
    private int groupIndex;//当前编辑图片的小组
    private int coverWidth;
    private int coverHeight;
    private Realm realm;
    private boolean isShowNoWifi;//是否显示无wifi提示窗
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wedding_photo);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        setSwipeBackEnable(false);
        initValue();
        initView();
        registerNetworkBroadcast();
        registerRxBusEvent();
    }

    private void initValue() {
        subscriptionList = new ArrayList<>();
        uploaderList = new ArrayList<>();
        groupSelectedPaths = new ArrayList<>();
        coverPhotos = new RealmList<>();
        coverWidth = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 36);
        coverHeight = coverWidth * 9 / 16;
        isShowNoWifi = true;
        initDraft();
    }

    private void initView() {
        //非wifi提示
        if (!CommonUtil.isWifi(this) && isShowNoWifi) {
            noWifiTip.setVisibility(View.VISIBLE);
        } else {
            noWifiTip.setVisibility(View.GONE);
        }
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        View headerView = getLayoutInflater().inflate(R.layout.wedding_photo_group_header, null);
        View footerView = getLayoutInflater().inflate(R.layout.wedding_photo_group_footer, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        footerViewHolder = new FooterViewHolder(footerView);
        CharSequence hint = headerViewHolder.etWeddingPhotoTitle.getHint();
        SpannableString ss = new SpannableString(hint);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        headerViewHolder.etWeddingPhotoTitle.setHint(new SpannedString(ss));
        headerViewHolder.etWeddingPhotoTitle.addTextChangedListener(textWatcher);
        headerViewHolder.actionModifyCover.setOnClickListener(this);
        headerViewHolder.actionMerchantLayout.setOnClickListener(this);
        headerViewHolder.etWeddingPhotoTitle.setText(draft.getTitle());
        headerViewHolder.etWeddingPhotoMessage.setText(draft.getPreface());
        headerViewHolder.imgWeddingPhotoCover.getLayoutParams().height = coverHeight;
        //添加完第10组时，隐藏底部添加按钮
        footerViewHolder.addWeddingPhotoGroup.setOnClickListener(this);
        if (weddingPhotoItems.size() >= 10) {
            footerViewHolder.addWeddingPhotoGroup.setVisibility(View.GONE);
        } else {
            footerViewHolder.addWeddingPhotoGroup.setVisibility(View.VISIBLE);
        }
        rootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                boolean isKeyboard = (double) (bottom - top) / height < 0.8;
                if (isKeyboard) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollBy(0,
                                    CommonUtil.dp2px(CreateWeddingPhotoActivity.this, 16));
                        }
                    });
                }
            }
        });
        //第一步选图传入的数据，ps:setCoverPhoto()必须要initDraft()之后调用
        ArrayList<Photo> selectPhotos = getIntent().getParcelableArrayListExtra("selectedPhotos");
        if (selectPhotos != null) {
            groupIndex = 0;
            setCoverPhoto(selectPhotos);
            setGroupPhotos(selectPhotos);
        }
        refreshCoverView();
        refreshMerchantView();

        adapter = new CreateWeddingPhotoAdapter(this);
        adapter.setData(weddingPhotoItems);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        adapter.setOnGroupItemClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            TextPaint paint = headerViewHolder.etWeddingPhotoTitle.getPaint();
            if (TextUtils.isEmpty(s.toString())) {
                paint.setFakeBoldText(false);
            } else {
                paint.setFakeBoldText(true);
            }
        }
    };

    /**
     * 刷新封面信息
     */
    private void refreshCoverView() {
        if (draft.getCover() != null) {
            Glide.with(this)
                    .load(ImagePath.buildPath(draft.getCover()
                            .getPath())
                            .width(coverWidth)
                            .path())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(headerViewHolder.imgWeddingPhotoCover);
        }
    }

    /**
     * 刷新商家信息
     */
    private void refreshMerchantView() {
        if (merchant != null && merchant.getId() > 0) {
            headerViewHolder.merchantInfoLayout.setVisibility(View.VISIBLE);
            headerViewHolder.tvAddMerchant.setVisibility(View.GONE);
            headerViewHolder.tvMerchantName.setText(merchant.getName());
            Glide.with(this)
                    .load(ImagePath.buildPath(merchant.getLogoPath())
                            .width(CommonUtil.dp2px(this, 30))
                            .height(CommonUtil.dp2px(this, 30))
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.drawable.icon_merchant_empty))
                    .into(headerViewHolder.imgMerchantLogo);
        } else if (!TextUtils.isEmpty(draft.getUnrecordedMerchantName())) {
            headerViewHolder.merchantInfoLayout.setVisibility(View.VISIBLE);
            headerViewHolder.tvAddMerchant.setVisibility(View.GONE);
            headerViewHolder.tvMerchantName.setText(draft.getUnrecordedMerchantName());
            headerViewHolder.imgMerchantLogo.setImageResource(R.drawable.icon_merchant_empty);
        } else {
            headerViewHolder.merchantInfoLayout.setVisibility(View.GONE);
            headerViewHolder.tvAddMerchant.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        refreshDraft();
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.label_wedding_photo_draft_title),
                getString(R.string.label_wedding_photo_draft_msg),
                getString(R.string.label_save),
                getString(R.string.label_quit3),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 保存后退出
                        saveDraft();
                        finish();
                        overridePendingTransition(R.anim.activity_anim_default,
                                R.anim.slide_out_down);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 直接退出
                        deleteDraft();
                        finish();
                        overridePendingTransition(R.anim.activity_anim_default,
                                R.anim.slide_out_down);
                    }
                })
                .show();
    }

    /**
     * 更新草稿数据
     */
    private void refreshDraft() {
        String message = headerViewHolder.etWeddingPhotoMessage.getText()
                .toString();
        String title = headerViewHolder.etWeddingPhotoTitle.getText()
                .toString();
        draft.setPreface(message);
        draft.setTitle(title);
        draft.setWeddingPhotoItems(weddingPhotoItems);
        if (!CommonUtil.isCollectionEmpty(coverPhotos)) {
            draft.setCover(coverPhotos.get(0));
        }
    }

    /**
     * 跳转到预览页,需要校验输入内容
     */
    private boolean isDraftValid() {
        refreshDraft();
        if (weddingPhotoItems == null || weddingPhotoItems.size() <= 0) {
            ToastUtil.showToast(this, "你需要请上传一组婚纱～", 0);
            return false;
        } else if (draft.getCover() == null || TextUtils.isEmpty(draft.getCover()
                .getPath())) {
            ToastUtil.showToast(this, "你需要上传一张封面图～", 0);
            return false;
        } else if (TextUtils.isEmpty(draft.getTitle()) || draft.getTitle()
                .length() < 4) {
            ToastUtil.showToast(this, "你需要取一个 4-32 个字标题～", 0);
            return false;
        }
        return true;
    }

    /**
     * 添加下一组婚纱照
     *
     * @param groupSize 当前已添加的婚纱照小组数,用于计算下一小组的编号
     */
    private void onAddWeddingPhotoItem(int groupSize) {
        groupIndex = groupSize;
        //添加下一组婚纱照时，开启上一组婚纱照的上传任务
        for (int i = 0; i < groupIndex; i++) {
            onUploadPhoto(i);
        }
        onUploadCover();
        Intent intent = new Intent(this, WeddingImageChooserActivity.class);
        intent.putExtra("limit", GROUP_LIMIT_PHOTO_COUNT);
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY_WEDDING_PHOTO);
    }

    /**
     * 标记选择的小组
     *
     * @param position 婚纱照小组的编号
     */
    private void modifyWeddingPhotoItem(int position) {
        groupIndex = position;
        //编辑时暂停上一次上传
        ArrayList<Subscription> uploadSubscriptions = (subscriptionList.size() <= groupIndex) ?
                null : subscriptionList.get(
                groupIndex);
        if (uploadSubscriptions != null) {
            for (Subscription subscriber : uploadSubscriptions) {
                if (subscriber != null && !subscriber.isUnsubscribed()) {
                    subscriber.unsubscribe();
                }
            }
        }
        Intent intent = new Intent(this, WeddingImageChooserActivity.class);
        if (groupSelectedPaths.size() > groupIndex && !CommonUtil.isCollectionEmpty(
                groupSelectedPaths.get(groupIndex))) {
            intent.putExtra("selectedPaths", groupSelectedPaths.get(groupIndex));
        }
        intent.putExtra("limit", GROUP_LIMIT_PHOTO_COUNT);
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY_WEDDING_PHOTO);
    }

    /**
     * 初始化晒婚纱照（草稿）数据
     */
    private void initDraft() {
        long userId = UserSession.getInstance()
                .getUser(this)
                .getId();
        realm = Realm.getDefaultInstance();
        if (userId != 0) {
            realmResult = realm.where(WeddingPhotoDraft.class)
                    .equalTo("userId", userId)
                    .findFirst();
            if (realmResult != null) {
                draft = realm.copyFromRealm(realmResult);
            }
        }
        weddingPhotoItems = new RealmList<>();
        if (draft != null) {
            if (draft.getWeddingPhotoItems() != null) {
                weddingPhotoItems.addAll(draft.getWeddingPhotoItems());
            }
            if (!TextUtils.isEmpty(draft.getMerchantJson())) {
                merchant = GsonUtil.getGsonInstance()
                        .fromJson(draft.getMerchantJson(), Merchant.class);
            }
        } else {
            draft = new WeddingPhotoDraft();
            draft.setUserId(userId);
            //无草稿，跳转到选图页面
            Intent intent = new Intent(this, WeddingImageChooserActivity.class);
            intent.putExtra("isFirstChoose", true);
            intent.putExtra("limit", GROUP_LIMIT_PHOTO_COUNT);
            startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY_WEDDING_PHOTO);
        }
        for (int i = 0; i < weddingPhotoItems.size(); i++) {
            WeddingPhotoItemDraft itemDraft = weddingPhotoItems.get(i);
            ArrayList<String> localPaths = new ArrayList<>();
            RealmList<RealmJsonPic> pics = itemDraft.getPics();
            for (int j = 0; j < pics.size(); j++) {
                RealmJsonPic realmJsonPic = pics.get(j);
                // 检测草稿中存储的图片路径中的图片是否还存在
                String path = realmJsonPic.getPath();
                File file = new File(path);
                if (!path.startsWith("http://") && !path.startsWith("https://") && !file.exists()) {
                    pics.remove(j);
                } else {
                    localPaths.add(realmJsonPic.getLocalPath());
                }
            }
            groupSelectedPaths.add(localPaths);
            if (CommonUtil.isCollectionEmpty(itemDraft.getPics())) {
                weddingPhotoItems.remove(i);
            }
        }
    }

    /**
     * 保存草稿
     */
    private void saveDraft() {
        if (weddingPhotoItems != null) {
            realm.beginTransaction();
            draft.setWeddingPhotoItems(weddingPhotoItems);
            realm.copyToRealmOrUpdate(draft);
            realm.commitTransaction();
        }
    }

    /**
     * 删除草稿
     */
    private void deleteDraft() {
        if (realmResult != null) {
            realm.beginTransaction();
            realmResult.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    /**
     * 设置每个分组的图片组
     *
     * @param selectedPhotos 图片选择控件返回的图片数组
     */
    private void setGroupPhotos(ArrayList<Photo> selectedPhotos) {
        if (!CommonUtil.isCollectionEmpty(selectedPhotos)) {
            RealmList<RealmJsonPic> groupPhotos = new RealmList<>();
            ArrayList<String> groupPaths = new ArrayList<>();
            for (Photo photo : selectedPhotos) {
                RealmJsonPic realmJsonPic = new RealmJsonPic();
                realmJsonPic.setLocalPath(photo.getImagePath());
                //当为修改已编辑过的小组图片时，需要循环遍历，将已上传过的图片地址拷贝给新的图片数组
                if (groupIndex >= 0 && weddingPhotoItems.size() > groupIndex) {
                    RealmList<RealmJsonPic> groupPics = weddingPhotoItems.get(groupIndex)
                            .getPics();
                    for (RealmJsonPic pic : groupPics) {
                        //返回图片的本地地址与小组图片的本地地址进行比较，地址相同则将小组中地址（含http）拷贝出来
                        if (photo.getImagePath()
                                .equals(pic.getLocalPath())) {
                            realmJsonPic.setPath(pic.getPath());
                        }
                    }
                }
                realmJsonPic.setWidth(photo.getWidth());
                realmJsonPic.setHeight(photo.getHeight());
                groupPhotos.add(realmJsonPic);
                //组图片的地址列表，用于传递给选图控件
                groupPaths.add(photo.getImagePath());
            }
            if (groupSelectedPaths.size() > groupIndex) {
                groupSelectedPaths.get(groupIndex)
                        .clear();
                groupSelectedPaths.get(groupIndex)
                        .addAll(groupPaths);
            } else {
                groupSelectedPaths.add(groupPaths);
            }
            if (weddingPhotoItems.size() > groupIndex) {
                weddingPhotoItems.get(groupIndex)
                        .setPics(groupPhotos);
            } else {
                WeddingPhotoItemDraft itemDraft = new WeddingPhotoItemDraft();
                itemDraft.setPics(groupPhotos);
                weddingPhotoItems.add(itemDraft);
            }
            //添加完第10组时，隐藏底部添加按钮
            if (weddingPhotoItems.size() >= 10) {
                footerViewHolder.addWeddingPhotoGroup.setVisibility(View.GONE);
            } else {
                footerViewHolder.addWeddingPhotoGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置封面图片数据（单张）
     *
     * @param selectedPhotos 图片选择控件返回的图片数组
     */
    private void setCoverPhoto(ArrayList<Photo> selectedPhotos) {
        //groupIndex为-1表示封面图,不添加到婚纱照小组列表中
        if (!CommonUtil.isCollectionEmpty(selectedPhotos)) {
            Photo photo = selectedPhotos.get(0);
            RealmJsonPic realmJsonPic = new RealmJsonPic();
            realmJsonPic.setLocalPath(photo.getImagePath());
            if (coverPhotos.size() > 0 && coverPhotos.get(0) != null && coverPhotos.get(0)
                    .getLocalPath()
                    .equals(photo.getImagePath())) {
                realmJsonPic.setPath(coverPhotos.get(0)
                        .getPath());
            }
            realmJsonPic.setWidth(photo.getWidth());
            realmJsonPic.setHeight(photo.getHeight());
            draft.setCover(realmJsonPic);
            coverPhotos.clear();
            coverPhotos.add(realmJsonPic);
            refreshCoverView();
        }
    }

    private void deleteGroup() {
        if (weddingPhotoItems.size() > groupIndex && weddingPhotoItems.get(groupIndex) != null) {
            weddingPhotoItems.remove(groupIndex);
        }
        if (groupSelectedPaths.size() > groupIndex && groupSelectedPaths.get(groupIndex) != null) {
            groupSelectedPaths.remove(groupIndex);
        }
        if (subscriptionList.size() > groupIndex && subscriptionList.get(groupIndex) != null) {
            subscriptionList.remove(groupIndex);
        }
        //删除完显示底部添加按钮
        if (weddingPhotoItems.size() < 10) {
            footerViewHolder.addWeddingPhotoGroup.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 注册网络状态广播
     */
    private void registerNetworkBroadcast() {
        try {
            mNetworkStateReceiver = new NetworkReceiver();
            mNetworkStateReceiver.setChangeListener(new NetworkReceiver.NetworkInfoChangeListener
                    () {
                @Override
                public void networkInfoChange(NetworkInfo net) {
                    if (isFinishing()) {
                        return;
                    }
                    //非wifi提示
                    if (!(net != null && net.getType() == ConnectivityManager.TYPE_WIFI) &&
                            isShowNoWifi) {
                        noWifiTip.setVisibility(View.VISIBLE);
                    } else {
                        noWifiTip.setVisibility(View.GONE);
                    }
                }
            });
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetworkStateReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销广播
     */
    private void unregisterNetworkBroadcast() {
        if (mNetworkStateReceiver != null) {
            try {
                unregisterReceiver(mNetworkStateReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mNetworkStateReceiver = null;
        }
    }

    /**
     * 触发后台上传任务,每个小组单独维护一个上传任务
     * 当删除一个分组后，分组编号重新分配（组编号与position一致）
     *
     * @param groupIndex 组编号
     */
    private void onUploadPhoto(int groupIndex) {
        ArrayList<Subscription> uploadSubscriptions = null;
        if (subscriptionList.size() > groupIndex) {
            uploadSubscriptions = subscriptionList.get(groupIndex);
        }
        if (uploadSubscriptions == null) {
            uploadSubscriptions = new ArrayList<>();
            subscriptionList.add(uploadSubscriptions);
        } else if (isUploading(uploadSubscriptions)) {
            return;
        }
        RealmList<RealmJsonPic> photos = null;
        if (weddingPhotoItems.size() > groupIndex) {
            photos = weddingPhotoItems.get(groupIndex)
                    .getPics();
        }
        if (!CommonUtil.isCollectionEmpty(photos)) {
            WeddingPhotoListUploader weddingPhotoListUploader = null;
            if (uploaderList.size() > groupIndex) {
                weddingPhotoListUploader = uploaderList.get(groupIndex);
            }
            if (weddingPhotoListUploader == null) {
                weddingPhotoListUploader = new WeddingPhotoListUploader(this,
                        subscriptionList.get(groupIndex));
                uploaderList.add(weddingPhotoListUploader);
            }
            weddingPhotoListUploader.setPhotos(photos);
            weddingPhotoListUploader.startUploadPhoto();
        }
    }

    /**
     * 封面单独上传
     */
    private void onUploadCover() {
        if (coverUploadSubscriptions == null) {
            coverUploadSubscriptions = new ArrayList<>();
        }
        if (!CommonUtil.isCollectionEmpty(coverPhotos)) {
            if (coverUpLoad == null) {
                coverUpLoad = new WeddingPhotoListUploader(this, coverUploadSubscriptions);
            }
            coverUpLoad.setPhotos(coverPhotos);
            coverUpLoad.startUploadPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY_WEDDING_PHOTO:
                    if (data != null) {
                        boolean isDelete = data.getBooleanExtra("is_deleted", false);
                        boolean isFirstChoose = data.getBooleanExtra("isFirstChoose", false);
                        boolean isBackCreate = data.getBooleanExtra("isBackCreate", false);
                        if (isBackCreate) {
                            finish();
                            overridePendingTransition(0, R.anim.slide_out_right);
                        } else if (isDelete) {
                            deleteGroup();
                        } else {
                            ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                                    "selectedPhotos");
                            if (isFirstChoose) {
                                groupIndex = 0;
                                setCoverPhoto(selectedPhotos);
                                refreshCoverView();
                            }
                            setGroupPhotos(selectedPhotos);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data != null) {
                        ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                                "selectedPhotos");
                        setCoverPhoto(selectedPhotos);
                        refreshCoverView();
                    }
                    break;
                case Constants.RequestCode.SELECT_MERCHANT_LIST:
                    if (data != null) {
                        merchant = data.getParcelableExtra("merchant");
                        if (merchant != null) {
                            draft.setMerchantId(merchant.getId());
                            draft.setUnrecordedMerchantName(null);
                            draft.setMerchantJson(GsonUtil.getGsonInstance()
                                    .toJson(merchant));
                        } else {
                            String unRecordedMerchant = data.getStringExtra("unRecordedMerchant");
                            draft.setMerchantId(null);
                            draft.setUnrecordedMerchantName(unRecordedMerchant);
                            draft.setMerchantJson(null);
                        }
                        refreshMerchantView();
                    }
                    break;
                case Constants.RequestCode.WEDDING_PHOTO_PREVIEW:
                    if (data != null) {
                        WeddingPhotoDraft draft = data.getParcelableExtra("draft");
                        if (draft != null) {
                            if (draft.getWeddingPhotoItems() != null) {
                                weddingPhotoItems.clear();
                                weddingPhotoItems.addAll(draft.getWeddingPhotoItems());
                            }
                            if (draft.getCover() != null) {
                                coverPhotos.clear();
                                coverPhotos.add(draft.getCover());
                            }
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        unSubscriberUpLoad();
        unregisterNetworkBroadcast();
        if (realm != null) {
            realm.close();
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case FINISH_CREATE_PHOTO:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }

    /**
     * 取消上传任务
     */
    private void unSubscriberUpLoad() {
        if (!CommonUtil.isCollectionEmpty(subscriptionList)) {
            for (ArrayList<Subscription> subscriptions : subscriptionList) {
                for (Subscription subscription : subscriptions) {
                    CommonUtil.unSubscribeSubs(subscription);
                }
            }
        }
        if (!CommonUtil.isCollectionEmpty(coverUploadSubscriptions)) {
            for (Subscription subscription : coverUploadSubscriptions) {
                CommonUtil.unSubscribeSubs(subscription);
            }
        }
    }

    /**
     * 是否正在上传图片
     *
     * @param uploadSubscriptions 上传图片的任务列表
     * @return
     */
    private boolean isUploading(ArrayList<Subscription> uploadSubscriptions) {
        for (Subscription subscription : uploadSubscriptions) {
            if (!subscription.isUnsubscribed()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.add_wedding_photo_group:
                onAddWeddingPhotoItem(weddingPhotoItems.size());
                break;
            case R.id.action_modify_cover:
                groupIndex = -1;
                intent.setClass(this, ImageChooserActivity.class);
                intent.putExtra("limit", 1);
                startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
                break;
            case R.id.action_merchant_layout:
                intent.setClass(this, SelectMerchantListActivity.class);
                startActivityForResult(intent, Constants.RequestCode.SELECT_MERCHANT_LIST);
                break;
        }
    }

    @Override
    public void onGroupClick(int groupIndex) {
        modifyWeddingPhotoItem(groupIndex);
    }

    @OnClick(R.id.action_preview)
    public void onActionPreview() {
        if (isDraftValid()) {
            unSubscriberUpLoad();
            Intent intent = new Intent(this, WeddingPhotoPreviewActivity.class);
            intent.putExtra("draft", draft);
            startActivityForResult(intent, Constants.RequestCode.WEDDING_PHOTO_PREVIEW);
        }
    }

    @OnClick(R.id.action_back)
    public void onActionBack() {
        onBackPressed();
    }

    @OnClick(R.id.action_close)
    public void onActionClose() {
        noWifiTip.setVisibility(View.GONE);
        isShowNoWifi = false;
    }

    static class FooterViewHolder {
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.add_wedding_photo_group)
        LinearLayout addWeddingPhotoGroup;
        @BindView(R.id.tv_max_group_tip)
        TextView tvMaxGroupTip;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class HeaderViewHolder {
        @BindView(R.id.img_wedding_photo_cover)
        ImageView imgWeddingPhotoCover;
        @BindView(R.id.tv_modify_photo)
        TextView tvModifyPhoto;
        @BindView(R.id.modify_photo_layout)
        LinearLayout modifyPhotoLayout;
        @BindView(R.id.action_modify_cover)
        RelativeLayout actionModifyCover;
        @BindView(R.id.et_wedding_photo_title)
        ClearableEditText etWeddingPhotoTitle;
        @BindView(R.id.img_merchant_logo)
        ImageView imgMerchantLogo;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.merchant_info_layout)
        LinearLayout merchantInfoLayout;
        @BindView(R.id.tv_add_merchant)
        TextView tvAddMerchant;
        @BindView(R.id.et_wedding_photo_message)
        ClearableEditText etWeddingPhotoMessage;
        @BindView(R.id.action_merchant_layout)
        LinearLayout actionMerchantLayout;


        HeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }

}
