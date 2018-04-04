package me.suncloud.marrymemo.adpter.finder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljnotelibrary.utils.CaseGridInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.FinderCaseMediaPagerActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by mo_yu on 2018/2/7.发现页-案例列表
 */

public class SameCaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = 2;
    private static final int HEADER_TYPE = 3;
    private static final int EMPTY_TYPE = 4;

    private Context context;
    private View footerView;
    private View headerView;
    private List<Work> cases;
    private LayoutInflater inflater;
    private OnCollectCaseListener onCollectCaseListener;

    public SameCaseRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCases(List<Work> cases) {
        this.cases = cases;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    private int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    private int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setOnCollectCaseListener(
            OnCollectCaseListener onCollectCaseListener) {
        this.onCollectCaseListener = onCollectCaseListener;
    }

    public void addCases(List<Work> list) {
        if (!CommonUtil.isCollectionEmpty(list)) {
            int start = getItemCount() - getFooterViewCount();
            cases.addAll(list);
            notifyItemRangeInserted(start, list.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return HEADER_TYPE;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case EMPTY_TYPE:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder___cm, parent, false));
            case ITEM_TYPE:
                return new SameCaseViewHolder(inflater.inflate(R.layout
                                .same_case_list_item___cv,
                        parent,
                        false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case ITEM_TYPE:
                holder.setView(context, getItem(position), position, type);
                break;
        }
    }

    private Work getItem(int position) {
        position = position - getHeaderViewCount();
        return cases.get(position);
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + getHeaderViewCount() + (cases == null ? 0 : cases.size());
    }

    public interface OnCollectCaseListener {
        void onCollectCase(
                int position, Object object, ImageView imgCollect, TextView tvCollectState);
    }

    class SameCaseViewHolder extends BaseViewHolder<Work> {
        @BindView(R.id.tv_case_title)
        TextView tvCaseTitle;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.img_collect_case)
        ImageView imgCollectCase;
        @BindView(R.id.tv_collect_state)
        TextView tvCollectState;
        @BindView(R.id.action_collect_case)
        LinearLayout actionCollectCase;
        @BindView(R.id.grid_view)
        HljGridView gridView;
        CaseGridInterface gridInterface;

        SameCaseViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            gridInterface= new CaseGridInterface(itemView.getContext());
            gridView.setGridInterface(gridInterface);
            tvCaseTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = getItem();
                    goMerchantDetail(v.getContext(), work);
                }
            });
            tvMerchantName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = getItem();
                    goMerchantDetail(v.getContext(), work);
                }
            });
            actionCollectCase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCollectCaseListener != null) {
                        onCollectCaseListener.onCollectCase(getAdapterPosition(),
                                getItem(),
                                imgCollectCase,
                                tvCollectState);
                    }
                }
            });
            gridView.setItemClickListener(new HljGridView.GridItemClickListener() {
                @Override
                public void onItemClick(View itemView, int index) {
                    Work work = getItem();
                    Activity activity = (Activity) itemView.getContext();
                    if (work == null) {
                        return;
                    }
                    Intent intent = new Intent(itemView.getContext(),
                            FinderCaseMediaPagerActivity.class);
                    intent.putExtra(FinderCaseMediaPagerActivity.ARG_CASE, work);
                    intent.putExtra(FinderCaseMediaPagerActivity.ARG_IS_SHOW_MORE, false);
                    intent.putExtra(FinderCaseMediaPagerActivity.ARG_POSITION, index);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = getItem();
                    Activity activity = (Activity) v.getContext();
                    if (work == null) {
                        return;
                    }
                    Intent intent = new Intent(itemView.getContext(),
                            FinderCaseMediaPagerActivity.class);
                    intent.putExtra(FinderCaseMediaPagerActivity.ARG_CASE, work);
                    intent.putExtra(FinderCaseMediaPagerActivity.ARG_IS_SHOW_MORE, false);
                    intent.putExtra(FinderCaseMediaPagerActivity.ARG_POSITION, 0);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }

        @Override
        protected void setViewData(Context mContext, Work item, int position, int viewType) {
            Merchant merchant = item.getMerchant();
            if (merchant != null) {
                tvMerchantName.setText(merchant.getName());
            }
            tvCaseTitle.setText(item.getTitle());
            if (item.isCollected()) {
                imgCollectCase.setImageResource(R.mipmap.icon_collect_primary_32_32_selected);
                tvCollectState.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else {
                imgCollectCase.setImageResource(R.mipmap.icon_collect_black2_32_32);
                tvCollectState.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
            }
            if (!CommonUtil.isCollectionEmpty(item.getMediaItems())) {
                gridView.setVisibility(View.VISIBLE);
                gridView.setDate(item.getMediaItems());
            } else {
                gridView.setVisibility(View.GONE);
            }
        }

        private void goMerchantDetail(Context context, Work work) {
            Activity activity = (Activity) context;
            if (work == null) {
                return;
            }
            Intent intent = new Intent(activity, MerchantDetailActivity.class);
            intent.putExtra(MerchantDetailActivity.ARG_ID,
                    work.getMerchant()
                            .getId());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }

    }
}
