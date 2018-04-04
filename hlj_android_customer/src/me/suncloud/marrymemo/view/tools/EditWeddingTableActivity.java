package me.suncloud.marrymemo.view.tools;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.WeddingGuestListAdapter;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingGuestFooterViewHolder;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingGuestHeaderViewHolder;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingGuestViewHolder;
import me.suncloud.marrymemo.api.tools.ToolsApi;
import me.suncloud.marrymemo.fragment.tools.AddWeddingGuestsFragment;
import me.suncloud.marrymemo.model.tools.WeddingGuest;
import me.suncloud.marrymemo.model.tools.WeddingTable;

/**
 * 编辑宾客信息
 * Created by chen_bin on 2017/11/23 0023.
 */
public class EditWeddingTableActivity extends HljBaseActivity implements WeddingGuestViewHolder
        .OnDeleteGuestListener, WeddingGuestViewHolder.OnSubtractGuestLister,
        WeddingGuestViewHolder.OnPlusGuestListener, WeddingGuestFooterViewHolder
                .OnImportGuestsListener, WeddingGuestFooterViewHolder.OnDeleteTableListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_add_guests)
    Button btnAddGuests;

    private View headerView;
    private View footerView;
    private Dialog deleteDialog;
    private Dialog exitDialog;
    private WeddingGuestHeaderViewHolder headerViewHolder;
    private WeddingGuestFooterViewHolder footerViewHolder;
    private WeddingGuestListAdapter adapter;
    private WeddingTable table;
    private String oldTableJson;

    private HljHttpSubscriber updateSub;
    private HljHttpSubscriber deleteSub;

    public final static String ARG_TABLE = "table";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wedding_table);
        ButterKnife.bind(this);
        initValues();
        initViews();
        setGuests();
    }

    private void initValues() {
        table = getIntent().getParcelableExtra(ARG_TABLE);
        setTitle(table == null || table.getId() == 0 ? getString(R.string.label_to_be_arranged) :
                getString(
                R.string.label_table_no,
                table.getTableNo()));
        setOkText(R.string.label_save___cm);
    }

    private void initViews() {
        headerView = View.inflate(this, R.layout.wedding_guest_header_item, null);
        footerView = View.inflate(this, R.layout.wedding_guest_footer_item, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations
                    (false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter = new WeddingGuestListAdapter(this);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        adapter.setOnDeleteGuestListener(this);
        adapter.setOnSubtractGuestLister(this);
        adapter.setOnPlusGuestListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setGuests() {
        if (table != null) {
            recyclerView.scrollToPosition(0);
            headerViewHolder = new WeddingGuestHeaderViewHolder(headerView);
            headerViewHolder.setView(this, table, 0, 0);
            footerViewHolder = new WeddingGuestFooterViewHolder(footerView);
            footerViewHolder.setOnImportGuestsListener(this);
            footerViewHolder.setOnDeleteTableListener(this);
            footerViewHolder.setView(this, table, 0, 0);
            adapter.setGuests(table.getGuests());
            oldTableJson = GsonUtil.getGsonInstance()
                    .toJson(table);
        }
    }

    @OnClick(R.id.btn_add_guests)
    void onAddGuests() {
        if (table != null) {
            AddWeddingGuestsFragment fragment = AddWeddingGuestsFragment.newInstance(table);
            fragment.show(getSupportFragmentManager(), "AddWeddingGuestsFragment");
        }
    }

    @Override
    public void onOkButtonClick() {
        if (table != null) {
            CommonUtil.unSubscribeSubs(updateSub);
            updateSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            Intent intent = getIntent();
                            setResult(RESULT_OK, intent);
                            EditWeddingTableActivity.super.onBackPressed();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            ToolsApi.updateTableObb(table)
                    .subscribe(updateSub);
        }
    }

    @Override
    public void onDeleteGuest(int position, WeddingGuest guest) {
        if (table != null && guest != null) {
            table.getGuests()
                    .remove(guest);
            table.setTotalNum(table.getTotalNum() - guest.getNum());
            adapter.notifyItemRemoved(position);
            if (headerViewHolder != null) {
                headerViewHolder.setTotalNum(this, table);
            }
        }
    }

    @Override
    public void onSubtractGuest(int position, WeddingGuest guest) {
        if (table != null && guest != null && guest.getNum() > 1) {
            guest.setNum(guest.getNum() - 1);
            table.setTotalNum(table.getTotalNum() - 1);
            adapter.notifyItemChanged(position);
            if (headerViewHolder != null) {
                headerViewHolder.setTotalNum(this, table);
            }
        }
    }

    @Override
    public void onPlusGuest(int position, WeddingGuest guest) {
        if (table != null && guest != null) {
            guest.setNum(guest.getNum() + 1);
            table.setTotalNum(table.getTotalNum() + 1);
            adapter.notifyItemChanged(position);
            if (headerViewHolder != null) {
                headerViewHolder.setTotalNum(this, table);
            }
        }
    }

    @Override
    public void onImportGuests(WeddingTable table) {
        if (table != null) {
            Intent intent = new Intent(this, ImportWeddingGuestsActivity.class);
            intent.putExtra(ImportWeddingGuestsActivity.ARG_TABLE, table);
            intent.putExtra(ImportWeddingGuestsActivity.ARG_TYPE,
                    ImportWeddingGuestsActivity.ARG_GUESTS);
            startActivityForResult(intent, Constants.RequestCode.IMPORT_WEDDING_GUESTS);
        }
    }

    @Override
    public void onDeleteTable(final WeddingTable table) {
        if (table == null) {
            return;
        }
        if (deleteDialog != null && deleteDialog.isShowing()) {
            return;
        }
        if (deleteDialog == null) {
            deleteDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_dialog_delete_table_title),
                    getString(R.string.label_dialog_delete_table_msg),
                    getString(R.string.label_delete_table),
                    null,
                    null,
                    null);
        }
        deleteDialog.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deleteSub == null || deleteSub.isUnsubscribed()) {
                            deleteSub = HljHttpSubscriber.buildSubscriber
                                    (EditWeddingTableActivity.this)
                                    .setOnNextListener(new SubscriberOnNextListener() {
                                        @Override
                                        public void onNext(Object o) {
                                            Intent intent = getIntent();
                                            setResult(RESULT_OK, intent);
                                            EditWeddingTableActivity.super.onBackPressed();
                                        }
                                    })
                                    .build();
                            ToolsApi.deleteTableObb(table.getId())
                                    .subscribe(deleteSub);
                        }
                    }
                });
        deleteDialog.show();
    }

    @Override
    public void onBackPressed() {
        String tableJson = GsonUtil.getGsonInstance()
                .toJson(table);
        if (TextUtils.equals(oldTableJson, tableJson)) {
            super.onBackPressed();
            return;
        }
        if (exitDialog != null && exitDialog.isShowing()) {
            return;
        }
        if (exitDialog == null) {
            exitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_dialog_exit_edit_wedding_table_title),
                    getString(R.string.label_dialog_exit_edit_wedding_table_msg),
                    getString(R.string.label_exit),
                    getString(R.string.label_not_exit),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditWeddingTableActivity.super.onBackPressed();
                        }
                    },
                    null);
        }
        exitDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.IMPORT_WEDDING_GUESTS:
                    if (data != null) {
                        List<WeddingGuest> guests = data.getParcelableArrayListExtra(
                                ImportWeddingGuestsActivity.ARG_GUESTS);
                        addGuestsData(guests);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addGuestsData(List<WeddingGuest> guests) {
        if (table != null && !CommonUtil.isCollectionEmpty(guests)) {
            recyclerView.scrollToPosition(adapter.getItemCount() - adapter.getFooterViewCount());
            table.setTotalNum(table.getTotalNum() + guests.size());
            adapter.addGuests(guests);
            if (headerViewHolder != null) {
                headerViewHolder.setTotalNum(this, table);
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(deleteSub, updateSub);
    }
}