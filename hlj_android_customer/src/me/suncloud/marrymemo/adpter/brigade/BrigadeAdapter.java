package me.suncloud.marrymemo.adpter.brigade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.brigade.BrigadeWeekHotsActivity;

/**
 * Created by hua_rong on 2017/7/24.
 * 旅拍本周热卖
 */

public class BrigadeAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private Context context;
    private List<Work> brigadeList;
    private View headerView;
    private View footerView;
    private final LayoutInflater inflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;


    public BrigadeAdapter(Context context, List<Work> BrigadeList) {
        this.context = context;
        this.brigadeList = BrigadeList;
        inflater = LayoutInflater.from(context);
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setBrigadeList(List<Work> brigadeList) {
        this.brigadeList = brigadeList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                View itemView = inflater.inflate(R.layout.brigade_hot_this_week_item,
                        parent,
                        false);
                return new BrigadeViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof BrigadeViewHolder) {
            BrigadeViewHolder viewHolder = (BrigadeViewHolder) holder;
            viewHolder.setView(context,
                    getItem(position),
                    position - (headerView == null ? 0 : 1),
                    getItemViewType(position));
        }
    }

    private Work getItem(int position) {
        return brigadeList.get(headerView == null ? position : position - 1);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return brigadeList.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    public class BrigadeViewHolder extends TrackerWorkViewHolder {

        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_actual_price)
        TextView tvActualPrice;
        @BindView(R.id.tv_market_price)
        TextView tvMarketPrice;
        @BindView(R.id.btn_buy)
        Button btnBuy;
        @BindView(R.id.ll_tag)
        LinearLayout llTag;
        @BindView(R.id.ll_item)
        LinearLayout llItem;
        private int width;
        private int height;

        @Override
        public String cpmSource() {
            return BrigadeWeekHotsActivity.CPM_SOURCE;
        }

        public BrigadeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Point point = CommonUtil.getDeviceSize(context);
            width = point.x - CommonUtil.dp2px(context, 24);
            height = width * 9 / 16;
            ivCover.getLayoutParams().width = width;
            ivCover.getLayoutParams().height = height;
            btnBuy.getLayoutParams().width = point.x * 3 / 16;
            Paint paint = tvMarketPrice.getPaint();
            paint.setAntiAlias(true);
            paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }

        @Override
        public View trackerView() {
            return llItem;
        }

        @Override
        protected void setViewData(Context mContext, Work brigade, int position, int viewType) {
            if (brigade != null) {
                final long id = brigade.getId();
                itemView.setVisibility(View.VISIBLE);
                llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goWorkActivity(v, id);
                    }
                });
                btnBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llItem.performClick();
                    }
                });
                tvTitle.setText(brigade.getTitle());
                double marketPrice = brigade.getMarketPrice();
                if (marketPrice > 0) {
                    tvMarketPrice.setVisibility(View.VISIBLE);
                    tvMarketPrice.setText(context.getString(R.string.rmb) + Util
                            .formatDouble2String(
                            marketPrice) + "");
                } else {
                    tvMarketPrice.setVisibility(View.INVISIBLE);
                }
                tvActualPrice.setText(Util.formatDouble2String(brigade.getShowPrice()));
                String imagePath = brigade.getCoverPath();
                if (!TextUtils.isEmpty(imagePath)) {
                    Glide.with(context)
                            .load(ImagePath.buildPath(imagePath)
                                    .width(width)
                                    .height(height)
                                    .cropPath())
                            .into(ivCover);
                } else {
                    Glide.with(context)
                            .clear(ivCover);
                    ivCover.setImageBitmap(null);
                }
                List<String> marks = brigade.getUpgradeMarks();
                if (CommonUtil.isCollectionEmpty(marks)) {
                    llTag.setVisibility(View.GONE);
                } else {
                    llTag.setVisibility(View.VISIBLE);
                    int count = llTag.getChildCount();
                    int size = Math.min(marks.size(), count);
                    for (int i = 0; i < count; i++) {
                        TextView textView = (TextView) llTag.getChildAt(i);
                        if (i < size) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(marks.get(i));
                        } else {
                            textView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }

        private void goWorkActivity(View v, long id) {
            if (id > 0) {
                Activity activity = (Activity) v.getContext();
                Intent intent = new Intent(activity, WorkActivity.class);
                intent.putExtra("id", id);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }

    }
}
