package me.suncloud.marrymemo.adpter.brigade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.wrappers.LimitBuyContent;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.brigade.BrigadeLimitBuyActivity;

/**
 * Created by mo_yu on 2017/9/6.
 * 旅拍限时团购
 */

public class BrigadeLimitBuyAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<LimitBuyContent> brigadeList;
    private View headerView;
    private View footerView;
    private final LayoutInflater inflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_ITEM_WORK = 1;
    private static final int TYPE_ITEM_IMAGE = 3;


    public BrigadeLimitBuyAdapter(Context context, List<LimitBuyContent> BrigadeList) {
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

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case TYPE_ITEM_WORK:
                View itemView = inflater.inflate(R.layout.brigade_limit_buy_item, parent, false);
                return new BrigadeWorkViewHolder(itemView);
            default:
                itemView = inflater.inflate(R.layout.brigade_limit_buy_item, parent, false);
                return new BrigadeImageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof BrigadeImageViewHolder) {
            BrigadeImageViewHolder viewHolder = (BrigadeImageViewHolder) holder;
            viewHolder.setView(context,
                    getItem(position),
                    position - (headerView == null ? 0 : 1),
                    getItemViewType(position));
        }else if(holder instanceof BrigadeWorkViewHolder) {
            BrigadeWorkViewHolder viewHolder = (BrigadeWorkViewHolder) holder;
            viewHolder.setView(context,
                    getItem(position),
                    position - (headerView == null ? 0 : 1),
                    getItemViewType(position));
        }
    }

    private LimitBuyContent getItem(int position) {
        return brigadeList.get(headerView == null ? position : position - 1);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            LimitBuyContent item = getItem(position);
            if (item!=null&&item.getType()
                    .endsWith(LimitBuyContent.MEAL)) {
                return TYPE_ITEM_WORK;
            } else {
                return TYPE_ITEM_IMAGE;
            }
        }
    }

    @Override
    public int getItemCount() {
        return brigadeList.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    public class BrigadeImageViewHolder extends BaseViewHolder<LimitBuyContent> {

        @BindView(R.id.meal_layout)
        LinearLayout mealLayout;
        @BindView(R.id.img_city_pic)
        ImageView imgCityPic;
        private int width;
        private int picHeight;

        BrigadeImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Point point = CommonUtil.getDeviceSize(itemView.getContext());
            width = point.x - CommonUtil.dp2px(itemView.getContext(), 32);
            picHeight = CommonUtil.dp2px(itemView.getContext(), 30);
            mealLayout.setVisibility(View.GONE);
            imgCityPic.setVisibility(View.VISIBLE);
        }

        @Override
        protected void setViewData(
                Context mContext, LimitBuyContent item, int position, int viewType) {
            if (item != null) {
                String imagePath = item.getPic();
                Glide.with(context)
                        .load(ImagePath.buildPath(imagePath)
                                .width(width)
                                .height(picHeight)
                                .cropPath())
                        .into(imgCityPic);
            }
        }

    }

    public class BrigadeWorkViewHolder extends TrackerWorkViewHolder {

        @BindView(R.id.img_meal_cover)
        ImageView imgMealCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_show_price)
        TextView tvShowPrice;
        @BindView(R.id.tv_market_price)
        TextView tvMarketPrice;
        @BindView(R.id.tv_discount)
        TextView tvDiscount;
        @BindView(R.id.meal_layout)
        LinearLayout mealLayout;
        @BindView(R.id.img_city_pic)
        ImageView imgCityPic;
        private int width;
        private int height;

        BrigadeWorkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Point point = CommonUtil.getDeviceSize(itemView.getContext());
            width = point.x - CommonUtil.dp2px(itemView.getContext(), 32);
            height = width * 360 / 592;
            imgMealCover.getLayoutParams().width = width;
            imgMealCover.getLayoutParams().height = height;
            Paint paint = tvMarketPrice.getPaint();
            paint.setAntiAlias(true);
            paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            mealLayout.setVisibility(View.VISIBLE);
            imgCityPic.setVisibility(View.GONE);
        }

        @Override
        public String cpmSource() {
            return BrigadeLimitBuyActivity.CPM_SOURCE;
        }

        @Override
        public View trackerView() {
            return itemView;
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

        @Override
        protected void setViewData(
                Context mContext, Work item, int position, int viewType) {
            final long id = item.getId();
            itemView.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goWorkActivity(v, id);
                }
            });
            tvTitle.setText(item.getTitle());
            double marketPrice = item.getMarketPrice();
            if (marketPrice > 0) {
                tvMarketPrice.setVisibility(View.VISIBLE);
                tvMarketPrice.setText("¥" + Util.formatDouble2String(marketPrice));
            } else {
                tvMarketPrice.setVisibility(View.INVISIBLE);
            }
            tvShowPrice.setText(Util.formatDouble2String(item.getShowPrice()));
            tvDiscount.setText(String.valueOf((float) Math.round(item.getShowPrice() *
                    100 / marketPrice) / 10) + "折");
            String imagePath = item.getCoverPath();
            Glide.with(context)
                    .load(ImagePath.buildPath(imagePath)
                            .width(width)
                            .height(height)
                            .cropPath())
                    .into(imgMealCover);
        }
    }
}
