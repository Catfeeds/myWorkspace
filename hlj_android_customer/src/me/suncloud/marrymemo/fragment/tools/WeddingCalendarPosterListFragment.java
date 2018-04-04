package me.suncloud.marrymemo.fragment.tools;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljsharelibrary.utils.ShareLocalImageUtil;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.WeddingCalendarPosterListAdapter;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.functions.Action1;

/**
 * 当前日期和所有收藏的日期海报列表
 * Created by chen_bin on 2017/12/12 0012.
 */
@RuntimePermissions
public class WeddingCalendarPosterListFragment extends DialogFragment {

    @BindView(R.id.btn_close)
    ImageButton btnClose;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private PagerSnapHelper snapHelper;
    private LinearLayoutManager layoutManager;
    private WeddingCalendarPosterListAdapter adapter;

    private List<WeddingCalendarItem> calendarItems;
    private WeddingCalendarItem calendarItem;
    private DateTime statisticEndAt;

    private int position;
    private int shareViewId;
    private int space;

    private Unbinder unbinder;

    public final static String ARG_CALENDAR_ITEMS = "calendar_items";
    public final static String ARG_CALENDAR_ITEM = "calendar_item";
    public final static String ARG_STATISTIC_END_AT = "statistic_end_at";

    public static WeddingCalendarPosterListFragment newInstance(
            List<WeddingCalendarItem> calendarItems,
            WeddingCalendarItem calendarItem,
            DateTime statisticEndAt) {
        WeddingCalendarPosterListFragment fragment = new WeddingCalendarPosterListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_CALENDAR_ITEMS,
                (ArrayList<? extends Parcelable>) calendarItems);
        bundle.putParcelable(ARG_CALENDAR_ITEM, calendarItem);
        bundle.putSerializable(ARG_STATISTIC_END_AT, statisticEndAt);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BubbleDialogFragment);
        if (getArguments() != null) {
            calendarItems = getArguments().getParcelableArrayList(ARG_CALENDAR_ITEMS);
            calendarItem = getArguments().getParcelable(ARG_CALENDAR_ITEM);
            statisticEndAt = new DateTime(getArguments().getSerializable(ARG_STATISTIC_END_AT));
        }
        Collections.sort(calendarItems, new Comparator<WeddingCalendarItem>() {
            @Override
            public int compare(WeddingCalendarItem i1, WeddingCalendarItem i2) {
                DateTime date = i1.getDate();
                DateTime date2 = i2.getDate();
                if (date == null || date2 == null) {
                    return 0;
                }
                return date.compareTo(date2);
            }
        });
        position = Math.max(0, calendarItems.indexOf(calendarItem));
        space = CommonUtil.dp2px(getContext(), 10);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_wedding_calendar_poster,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window win = getDialog().getWindow();
        if (win != null) {
            getDialog().setCanceledOnTouchOutside(true);
            win.setDimAmount(0.9f);
            win.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration());
        adapter = new WeddingCalendarPosterListAdapter(getContext(), statisticEndAt);
        recyclerView.setAdapter(adapter);
        recyclerView.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        recyclerView.scrollToPosition(position);
                        Point point = CommonUtil.getDeviceSize(getContext());
                        int width = (int) (recyclerView.getMeasuredHeight() * 600.0f / 890.0f);
                        int offset = (point.x - width) / 2;
                        recyclerView.setPadding(offset - space, 0, offset - space, 0);
                        ((ViewGroup.MarginLayoutParams) btnClose.getLayoutParams()).rightMargin =
                                offset;
                        adapter.setWidth(width);
                        adapter.setCalendarItems(calendarItems);
                        return false;
                    }
                });
    }

    @OnClick(R.id.btn_close)
    void onClose() {
        dismiss();
    }

    @OnClick({R.id.btn_share_wx, R.id.btn_share_pengyou, R.id.btn_share_qq, R.id.btn_share_weibo})
    void onShare(View v) {
        shareViewId = v.getId();
        WeddingCalendarPosterListFragmentPermissionsDispatcher.onShareWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void onShare() {
        final Bitmap bitmap = getPosterScreenShot();
        if (bitmap == null) {
            return;
        }
        File file = FileUtil.createImagePngFile(FileUtil.FILE_NAME_CALENDAR_SHARE);
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
        WeddingCalendarPosterListFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Bitmap getPosterScreenShot() {
        CardView view = (CardView) snapHelper.findSnapView(layoutManager);
        if (view == null) {
            return null;
        }
        int width = 750;
        int height = (int) (width * 890.0f / 600.0f);
        return ImageUtil.getViewScreenShoot(view.getChildAt(0), width, height);
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        SpacesItemDecoration() {}

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(space, 0, space, 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}