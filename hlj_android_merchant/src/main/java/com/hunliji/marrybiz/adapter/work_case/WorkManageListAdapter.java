package com.hunliji.marrybiz.adapter.work_case;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.work_case.HotTag;
import com.hunliji.marrybiz.util.work_case.WorkStatusEnum;
import com.hunliji.marrybiz.view.CaseDetailActivity;
import com.hunliji.marrybiz.view.WorkActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 套餐跟案例管理列表
 * Created by chen_bin on 2017/2/3 0003.
 */
public class WorkManageListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView; //套餐、案例需要优化的headerView
    private View footerView;
    private List<Work> works;
    private WorkStatusEnum statusEnum;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;
    private int imageWidth;
    private int imageHeight;
    private OnTurnListener onTurnListener; //上架与下架
    private OnItemDeleteListener onItemDeleteListener; //删除

    public WorkManageListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.imageWidth = CommonUtil.dp2px(context, 99);
        this.imageHeight = CommonUtil.dp2px(context, 62);
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
        notifyDataSetChanged();
    }

    public void addWorks(List<Work> works) {
        if (!CommonUtil.isCollectionEmpty(works)) {
            int start = getItemCount() - getFooterViewCount();
            this.works.addAll(works);
            notifyItemRangeInserted(start, works.size());
        }
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {this.footerView = footerView;}

    public void setOnTurnClickListener(OnTurnListener onTurnListener) {
        this.onTurnListener = onTurnListener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public void setStatusEnum(WorkStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(works);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new WorkViewHolder(inflater.inflate(R.layout.work_manage_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                int index = position - getHeaderViewCount();
                holder.setView(context, works.get(index), index, viewType);
                break;
        }
    }

    public class WorkViewHolder extends BaseViewHolder<Work> {
        @BindView(R.id.reason_layout)
        LinearLayout reasonLayout;
        @BindView(R.id.tv_reason)
        TextView tvReason;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_installment)
        ImageView imgInstallment;
        @BindView(R.id.shadow_layout)
        LinearLayout shadowLayout;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.show_price_layout)
        LinearLayout showPriceLayout;
        @BindView(R.id.tv_show_price)
        TextView tvShowPrice;
        @BindView(R.id.tv_market_price)
        TextView tvMarketPrice;
        @BindView(R.id.tv_sale_count)
        TextView tvSaleCount;
        @BindView(R.id.tv_collect_count)
        TextView tvCollectCount;
        @BindView(R.id.on_layout)
        LinearLayout onLayout;
        @BindView(R.id.off_layout)
        LinearLayout offLayout;
        @BindView(R.id.split_layout)
        View splitLayout;
        @BindView(R.id.delete_layout)
        LinearLayout deleteLayout;

        public WorkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvCollectCount.setVisibility(View.VISIBLE);
            tvMarketPrice.getPaint()
                    .setAntiAlias(true);
            tvMarketPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            onLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTurnListener != null) {
                        onTurnListener.onTurn(getAdapterPosition(), getItem());
                    }
                }
            });
            offLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTurnListener != null) {
                        onTurnListener.onTurn(getAdapterPosition(), getItem());
                    }
                }
            });
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDeleteListener != null) {
                        onItemDeleteListener.onItemDelete(getAdapterPosition(), getItem());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = getItem();
                    if (work == null || work.getId() == 0) {
                        return;
                    }
                    Intent intent;
                    if (work.getCommodityType() == 0) {
                        intent = new Intent(context, WorkActivity.class);
                    } else {
                        intent = new Intent(context, CaseDetailActivity.class);
                    }
                    intent.putExtra("w_id", work.getId());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context, final Work work, final int position, int viewType) {
            if (work == null) {
                return;
            }
            //已下架
            if (statusEnum == WorkStatusEnum.OFF) {
                onLayout.setVisibility(View.VISIBLE);
                offLayout.setVisibility(View.GONE);
                reasonLayout.setVisibility(View.GONE);
                shadowLayout.setVisibility(View.GONE);
            }
            //审核中
            else if (statusEnum == WorkStatusEnum.REVIEW) {
                onLayout.setVisibility(View.GONE);
                offLayout.setVisibility(View.GONE);
                reasonLayout.setVisibility(View.GONE);
                shadowLayout.setVisibility(View.VISIBLE);
                tvStatus.setText(statusEnum.getStatusDescription());
            }
            //待优化列表和已上线列表
            else if (statusEnum == WorkStatusEnum.OPTIMIZE || statusEnum == WorkStatusEnum.ON) {
                onLayout.setVisibility(View.GONE);
                offLayout.setVisibility(View.VISIBLE);
                shadowLayout.setVisibility(View.GONE);
                //已上线列表不显示原因,待优化界面显示原因。
                if (TextUtils.isEmpty(work.getReason()) || statusEnum == WorkStatusEnum.ON) {
                    reasonLayout.setVisibility(View.GONE);
                } else {
                    reasonLayout.setVisibility(View.VISIBLE);
                    tvReason.setText(context.getString(R.string.label_optimize_suggestion,
                            work.getReason()));
                }
            }
            //审核未通过
            else if (statusEnum == WorkStatusEnum.REJECTED) {
                onLayout.setVisibility(View.GONE);
                offLayout.setVisibility(View.GONE);
                shadowLayout.setVisibility(View.VISIBLE);
                tvStatus.setText(statusEnum.getStatusDescription());
                if (TextUtils.isEmpty(work.getReason())) {
                    reasonLayout.setVisibility(View.GONE);
                } else {
                    reasonLayout.setVisibility(View.VISIBLE);
                    tvReason.setText(context.getString(R.string.label_failed_reason,
                            work.getReason()));
                }
            }
            splitLayout.setVisibility(onLayout.getVisibility() == View.VISIBLE || offLayout
                    .getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
            Glide.with(context)
                    .load(ImagePath.buildPath(work.getCoverPath())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
            imgInstallment.setVisibility(View.GONE);
            HotTag tag = HotTag.getHotTag(work.getHotTag());
            if (tag == null) {
                tvTitle.setText(work.getTitle());
            } else {
                SpannableStringBuilder builder = new SpannableStringBuilder(" " + work.getTitle());
                Drawable drawable = ContextCompat.getDrawable(context, tag.getDrawable());
                drawable.setBounds(0,
                        0,
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                builder.setSpan(new HljImageSpan(drawable), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvTitle.setText(builder);
            }
            //套餐
            if (work.getCommodityType() == 0) {
                showPriceLayout.setVisibility(View.VISIBLE);
                tvShowPrice.setText(CommonUtil.formatDouble2String(work.getShowPrice()));
                tvSaleCount.setVisibility(View.VISIBLE);
                if (work.getMarketPrice() > 0) {
                    tvMarketPrice.setVisibility(View.VISIBLE);
                    tvMarketPrice.setText(context.getString(R.string.label_price9___cv,
                            CommonUtil.formatDouble2String(work.getMarketPrice())));
                } else {
                    tvMarketPrice.setVisibility(View.GONE);
                }
            }
            //案例
            else {
                showPriceLayout.setVisibility(View.GONE);
                tvSaleCount.setVisibility(View.GONE);
                tvMarketPrice.setVisibility(View.GONE);
            }
            tvSaleCount.setText(context.getString(R.string.label_sale_count___cv,
                    work.getSalesCount()));
            tvCollectCount.setText(context.getString(R.string.label_collect_count___cv,
                    work.getCollectorsCount()));
        }
    }

    public interface OnTurnListener {
        void onTurn(int position, Work work);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position, Work work);
    }
}