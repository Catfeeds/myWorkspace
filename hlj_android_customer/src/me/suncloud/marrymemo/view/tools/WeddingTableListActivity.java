package me.suncloud.marrymemo.view.tools;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.WeddingTableListAdapter;
import me.suncloud.marrymemo.api.tools.ToolsApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.tools.WeddingGuest;
import me.suncloud.marrymemo.model.tools.WeddingTable;
import me.suncloud.marrymemo.model.user.wrappers.HljWeddingTablesData;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.user.UserPrefUtil;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;

/**
 * 我的宾客页
 * Created by chen_bin on 2017/11/23 0023.
 */
@Route(path = RouterPath.IntentPath.Customer.WEDDING_TABLE_LIST_ACTIVITY)
public class WeddingTableListActivity extends HljBaseNoBarActivity implements
        OnItemClickListener<WeddingTable>, WeddingTableListAdapter.OnItemDragSucceedListener,
        PullToRefreshBase.OnRefreshListener<RecyclerView> {

    @Override
    public String pageTrackTagName() {
        return "婚宴座位表";
    }

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.et_keyword)
    ClearableEditText etKeyword;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.tv_partner_hint)
    TextView tvPartnerHint;
    @BindView(R.id.tv_get)
    TextView tvGet;
    @BindView(R.id.partner_hint_layout)
    RelativeLayout partnerHintLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.share_layout)
    LinearLayout shareLayout;

    private ShareUtil shareUtil;
    private Dialog shareDialog;
    private RecyclerViewDragDropManager dragDropManager;
    private LinearLayoutManager layoutManager;
    private WeddingTableListAdapter adapter;
    private RecyclerView.Adapter wrappedAdapter;
    private User user;
    private ShareInfo shareInfo;
    private boolean isShowPartnerHintView;
    private boolean isShowProgressBar = true;
    private boolean isShowedTableHintView;
    private String previewUrl;

    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber addTableSub;
    private HljHttpSubscriber updateTableNoSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_table_list);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
        onRefresh(null);
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(shareLayout)
                .tagName("search_seat_item")
                .hitTag();
    }

    private void initValues() {
        user = Session.getInstance()
                .getCurrentUser(this);
        if (user == null) {
            return;
        }
        CustomerUser partnerUser = user.getPartnerUser();
        if (partnerUser == null || partnerUser.getId() == 0) {
            UserPrefUtil.getInstance(this)
                    .setWeddingTablePartnerHintClicked(user.getId(), false);
            isShowPartnerHintView = true;
            tvPartnerHint.setText(R.string.hint_wedding_table_unbind);
            tvGet.setText(R.string.label_to_bind_partner);
        } else {
            isShowPartnerHintView = !UserPrefUtil.getInstance(this)
                    .isWeddingTablePartnerHintClicked(user.getId());
            tvPartnerHint.setText(R.string.hint_wedding_table_bind);
            tvGet.setText(R.string.label_get);
        }
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setNeedChangeSize(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setPadding(0, CommonUtil.dp2px(this, 3), 0, 0);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.attachRecyclerView(recyclerView.getRefreshableView());
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setLongPressTimeout(250);
        adapter = new WeddingTableListAdapter(this);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemDragSucceedListener(this);
        wrappedAdapter = dragDropManager.createWrappedAdapter(adapter);
        recyclerView.getRefreshableView()
                .setAdapter(wrappedAdapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljWeddingTablesData>() {

                        @Override
                        public void onNext(HljWeddingTablesData tablesData) {
                            partnerHintLayout.setVisibility(isShowPartnerHintView ? View.VISIBLE
                                    : View.GONE);
                            bottomLayout.setVisibility(View.VISIBLE);
                            shareInfo = null;
                            previewUrl = null;
                            List<WeddingTable> tables = new ArrayList<>();
                            if (tablesData != null) {
                                shareInfo = tablesData.getShareInfo();
                                previewUrl = tablesData.getPreviewUrl();
                                if (!tablesData.isEmpty()) {
                                    tables.addAll(tablesData.getData());
                                }
                            }
                            adapter.setKeyword(null);
                            adapter.setTables(tables);
                            setShowTableHintView();
                            setShowTableDragHintView();
                            setShowEmptyView(tables);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() || !isShowProgressBar ? null :
                            progressBar)
                    .build();
            isShowProgressBar = true;
            ToolsApi.getTablesObb()
                    .subscribe(refreshSub);
        }
    }

    @OnTextChanged(R.id.et_keyword)
    void afterTextChanged(Editable s) {
        String keyword = s.toString();
        adapter.setKeyword(keyword);
        List<WeddingTable> tables = adapter.getTables();
        if (TextUtils.isEmpty(keyword)) {
            adapter.setTables(tables);
            setShowEmptyView(tables);
        } else if (!CommonUtil.isCollectionEmpty(tables)) {
            List<WeddingTable> searchTables = new ArrayList<>();
            for (WeddingTable table : tables) {
                List<WeddingGuest> guests = table.getGuests();
                if (!CommonUtil.isCollectionEmpty(guests)) {
                    for (WeddingGuest guest : guests) {
                        if (!TextUtils.isEmpty(guest.getFullName()) && guest.getFullName()
                                .contains(keyword)) {
                            searchTables.add(table);
                            break;
                        }
                    }
                }
            }
            adapter.setSearchTables(searchTables);
            setShowEmptyView(searchTables);
        }
    }

    @OnClick(R.id.btn_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.partner_hint_layout)
    void onPartnerHintClicked() {
        if (user == null) {
            return;
        }
        isShowPartnerHintView = false;
        partnerHintLayout.setVisibility(View.GONE);
        CustomerUser partnerUser = user.getPartnerUser();
        if (partnerUser == null || partnerUser.getId() == 0) {
            startActivity(new Intent(this, BindingPartnerActivity.class));
        } else {
            UserPrefUtil.getInstance(this)
                    .setWeddingTablePartnerHintClicked(user.getId(), true);
        }
    }

    @OnClick(R.id.add_table_layout)
    void onAddTable() {
        if (addTableSub == null || addTableSub.isUnsubscribed()) {
            addTableSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<List<WeddingTable>>() {
                        @Override
                        public void onNext(List<WeddingTable> tables) {
                            adapter.setKeyword(null);
                            adapter.addTables(tables);
                            setShowEmptyView(adapter.getTables());
                            int position = adapter.getItemCount() - 1;
                            if (adapter.hasToBeArranged()) {
                                position = position - 1;
                            }
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(position);
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            ToolsApi.addTableObb()
                    .subscribe(addTableSub);
        }
    }

    @OnClick(R.id.preview_layout)
    void onPreview() {
        if (shareInfo != null && !TextUtils.isEmpty(previewUrl)) {
            Intent intent = new Intent(this, PreviewWeddingTableActivity.class);
            intent.putExtra(PreviewWeddingTableActivity.ARG_PATH, previewUrl);
            intent.putExtra(PreviewWeddingTableActivity.ARG_SHARE, shareInfo);
            intent.putExtra(PreviewWeddingTableActivity.ARG_BAR_STYLE,
                    PreviewWeddingTableActivity.BAR_STYLE_CUSTOM);
            startActivity(intent);
        }
    }

    @OnClick(R.id.qr_code_layout)
    void onQRCode() {
        if (!TextUtils.isEmpty(previewUrl)) {
            Intent intent = new Intent(WeddingTableListActivity.this,
                    WeddingTableQRCodeActivity.class);
            intent.putExtra(WeddingTableQRCodeActivity.ARG_URL, previewUrl);
            startActivity(intent);
        }
    }

    @OnClick(R.id.share_layout)
    void onShare() {
        if (shareInfo == null) {
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareUtil == null) {
            shareUtil = new ShareUtil(this, shareInfo, null);
        }
        if (shareDialog == null) {
            shareDialog = ShareDialogUtil.onWeddingTableShare(this,
                    getString(R.string.label_dialog_wedding_table_share_title),
                    new ShareActionViewHolder.OnShareClickListener() {
                        @Override
                        public void onShare(View v, ShareAction action) {
                            switch (action) {
                                case WeiXin:
                                    if (shareUtil != null) {
                                        shareUtil.shareToWeiXin();
                                    }
                                    break;
                                case QQ:
                                    if (shareUtil != null) {
                                        shareUtil.shareToQQ();
                                    }
                                    break;
                                case WeddingTableQRCode:
                                    onQRCode();
                                    break;
                                case SMS:
                                    if (shareUtil != null) {
                                        shareUtil.shareToSms();
                                    }
                                    break;
                            }
                        }
                    });
        }
        shareDialog.show();
    }

    @Override
    public void onItemClick(int position, WeddingTable table) {
        if (table != null) {
            adapter.hideTableDragHintView(WeddingTableListAdapter.DRAG_HINT_POSITION);
            Intent intent = new Intent(this, EditWeddingTableActivity.class);
            intent.putExtra(EditWeddingTableActivity.ARG_TABLE, table);
            startActivityForResult(intent, Constants.RequestCode.EDIT_WEDDING_TABLE);
        }
    }

    @Override
    public void onItemDragSucceed() {
        CommonUtil.unSubscribeSubs(updateTableNoSub, refreshSub);
        List<WeddingTable> tables = adapter.getTables();
        int size = tables.size();
        if (adapter.hasToBeArranged()) {
            size = size - 1;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            WeddingTable table = tables.get(i);
            table.setTableNo(i + 1);
            sb.append(table.getId());
            if (i < size - 1) {
                sb.append(",");
            }
        }
        adapter.notifyDataSetChanged();
        updateTableNoSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        isShowProgressBar = false;
                        onRefresh(null);
                    }
                })
                .build();
        ToolsApi.updateTableNoObb(sb.toString())
                .subscribe(updateTableNoSub);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_WEDDING_TABLE:
                    onRefresh(null);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setShowTableHintView() {
        if (!UserPrefUtil.getInstance(this)
                .isWeddingTableHintClicked()) {
            isShowedTableHintView = true;
            UserPrefUtil.getInstance(this)
                    .setWeddingTableHintClicked(true);
            final Dialog dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_wedding_table_hint);
            dialog.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }

    private void setShowTableDragHintView() {
        if (isShowedTableHintView || UserPrefUtil.getInstance(this)
                .isWeddingTableDragHintClicked()) {
            return;
        }
        List<WeddingTable> tables = adapter.getTables();
        int start = adapter.hasToBeArranged() ? WeddingTableListAdapter.START_WITH_TO_BE_ARRANGED
                : WeddingTableListAdapter.START_WITHOUT_TO_BE_ARRANGED;
        adapter.setShowTableDragHintView(CommonUtil.getCollectionSize(tables) >= start);
    }

    private void setShowEmptyView(List<WeddingTable> tables) {
        if (CommonUtil.isCollectionEmpty(tables)) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.hideEmptyView();
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dragDropManager != null) {
            dragDropManager.cancelDrag();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub, addTableSub, updateTableNoSub);
        if (dragDropManager != null) {
            dragDropManager.release();
            dragDropManager = null;
        }
        if (recyclerView != null) {
            recyclerView.getRefreshableView()
                    .setItemAnimator(null);
            recyclerView.getRefreshableView()
                    .setAdapter(null);
            recyclerView = null;
        }
        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        layoutManager = null;
    }
}