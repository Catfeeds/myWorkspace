package com.hunliji.marrybiz.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Withdraw;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SubWithdrawListActivity extends HljBaseActivity {
    @BindView(R.id.list)
    StickyListHeadersListView listView;
    @BindView(R.id.progressBar)
    View progressBar;

    private int emptyHintDrawableId;
    private int emptyHintId;
    private View footView;
    private View loadView;
    private View endView;

    private ArrayList<Withdraw.WithdrawSub> subs;
    private StickyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_withdraw_list);
        ButterKnife.bind(this);

        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);

        emptyHintId = R.string.hint_no_orders;
        emptyHintDrawableId = R.mipmap.icon_empty_order;

        Withdraw withdraw = (Withdraw) getIntent().getSerializableExtra("withdraw");
        if (withdraw != null) {
            subs = withdraw.getSubs();
            adapter = new StickyAdapter(this, subs);
            listView.addFooterView(footView);
            listView.setAdapter(adapter);

            endView.setVisibility(View.GONE);
            loadView.setVisibility(View.GONE);
        } else {
            View emptyView = listView.getEmptyView();

            if (emptyView == null) {
                emptyView = findViewById(R.id.empty_hint_layout);
                listView.setEmptyView(emptyView);
            }

            ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_list_hint);
            TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id.empty_hint);
            imgEmptyHint.setVisibility(View.VISIBLE);
            emptyHintTextView.setVisibility(View.VISIBLE);
            if (JSONUtil.isNetworkConnected(SubWithdrawListActivity.this)) {
                imgEmptyHint.setImageResource(emptyHintDrawableId);
                emptyHintTextView.setText(emptyHintId);
            } else {
                imgEmptyHint.setVisibility(View.GONE);
                emptyHintTextView.setText(R.string.net_disconnected);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    public class StickyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;
        private ArrayList<Withdraw.WithdrawSub> mData;

        public StickyAdapter(Context context, ArrayList<Withdraw.WithdrawSub> withdraws) {
            inflater = LayoutInflater.from(context);
            mData = withdraws;
        }

        @Override
        public View getHeaderView(int i, View view, ViewGroup viewGroup) {
            HeaderViewHolder holder;
            if (view == null) {
                holder = new HeaderViewHolder();
                view = inflater.inflate(R.layout.date_head_item, viewGroup, false);
                holder.date = (TextView) view.findViewById(R.id.date);

                view.setTag(holder);
            } else {
                holder = (HeaderViewHolder) view.getTag();
            }

            holder.date.setText(mData.get(i)
                    .getCreatedAt()
                    .toString(getString(R.string.format_date_type10)));

            return view;
        }

        @Override
        public long getHeaderId(int i) {
            return mData.get(i)
                    .getDateVal();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData == null ? 0 : mData.get(position)
                    .getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.withdraw_order_list_item, parent, false);
                holder.orderNoTv = (TextView) convertView.findViewById(R.id.tv_order_no);
                holder.moneyTv = (TextView) convertView.findViewById(R.id.tv_money);
                holder.paidTimeTv = (TextView) convertView.findViewById(R.id.tv_paid_time);
                holder.withdrawStatusTv = (TextView) convertView.findViewById(R.id
                        .tv_withdraw_status);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Withdraw.WithdrawSub sub = mData.get(position);

            holder.paidTimeTv.setText(getString(R.string.label_paid_time2,
                    sub.getCreatedAt()
                            .toString(getString(R.string.format_date_type11))));
            holder.withdrawStatusTv.setText(getString(R.string.label_withdraw_type,
                    sub.getPayType()));
            holder.moneyTv.setText(getString(R.string.label_price,
                    Util.formatDouble2String(sub.getMoney())));
            holder.orderNoTv.setText(getString(R.string.label_order_no3, sub.getOrderNo()));

            return convertView;
        }

        class HeaderViewHolder {
            TextView date;
        }

        class ViewHolder {
            TextView orderNoTv;
            TextView paidTimeTv;
            TextView moneyTv;
            TextView withdrawStatusTv;
        }
    }
}
