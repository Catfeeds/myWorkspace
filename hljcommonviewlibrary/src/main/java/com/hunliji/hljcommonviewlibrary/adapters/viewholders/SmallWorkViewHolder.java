package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.SecondCategory;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljcommonviewlibrary.models.HotTag;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 小图样式
 * Created by chen_bin on 2016/11/10 0010.
 */
public class SmallWorkViewHolder extends TrackerWorkViewHolder {
    @BindView(R2.id.work_header_view)
    RelativeLayout workHeaderView;
    @BindView(R2.id.tv_work_header_title)
    TextView tvWorkHeaderTitle;
    @BindView(R2.id.work_view)
    LinearLayout workView;
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_hot_tag)
    ImageView imgHotTag;
    @BindView(R2.id.tv_merchant_property)
    TextView tvMerchantProperty;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.show_price_layout)
    LinearLayout showPriceLayout;
    @BindView(R2.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R2.id.tv_market_price)
    TextView tvMarketPrice;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R2.id.bottom_thin_line_layout)
    View bottomThinLineLayout;
    @BindView(R2.id.bottom_thick_line_layout)
    View bottomThickLineLayout;
    @BindView(R2.id.tv_city_name)
    TextView tvCityName;
    private int imageWidth;
    private int imageHeight;
    private int style; //样式类别
    private OnItemClickListener onItemClickListener;
    public static final int STYLE_COMMON = 0;//通用样式
    public static final int STYLE_MERCHANT_HOME_PAGE = 1; //商家主页
    public static final int STYLE_SEARCH = 2;  //搜索样式
    public static final int STYLE_GUESS_YOU_LIKE = 3;//猜你喜欢
    public static final int STYLE_CASE_DETAIL_RELATIVE = 4;//案例详情相关套餐

    public SmallWorkViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.small_common_work_item___cv, parent, false));
    }

    public SmallWorkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvMarketPrice.getPaint()
                .setAntiAlias(true);
        tvMarketPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 120);
        this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 75);
        workView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return workView;
    }

    @Override
    protected void setViewData(
            final Context mContext, final Work work, final int position, int viewType) {
        if (work == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(work.getCoverPath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        if (style != STYLE_CASE_DETAIL_RELATIVE) {
            SecondCategory category = work.getSecondCategory();
            if (category == null || TextUtils.isEmpty(category.getTitle()) || category.isListHidden()) {
                tvTitle.setText(work.getTitle());
            } else {
                SpannableStringBuilder builder = new SpannableStringBuilder(" " + work.getTitle());
                View view = View.inflate(mContext, R.layout.work_second_category___cv, null);
                ((TextView) view.findViewById(R.id.tv_title)).setText(category.getTitle());
                builder.setSpan(new HljImageSpan(ImageUtil.getDrawingCache(mContext, view)),
                        0,
                        1,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvTitle.setText(builder);
            }
        }else{
            SpannableStringBuilder builder = new SpannableStringBuilder(" " + work.getTitle());
            View view = View.inflate(mContext, R.layout.work_second_category___cv, null);
            ((TextView) view.findViewById(R.id.tv_title)).setText("相关套餐");
            builder.setSpan(new HljImageSpan(ImageUtil.getDrawingCache(mContext, view)),
                    0,
                    1,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvTitle.setText(builder);
        }
        switch (style) {
            //套餐小图通用样式
            case STYLE_COMMON:
                tvMerchantProperty.setVisibility(View.VISIBLE);
                tvMarketPrice.setVisibility(View.VISIBLE);
                tvCollectCount.setVisibility(View.VISIBLE);
                break;
            //商家主页样式
            case STYLE_MERCHANT_HOME_PAGE:
                HotTag tag = HotTag.getHotTag(work.getHotTag());
                if (tag == null) {
                    imgHotTag.setVisibility(View.GONE);
                } else {
                    imgHotTag.setVisibility(View.VISIBLE);
                    imgHotTag.setImageResource(tag.getDrawable());
                }
                tvMarketPrice.setVisibility(View.VISIBLE);
                tvCollectCount.setVisibility(View.VISIBLE);
                break;
            //套餐小图搜索样式
            case STYLE_SEARCH:
                tvMerchantProperty.setVisibility(View.VISIBLE);
                tvMerchantName.setVisibility(View.VISIBLE);
                break;
            //猜你喜欢样式
            case STYLE_GUESS_YOU_LIKE:
                tvMerchantName.setVisibility(View.VISIBLE);
                break;
            case STYLE_CASE_DETAIL_RELATIVE:
                tvMarketPrice.setVisibility(View.VISIBLE);
                tvCollectCount.setVisibility(View.VISIBLE);
                break;
        }
        if (tvMerchantProperty.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(work
                .getMerchant()
                .getProperty()
                .getName())) {
            tvMerchantProperty.setText(work.getMerchant()
                    .getProperty()
                    .getName());
        } else {
            tvMerchantProperty.setVisibility(View.GONE);
        }
        if (work.getCommodityType() == 0) {
            showPriceLayout.setVisibility(View.VISIBLE);
            tvShowPrice.setText(CommonUtil.formatDouble2String(work.getShowPrice()));
        } else {
            showPriceLayout.setVisibility(View.GONE);
        }
        if (tvMarketPrice.getVisibility() == View.VISIBLE && work.getMarketPrice() > 0) {
            tvMarketPrice.setText(CommonUtil.formatDouble2String(work.getMarketPrice()));
        } else {
            tvMarketPrice.setVisibility(View.GONE);
        }
        //商家名
        if (tvMerchantName.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(work.getMerchant()
                .getName())) {
            tvMerchantName.setText(work.getMerchant()
                    .getName());
        } else {
            tvMerchantName.setVisibility(View.GONE);
        }
        if (tvCollectCount.getVisibility() == View.VISIBLE) {
            tvCollectCount.setText(mContext.getString(R.string.label_collect_count___cv,
                    String.valueOf(work.getCollectorsCount())));
        }
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setCityName(String cityName) {
        tvCityName.setVisibility(View.VISIBLE);
        tvCityName.setText(cityName);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setShowWorkHeaderView(boolean showWorkHeaderView) {
        workHeaderView.setVisibility(showWorkHeaderView ? View.VISIBLE : View.GONE);
    }

    public void setWorkHeaderTitle(String workHeaderTitle) {
        tvWorkHeaderTitle.setText(workHeaderTitle);
    }

    public void setShowBottomThinLineView(boolean showBottomThinLineView) {
        bottomThinLineLayout.setVisibility(showBottomThinLineView ? View.VISIBLE : View.GONE);
        bottomThickLineLayout.setVisibility(View.GONE);
    }

    public void setShowBottomThickLineView(boolean showBottomThickLineView) {
        bottomThinLineLayout.setVisibility(View.GONE);
        bottomThickLineLayout.setVisibility(showBottomThickLineView ? View.VISIBLE : View.GONE);
    }

    public void setItemBottomMargin(int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) workView
                .getLayoutParams();
        params.bottomMargin = bottomMargin;
    }
}