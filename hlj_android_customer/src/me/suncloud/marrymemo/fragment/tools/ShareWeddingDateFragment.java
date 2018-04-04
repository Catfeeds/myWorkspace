package me.suncloud.marrymemo.fragment.tools;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardlibrary.utils.Lunar;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljsharelibrary.utils.ShareLocalImageUtil;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.functions.Action1;

/**
 * 婚期分享
 * Created by chen_bin on 2017/12/14 0014.
 */
@RuntimePermissions
public class ShareWeddingDateFragment extends DialogFragment {

    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_lunar)
    TextView tvLunar;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.img_partner_avatar)
    RoundedImageView imgPartnerAvatar;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.content_layout)
    FrameLayout contentLayout;

    private int avatarSize;
    private int shareViewId;

    private Unbinder unbinder;

    public static ShareWeddingDateFragment newInstance() {
        Bundle args = new Bundle();
        ShareWeddingDateFragment fragment = new ShareWeddingDateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BubbleDialogFragment);
        avatarSize = CommonUtil.dp2px(getContext(), 32);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_share_wedding_date,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
    }

    private void initViews() {
        Window win = getDialog().getWindow();
        if (win != null) {
            getDialog().setCanceledOnTouchOutside(true);
            win.setDimAmount(0.9f);
            win.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        Point point = CommonUtil.getDeviceSize(getContext());
        int width = (int) (point.x * 600.0f / 750.0f);
        contentLayout.getLayoutParams().width = width;
        contentLayout.getLayoutParams().height = (int) (width * 700.0f / 600.0f);

        float scale = width * 1.0f / CommonUtil.dp2px(getContext(), 300);
        cardView.setScaleX(scale);
        cardView.setScaleY(scale);
    }

    private void initData() {
        User user = Session.getInstance()
                .getCurrentUser(getContext());
        if (user.getWeddingDay() != null) {
            DateTime currentDate = new DateTime();
            DateTime weddingDate = new DateTime(user.getWeddingDay());
            int days = Days.daysBetween(currentDate.toLocalDate(), weddingDate.toLocalDate())
                    .getDays();
            tvDay.setText(String.valueOf(Math.max(0, days)));
            tvDate.setText(weddingDate.toString(getString(R.string.format_date_type7)));
            Lunar lunar = new Lunar(weddingDate);
            tvLunar.setText(lunar.getLunar() + "，" + getString(R.string.label_week,
                    lunar.getWeek()));
        }
        Glide.with(getContext())
                .load(ImagePath.buildPath(user.getAvatar())
                        .width(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        CustomerUser partnerUser = user.getPartnerUser();
        if (partnerUser != null) {
            Glide.with(getContext())
                    .load(ImagePath.buildPath(partnerUser.getAvatar())
                            .width(avatarSize)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary)
                            .error(R.mipmap.icon_avatar_primary))
                    .into(imgPartnerAvatar);
        }
    }

    @OnClick(R.id.btn_close)
    void onClose() {
        dismiss();
    }

    @OnClick({R.id.btn_share_wx, R.id.btn_share_pengyou, R.id.btn_share_qq, R.id.btn_share_weibo})
    void onShare(View v) {
        shareViewId = v.getId();
        ShareWeddingDateFragmentPermissionsDispatcher.onShareWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void onShare() {
        final Bitmap bitmap = getContentScreenShot();
        if (bitmap == null) {
            return;
        }
        File file = FileUtil.createImagePngFile(FileUtil.FILE_NAME_WEDDING_DATE_SHARE);
        FileUtil.saveImageToLocalFileWithOutNotify(bitmap, file, new Action1<String>() {
            @Override
            public void call(String path) {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                ShareLocalImageUtil shareUtil = new ShareLocalImageUtil(getContext(), path);
                switch (shareViewId) {
                    case R.id.btn_share_wx:
                        shareUtil.shareToWeiXin(bitmap);
                        break;
                    case R.id.btn_share_pengyou:
                        shareUtil.shareToPengYou(bitmap);
                        break;
                    case R.id.btn_share_qq:
                        shareUtil.shareToQQ();
                        break;
                    case R.id.btn_share_weibo:
                        shareUtil.shareToWeiBo();
                        break;
                }
            }
        });
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void onRationale(final PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(getContext(),
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ShareWeddingDateFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Bitmap getContentScreenShot() {
        int width = 750;
        int height = Math.round(width * 700.0f / 600.0f);
        return ImageUtil.getViewScreenShoot(cardView.getChildAt(0), width, height);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
