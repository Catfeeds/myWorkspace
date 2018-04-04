package com.hunliji.marrybiz.adapter.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.event.RecordInfo;
import com.hunliji.marrybiz.view.event.ApplyEventActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活动申请记录
 * Created by chen_bin on 2017/3/8 0008.
 */
public class ApplyRecordRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<RecordInfo> records;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public ApplyRecordRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setRecords(List<RecordInfo> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    public void addRecords(List<RecordInfo> records) {
        if (!CommonUtil.isCollectionEmpty(records)) {
            int start = getItemCount() - getFooterViewCount();
            this.records.addAll(records);
            notifyItemRangeInserted(start, records.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(records);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new ApplyRecordViewHolder(inflater.inflate(R.layout.apply_record_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, records.get(position), position, viewType);
                break;
        }
    }

    public class ApplyRecordViewHolder extends BaseViewHolder<RecordInfo> {
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.line_layout)
        View lineLayout;

        public ApplyRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordInfo recordInfo = getItem();
                    if (recordInfo != null && recordInfo.getId() > 0) {
                        Intent intent = new Intent(context, ApplyEventActivity.class);
                        intent.putExtra("id", recordInfo.getId());
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context,
                final RecordInfo recordInfo,
                final int position,
                int viewType) {
            if (recordInfo == null) {
                return;
            }
            lineLayout.setVisibility(position < records.size() - 1 ? View.VISIBLE : View.GONE);
            if (TextUtils.isEmpty(recordInfo.getContent())) {
                tvContent.setText("");
            } else {
                SpannableString builder = new SpannableString(" " + recordInfo.getContent());
                Drawable drawable;
                if (recordInfo.getStatus() == 0 || recordInfo.getStatus() == 3) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.icon_reviewing_tag);
                } else if (recordInfo.getStatus() == 1) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.icon_review_pass_tag);
                } else {
                    drawable = ContextCompat.getDrawable(context, R.drawable.icon_review_fail_tag);
                }
                drawable.setBounds(0,
                        0,
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                builder.setSpan(new HljImageSpan(drawable),
                        0,
                        1,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tvContent.setText(builder);
            }
            tvTime.setText(recordInfo.getCreatedAt() == null ? "" : context.getString(R.string
                            .label_apply_time_title,
                    recordInfo.getCreatedAt()
                            .toString(context.getString(R.string.format_date_type12))));
        }
    }
}
