package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.OriginalImageScaleListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.lvpai.NinetyNineLvPaiActivity;

/**
 * Created by luohanlin on 2018/1/4.
 */

public class NinetyNineWorkAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<Work> works;
    private Poster banner;

    private static final int TYPE_BANNER = 0;
    private static final int TYPE_ITEM = 1;

    private int imgWidth;
    private int imgHeight;
    private int ruleWidth;
    private int ruleHeight;

    public NinetyNineWorkAdapter(
            Context context, List<Work> works) {
        this.context = context;
        this.works = works;

        imgWidth = CommonUtil.getDeviceSize(context).x;
        imgHeight = Math.round(imgWidth * 10.0f / 16.0f);
        this.ruleWidth = CommonUtil.dp2px(context, 120);
        this.ruleHeight = CommonUtil.dp2px(context, 100);
    }

    public void setBanner(Poster banner) {
        this.banner = banner;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BANNER) {
            return new BannerViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.ninety_nine_banner, parent, false));
        } else {
            return new NNWorkViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.ninety_nine_item_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof NNWorkViewHolder) {
            ((NNWorkViewHolder) holder).setView(context,
                    works.get(position - getHeaderCount()),
                    position,
                    getItemViewType(position));
        } else if (holder instanceof BannerViewHolder) {
            holder.setView(context, banner, position, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && banner != null) {
            return TYPE_BANNER;
        }
        return TYPE_ITEM;
    }

    private int getHeaderCount() {
        return banner == null ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return (works == null ? 0 : works.size()) + getHeaderCount();
    }

    class NNWorkViewHolder extends TrackerWorkViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_badge)
        ImageView imgBadge;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_market_price)
        TextView tvMarketPrice;
        @BindView(R.id.tv_count_left)
        TextView tvCountLeft;
        @BindView(R.id.tv_intent_price)
        TextView tvIntentPrice;
        View view;

        NNWorkViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            imgCover.getLayoutParams().height = imgHeight;
            tvMarketPrice.getPaint()
                    .setAntiAlias(true);
            tvMarketPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }

        @Override
        public String cpmSource() {
            return NinetyNineLvPaiActivity.CPM_SOURCE_IN_PAGE;
        }

        @Override
        public View trackerView() {
            return view;
        }

        @Override
        protected void setViewData(
                Context mContext, final Work item, int position, int viewType) {
            if (item == null) {
                return;
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorkActivity.class);
                    intent.putExtra("id", item.getId());
                    context.startActivity(intent);
                }
            });
            Glide.with(context)
                    .load(ImagePath.buildPath(item.getCoverPath())
                            .width(imgWidth)
                            .height(imgHeight)
                            .path())
                    .into(imgCover);
            StringBuilder stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(item.getLvPaiCity())) {
                stringBuilder.append(item.getLvPaiCity())
                        .append(" · ");
            } else if (item.getMerchant() != null && !TextUtils.isEmpty(item.getMerchant()
                    .getCityName())) {
                stringBuilder.append(item.getMerchant()
                        .getCityName())
                        .append(" · ");
            }
            stringBuilder.append(item.getTitle());
            tvTitle.setText(stringBuilder);
            if (item.getMarketPrice() > 0) {
                tvMarketPrice.setVisibility(View.VISIBLE);
                tvMarketPrice.setText(context.getString(R.string.label_price,
                        NumberFormatUtil.formatDouble2String(item.getMarketPrice())));
            } else {
                tvMarketPrice.setVisibility(View.GONE);
            }
            tvPrice.setText(NumberFormatUtil.formatDouble2String(item.getShowPrice()));

            tvCountLeft.setText(Html.fromHtml(context.getString(R.string.html_fmt_limit_count,
                    item.getLimitNum() - item.getLimitSoldOut())));
            tvIntentPrice.setText(NumberFormatUtil.formatDouble2String(item.getIntentPrice()));

            WorkRule rule = item.getRule();
            if (rule == null || TextUtils.isEmpty(rule.getBigImg())) {
                imgBadge.setVisibility(View.GONE);
            } else {
                imgBadge.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(ImagePath.buildPath(rule.getBigImg())
                                .width(ruleWidth)
                                .height(ruleHeight)
                                .cropPath())
                        .into(imgBadge);
            }
        }
    }

    class BannerViewHolder extends BaseViewHolder {
        @BindView(R.id.img_banner)
        ImageView imgBanner;

        BannerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {
            final Poster poster = (Poster) item;
            if (poster == null) {
                imgBanner.setVisibility(View.GONE);
                return;
            } else {
                imgBanner.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(ImagePath.buildPath(poster.getPath())
                                .width(imgWidth)
                                .path())
                        .listener(new OriginalImageScaleListener(imgBanner, imgWidth, 0))
                        .into(imgBanner);
                imgBanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BannerUtil.bannerJump(context, poster, null);
                    }
                });
            }

        }
    }
}
