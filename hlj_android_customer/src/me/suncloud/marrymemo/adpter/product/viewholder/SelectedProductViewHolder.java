package me.suncloud.marrymemo.adpter.product.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.widgets.MarkFlowLayout;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 精选婚品viewHolder
 * Created by chen_bin on 2017/5/2 0002.
 */
public class SelectedProductViewHolder extends TrackerProductViewHolder {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.img_badge)
    ImageView imgBadge;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.slogan_flow_layout)
    MarkFlowLayout sloganFlowLayout;
    @BindView(R.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R.id.line_layout)
    View lineLayout;
    private int imageWidth;
    private OnItemClickListener onItemClickListener;
    public int badgeSize;

    public SelectedProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 123);
        this.badgeSize = CommonUtil.dp2px(itemView.getContext(), 40);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
        sloganFlowLayout.setMaxLineCount(1);
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void setViewData(
            Context context, final ShopProduct product, final int position, int viewType) {
        if (product == null) {
            return;
        }
        if (product.getRule() == null || TextUtils.isEmpty(product.getRule()
                .getShowImg())) {
            imgBadge.setVisibility(View.GONE);
        } else {
            imgBadge.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(ImageUtil.getImagePath2(product.getRule()
                            .getShowImg(), badgeSize))
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgBadge);
        }
        Glide.with(context)
                .load(ImagePath.buildPath(product.getCoverPath())
                        .width(imageWidth)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(product.getTitle());
        addSlogansView(product);
        tvShowPrice.setText(context.getString(R.string.label_price___cv,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getShowPrice())));
        tvCollectCount.setText(context.getString(R.string.label_collect_count3___cv,
                product.getCollectCount()));
    }

    //添加婚品描述
    private void addSlogansView(ShopProduct product) {
        if (CommonUtil.isCollectionEmpty(product.getSlogans())) {
            sloganFlowLayout.setVisibility(View.GONE);
        } else {
            sloganFlowLayout.setVisibility(View.VISIBLE);
            int count = sloganFlowLayout.getChildCount();
            int size = product.getSlogans()
                    .size();
            if (count > size) {
                sloganFlowLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = null;
                if (count > i) {
                    view = sloganFlowLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(itemView.getContext(),
                            R.layout.product_slogan_flow_item,
                            sloganFlowLayout);
                    view = sloganFlowLayout.getChildAt(sloganFlowLayout.getChildCount() - 1);
                }
                TextView tvSlogan = (TextView) view.findViewById(R.id.tv_slogan);
                tvSlogan.setText(product.getSlogans()
                        .get(i));
            }
        }
    }

    //控制底部分割线的显示和隐藏
    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }
}